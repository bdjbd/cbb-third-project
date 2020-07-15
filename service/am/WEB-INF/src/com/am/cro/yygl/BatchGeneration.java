package com.am.cro.yygl;

import java.util.UUID;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.context.ActionContext;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 预约管理批量生成
 * 
 * @author guorenjie
 */
public class BatchGeneration extends DefaultAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		DBManager db = new DBManager();

		String starttime1 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.starttime1");// 上班时间
																							// 时
		String starttime2 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.starttime2");// 上班时间
																							// 分
		String stoptime1 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.stoptime1");// 下班时间
																						// 时
		String stoptime2 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.stoptime2");// 下班时间
																						// 分
		String step1 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.step1");// 时间步长
																					// 时
		String step2 = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.step2");// 时间步长
																					// 分
		String orgcode = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.orgcode");// 组织机构代码
		String weekday = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.weekday");// 星期值
		String maxbooknum = ac
				.getRequestParameter("cro_bookingmanagement.batchgeneration.maxbooknum");// 最大预约数量
		String isdelete = ac.getActionParameter();//是否清楚原来的数据		1 清楚	并新增		2 保留并新增
		
			
		
		if (Checker.isEmpty(maxbooknum)) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().setUrl("/am_bdp/cro_bookingmanagement.batchgeneration.do");
			ac.getActionResult().addErrorMessage("最大预约数量不能为空！");
		} else {
			// 上班时间
			int starttime = Integer.parseInt(starttime1) * 60
					+ Integer.parseInt(starttime2);
			// 下班时间
			int stoptime = Integer.parseInt(stoptime1) * 60
					+ Integer.parseInt(stoptime2);
			if (stoptime > starttime) {
				if(isdelete.equals("1")){
					String deleteSql = "delete from CRO_BOOKINGMANAGEMENT where orgcode='"+orgcode+"' and weekday='"+weekday+"'";
					db.execute(deleteSql);
				}
				// 时间步长 单位是分
				int step = Integer.parseInt(step1) * 60
						+ Integer.parseInt(step2);
				// 时间步长 时
				int hour = Integer.parseInt(step1);
				// 时间步长 分
				int minute = Integer.parseInt(step2);
				String id = "";// 主键id
				String parms = "";// sql参数
				String parms1 = "";
				String sql = "";
				String sql1 = "";
				// 时
				int time1 = Integer.parseInt(starttime1);
				// 分
				int time2 = Integer.parseInt(starttime2);

				// 时（int转成string，小于10时前面加0，然后拼入sql）
				String temp1 = "";
				// 分（int转成string，，大于60分时，分-60.时+1，然后拼入sql）
				String temp2 = "";
				// 时（int转成string，小于10时前面加0，然后拼入sql）
				String temp3 = "";
				// 分（int转成string，，大于60分时，分-60.时+1，然后拼入sql）
				String temp4 = "";
				
				// //先插入开始时间
				// if (time1 <= 12) {

				if (time1 < 10) {
					temp1 = "0" + time1;
				}
				 else {
				 temp1 = " " + time1;
				 }
				if (time2 < 10) {
					temp2 = "0" + time2;
				}
				 else {
				 temp2 = "" + time2;
				 }
				id = UUID.randomUUID().toString();
				parms = "('" + id + "','" + orgcode + "'," + weekday + ","
						+ maxbooknum + ",'" + temp1 + ":" + temp2 + "')";
				sql = "insert into CRO_BOOKINGMANAGEMENT values " + parms;
				db.execute(sql);

				for (int i = 1; i < (stoptime - starttime) / step; i++) {
					time1 += hour;
					time2 += minute;
					while (time2 >= 60) {
						time2 -= 60;
						time1 += 1;
					}

					if (time1 < 10) {
						temp3 = "0" + time1;
					}else{
						temp3 = ""+time1;
					}

					if (time2 < 10) {
						temp4 = "0" + time2;
					}
					 else {
					 temp4 = "" + time2;
					 }
					id = UUID.randomUUID().toString();

					parms1 = "('" + id + "','" + orgcode + "'," + weekday + ","
							+ maxbooknum + ",'" + temp3 + ":" + temp4 + "')";

					sql1 = "insert into CRO_BOOKINGMANAGEMENT values " + parms1;

					db.execute(sql1);
					ac.getActionResult().setSuccessful(true);
				}
			} else {
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().setUrl("/am_bdp/cro_bookingmanagement.batchgeneration.do");
				ac.getActionResult().addErrorMessage("可预约结束时间必须大于开始时间！请重新选择");
			}
		}
		return ac;
	}

}
