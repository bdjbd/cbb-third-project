package com.cdms.ExcelOutPut.action;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class ExcelAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
	String orgcode = ac.getVisitor().getUser().getOrgId();// 获取当前机构id
	String starttime = "";
	String endtime = "";
	String carmembers = "";
	String orgids = "";
		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_general_statement.query");
		if (queryRow != null) {
			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				starttime = queryRow.get("starttime");
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
			}

			// 如果得到的车牌号不为空，就按照用户输入的车牌号查询
			if (!Checker.isEmpty(queryRow.get("car_number"))) {
				carmembers = queryRow.get("car_number");
			}

			// 如果获取的机构不为空
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgids= queryRow.get("orgcode");
			}
		ac.getActionResult().setScript("location.href='/p2p/com.cdms.ExcelOutPut.action.ExcelOutPut.do?orgcode="+orgcode+"&orgids="+orgids+"&starttime="+starttime+"&endtime="+endtime+"&carmembers="+carmembers+"'");

		}
	}
}
