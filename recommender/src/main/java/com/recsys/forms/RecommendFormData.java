package com.recsys.forms;

import java.util.ArrayList;
import java.util.List;

import com.recsys.model.RecommendedMovie;

public class RecommendFormData {

	public int userId;
	private List<RecommendedMovie> recs;
	
	public RecommendFormData() {
		this.recs = new ArrayList<>();
	}
	
	public RecommendFormData(int userId) {
		this.userId = userId;
		this.recs = new ArrayList<>();
	}
	
//	public RecommendFormData(int numRecs, int userId) {
//		this.numRecs = numRecs;
//		this.userId = userId;
//		this.recs = new ArrayList<>();
//	}
	
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	public void addRecommendation(RecommendedMovie movie) {
		this.recs.add(movie);
	}

	/**
	 * @return the recs
	 */
	public List<RecommendedMovie> getRecommendations() {
		return recs;
	}

	/**
	 * @param the recs to set
	 */
	public void setRecommendations(List<RecommendedMovie> recs) {
		this.recs = recs;
	}

}
