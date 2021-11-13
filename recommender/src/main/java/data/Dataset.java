package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.Rating;

public class Dataset {

	private String filePath;
	private ArrayList<Rating> ratingsList;
	private boolean hasHeader;
	private static String sep = ",";
	
	private static Logger log = LogManager.getLogger(Dataset.class);

	public Dataset(String filePath) {
		this.filePath = filePath;
		parseData();
	}

	public Dataset(String filePath, boolean hasHeader) {
		this.filePath = filePath;
		this.hasHeader = hasHeader;
		parseData();
	}

	private void parseData(){
		ratingsList = new ArrayList<Rating>();
		
		try {
			Reader fr = new FileReader(new File(filePath));
			BufferedReader br = new BufferedReader(fr);
			String line;

			if(hasHeader) {
				br.readLine();
			}

			while(br.ready()) {
				line = br.readLine();
				String[] row = line.split(sep);
				
				int uid = Integer.parseInt(row[0]);
				int mid = Integer.parseInt(row[1]);
				double rating = Double.parseDouble(row[2]);
				long ts = Long.parseLong(row[3]);
				
				ratingsList.add(new Rating(uid,mid,rating,ts));
			}
			br.close();
			log.info("parsed "+ratingsList.size()+" ratings");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public ArrayList<Rating> getRatings() {
		return ratingsList;
	}
}
