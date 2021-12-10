package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.recsys.Consts;
import com.recsys.model.Movie;
import com.recsys.model.UserRating;
import com.recsys.util.MyDataModel;

public class DataLoader {

	private static String delimiter = ",";
	private static Logger log = LogManager.getLogger(DataLoader.class);

	public static ArrayList<Movie> getDefaultMovies() {
		return parseMovies(Consts.MOVIES_FILE);
	}

	public static ArrayList<ArrayList<UserRating>> getDefaultRatings() {
		return parseRatingData(true,Consts.RATINGS_FILE);
	}

	/**
	 * parses a text file and returns 
	 * @param hasHeader
	 * @return
	 */
	public static ArrayList<ArrayList<UserRating>> parseRatingData(boolean hasHeader,String inputFile){
		ArrayList<ArrayList<UserRating>> ratings = new ArrayList<ArrayList<UserRating>>();
		try {
			Reader fr = new FileReader(new File(inputFile));
			BufferedReader br = new BufferedReader(fr);
			String line;

			// check if data has headers, otherwise includes first line
			if(hasHeader) {
				br.readLine();
			}

			while(br.ready()) {
				line = br.readLine();
				UserRating rating = parseRatingLine(line);
				if(ratings.size() < rating.getUserId()) {
					ratings.add(new ArrayList<UserRating>());
				}
				ratings.get(rating.getUserId()-1).add(rating);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ratings;
	}

	public static UserRating parseRatingLine(String row) {
		String[] col = row.split(delimiter);
		int uid = Integer.parseInt(col[0]);
		int mid = Integer.parseInt(col[1]);
		double rating = normal(Double.parseDouble(col[2]));
		if(col.length > 3) {
			long ts = Long.parseLong(col[3]);
			return new UserRating(uid,mid,rating,ts);
		}
		return new UserRating(uid,mid,rating);
	}

	public static ArrayList<Movie> parseMovies(String inputFile) {
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		try {
			Reader fr = new FileReader(new File(inputFile));
			BufferedReader br = new BufferedReader(fr);
			String row;

			br.readLine();

			while(br.ready()) {
				row = br.readLine();
				Movie movie = parseMovieLine(row);
				movieList.add(movie);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return movieList;
	}

	private static Movie parseMovieLine(String row) {
		String[] col = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		int movieId = Integer.parseInt(col[0]);
		String title = col[1];
		String genre = col[2];

		return new Movie(movieId,title,genre);
	}

	/**
	 * converts rating uid and mids to indexes
	 * then writes to file
	 */
	public static void convertIdToIndex() {
		ArrayList<ArrayList<UserRating>> ratings = parseRatingData(false,"datasets/ratings_new.csv");
		MyDataModel model = new MyDataModel(ratings);
		model.updateIndexes();
		ratings = model.getRatings();

		ArrayList<UserRating> allRatings = new ArrayList<UserRating>();
		ratings.forEach(allRatings::addAll);
		writeRatingsToFile(allRatings,"datasets/ratings_new");
	}

	/**
	 * removing header from ratings file
	 * returns ratings arraylist 
	 */
	public static ArrayList<UserRating> removeHeader(String inputFile) {
		ArrayList<UserRating> ratingsList = new ArrayList<UserRating>();
		try {
			Reader fr = new FileReader(new File(inputFile));
			BufferedReader br = new BufferedReader(fr);
			String line;

			br.readLine();

			while(br.ready()) {
				line = br.readLine();
				UserRating rating = parseRatingLine(line);
				ratingsList.add(rating);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ratingsList;
	}

	/**
	 * write new ratings to text file from a arraylist
	 * @param ratings arraylist
	 */
	public static void writeRatingsToFile(List<UserRating> ratings,String outputFile) {
		File file = new File(outputFile);
		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			for(int i = 0; i < ratings.size(); i++) {
				int uid = ratings.get(i).getUserId();
				int mid = ratings.get(i).getMovieId();
				double rating = ratings.get(i).getRating();
				String ts = String.valueOf(ratings.get(i).getTimestamp());

				bw.write(uid + delimiter 
						+ mid + delimiter 
						+ rating + delimiter 
						+ ts);
				bw.newLine();
			}
			log.info("saved {} ratings to {}",ratings.size(),file.getPath());
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeDatasetToFile(Dataset<Row> dataset,String outputFile) {
		dataset.coalesce(1).write().csv(outputFile);
	}

	public static Map<Integer,Integer> getIdIndexMap(boolean flag) {
		Map<Integer,Integer> mid2idx = new HashMap<Integer,Integer>();
		Map<Integer,Integer> idx2mid = new HashMap<Integer,Integer>();

		try {
			Reader fr = new FileReader(new File("datasets/midKeys"));
			BufferedReader br = new BufferedReader(fr);
			String line;

			while(br.ready()) {
				line = br.readLine();
				String[] col = line.split(",");
				int id = Integer.parseInt(col[0]);
				int index = Integer.parseInt(col[1]);

				mid2idx.put(id, index);
				idx2mid.put(index, id);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(flag == true) {
			return mid2idx;
		}
		else {
			return idx2mid;
		}
	}

	/**
	 * parse raw data into arraylist
	 * @param hasHeader
	 * @return
	 */
	public static List<UserRating> parseDataList(boolean hasHeader){
		// initialise ratings
		List<UserRating> ratingsList = new ArrayList<UserRating>();
		try {
			Reader fr = new FileReader(new File("datasets/ratings.csv"));
			BufferedReader br = new BufferedReader(fr);
			String line;

			// check if data has headers, otherwise includes first line
			if(hasHeader) {
				br.readLine();
			}

			while(br.ready()) {
				line = br.readLine();
				UserRating rating = parseRatingLine(line);
				ratingsList.add(rating);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("parsed {} user ratings",ratingsList.size());

		return ratingsList;
	}

	public void writeTrainTest(ArrayList<UserRating> train, ArrayList<UserRating> test) {
		File trainFile = new File("datasets/train.csv");
		try {
			FileWriter trainFW = new FileWriter(trainFile);
			BufferedWriter trainBW = new BufferedWriter(trainFW);

			for(int i = 0; i < train.size(); i++) {
				int uid = train.get(i).getUserId();
				int mid = train.get(i).getMovieId();
				double rating = train.get(i).getRating();
				String ts = String.valueOf(train.get(i).getTimestamp());

				trainBW.write(uid + delimiter 
						+ mid + delimiter 
						+ rating + delimiter 
						+ ts);
				trainBW.newLine();
			}
			log.info("saved {} ratings to {}",train.size(),trainFile.getPath());
			trainBW.close();
			trainFW.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		File testFile = new File("datasets/test.csv");
		try {
			FileWriter testFW = new FileWriter(testFile);
			BufferedWriter testBW = new BufferedWriter(testFW);

			for(int i = 0; i < test.size(); i++) {
				int uid = test.get(i).getUserId();
				int mid = test.get(i).getMovieId();
				double rating = test.get(i).getRating();
				String ts = String.valueOf(test.get(i).getTimestamp());

				testBW.write(uid + delimiter 
						+ mid + delimiter 
						+ rating + delimiter 
						+ ts);
				testBW.newLine();
			}
			log.info("saved {} ratings to {}",test.size(),testFile.getPath());
			testBW.close();
			testFW.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double normal(double x) {
		double max = 5.0;
		double min = 0.0;

		return (x-min)/(max-min);
	}
}
