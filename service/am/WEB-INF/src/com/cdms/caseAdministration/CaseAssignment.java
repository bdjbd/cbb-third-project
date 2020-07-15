package com.cdms.caseAdministration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.push.server.JPushNoticeCK;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

// 案件分配
public class CaseAssignment extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 取得案件id
		String caseId = ac.getRequestParameter("case_assignment.caseid");

		// 获得列表所有行: _s_单元编号 当前单元编号：case_assignment
		String[] memberList = ac.getRequestParameters("_s_" + "case_assignment");

		// 获取所有驾驶员id: 单元编号.元素编号
		String[] memberIds = ac.getRequestParameters("case_assignment.id");

		// 被选中驾驶员id的集合
		List<String> memberIdsList = new ArrayList<String>();

		if (!Checker.isEmpty(memberList)) { // 保证列表有值，不是空列表
			for (int i = 0; i < memberList.length; i++) {
				if ("1".equals(memberList[i])) {// '1'为选中,'0'为未选中
					memberIdsList.add(memberIds[i]);
				}
			}
		}

		// 如果得到的驾驶员id的集;合有值，则分配，否则发送提示
		if (!memberIdsList.isEmpty()) {
			for (String memberId : memberIdsList) {

				// 先查询当前人员是否被分配过该案件，如果被分配过就不再分配，如果没有，则分配
				String select = "select * from cdms_caseorderpersonnel where case_id='" + caseId + "' and member_id='"
						+ memberId + "'";
				DBManager dbm = new DBManager();
				MapList ml = dbm.query(select);

				if (ml.size() == 0) {
					UUID uuid = UUID.randomUUID();
					String sql1 = "insert into cdms_caseorderpersonnel (id,case_id,member_id) values ('" + uuid + "','"
							+ caseId + "','" + memberId + "')";

					// 修改案件状态
					// -1=被拒单、0=无人接单、1=新案件、2=派单中、3=已接单、5=查勘中、6=已结束
					String sql2 = "update cdms_case set case_state='2',distribution_time=now() where id='" + caseId
							+ "'";

					db.execute(sql1);
					db.execute(sql2);
					JSONObject content = new JSONObject();
					JSONObject pushData = new JSONObject();
					content.put("describe", "您有一个新案件");
					content.put("title", "案件 通知");
					content.put("url", "mall.content");
					pushData.put("url", "mall.content");
					content.put("DATA", pushData);
					pushCase(dbm, memberId,content);
				}
			}

		} else {
			String msg = "请选择驾驶员！";
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().addSuccessMessage(msg);
			ac.getActionResult().setUrl("/cdms/case_assignment.do?m=e");

		}

	}
	private void pushCase(DBManager db, String memberId, JSONObject content) {
		// 根据当前车辆所在机构的机构id，查询人员，得到同一机构下的所有人员的xtoken注册码
		String memberSql = "select * from mall_mobile_type_record m where m.member_id ='"+memberId+"'";
		MapList memberMapList = db.query(memberSql);
		for (int j = 0; j < memberMapList.size(); j++) {

			// 进行消息推送(mobile_type: 手机类型，1为android，2为ios)
			if ("1".equals(memberMapList.getRow(j).get("mobile_type"))) {
				Collection<Object> coll = new LinkedList<Object>();
				coll.add(memberMapList.getRow(j).get("xtoken"));
				// 发送
				JPushNoticeCK.sendNoticeById(content, coll, "1");
			} else if ("2".equals(memberMapList.getRow(j).get("mobile_type"))) {
				Collection<Object> coll = new LinkedList<Object>();
				coll.add(memberMapList.getRow(j).get("xtoken"));
				// 发送
				JPushNoticeCK.sendNoticeById(content, coll, "2");
			}
		}
	}

}
