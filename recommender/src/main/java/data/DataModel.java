package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.Rating;
import recommender.Recommender;

public class DataModel {
	
	private static Logger log = LogManager.getLogger(Recommender.class);
	
	private ArrayList<Rating> ratings;
	private Set<Integer> users;
	private Set<Integer> movies;
	
	public DataModel() {
		super();
	}
	
	public DataModel(Dataset ds) {
		this.ratings = ds.getRatings();
		this.users = new HashSet<Integer>();
		this.movies = new HashSet<Integer>();
	}
	
	public DataModel(ArrayList<Rating> ratings) {
		this.ratings = new ArrayList<Rating>(ratings);
		this.users = new HashSet<Integer>();
		this.movies = new HashSet<Integer>();
	}
	
	protected void addRating(Rating r) {
		if(!users.contains(r.getUserId())) {
			users.add(r.getUserId());
		}
		
		if(!movies.contains(r.getMovieId())) {
			movies.add(r.getMovieId());
		}
	}
	
	protected ArrayList<Rating> ratingsForUser(int user){
		ArrayList<Rating> currentUser = new ArrayList<Rating>();
		for(Rating r : ratings) {
			if(r.getUserId() == user) {
				currentUser.add(r);
			}
		}
		return currentUser;
	}
	
	public Map<Integer,ArrayList<Rating>> userRatingsMap(){
		Map<Integer, ArrayList<Rating>> userMap = new HashMap<Integer,ArrayList<Rating>>();
		ArrayList<Integer> usersList = new ArrayList<Integer>(getUsers());
		for(Integer u : usersList) {
			userMap.put(u, ratingsForUser(u));
		}
		return userMap;
	}
	
	protected Date oldestRating() {
		Collections.sort(ratings, new DateComparator());
		return new Date(ratings.get(0).getTimestamp() * 1000);
	}
	
	protected Date newestRating() {
		Collections.sort(ratings, new DateComparator());
		return new Date(ratings.get(ratings.size()-1).getTimestamp() * 1000);
	}

	protected Set<Integer> getUsers() {
		for(Rating r : ratings) {
			int uid = r.getUserId();
			if(!users.contains(uid)) {
				users.add(uid);
			}
		}
		return users;
	}

	protected Set<Integer> getMovies() {
		for(Rating r : ratings) {
			int mid = r.getMovieId();
			if(!movies.contains(mid)) {
				movies.add(mid);
			}
		}
		return movies;
	}

	public ArrayList<Rating> getRatings() {
		return this.ratings;
	}
}
