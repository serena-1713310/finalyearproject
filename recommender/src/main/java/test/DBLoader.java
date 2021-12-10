package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static com.recsys.Consts.*;

public class DBLoader {

	public static void loadMoviesToDB() {
		int batchSize=20;
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			connection.setAutoCommit(false);
			
			String sql = "INSERT INTO movies(movieId,title,genre) values"
					+ "(?,?,?)";
			
			PreparedStatement sm = connection.prepareStatement(sql);
			
			BufferedReader br = new BufferedReader(
					new FileReader(new File("datasets/movies.csv")));
			
			String line = null;
			int count=0;
			
			System.out.println(br.readLine());
			
			while((line = br.readLine())!= null) {
				String[] data = line.split(",");
				
				int mid = Integer.parseInt(data[0]);
				String title = data[1];
				String genre = data[2];
				
				sm.setInt(1,mid);
				sm.setString(2,title);
				sm.setString(3, genre);
				
				sm.addBatch();
				
				if(count % batchSize == 0) {
					sm.executeBatch();
				}
			}
			br.close();
			sm.executeBatch();
			connection.commit();
			connection.close();
			
			System.out.println("Inserted data successfully!");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
