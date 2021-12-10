package com.recsys.model;

import javax.persistence.Table;

@Table(name="recommendations")
public class RecommendedMovie {
	
	public int userId;
	public int movieId;
	public double value;
	
	public RecommendedMovie(int userId, int movieId, double value) {
		this.userId = userId;
		this.movieId = movieId;
		this.value = value;
	}
	public RecommendedMovie() {	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	/**
	 * @param movieId the movieId to set
	 */
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @return the movieId
	 */
	public int getMovieId() {
		return movieId;
	}
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return userId + " " + movieId + " " + value;
	}
	
}
