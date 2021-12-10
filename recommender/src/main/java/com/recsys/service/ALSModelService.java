package com.recsys.service;

import static com.recsys.Consts.DB_DRIVER;
import static com.recsys.Consts.DB_PASS;
import static com.recsys.Consts.DB_RATINGS_TABLE;
import static com.recsys.Consts.DB_URL;
import static com.recsys.Consts.DB_USER;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recsys.model.UserRating;
import com.recsys.repo.RatingRepository;
import com.recsys.util.NewestDateComparator;

@Service
public class ALSModelService {

	@Autowired
	private SparkSession spark;

	@Autowired
	private RatingRepository rRepo;
	
	public ALSModelService(SparkSession spark) {
		this.spark = spark;
	}

	public void trainModel() {
		Dataset<Row> ratings = getRatingsDatasetFromDB();
		
		Dataset<Row>[] split = ratings.randomSplit(new double[] {0.8, 0.2});
		Dataset<Row> train = split[0];
		Dataset<Row> test = split[1];
				
		int iterations = 5;
		int rank = 5;
		double regParam = 0.01;
		
		// build model
		ALS als = new ALS().setMaxIter(iterations)
				.setRank(rank)
				.setRegParam(regParam)
				.setNonnegative(true)
				.setUserCol("user_id")
				.setItemCol("movie_id")
				.setRatingCol("rating");

		// fit model
		ALSModel model = als.fit(train);
		Dataset<Row> oldRecs = model.recommendForAllUsers(10);
		Dataset<Row> recsDataset = oldRecs.withColumn("recommendations", oldRecs.col("recommendations")
				.cast(DataTypes.StringType));
		recsDataset.coalesce(1).write().mode(SaveMode.Overwrite).csv("result/spark");
		renameRecFile("result/spark");
	}
	
	/**
	 * split 
	 * @param ratio
	 * @return train-test splits
	 */
	public List<ArrayList<UserRating>> splitByTime(double ratio){
		if(ratio < 0 || ratio > 1) throw new IllegalArgumentException("test ratio has to between 0 and 1");
		
		// intiialising lists
		List<UserRating> all = rRepo.findAll();
		
		ArrayList<UserRating> train = new ArrayList<UserRating>();
		ArrayList<UserRating> test = new ArrayList<UserRating>();

		HashMap<Integer, ArrayList<UserRating>> userRatings = new HashMap<Integer, ArrayList<UserRating>>();
		
		for(UserRating r : all) {
			int id = r.getUserId();
			if(!userRatings.containsKey(id)) {
				userRatings.put(id, new ArrayList<UserRating>());
			}
			userRatings.get(id).add(r);
		}
		
		sortMapByDate(userRatings); // sorting lists by date for each userId key
		
		// iterate through each entry
		for(Map.Entry<Integer, ArrayList<UserRating>> entry : userRatings.entrySet()) {
			ArrayList<UserRating> currentUserRatings = entry.getValue();

			// calculating ratings split for each user
			int numTestSet = (int) Math.ceil(currentUserRatings.size() * ratio);
			int numTrainSet = currentUserRatings.size() - numTestSet;

			/* iterate through current user's set of ratings
			 * sorted in descending order so oldest gets added train first
			 */
			int idx = 0;
			for(UserRating r : currentUserRatings) {
				if(idx < numTrainSet) {
					train.add(r);
				}
				else {
					test.add(r);
				}
				idx++;
			}
		}
		
		System.out.println(userRatings.size());
		
		List<ArrayList<UserRating>> splits = new ArrayList<ArrayList<UserRating>>();
		splits.add(train);
		splits.add(test);
		return splits;
	}
	
	/*
	 * returns userid-ratingslist map sorted by timestamp
	 */
	protected static Map<Integer, ArrayList<UserRating>> sortMapByDate(Map<Integer,
			ArrayList<UserRating>> map) {
		Map<Integer, ArrayList<UserRating>> sortedMap = new HashMap<Integer,
				ArrayList<UserRating>>();

		for(Map.Entry<Integer, ArrayList<UserRating>> entry : map.entrySet()) {
			int uid = entry.getKey();
			ArrayList<UserRating> currentRatings = entry.getValue();
			Collections.sort(currentRatings, new NewestDateComparator());
			sortedMap.put(uid, currentRatings);
		}
		return sortedMap;
	}
	
	
	/*
	 * Get non-duplicate list of all user ids
	 */
	public Set<Integer> getUsersIdList(List<UserRating> ratings){
		Set<Integer> users = new HashSet<Integer>();
		for(UserRating r : ratings) {
			if(!users.contains(r.getUserId())) {
				users.add(r.getUserId());
			}
		}
		return users;
	}
	
	/*
	 * Get non-duplicate list of all movie ids
	 */
	public Set<Integer> getMovieIdList(List<UserRating> ratings){
		Set<Integer> movies = new HashSet<Integer>();
		for(UserRating r : ratings) {
			if(!movies.contains(r.getUserId())) {
				movies.add(r.getUserId());
			}
		}
		return movies;
	}
	
	/**
	 * renaming auto filename to generic name
	 * @param directory
	 */
	private static void renameRecFile(String directory) {
		File dir = new File(directory);
		File[] files = dir.listFiles();
		File target = files[2];
		target.renameTo(new File(directory+"/recommendations.csv"));
	}
	
	/**
	 * evalauting model by computing rmse
	 * @param model
	 * @param test
	 * @return
	 */
	public double evaluateModel(ALSModel model, Dataset<Row> test) {
		Dataset<Row> predictions = model.transform(test);
		
		RegressionEvaluator evaluator = new RegressionEvaluator()
				  .setMetricName("rmse")
				  .setLabelCol("rating")
				  .setPredictionCol("prediction");
		double rmse = evaluator.evaluate(predictions);
		return rmse;
	}
	
	public Dataset<Row> getTopNMovies(int n) {
		Dataset<Row> topRated = spark.sql(
				"SELECT movies.title,"
						+ "(SELECT COUNT(*) FROM ratings"
						+ "WHERE ratings.movieid = movies.movieid) AS raters"
						+ "FROM movies ORDER BY raters DESC").limit(n);
		return topRated;
	}
		
	private Dataset<Row> getRatingsDatasetFromDB() {
		Dataset<Row> ratings = spark.read()
				.format("jdbc")
				.option("driver", DB_DRIVER)
				.option("url", DB_URL)
				.option("dbTable", DB_RATINGS_TABLE)
				.option("user", DB_USER)
				.option("password", DB_PASS)
				.load();

		ratings.createOrReplaceGlobalTempView("ratings");
		return ratings;
	}
	
	public void convertListToDS(List<UserRating> list) {
		List<Row> ls = new ArrayList<Row>();
		Row row = RowFactory.create(list.toArray());
		ls.add(row);
	}

	public Dataset<Row> getRatingsRDD(){
		JavaRDD<UserRating> ratingsRDD = spark
				.read().option("header", "true")
				.textFile("datasets/ratings_noheader.csv")
				.javaRDD()
				.map(UserRating::parseRating);
		Dataset<Row> ratings = spark.createDataFrame(ratingsRDD, UserRating.class);

		return ratings;
	}

	public void addNewUser(ArrayList<UserRating> ratings) {
		Dataset<Row> allRatings = getRatingsDatasetFromDB();

		List<Row> rows = new ArrayList<Row>();
		Row row = RowFactory.create(ratings.toArray());
		rows.add(row);
	}
	
	
}
