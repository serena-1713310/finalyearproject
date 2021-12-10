package com.recsys.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

	private String sparkHome = "E:\\spark\\bin";

	@Bean
	public SparkConf sparkConf() {
		return new SparkConf()
				.setAppName("recsys")
				.setSparkHome(sparkHome)
				.setMaster("local[*]");
	}

	@Bean
	public JavaSparkContext javaSparkContext() {
		return new JavaSparkContext(sparkConf());
	}

	@Bean
	public SparkSession sparkSession() {
		return SparkSession
				.builder()
				.sparkContext(javaSparkContext().sc())
				.appName("ALS Algorithm")
				.getOrCreate();
	}

}
