package com.am.app_plugins_common.redpacket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 拆红包
 * @author mac
 *
 */
public class OpenRedPacketWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		String packetId = request.getParameter("packet_id");
		
		String in_account = request.getParameter("in_account");
		
		String sql = "select * from mall_red_packet where id = '"+packetId+"' and status='1' ";
		
		DB db = null;
		
		
		String inAccount="";
		
		JSONObject jso = null;
		
		JSONObject result = null;
		
		try {
			
			db = DBFactory.newDB();
			MapList list = db.query(sql);
			if(!Checker.isEmpty(list)){
				if("1".equals(list.getRow(0).get("status"))){
					String ssql = "select * from mall_account where loginaccount='"+list.getRow(0).get("recv_login_account")+"'";
					
					MapList mlist = db.query(ssql);
					
					if(!Checker.isEmpty(mlist)){
						
						if("aorg".equals(mlist.getRow(0).get("tablename"))){
							
							inAccount = SystemAccountClass.GROUP_CASH_ACCOUNT;
							
						}else if("am_member".equals(mlist.getRow(0).get("tablename"))){
							
							inAccount = SystemAccountClass.CASH_ACCOUNT;
							
						}
						
						VirementManager virementManager = new VirementManager();
						jso = virementManager.execute(db, "",mlist.getRow(0).get("id"), "", in_account, (Double.parseDouble(list.getRow(0).get("amount"))/100)+"", "拆红包", "", "",false);
						
						if("0".equals(jso.get("code").toString())){
							String usql = "update  mall_red_packet set status = '"+2+"',recv_account_id='"+in_account+"' where id = '"+packetId+"'";
							db.execute(usql);
							result = new JSONObject();
							result.put("code", "0");
							result.put("money", list.getRow(0).get("amount"));
							result.put("msg", "操作成功");
						}
						
					}
				}
			}else{
				result = new JSONObject();
				result.put("code", "99");
//				result.put("money", list.getRow(0).get("amount"));
				result.put("msg", "失败");
			}
		} catch (Exception e) {
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
