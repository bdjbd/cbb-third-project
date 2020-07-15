package com.am.frame.bonus.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 平台消费计算
 * @author xianlin
 *2016年4月15日
 */
public class PlatformConsumption{

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	public  void calculateTotalMoney(String  orderid)
	{
		DB db = null;
		//查询销售额计算时间
		String parametersql = "SELECT vvalue FROM avar WHERE vid='salescalculationtime'";
		MapList paramelist = new MapList();
		String param = null;
		try{
			db = DBFactory.newDB();
			paramelist = db.query(parametersql);
			param = paramelist.getRow(0).get(0);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		//转换时间
		SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd "+param+":00:00" );
		String nowdate = sdf.format(new Date());
		
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
		dBefore = calendar.getTime();   //得到前一天的时间
		
		String beforedate = sdf.format(dBefore);
		
		//查询订单信息
		StringBuilder queryOrderSQL = new StringBuilder();
		queryOrderSQL.append(" SELECT COALESCE(totalprice,0) FROM mall_memberorder WHERE id = '"+orderid+"'  ");
		MapList maplist = null;
		long totalprice = 0;
		try {
			db = DBFactory.newDB();
			maplist = db.query(queryOrderSQL.toString());
			if(!Checker.isEmpty(maplist)){
				totalprice = (long)(Double.parseDouble(maplist.getRow(0).get(0))*100);
			}

			//查询时间区域内是否有订单
			StringBuilder queryOrderTimeSQL = new StringBuilder();
//			queryOrderTimeSQL.append(" SELECT * FROM mall_memberorder WHERE  ");
//			queryOrderTimeSQL.append(" completedate < to_date('"+nowdate+"','YYYY-MM-dd hh24:mi:ss') ");
//			queryOrderTimeSQL.append(" AND completedate > to_date('"+beforedate+"','YYYY-MM-dd hh24:mi:ss') ");
			
			String thisdate = BonusJobclassImpl.getInstance().timeconversion(db);
			
			queryOrderTimeSQL.append("SELECT * FROM mall_consumer_total_record");
			queryOrderTimeSQL.append(" WHERE create_time > '"+thisdate+"' :: TIMESTAMP - INTERVAL '1 day'");
			queryOrderTimeSQL.append(" AND create_time <= '"+thisdate+"'");
			
			MapList orderlist = db.query(queryOrderTimeSQL.toString());
			
			if(!Checker.isEmpty(orderlist)){
				
				StringBuilder updateSQL = new StringBuilder();
				updateSQL.append(" UPDATE mall_consumer_total_record SET total_amount_money = COALESCE(total_amount_money,0) + "+totalprice+" ");
				updateSQL.append(" WHERE create_time = (SELECT create_time FROM mall_consumer_total_record ORDER BY ");
				updateSQL.append(" create_time DESC LIMIT 1 ) ");
				db.execute(updateSQL.toString());
			}else{
				Table table=new Table("am_bdp", "mall_consumer_total_record");
				TableRow row=table.addInsertRow();
				row.setValue("id", UUID.randomUUID().toString());
				row.setValue("total_amount_money", totalprice);
				db.save(table);
			}
			
		} catch (JDBCException e) {
			e.printStackTrace();
		} finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
