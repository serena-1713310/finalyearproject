package data;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.Rating;

public class Splitter {

	private ArrayList<Rating> ratings;
	private ArrayList<Rating> train;
	private ArrayList<Rating> test;

	private Set<Integer> users;
	private double ratio;

	private static Logger log = LogManager.getLogger(Splitter.class);

	public Splitter(double ratio) {
		this.ratio = ratio;
	}

	protected void sortByDate(List<Rating> allRatings) {
		Collections.sort(allRatings, new Comparator<Rating>() {

			@Override
			public int compare(Rating r1, Rating r2) {
				return (r1.getTimestamp() - r2.getTimestamp()) > 0 ? 1 : -1;
			}
		});
	}

	protected LinkedHashMap<Integer, ArrayList<Rating>> sortMapByDate(Map<Integer,ArrayList<Rating>> map) {
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

	public void timeSplitPerUser(DataModel dm) {
		Instant start = Instant.now();
		if(ratio < 0 || ratio > 1) throw new IllegalArgumentException("test ratio has to between 0 and 1");

		train = new ArrayList<Rating>();
		test = new ArrayList<Rating>();
		Map<Integer, ArrayList<Rating>> map = dm.userRatingsMap();
		// sort each user's ratings list by time in descending order
		LinkedHashMap<Integer, ArrayList<Rating>> userRatings = sortMapByDate(map);

		// iterate through each user's ratings
		for(Map.Entry<Integer, ArrayList<Rating>> entry : userRatings.entrySet()) {
			ArrayList<Rating> currentUserRatings = entry.getValue();

			int numTestSet = (int) Math.ceil(currentUserRatings.size() * ratio);
			int numTrainSet = currentUserRatings.size() - numTestSet;

			/* add first half of dataset to train set
			 * sorted in descending order so oldest gets
			 */
			int idx = 0;
			for(Rating r : currentUserRatings) {
				if(idx < numTrainSet) {
					train.add(r);
				}
				else {
					test.add(r);
				}
				idx++;
			}
		}
		Instant end = Instant.now();

		log.info("assigned "+train.size()+" ratings to train set");
		log.info("assigned "+test.size()+" ratings to test set");
		log.info("split took "+Duration.between(start, end).toMillis()+" ms");
	}

	private void randomSplit() {

	}

}
