package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgroups.util.UUID;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/** * @author  作者：yangdong
 * @date 创建时间：2016年3月29日 上午9:46:53
 * @version  积分商城 兑换优惠券
 */
public class ExchangeCoupon implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		//UUID
		String uuid = UUID.randomUUID().toString();
		//会员id
		String memberid = request.getParameter("memberid");
		//商城优惠券id
		String eterpelectticketid = request.getParameter("eterpelectticketid");
		//优惠券有效天数
		int effectivedate = Integer.parseInt(request.getParameter("effectivedate"));
		//兑换所需积分
		int scorevalue = Integer.parseInt(request.getParameter("scorevalue"));
		
		//扣除会员积分
		StringBuilder deductionScoreSQL = new StringBuilder();
		deductionScoreSQL.append(" UPDATE am_member SET score = score - "+scorevalue+" ");
		deductionScoreSQL.append(" WHERE id = '"+memberid+"' ");
		DB db = null;
		int res = 0;
		try{
			db = DBFactory.newDB();
			res = db.execute(deductionScoreSQL.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		//添加优惠券
		StringBuilder addCouponSQL = new StringBuilder();
		addCouponSQL.append(" INSERT INTO am_orgelectticker (id,eterpelectticketid,am_memberid,usestatus,getdatetime,expired) ");
		addCouponSQL.append(" VALUES('"+uuid+"','"+eterpelectticketid+"','"+memberid+"',1,now(),now()+'"+effectivedate+"day') ");
		if(res != 0){
			res = 0;
			try {
				res = db.execute(addCouponSQL.toString());
			} catch (JDBCException e) {
				e.printStackTrace();
			}finally{
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		if(res==0){
			return "{\"CODE\" : \"1\",\"MSG\" : \"兑换失败\"}";
		}else{
			return "{\"CODE\" : \"0\",\"MSG\" : \"兑换成功\"}";
		}
		
	}

}
