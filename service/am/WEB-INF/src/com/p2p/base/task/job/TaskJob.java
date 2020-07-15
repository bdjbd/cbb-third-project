package com.p2p.base.task.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.task.init.TaskInit;

/**
 * Author: Mike
 * 2014年7月16日
 * 说明：任务模板引擎
 *
 **/
public class TaskJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//获取所有的企业
		String getOrg="SELECT * FROM ws_org_baseinfo";
		try {
			DB db=DBFactory.getDB();
			MapList orgMap=db.query(getOrg);
			if(Checker.isEmpty(orgMap))return ;
			
			//查询企业企业会员的日常任务
			//删除会员未完成的日常任务
			String deleteFaildTaskSQL="DELETE FROM p2p_usertask "
					+ " WHERE enterprisetaskid "
					+ " IN ( SELECT et.id FROM p2p_enterprisetask AS et "
					+ " LEFT JOIN p2p_tasktemplate AS tt ON et.tasktemplateid=tt.id "
					+ " WHERE tt.tasktemplatetype='0' ) AND taskrunstate='1'";
			db.execute(deleteFaildTaskSQL);
			
			//为所有会员初始化日常任务
			String enterTaskSQL="SELECT et.id,et.orgid FROM p2p_enterprisetask AS et "
					+ " LEFT JOIN p2p_tasktemplate AS tt "
					+ " ON et.tasktemplateid=tt.id WHERE tt.tasktemplatetype='0'";
			MapList orgTaskMap=db.query(enterTaskSQL);
			if(Checker.isEmpty(orgTaskMap))return;
			for(int i=0;i<orgTaskMap.size();i++){
				TaskInit.initUserTask(orgTaskMap.getRow(i).get("orgid"),orgTaskMap.getRow(i).get("id"));
			}
			
			/***设置是否为推荐商品**/
			//查询企业销售阈值和粉丝阈值
			//判断该企业商品是否达到次阈值
			for(int i=0;i<orgMap.size();i++){
				//销售阈值
				int sellThre=orgMap.getRow(i).getInt("sellthreshold", 1000);
				//粉丝阈值
				int fansThre=orgMap.getRow(i).getInt("fansthreshold",1000);
				//SELECT * FROM ws_commodity_name WHERE recommend<1 OR recommend is NULL AND deadline>now()
				String shopSQL="SELECT comdity_id,amount,fans FROM ws_commodity_name "
						+ " WHERE orgid='"+orgMap.getRow(i).get("orgid")+"' "
						+ " AND (recommend<1 OR recommend is NULL) AND deadline>now()";
				MapList shopMap=db.query(shopSQL);
				if(Checker.isEmpty(shopMap))return ;
				for(int j=0;j<shopMap.size();j++){//遍历机构的所有商品
					Row row=shopMap.getRow(j);
					if(sellThre<row.getInt("amount", 0)||fansThre<row.getInt("fans", 0)){
						//如果销售量大于阈值或者粉丝量大于阈值，则设置为推荐商品，推荐值为1
						db.execute("update ws_commodity_name SET recommend=1 WHERE comdity_id="+row.getInt("comdity_id",0));
					}
				}
			}
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}

}
