package com.am.frame.member.bindingaccount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgroups.util.UUID;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月8日 下午5:16:31
 * @version 添加交易账号  memberid:会员Id,idcode:账号,idtype:账号类型,idname:账号名称,cardholde:持卡人,paypassword:支付密码,phone: 	银行预留手机号
 */
public class BindingAccount implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String id = UUID.randomUUID().toString();
		String memberid = request.getParameter("memberid");
		String idcode = request.getParameter("account");
		int idtype = Integer.parseInt(request.getParameter("type"));
		String idname = request.getParameter("name");
		String cardholde = request.getParameter("cardholde");
		String paypassword = request.getParameter("password");
		String bankphone = request.getParameter("phone");
		
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO mall_transactions_id(id,member_id,id_type,id_name,id_code,card_holder,pay_password,certificates,bank_phone,create_time) ");
		sql.append(" VALUES('"+id+"','"+memberid+"',"+idtype+",'"+idname+"','"+idcode+"','"+cardholde+"','"+paypassword+"','证件信息','"+bankphone+"',now()) ");
		
		
		DB db = null;
		try{
			db = DBFactory.newDB();
			db.execute(sql.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
