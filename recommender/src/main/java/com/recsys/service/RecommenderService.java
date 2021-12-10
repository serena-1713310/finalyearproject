package com.recsys.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.recsys.Consts;
import com.recsys.model.Movie;
import com.recsys.model.RecommendedMovie;
import com.recsys.util.MyDataModel;

import test.DataLoader;

@Service
public class RecommenderService {
	
	public MyDataModel getDataModel() {
		return new MyDataModel(DataLoader.getDefaultRatings());
	}
	
	public boolean userExistsInModel(int user) {
		MyDataModel dataModel = getDataModel();
		if(dataModel.getUsers().contains(user)) {
			return true;
		}
		else {
			return false;
		}
	}

	public ArrayList<Movie> getRecommendationForUser(int userId){
		ArrayList<RecommendedMovie> allRecommendations = getRecommendationList();
		ArrayList<RecommendedMovie> currentUser = new ArrayList<RecommendedMovie>();

		for(RecommendedMovie rec : allRecommendations) {
			if(rec.getUserId() == userId) {
				currentUser.add(rec);
			} else {
				continue;
			}
		}
		
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		for(RecommendedMovie rec : currentUser) {
			Movie movie = getMovie(rec);
			movieList.add(movie);
		}
		return movieList;
	}

	public ArrayList<RecommendedMovie> getRecommendationList() {
		String inputFile = "result/spark/recommendations.csv";
		ArrayList<RecommendedMovie> recList = new ArrayList<RecommendedMovie>();
		try {
			Reader fr = new FileReader(new File(inputFile));
			BufferedReader br = new BufferedReader(fr);
			String line;
			br.readLine();
			while(br.ready()) {
				line = br.readLine();
				ArrayList<RecommendedMovie> userRecs = parseRecommendations(line);
				recList.addAll(userRecs);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Collections.sort(recList, new Comparator<RecommendedMovie>() {
			@Override
			public int compare(RecommendedMovie o1, RecommendedMovie o2) {
				int uid1 = o1.getUserId();
				int uid2 = o2.getUserId();
				return uid1-uid2;
			}
		});
		return recList;
	}

	public static ArrayList<RecommendedMovie> parseRecommendations(String row){
		String[] col = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		int user = Integer.parseInt(col[0]);
		String recs = col[1];

		// use regex to extract recommendations from string
		Pattern p = Pattern.compile("\\{([^}]*)\\}");
		Matcher m = p.matcher(recs);
		ArrayList<RecommendedMovie> currentUser = new ArrayList<RecommendedMovie>();
		while(m.find()) {
			String[] userRecs = m.group(1).split(",");
			int movieId = Integer.parseInt(userRecs[0]);
			double value = Double.parseDouble(userRecs[1]);
			currentUser.add(new RecommendedMovie(user,movieId,value));
		}
		return currentUser;
	}
	
	public Movie getMovie(RecommendedMovie rec) {
		ArrayList<Movie> movies = getMovies();
		
		for(Movie m : movies) {
			if(m.getMovieId() == rec.getMovieId()) {
				return m;
			}
			else {
				continue;
			}
		}
		return null;
	}

	private ArrayList<Movie> getMovies() {
		return DataLoader.parseMovies(Consts.MOVIES_FILE);
	}

	// ======================== NEED TO REMOVE ============================= //
	//	public ALSModel trainModel(RecommendFormData params) {
	//
	//		//		Binarizer binarize = new Binarizer()
	//		//				.setInputCol("rating")
	//		//				.setOutputCol("pref")
	//		//				.setThreshold(3.0);
	//
	//		// train-test split
	//		Dataset<Row> train = getTrainOrTestSet(0);
	//		//		Dataset<Row> train = binarize.transform(trainTemp);
	//		train.cache();
	//
	//		int iterations = 5;
	//		int rank = 5;
	//		double regParam = 0.01;
	//
	//		// build model
	//		ALS als = new ALS().setMaxIter(iterations)
	//				.setRank(rank)
	//				.setRegParam(regParam)
	//				.setNonnegative(true)
	//				.setUserCol("userId")
	//				.setItemCol("movieId")
	//				.setRatingCol("rating");
	//
	//		// fit model
	//		ALSModel model = als.fit(train);
	//		model.setColdStartStrategy("drop");
	//
	//		return model;
	//	}
	//
	//	public Map<String,Double> evaluateModel(Dataset<Row> predictions) {
	//
	//		String[] metrics = new String[] {"rmse","mse","r2","mae","var"};
	//		Map<String,Double> results = new HashMap<>();
	//
	//		RegressionEvaluator evaluator = new RegressionEvaluator()
	//				.setLabelCol("rating")
	//				.setPredictionCol("prediction");
	//
	//		for(int i = 0; i < metrics.length; i++) {
	//			evaluator.setMetricName(metrics[i]);
	//			double result = evaluator.evaluate(predictions);
	//			results.put(metrics[i],result);
	//		}
	//
	//		System.out.println(results.toString());
	//		return results;
	//	}
	//
	//	private Dataset<Row> getTrainOrTestSet(int splits) {
	//		SparkSession spark = SparkSession
	//				.builder().master("local[*]")
	//				.appName("ALS")
	//				.getOrCreate();
	//
	//		ratingsDF = spark.read().format("com.databricks.spark.csv").option("inferSchema", "true")
	//				.option("header", "true").load(Consts.RATINGS_FILE);
	//		ratingsDF = ratingsDF.select(ratingsDF.col("userId"), ratingsDF.col("movieId"), ratingsDF.col("rating"), ratingsDF.col("timestamp"));
	//		ratingsDF.createOrReplaceTempView("ratings");
	//
	//		moviesDF = spark.read().format("com.databricks.spark.csv").option("inferSchema", "true")
	//				.option("header", "true").load(Consts.MOVIES_FILE);
	//		moviesDF = moviesDF.select(moviesDF.col("movieId"), moviesDF.col("title"), moviesDF.col("year"),moviesDF.col("genres"));
	//		moviesDF.createOrReplaceTempView("movies");
	//
	//		Dataset<Row>[] split = ratingsDF.randomSplit(new double[] {0.8, 0.2});
	//		Dataset<Row> train = split[0];
	//		Dataset<Row> test = split[1];
	//
	//		if (splits == 0) {
	//			return train;
	//		}
	//		if(splits == 1){
	//			return test;
	//		}
	//		if(splits == 2) {
	//			return moviesDF;
	//		}
	//		return ratingsDF;
	//
	//	}
	//
	//	// get the top n most rated movies
	//	public Dataset<Row> getTopNMovies(int n) {
	//		SparkSession spark = SparkSession
	//				.builder().master("local[*]")
	//				.appName("ALS")
	//				.getOrCreate();
	//		Dataset<Row> topRated = spark.sql(
	//				"SELECT movies.title,"
	//						+ "(SELECT COUNT(*) FROM ratings"
	//						+ "WHERE ratings.movieid = movies.movieid) AS raters"
	//						+ "FROM movies ORDER BY raters DESC").limit(n);
	//		return topRated;
	//	}
	//
	//	public String getDatasetStats(){
	//
	//		// number of rows in ratings dataset
	//		long numberOfRatings = ratingsDF.count();
	//
	//		// total users in ratings dataset
	//		long numberOfUsers = ratingsDF.select(ratingsDF.col("userId")).distinct().count();
	//
	//		// number of movies in dataset
	//		long numberOfMovies = moviesDF.select(moviesDF.col("movieId")).distinct().count();
	//
	//		String stats = String.format("Got %d ratings from %d users on %d movies.",
	//				numberOfRatings, numberOfUsers, numberOfMovies);
	//
	//		return stats;
	//	}
	//
	//	public String trainTime(long start,long end) {
	//		long total = (end - start)/1000;
	//
	//		return String.valueOf(total);
	//
	//	}

}
