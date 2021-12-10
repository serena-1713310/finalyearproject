package com.recsys.util;
import static com.recsys.Consts.*;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

	public static Connection connectToDB(){
		Connection con = null;
		try {
			try {
				Class.forName(DB_DRIVER); //loading mysql driver`
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); //attempting to connect to MySQL database
			System.out.println("Printing connection object "+con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}
