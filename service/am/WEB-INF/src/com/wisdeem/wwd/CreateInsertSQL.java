package com.wisdeem.wwd;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

public class CreateInsertSQL {
	
	public String getCreateSQL(String tableName) throws JDBCException{
		DB db = DBFactory.getDB();
		
		String tsql = "select * from "+tableName;
		
		StringBuffer keys=new StringBuffer();
		
		StringBuffer inserSQL=new StringBuffer();
		
		MapList map = db.query(tsql);
		
		
		for(int i = 0;i < map.size();i++){
			
			Row row=map.getRow(i);
			
			StringBuffer values=new StringBuffer();
			for(int j=0;j<row.size();j++){
				
				if(i==0){
					keys.append(map.getKey(j));
					keys.append(",");
				}
				
				values.append("'");
				values.append(row.get(j));
				values.append("',");
				
			}
			if(i==0){
				keys.delete(keys.length()-1, keys.length());
			}
			
			values.delete(values.length()-1, values.length());	
			
			inserSQL.append("INSERT INTO "+tableName+"("+keys.toString()+") VALUES("+values.toString()+");\n\r\t");
			
		}
		
		System.out.println(inserSQL.toString());
		
		return inserSQL.toString();
	}
	


}
