package data;

import java.util.Comparator;

import model.Rating;

public class DateComparator implements Comparator<Rating>{

	@Override
	public int compare(Rating o1, Rating o2) {
		long r1 = o1.getTimestamp();
		long r2 = o2.getTimestamp();
		
		return r1 == r2 ? 0 : r1 < r2 ? -1 : 1;
	}
}
