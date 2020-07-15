package com.am.app_plugins.food_safety;

import java.util.UUID;

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
 *说明：冷柜开通WebApi
 */
public class FreezerOpenIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//支付ID
		String payId = request.getParameter("payId");
		//会员id
		String memberid = request.getParameter("memberid");
		//转账金额
		String paymoney = request.getParameter("paymoney");

		//根据订单id查询交易是否成功未处理
		String selPaySql="SELECT * FROM mall_trade_detail WHERE id='"+payId+"' AND trade_state='1' AND is_process_buissnes='0'";
		
		resultJson = new JSONObject();
			MapList selPayMap = db.query(selPaySql);
			if(!Checker.isEmpty(selPayMap)){
				
				//冷柜开通记录表插数据
				freezerOpen(db,memberid,paymoney);
				
				//修改交易表的状态
				UpdatePlayersServer server = new UpdatePlayersServer();
				server.updateTransaction(db, payId);
			}else{
				resultJson.put("code", "999");
				resultJson.put("msg", "交易失败");
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

	/**
	 * 冷柜开通记录表插数据
	 * @param db
	 * @param memberid  会员iD
	 * @param paymoney  转账金额
	 * @throws JDBCException 
	 */
	private void freezerOpen(DB db, String memberid, String paymoney) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		
		String insertFreezerOpenSql = "INSERT INTO mall_freezer_open_record( id, member_id, transfer_amount, transfer_time) "
				+ "values ('"+uuid+"','"+memberid+ "','"+paymoney+ "',now()) ";
		db.execute(insertFreezerOpenSql);
		
	}

}
