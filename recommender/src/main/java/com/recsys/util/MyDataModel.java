package com.recsys.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.recsys.model.Movie;
import com.recsys.model.UserRating;

public class MyDataModel {

	private static Logger log = LogManager.getLogger(MyDataModel.class);

	private ArrayList<ArrayList<UserRating>> ratings;
	private ArrayList<Integer> users;
	private ArrayList<Integer> movies;

	public MyDataModel(ArrayList<ArrayList<UserRating>> ratings) {
		this.ratings = ratings;
	}
	
	public double calculateMeanRating() {
		double sum = 0.0;
		int total = 0;
		
		for (int i = 0; i < ratings.size(); i++) {
			for(UserRating r : ratings.get(i)) {
				sum += r.getRating();
				total++;
			}
		}
		return sum/total;
	}
	
	public void updateIndexesToZeroBased() {
		Instant start = Instant.now();
		ArrayList<ArrayList<UserRating>> newRatings = new ArrayList<ArrayList<UserRating>>();
		
		for(int i = 0; i < ratings.size(); i++) {
			if(newRatings.size() -1 < i) {
				newRatings.add(new ArrayList<UserRating>());
			}
			List<UserRating> currentUser = ratings.get(i);
			ArrayList<UserRating> update = new ArrayList<UserRating>();
			for(UserRating r : currentUser) {
				int uid = r.getUserId(); 
				uid -= 1;
				int mid = r.getMovieId();
				mid -= 1;
				update.add(new UserRating(uid, mid, r.getRating(),r.getTimestamp()));
			}
			newRatings.get(i).addAll(update);
		}
		ratings = newRatings;
		log.info("converted {} ratings indexes to zero-based ({} ms)",ratings.size(),Duration.between(start, Instant.now()).toMillis());
	}
	
	public void updateIndexes(){
		int midx = 1;
		int uidx = 1;
		ArrayList<Integer> currentUsers = new ArrayList<Integer>(getUsers());
		ArrayList<Integer> currentMovies = new ArrayList<Integer>(getMovies());
		
		Map<Integer,Integer> userMap = new HashMap<Integer,Integer>();
		for(int uid : currentUsers) {
			userMap.put(uid, uidx);
			uidx++;
		} 
		
		Map<Integer,Integer> movieMap = new HashMap<Integer,Integer>();
		File midKeys = new File("datasets/midKeys");
		String delimiter = ",";
		try {
			FileWriter fw = new FileWriter(midKeys);
			BufferedWriter bw = new BufferedWriter(fw);
			for(int mid: currentMovies) {
				movieMap.put(mid, midx);
				midx++;
				bw.write(mid+delimiter+midx);
				bw.newLine();			
			}
			bw.close();
			log.info("saved movieId-indexes to {}",midKeys.getPath());
		} catch (IOException e) {
		e.printStackTrace();
		}
		
		ArrayList<ArrayList<UserRating>> newRatings = new ArrayList<ArrayList<UserRating>>();
		// iterate over all ratings
		for(int i = 0; i < ratings.size(); i++) {
			if(newRatings.size() <= i) {
				// creating new user in new ratings list
				newRatings.add(new ArrayList<UserRating>());
			}
			// getting current user's rating
			ArrayList<UserRating> userRatings= ratings.get(i);
			for(int j = 0; j < userRatings.size(); j++) { // iterate through each rating for current user
				UserRating r = userRatings.get(j);
				int uid = r.getUserId();
				uid = userMap.get(uid);
				int mid = r.getMovieId();
				mid = movieMap.get(mid);
				newRatings.get(r.getUserId()-1).add(new UserRating(uid,mid,r.getRating(),r.getTimestamp()));
			}
		}
		ratings = newRatings;
	}
	
	/*
	 *  applies a ratings threshold for each user
	 */
	public void filter(int threshold) {
		for(int i = 0; i < ratings.size(); i++) {
			List<UserRating> currentUser = ratings.get(i);
			for(int j = threshold; j < currentUser.size(); j++) {
				if(currentUser.size() > threshold) {
					currentUser.remove(j);
				}
			}
		}
	}
	
	public ArrayList<Integer> getUsers() {
		users = new ArrayList<Integer>();
		
		for(int i = 0; i < ratings.size(); i++) {
			List<UserRating> currentUser = ratings.get(i);
			for(int j = 0; j < currentUser.size(); j++) {
				int uid = currentUser.get(j).getUserId();
				if(users.contains(uid) == false) {
					users.add(uid);
				} else {
					continue;
				}
			}
		}
		Collections.sort(users);
		return users;
	}

	public ArrayList<Integer> getMovies() {
		ArrayList<UserRating> allRatings = new ArrayList<UserRating>();
		ratings.forEach(allRatings::addAll);
		
		movies = new ArrayList<Integer>();
		
		for(UserRating r : allRatings) {
			int mid = r.getMovieId();
			if(movies.contains(mid) == false) {
				movies.add(mid);
			} else {
				continue;
			}
		}
		return movies;
	}

	public ArrayList<UserRating> ratingsForUser(int user){
		return ratings.get(user);
	}

	public int maxUserId() {
		int maxId = 0;
		for(int i = 0; i < ratings.size(); i++) {
			List<UserRating> currentUser = ratings.get(i);
			UserRating r = currentUser.stream()
					.reduce((a,b) -> a.getUserId() < b.getUserId() ? a : b)
					.get();
			
			if(r.getUserId() > maxId) {
				maxId = r.getUserId();
			}
			else {
				continue;
			}
		}
		return maxId;
	}

	public int maxMovieId() {
		ArrayList<UserRating> allRatings = new ArrayList<UserRating>();
		ratings.forEach(allRatings::addAll);
		
		UserRating r = allRatings.stream()
				.reduce((a,b) -> a.getMovieId() > b.getMovieId() ? a : b)
				.get();
		return r.getMovieId();
	}

	/*
	 * returns oldest rating in the datamodel
	 */
	protected void oldestRating() {
		for(int i = 0; i < ratings.size(); i++) {
			ArrayList<UserRating> userRatings = ratings.get(i);
			Collections.sort(userRatings, new OldestDateComparator());
		}
	}

	/*
	 * sorts each user's ratings from newest -> oldest
	 */
	protected void newestRating() {
		for(int i = 0; i < ratings.size(); i++) {
			ArrayList<UserRating> userRatings = ratings.get(i);
			Collections.sort(userRatings, new NewestDateComparator());
		}
	}
	
	public ArrayList<ArrayList<UserRating>> getRatings() {
		return this.ratings;
	}

	@Override
	public String toString() {
		return "DataModel [ratings=" + ratings.size() 
		+ ", users=" + getUsers().size() 
		+ ", movies=" + getMovies().size() + "]";
	}
}
