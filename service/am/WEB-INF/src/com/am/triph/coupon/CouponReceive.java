package com.am.triph.coupon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgroups.util.UUID;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/*
 *优惠券 领取功能
 */ 
public class CouponReceive implements IWebApiService 
{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{
		
		String memberid = request.getParameter("memberid");
		
		String couponid = request.getParameter("couponid");
		
		
		//检查优惠是否超量领取了
		//领取优惠劵
		//更新领取数量
		
		
		JSONObject json = new JSONObject();
		
		//查询优惠券信息
//		String sql = "select aetp.*,aet.cash,aet.iconpath,aet.effectivedate  from am_eterpelectticket as aet "
//				+ "left join am_eterpelectticket_publish as aetp on aetp.electtickettype = aet.id "
//				+ "left join am_notice as an on aetp.id =an.jump_id "
//				+ "where aetp.id = '"+couponid+"' and  aet.datastatus ='2'";
		
		String tSQL="select b.*,a.cash,a.iconpath,a.effectivedate from am_eterpelectticket a,am_eterpelectticket_publish b where a.id=b.electtickettype and b.id='" + couponid + "'";
		
		
		
		DBManager db  = new DBManager();
		
		MapList mlist = db.query(tSQL);
		
		//如果存在则查询用户是否领取过优惠券
		if(!Checker.isEmpty(mlist))
		{
			//查询用户领取的总张数
			String osql = "select * from am_OrgElectTicker where am_memberid = '"+memberid+"' and coupon_publish_id = '"+mlist.getRow(0).get("id")+"'";
			MapList olist = db.query(osql);
			
			//计算过期天数后的日期
			String expiredSql = "SELECT now()+interval '"+mlist.getRow(0).get("effectivedate")+" days' AS expired  ";
			MapList expiredSqlList = db.query(expiredSql);
			
			try{
				
				//如果用户优惠券表中有值并且大于个人最大领取张数,则该用户不能领取
				if(!Checker.isEmpty(olist) && olist.size() >= mlist.getRow(0).getInt("max_number", 0))
				{
					json.put("code","999");
					json.put("msg", "已超过领取次数,无法领取此优惠券！");
					
				}
				else if(mlist.getRow(0).getLong("surplus_number", 0) <=0 )
				{
					json.put("code","999");
					json.put("msg", "优惠券已被领取完,无法领取！");
				}
				else
				{
					//插入用户优惠劵
					String isql = "insert into  am_OrgElectTicker (id,eterpelectticketid,am_memberid,usestatus,getdatetime,coupon_publish_id,expired)"
							+ " values('"+UUID.randomUUID()+"','"+mlist.getRow(0).get("electtickettype")+"','"+memberid+"','1','now()','"+mlist.getRow(0).get("id")+"','"+expiredSqlList.getRow(0).get("expired")+"')";
					db.update(isql);
					
					//更新剩余张数
					String upsql = "update am_eterpelectticket_publish"
							+ " set surplus_number='"+(mlist.getRow(0).getLong("surplus_number", 0)-1)+"'";
					db.update(upsql);
					
					json.put("code","0");
					json.put("msg", "领取优惠券成功！");
				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
		}
		
		return json.toString();
	}
	
	//计算时间
	public long  getDate(String startDate) 
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		

	    long to = 0L;
	    long from = 0L;
		try {
			to = df.parse(startDate).getTime();
			from = df.parse(df.format(new Date()).toString()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    
	    
	    return to - from;
	}
	

}
