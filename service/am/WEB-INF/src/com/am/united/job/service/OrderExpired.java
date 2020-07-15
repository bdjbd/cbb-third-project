package com.am.united.job.service;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class OrderExpired {
	
	private DB db=null;
	public void orderExpiredstatus() {
		
		
		try {
			
			//初始化属性
			
			db=DBFactory.getDB();
			
			queryOder(db);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void queryOder(DB db) throws Exception {
		
		//查询订单在支付状态下停留超过半小时的数据
		String queryMYORDERSQL="select * from mall_buy_stock_record where status='1' AND create_time <now()-interval '10 min'";
			MapList map = db.query(queryMYORDERSQL);
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					//购买记录id
					String id = map.getRow(i).get("id");
					//农业项目id
					String project_id = map.getRow(i).get("project_id");
					//农业项目股数
					String buy_number = map.getRow(i).get("buy_number");
					//更新农业项目表数据
					String updateMYORDERSQL="UPDATE mall_agriculture_projects SET already_buy_number=already_buy_number-'"+buy_number+"' where id='"+project_id+"'";
					db.execute(updateMYORDERSQL);
					String deleteMYORDERSQL="delete from mall_buy_stock_record where id='"+id+"'";
					db.execute(deleteMYORDERSQL);
				}
			}
		}
	
}
