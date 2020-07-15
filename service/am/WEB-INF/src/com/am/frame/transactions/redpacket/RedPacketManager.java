package com.am.frame.transactions.redpacket;

import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;


/**
 * 发红包
 * @author mac
 *
 */
public class RedPacketManager {
	
	/**
	 * 发送红包方法
	/**
	 * 1.接收参数:
	 * @param outMemberid 发送人会员id
	 * @param outAccountCode 发送人账户常量编码
	 * @param inMemberAccount 接收人会员登录账号
	 * @param inAccountCode 接收人常量编码   可为空
	 * @param red_money   红包金额
	 * @param out_remakers 红包留言
	 * @param red_type   红包类型
	 * 
	 * 2.根据inMemberAccount 查询接收人是否存在
	 * 3.根据 outAccountCode和outMemberid  查询发送红包用户信息 比较 红包金额和余额
	 * 4.执行扣款操作 扣除 发送红包用户的账号金额
	 * 5.执行插入红包记录表 并将状态置为未拆状态
	 * 6.给 收红包人 发送红包通知消息 
	 */
	public JSONObject sendRedPacket(DB db,String outMemberid,String outAccountCode
			,String inMemberAccount,String inAccountId,String red_money
			,String out_remakers,String red_type,String remakers,String name) throws Exception{
		
		JSONObject json = null;
		
		VirementManager virementManager = new VirementManager();
		
//		json = virementManager.execute(db, outMemberid, outAccountCode, red_money, out_remakers, "", "");
//		
//		if(json.length()>0){
//			
//			if("0".equals(json.get("code"))){
//	
		String uid = UUID.randomUUID().toString();
		String isql = "INSERT INTO mall_red_packet (id,send_member_id,recv_login_account"
				+ ",recv_account_id,amount,red_pack_type,status,remarks,create_time,name)"
				+ " VALUES('"+uid+"','"+outMemberid+"','"+inMemberAccount+"','','"+VirementManager.changeY2F(red_money)+"','"+red_type+"','1','"+remakers+"','now()','"+name+"')";
		db.execute(isql);
//			}
		
//		}
	
		return json;
	}
	

}
