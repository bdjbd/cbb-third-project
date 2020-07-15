package com.cdms.peopleCar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author youzhi
 * @create 2018.01.17
 * @version 说明：分配按钮：将人与车辆匹配（指定小组的人可以使用小组的车）
 * 
 */
public class PeopleCarAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 取得人员id
		String peopleId = ac.getRequestParameter("cdms_peoplecar.form.people");

		// 获得列表所有行: _s_单元编号 当前单元编号：cdms_peoplecar.list
		String[] peoplecarList = ac.getRequestParameters("_s_" + "cdms_peoplecar.list");

		// 获取所有车辆主键id: 单元编号.元素编号
		String[] carIds = ac.getRequestParameters("cdms_peoplecar.list.id");

		List<String> carIdsList = new ArrayList<String>(); // 车辆id的集合

		if (!Checker.isEmpty(peoplecarList)) { // 保证列表有值，不是空列表
			for (int i = 0; i < peoplecarList.length; i++) {
				if ("1".equals(peoplecarList[i])) {// '1'为选中,'0'为未选中
					carIdsList.add(carIds[i]);
				}
			}
		}

		// 如果得到的车辆id的集合有值，则分配，否则发送提示
		if (!Checker.isEmpty(carIdsList)) {
			for (String carId : carIdsList) {

				// 先查询当前人员和该车辆是否被分配过，如果被分配过就不再分配，如果没有，则分配
				String select = "select id from cdms_pepolecarrelationship where member_id='" + peopleId
						+ "' and car_id='" + carId + "'";
				DBManager dbm = new DBManager();
				MapList ml = dbm.query(select);

				if (ml.size() == 0) {
					// 生成主键
					UUID uuid = UUID.randomUUID();
					// 将数据存入人车匹配关系表
					String sql1 = "insert into cdms_pepolecarrelationship (id,member_id,car_id) values ('" + uuid
							+ "','" + peopleId + "','" + carId + "')";

					db.execute(sql1);
				}
			}

		} else {
			String msg = "请选择车辆！";
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().addSuccessMessage(msg);
			ac.getActionResult().setUrl("/cdms/cdms_peoplecar.do?m=e");
		}
	}
}
