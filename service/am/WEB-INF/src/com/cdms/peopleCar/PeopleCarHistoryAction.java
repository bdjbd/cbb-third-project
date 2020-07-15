package com.cdms.peopleCar;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * @author youzhi
 * @create 2018.01.19
 * @version 说明：解除绑定：在车辆基础信息表中解除人与车辆的绑定，并添加解除绑定的时间到人车匹配历史表
 * 
 *          绑定状态：0:未绑定,1:绑定,2:解除
 */
public class PeopleCarHistoryAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 取得绑定人员id
		String peopleId = ac.getRequestParameter("member_id");
		// 获取车辆主键id
		String carId = ac.getRequestParameter("id");

		// 解除绑定
		String sql1 = "update cdms_vehiclebasicinformation set member_id=null,car_binding_time=null where id='" + carId
				+ "'";
		// 在人车匹配历史表中修改数据，即添加解除绑定时间，并修改绑定状态
		String sql2 = "update cdms_peoplecarshistory set end_time=now(),status='2' where member_id='" + peopleId
				+ "' and car_id='" + carId + "' and status='1'";

		db.execute(sql1);
		db.execute(sql2);

	}
}
