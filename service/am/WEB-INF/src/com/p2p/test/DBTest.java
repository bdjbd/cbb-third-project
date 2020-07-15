package com.p2p.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;


public class DBTest {

	@Test
	public void dbTest(){
		try {
//			ResultSet rst=DBUtil.getConnection().createStatement().executeQuery("SELECT * FROM newsdetail");
//			System.out.println(DBUtil.resultSetToJSON(rst));
//			DBUtil.closeConnection();
			System.out.println(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
			
			System.out.println(isNumeric("132131312321321"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isNumeric(String str){
		  for (int i = str.length();--i>=0;){   
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
}
