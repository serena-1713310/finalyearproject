package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.mllib.recommendation.Rating;

import com.recsys.model.Movie;

public class RecParser {
	
	private static String moviesFile = "datasets/movies";

	public static void main(String[] args) {
		// parse recommendations 
		ArrayList<ArrayList<Rating>> allRecommendations = getRecommendationList();
		
		System.out.println(allRecommendations.size());
	}

	public static ArrayList<ArrayList<Rating>> getRecommendationList() {
		String inputFile = "result/sparkals2/recommendations.csv";
		ArrayList<ArrayList<Rating>> recList = new ArrayList<ArrayList<Rating>>();
		try {
			Reader fr = new FileReader(new File(inputFile));
			BufferedReader br = new BufferedReader(fr);
			String line;

			br.readLine();

			while(br.ready()) {
				line = br.readLine();
				ArrayList<Rating> userRecs = parseRecommendations(line);
				recList.add(userRecs);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return recList;
	}
	
	public static ArrayList<Rating> parseRecommendations(String row){
		String[] col = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		int user = Integer.parseInt(col[0]);
		String recs = col[1];

		// use regex to extract recommendations from string
		Pattern p = Pattern.compile("\\{([^}]*)\\}");
		Matcher m = p.matcher(recs);
		ArrayList<Rating> currentUser = new ArrayList<Rating>();
		while(m.find()) {
			String[] userRecs = m.group(1).split(",");
			int movieId = Integer.parseInt(userRecs[0]);
			double value = Double.parseDouble(userRecs[1]);
			currentUser.add(new Rating(user,movieId,value));
		}
		return currentUser;
	}
	
	public static String getMovieTitle(ArrayList<Movie> movies, int id) {
		String movie = "";
		for (Movie m : movies) {
			if (m.getMovieId() == id) {
				movie = m.getTitle();
			} 
		}
		return movie;
	}
}
