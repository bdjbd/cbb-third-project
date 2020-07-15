package com.am.app_plugins.precision_help;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.app_plugins.precision_help.server.PrecisionHelpServer;
import com.am.more.kopp_train.server.UpdatePlayersServer;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月24日
 *@version
 *说明：生产者帮扶--还款WebApi
 */
public class ProducerHelpIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		DB db=null;
		JSONObject resultJson = null;
		try {
			 db = DBFactory.newDB();
		//交易ID
		String payId = request.getParameter("payId");
		//资助id
		String helpId = request.getParameter("helpId");

		//根据订单id查询交易是否成功未处理
		String selPaySql="SELECT * FROM mall_trade_detail WHERE id='"+payId+"' AND trade_state='1' AND is_process_buissnes='0'";
		
		resultJson = new JSONObject();
			MapList selPayMap = db.query(selPaySql);
			if(!Checker.isEmpty(selPayMap)){
				UpdatePlayersServer server = new UpdatePlayersServer();
				PrecisionHelpServer helpServer = new PrecisionHelpServer();
				//修改还款状态为已还款
				helpServer.updateHelpInfo(db,helpId);
				//修改交易表的状态
				server.updateTransaction(db, payId);
				resultJson.put("code", "0");
				resultJson.put("msg", "交易完成");
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
