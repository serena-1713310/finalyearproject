package test;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import com.recsys.model.UserRating;

public class SparkALS {

	public static void main(String[] args) {

		SparkSession spark = SparkSession
				.builder().master("local[*]")
				.appName("ALS")
				.getOrCreate();

		JavaRDD<UserRating> ratingsRDD = spark
				.read().option("header", "true").textFile("datasets/ratings_noheader.csv").javaRDD()
				.map(UserRating::parseRating);
		Dataset<Row> ratings = spark.createDataFrame(ratingsRDD, UserRating.class);

		// split data into train-test sets
		Dataset<Row>[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
		Dataset<Row> train = splits[0];
		Dataset<Row> test = splits[1];
		train.cache();

		ALSModel model = buildModel(train,test);
		model.setColdStartStrategy("drop");
//		Dataset<Row> predictions = model.transform(test);
//		RegressionEvaluator evaluator = new RegressionEvaluator()
//				.setMetricName("rmse")
//				.setLabelCol("rating")
//				.setPredictionCol("prediction");
//		double rmse = evaluator.evaluate(predictions);
//		System.out.println("Root-mean-square error = " + rmse);

		//		Generate top 10 recommendations for each user
		Dataset<Row> userRecs = model.recommendForAllUsers(10);
		Dataset<Row> newRecs = userRecs.withColumn("recommendations", 
				userRecs.col("recommendations")
				.cast(DataTypes.StringType));
		
		newRecs.coalesce(1).write()
		.mode(SaveMode.Overwrite)
		.csv("result/spark3");
	}

	public static ALSModel buildModel(Dataset<Row> train,Dataset<Row> test) {
		// Setting model parameters
		ALS als = new ALS()
				.setMaxIter(5)
				.setRank(5)
				.setRegParam(0.01)
				.setUserCol("userId")
				.setItemCol("movieId")
				.setRatingCol("rating")
				.setNonnegative(true);
		ALSModel model = als.fit(train);

		train.unpersist();
		test.unpersist();

		return model;
	}

	public static double evaluateModel(ALSModel model,Dataset<Row> test) {
		// set cold start strategy to 'drop' to avoid NaN eval values
		model.setColdStartStrategy("drop");
		//		Evaluate the model by computing the RMSE on the test data
		Dataset<Row> predictions = model.transform(test);
		RegressionEvaluator evaluator = new RegressionEvaluator()
				.setMetricName("rmse")
				.setLabelCol("rating")
				.setPredictionCol("prediction");
		double rmse = evaluator.evaluate(predictions);
		System.out.println("Root-mean-square error = " + rmse);
		return rmse;
	}
	
	/**
	 * performs hyperparamter tuning, returns the model with the bset params
	 * @param train
	 * @param test
	 */
	public static void findBestModel(Dataset<Row> train,Dataset<Row> test) {
		ALS als = new ALS()
				.setUserCol("userId")
				.setItemCol("movieId")
				.setRatingCol("rating")
				.setNonnegative(true);

		// constructs a grid of parameters to search over
		ParamMap[] paramGrid = new ParamGridBuilder()
				.addGrid(als.maxIter(),new int[] {5,10})
				.addGrid(als.regParam(),new double[] {0.01,0.05,0.1})
				.addGrid(als.rank(), new int[] {5,10,15,20,25})
				.build();

		// initialising cross validation
		TrainValidationSplit trainValidationSplit = new TrainValidationSplit()
				.setEstimator(als)
				.setEvaluator(new RegressionEvaluator()
						.setMetricName("rmse")
						.setLabelCol("rating")
						.setPredictionCol("prediction"))
				.setEstimatorParamMaps(paramGrid)
				.setTrainRatio(0.8);  

		// Run train validation split, and store the best set of parameters.
		TrainValidationSplitModel model = trainValidationSplit.fit(train);
		model.transform(test);
	}
}
