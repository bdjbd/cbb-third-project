package com.am.app_plugins_common.redpacket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.transactions.redpacket.RedPacketManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 发送红包 前台执行方法

 * @author mac
 *
 */
public class SendRedPacketWebapi implements IWebApiService{

	
	/**
	 * 1.接收参数:
	 * outMemberid 发送人会员id
	 * outAccountCode 发送人账户常量编码
	 * inMemberAccount 接收人会员登录账号
	 * inAccountCode 接收人常量编码   可为空
	 * red_money   红包金额
	 * out_remakers 出账描述
	 * red_type   红包类型
	 * remakers 红包留言
	 * 
	 * 2.根据inMemberAccount 查询接收人是否存在
	 * 3.根据 outAccountCode和outMemberid  查询发送红包用户信息 比较 红包金额和余额
	 * 4.执行扣款操作 扣除 发送红包用户的账号金额
	 * 5.执行插入红包记录表 并将状态置为未拆状态
	 * 6.给 收红包人 发送红包通知消息 
	 */
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		String outMemberid = request.getParameter("outMemberid");
		String outAccountCode =request.getParameter("outAccountCode");
		String inMemberAccount =request.getParameter("inMemberAccount");
		String inAccountId =request.getParameter("inAccountCode");
		String red_money =request.getParameter("red_money");
		String out_remakers =request.getParameter("out_remakers");
		String red_type =request.getParameter("red_type");
		String remakers = request.getParameter("remakers");
		String name = request.getParameter("name");
		
		if(Checker.isEmpty(out_remakers))
		{
			out_remakers = "发果蔬红包";
		}
		
		
		RedPacketManager rPack = new RedPacketManager();
		DB db = null;
		JSONObject json = null;
		
		try {
			
			db = DBFactory.newDB();
			json = rPack.sendRedPacket(db, outMemberid, outAccountCode, inMemberAccount, inAccountId, red_money, out_remakers, red_type,remakers,name);
		
		} catch (Exception e) {
			
			json = new JSONObject();
			
			try {
				json.put("code", "999");
				json.put("msg", "系统异常");
			} catch (JSONException e1) {
				
				e1.printStackTrace();
			}

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
		
		return json.toString();
	}

}
