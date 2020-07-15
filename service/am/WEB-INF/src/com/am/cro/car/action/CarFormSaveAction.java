package com.am.cro.car.action;

import org.apache.log4j.Logger;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 汽车公社汽修厂端 车辆表单页面保存按钮 对输入的车架号进行判断，当前修车厂的车架号不可重复
 * 
 * @author 张少飞 2017-09-06
 */
public class CarFormSaveAction extends DefaultAction {
	final Logger logger = Logger.getLogger(CarFormSaveAction.class);

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 输入的车架号
		String carframenumber = ac
				.getRequestParameter("cro_carmanager.form.carframenumber");
		String id = ac.getRequestParameter("cro_carmanager.form.id");
		logger.info("id====" + id);
		String orgcode = ac.getVisitor().getUser().getOrgId();
		// 当前修车厂机构代码
		// 判断是否为空
		String Sql = "";
		if (!Checker.isEmpty(carframenumber)) {
				Sql = "SELECT * FROM cro_CarManager WHERE CarFrameNumber = '"
						+ carframenumber + "' and OrgCode = '" + orgcode
						+ "' and id<>'" + id + "'";
			MapList list = db.query(Sql);
			// 若查到在当前修车厂已存在相同车架号，则返回错误提示，不进行保存操作；
			if (!Checker.isEmpty(list)) {
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("该车架号已存在！");
				ac.getActionResult().setUrl(
						"/am_bdp/cro_carmanager.form.do?m=s");
				return;
			} else {
				// 正常保存 保存成功
				Table table = ac.getTable("cro_CarManager");
				db.save(table);
				ac.getActionResult().setSuccessful(true);
			}
		}
	}

}
