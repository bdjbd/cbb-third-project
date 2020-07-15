package com.am.app_plugins_common.redpacket;

import org.json.JSONObject;

import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.redpacket.RedPacketManager;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

public class SendRedPacketPayCallBack implements IBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject businessJso = new JSONObject(business);
		
		
		String outMemberid = businessJso.getString("outMemberid");
		String outAccountCode =businessJso.getString("outAccountCode");
		String inMemberAccount =businessJso.getString("inMemberAccount");
		String inAccountId ="";
		String red_money =businessJso.getString("red_money");
		String out_remakers ="";
		String red_type =businessJso.getString("red_type");
		String remakers = businessJso.getString("remakers");
		String name = businessJso.getString("name");
		
		
		if(Checker.isEmpty(out_remakers))
		{
			out_remakers = "发果蔬红包";
		}
		
		RedPacketManager rPack = new RedPacketManager();
		JSONObject json = null;
		
		json = rPack.sendRedPacket(db, outMemberid, outAccountCode, inMemberAccount, inAccountId, red_money, out_remakers, red_type,remakers,name);
		
		return "";
	}

}
