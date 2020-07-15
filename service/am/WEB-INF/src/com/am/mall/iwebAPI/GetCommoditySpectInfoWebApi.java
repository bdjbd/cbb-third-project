package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.mall.commodity.CommodityManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月25日
 * @version 
 * 说明:<br />
 * 获取商品规格信息
 */
public class GetCommoditySpectInfoWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		
		//规格ID
		String specId=request.getParameter("fid");
		//商品ID
		String comdityId=request.getParameter("id");
		//会员ID
		String memberId=request.getParameter("memberId");
		//className 介绍分类
		String className=request.getParameter("className");
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			//根据流程状态ID获取商品信息
			CommodityManager comdityManager=CommodityManager.getInstance();
			
			result=comdityManager.getCommodityPaymentInfo(db,specId,comdityId,className);
						
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return result.toString();
	}

}
