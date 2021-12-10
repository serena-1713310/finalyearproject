package com.recsys.util;

import java.util.Comparator;
import java.util.Date;

import com.recsys.model.UserRating;

public class NewestDateComparator implements Comparator<UserRating>	{

	@Override
	public int compare(UserRating o1, UserRating o2) {
		Date d1 = new Date(o1.getTimestamp()*1000);
		Date d2= new Date(o2.getTimestamp()*1000);
		
		if(d1.after(d2)) {
			return -1;
		}
		if(d1.before(d2)) {
			return 1;
		}
		return d1.compareTo(d2);
	}

}
