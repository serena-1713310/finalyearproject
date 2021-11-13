package model;

public class Rating implements Comparable<Rating> {
	
	private int userId;
	private int movieId;
	private double rating;
	private long timestamp;
	
	public Rating(int userId, int movieId, double rating, long timestamp) {
		super();
		this.userId = userId;
		this.movieId = movieId;
		this.rating = rating;
		this.timestamp = timestamp;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getMovieId() {
		return movieId;
	}
	
	public double getRating() {
		return rating;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "Rating [userId=" + this.userId 
				+ ", movieId=" + this.movieId 
				+ ", rating=" + this.rating 
				+ ", timestamp=" + this.timestamp
				+ "]";
	}

	@Override
	public int compareTo(Rating other) {
		return Long.compare(getTimestamp(),
				other.getTimestamp());
	}
	
	
}
