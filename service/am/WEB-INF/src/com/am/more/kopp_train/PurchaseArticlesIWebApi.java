package com.am.more.kopp_train;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月18日
 *@version
 *说明：购买文章WebApi
 */
public class PurchaseArticlesIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//支付ID
		String payId = request.getParameter("payid");
		//内容id
		String id = request.getParameter("id");
		//会员id
		String memberid = request.getParameter("memberid");
		//次数
		String count = request.getParameter("count");
		//费用
		String cost = request.getParameter("cost");
		//根据订单id查询交易是否成功未处理
		String selPaySql="SELECT * FROM mall_trade_detail WHERE id='"+payId+"' AND trade_state='1' AND is_process_buissnes='0'";
		
		resultJson = new JSONObject();
			MapList selPayMap = db.query(selPaySql);
			if(!Checker.isEmpty(selPayMap)){
				UpdatePlayersServer server = new UpdatePlayersServer();
				//给文章购买表添加数据
				server.updatePurchaseRecords(db,memberid,id,count);
				//修改交易表的状态
				server.updateTransaction(db, payId);
			}else{
				resultJson.put("code", "999");
				resultJson.put("msg", "交易已处理");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
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
		
		return resultJson.toString();
	}

}
