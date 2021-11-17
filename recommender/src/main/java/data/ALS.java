package data;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

import model.Rating;

public class ALS {

	private static final Logger log = LogManager.getLogger(ALS.class);

	private int factors;
	private double lambda;
	private double learning_rate;

	private static int numUsers;
	private static int numMovies;

	public ALS(int factors, double lambda, double learning_rate) {
		this.factors = factors;
		this.lambda = lambda;
		this.learning_rate = learning_rate;
	}

	public static void main(String[] args) {
		// parsing raw ratings data
		Dataset ds = new Dataset("datasets/ratings.csv",true);
		// creating new datamodel
		DataModel model = new DataModel(ds);
		model.oldestRating();
		ArrayList<Rating> allRatings = model.getRatings();
		// Splitter split = new Splitter(0.1,model);

		// using linkedhashmap over map because generic map doesnt guarantee insertion order
		//LinkedHashMap<Integer,ArrayList<Rating>> sortedRatings = split.sortMapByDate(model);

		numUsers = model.getUsers().size();
		numMovies = model.getMovies().size();
		int max = numUsers * numMovies;
		
		System.out.println("matrix size: "+max+"("+numUsers+","+numMovies+")");
		
		System.out.println(model.toString());

//		model.filter(20);
//		log.info(model.toString());

		
//		SparseMatrix rMatrix = CRSMatrix.zero(numUsers,numMovies);
//
//		for(int i = 0; i < allRatings.size(); i++) {
//			Rating r = allRatings.get(1);
//			rMatrix.set(r.getUserId(), r.getMovieId(), r.getRating());
//		}
//
//		System.out.println(rMatrix.getRow(1));
	}


}
