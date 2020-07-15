package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年5月24日
 * @version 
 * 说明:<br />
 * 商品表，商品主图存储有逗号分割转换为JSON格式
 */
public class ProcessComdityImagesStr implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			//1,查询商品表，
			String querySQL="SELECT * FROM mall_commodity ";
			MapList comdMap=db.query(querySQL);
			//2,将商品表中 mainimages 
			if(!Checker.isEmpty(comdMap)){
				String updateSQL="UPDATE mall_commodity SET mainimages=? WHERE id=? ";
				for(int i=0;i<comdMap.size();i++){
					Row row=comdMap.getRow(i);
					String mainimages=row.get("mainimages");
					String id=row.get("id");
					if(!Checker.isEmpty(mainimages)){
						String[] files=mainimages.split(",");
						JSONArray arry=new JSONArray();
						for(String f:files){
							JSONObject item =new JSONObject();
							if(!Checker.isEmpty(f)){
								item.put("name", f.substring(f.lastIndexOf("/")+1, f.length()));
								item.put("path", f);
							}
							arry.put(item);
						}
						
						db.execute(updateSQL,new String[]{arry.toString(),id},
								new int[]{Type.VARCHAR,Type.VARCHAR});
					}
				}
			}
			
			
			
		}catch(Exception e){
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		
		return null;
	}

}
