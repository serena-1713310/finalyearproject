package recommender;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import data.DataModel;
import data.Dataset;
import data.Splitter;
import model.Rating;

public class Recommender {

	private static Logger log = LogManager.getLogger(Recommender.class);

	public static void main(String[] args) {
		// parsing raw ratings data
		Dataset ds = new Dataset("datasets/ratings.csv",true);
		// creating new datamodel
		DataModel model = new DataModel(ds);
		ArrayList<Rating> allRatings = model.getRatings();
		Splitter split = new Splitter(0.2);
		split.timeSplitPerUser(model);
		// using linkedhashmap over map because generic map doesnt guarantee insertion order
		//LinkedHashMap<Integer,ArrayList<Rating>> sortedRatings = split.sortMapByDate(model);
		
		
	}
}
