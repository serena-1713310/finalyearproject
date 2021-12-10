package com.recsys.model;

public class Recommendation {

	private int userId;
	private int movieId;
	private String title;
	private String genre;
	private double value;
	
	public Recommendation(int userId, int movieId, String title, String genre, double value) {
		this.userId = userId;
		this.movieId = movieId;
		this.title = title;
		this.genre = genre;
		this.value = value;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Recommendation [userId=" + userId 
				+ ", movieId=" + movieId 
				+ ", title=" + title 
				+ ", genre=" + genre
				+ ", value=" + value + "]";
	}
	
}
