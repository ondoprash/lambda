package com.ondo.lambda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Dbconnection {
	final static String URL = System.getenv("url");
	final static String USER = System.getenv("dbuser");
	final static String DRIVER = System.getenv("driver");
	final static String PASSWORD = System.getenv("dbpassword");

	public static Connection getCon() throws Exception {
		Connection conn = null;
		Class.forName(DRIVER);

		System.out.println("Connecting to a selected database...");
		conn = DriverManager.getConnection(URL, USER, PASSWORD);
		return conn;
	}

	public static void addPNLOGS(Integer WEARERID, Float TEMPERATURE )  {
		Connection conn;
		try {
			conn = getCon();
			PreparedStatement preparedStatement = conn
					.prepareStatement("INSERT INTO PNLOGS (WEARERID,TEMPERATURE,EVENTTIME,DISPLAYTIME) VALUES (?,?,CURTIME(), DATE_FORMAT(NOW(), '%m/%d/%Y %r') )");
			preparedStatement.setInt(1, WEARERID);
			preparedStatement.setFloat(2, TEMPERATURE);
		 
			preparedStatement.execute();
			conn.close();

		} catch (Exception e) {
			 
			e.printStackTrace();
		}
		
	}
}
