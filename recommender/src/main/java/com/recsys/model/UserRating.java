package com.recsys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ratings")
public class UserRating {
	
	@Id
	@GeneratedValue
	@Column
	private int id;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="movie_id")
	private int movieId;
	
	@Column
	private double rating;
	
	@Column
	private long timestamp;
	
	public UserRating() {}
	
	public UserRating(int userId, int movieId, double rating, long timestamp) {
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.timestamp = timestamp;
	}
	
	public UserRating(int id, int userId, int movieId, double rating, long timestamp) {
		this.id = id;
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.timestamp = timestamp;
	}

	public UserRating(int userId, int movieId, double rating) {
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}


	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}


	/**
	 * @return the movieId
	 */
	public int getMovieId() {
		return movieId;
	}


	/**
	 * @param movieId the movieId to set
	 */
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}


	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}


	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}


	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public static UserRating parseRating(String row) {
		String[] col = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		int userId = Integer.parseInt(col[0]);
		int movieId = Integer.parseInt(col[1]);
		double rating = Float.parseFloat(col[2]);
		if(col.length > 3) {
			long timestamp = Long.parseLong(col[3]);
			return new UserRating(userId, movieId, rating, timestamp);
		}
		else {
			return new UserRating(userId,movieId,rating);
		}
	}
	
	@Override
	public String toString() {
		return "UserRating [userId=" + this.getUserId() 
				+ ", movieId=" + this.getMovieId()
				+ ", rating=" + this.getRating()
				+ ", timestamp=" + this.getTimestamp()
				+ "]";
	}

}

