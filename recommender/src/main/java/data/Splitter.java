package data;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.Rating;

public class Splitter {
	
	private ArrayList<Rating> ratings;
	private Set<Integer> users;
	private double ratio;
	
	private static Logger log = LogManager.getLogger(Splitter.class);
	
	public Splitter(double ratio) {
		this.ratio = ratio;
	}
	
	public List<Rating> sortListByDate(List<Rating> allRatings){
		Collections.sort(allRatings, new DateComparator());
		return allRatings;
	}
	
	
	public LinkedHashMap<Integer, ArrayList<Rating>> sortMapByDate(DataModel dm) {
		Map<Integer, ArrayList<Rating>> map = dm.userRatingsMap();
		LinkedHashMap<Integer, ArrayList<Rating>> sortedMap = new LinkedHashMap<Integer,ArrayList<Rating>>();
		
		Instant start = Instant.now();
		
		for(Map.Entry<Integer, ArrayList<Rating>> entry : map.entrySet()) {
			int uid = entry.getKey();
			ArrayList<Rating> currentRatings = entry.getValue();
			Collections.sort(currentRatings, new DateComparator());
			sortedMap.put(uid, currentRatings);
		}
		Instant end = Instant.now();
		
		log.info("sorting took "+Duration.between(start, end).toMillis()+" ms");
		return sortedMap;
	}
	
	private DataModel timeSplitPerUser(DataModel dm) {
		
		for(Integer user : dm.getUsers()) {
			
		}
		
		return null;
	}
	
	private void randomSplit() {
		
	}
	
}
