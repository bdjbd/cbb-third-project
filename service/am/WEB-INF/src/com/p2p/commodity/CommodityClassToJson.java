package com.p2p.commodity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

public class CommodityClassToJson implements IWebApiService 
{
	private int mRowCount=0;
	private String mPrefix="    ";

	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) 
	{
		String tUpID=request.getParameter("UpID");
		String tOrgID=request.getParameter("OrgID");
		mPrefix=request.getParameter("Prefix");
		
		String tStr=getCommClass(tUpID,tOrgID,"");
		
		String tJsonString="{\"COUNT\" : \"" + mRowCount + "\",\"DATA\" : [" + tStr + "]}";
		
		return tJsonString;
	}
	
	private String getCommClass(String upID,String orgID,String str1)
	{
		String tSql=" select * from ws_commodity where parent_id='" + upID + "' and orgid='" + orgID + "'";
		String rValue=""; //{"COUNT":"1","DATA":[{"" : "","" : "","":""}]}";
		try 
		{
			System.out.println("getCommClass.tSql=" + tSql);
			
			DB db = DBFactory.getDB();
			MapList map=db.query(tSql);
			
			System.out.println("getCommClass.map.size()=" + map.size());
			
			
			
			for(int i=0;i<map.size();i++)
			{
				if(mRowCount>0)
					rValue+=",";
				
				rValue+="{";
				
				rValue+="\"" + map.getKey(0).toUpperCase() + "\"";
				rValue+=":\"" + map.getRow(i).get(0) + "\"";
				
				for(int l=1;l<map.getRow(i).size();l++)
				{
					rValue+=",\"" + map.getKey(l).toUpperCase() + "\"";
					rValue+=":\"" + map.getRow(i).get(l) + "\"";
				}
				
				rValue+=",\"PREFIX_STRING\" : \"" + str1 + "\"}";
				
				mRowCount++;
				
				rValue+= getCommClass(map.getRow(i).get("COMDY_CLASS_ID"),orgID,str1 + mPrefix);
			}
		} 
		catch (JDBCException e)
		{
			e.printStackTrace();
		}
		
		return rValue;
	}

}
