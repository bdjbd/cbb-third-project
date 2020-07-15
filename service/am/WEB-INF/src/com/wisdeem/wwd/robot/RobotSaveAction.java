package com.wisdeem.wwd.robot;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class RobotSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("WS_AUTO_REPLAY_RULE");
		Table ctable = new Table("wwd", "WS_SEND_MSG");
		// 获取当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(new Date());
		String pid = ac
				.getRequestParameter("ws_auto_replay_rule.form.public_id");
		String rule_id = ac
				.getRequestParameter("ws_auto_replay_rule.form.rule_id");
		String rule_name = ac
				.getRequestParameter("ws_auto_replay_rule.form.rule_name").trim();// 规则名称
		String match_key = ac
				.getRequestParameter("ws_auto_replay_rule.form.match_key").trim();// 关键字
		String match_moudle = ac
				.getRequestParameter("ws_auto_replay_rule.form.match_moudle");// 匹配模式
		String cust_match = ac
				.getRequestParameter("ws_auto_replay_rule.form.cust_match").trim();// 匹配规则
		String msg_type = ac
				.getRequestParameter("ws_auto_replay_rule.form.msg_type");// 消息类型
		String content = ac
				.getRequestParameter("ws_auto_replay_rule.form.content").trim();// 内容
		// 消息id
		String msg_id = ac.getRequestParameter("ws_auto_replay_rule.form.msg_id");
		// 图片附件
		String image = ac.getRequestParameter("ws_auto_replay_rule.form.image");
		// 标题
		String title = ac.getRequestParameter("ws_auto_replay_rule.form.title").trim();
		// 描述
		String description = ac.getRequestParameter("ws_auto_replay_rule.form.description").trim();
		// 封面
		String imagecover = ac.getRequestParameter("ws_auto_replay_rule.form.imagecover");
		// 链接
		String url = ac.getRequestParameter("ws_auto_replay_rule.form.url").trim();

		if (msg_type.equals("1") && content.equals("")) {
			ac.getActionResult().addErrorMessage("内容：不能为空");
			ac.getActionResult().setSuccessful(false);
			return;
		}
//		if (msg_type.equals("2") && image == null) {
//			ac.getActionResult().addErrorMessage("图片附件：不能为空");
//			ac.getActionResult().setSuccessful(false);
//			return;
//		}
		if (msg_type.equals("6")) {
			if ("".equals(title)) {
				ac.getActionResult().addErrorMessage("标题：不能为空");
				ac.getActionResult().setSuccessful(false);
				return;
			} 
			if ("".equals(description)) {
			    ac.getActionResult().addErrorMessage("描述：不能为空");
			    ac.getActionResult().setSuccessful(false);
			    return;
		    }
		}
		if (!pid.equals("") && pid != null) {
			String sqlstr = "select count(*) from WS_SEND_MSG a left join  WS_AUTO_REPLAY_RULE b on a.rule_id=b.rule_id left join WS_PUBLIC_ACCOUNTS "
					+ "c on c.public_id=b.public_id where c.wchat_account='"
					+ pid
					+ "' and b.match_moudle="
					+ match_moudle
					+ " and b.match_key='" + match_key + "' ";

			MapList liststr = db.query(sqlstr);
			int str = Integer.parseInt(liststr.getRow(0).get(0));
			if (str > 1) {
				ac.getActionResult().addErrorMessage(
						"同一公众帐号相同匹配模式的关键字不能重复,请重新输入！");
				ac.getActionResult().setSuccessful(false);
				return;
			}
		} 
		if(match_key.equalsIgnoreCase("help")||match_key.equalsIgnoreCase("zzs")){
			ac.getActionResult().addErrorMessage("关键字：不能为help和zzs");
			ac.getActionResult().setSuccessful(false);
			return;
		}
		if(pid.equals("")){
			String sqlstr = "select count(*) from WS_SEND_MSG a left join  WS_AUTO_REPLAY_RULE b on a.rule_id=b.rule_id left join WS_PUBLIC_ACCOUNTS "
					+ "c on c.public_id=b.public_id where c.public_id='"
					+ pid
					+ "' and b.match_moudle="
					+ match_moudle
					+ " and b.match_key='" + match_key + "' ";

			MapList liststr = db.query(sqlstr);
			int str = Integer.parseInt(liststr.getRow(0).get(0));
			if (str > 0) {
				ac.getActionResult().addErrorMessage(
						"同一公众帐号相同匹配模式的关键字不能重复,请重新输入！");
				ac.getActionResult().setSuccessful(false);
				return;
			}
		}
		if (table != null && table.getRows().size() > 0) {
			TableRow row = table.getRows().get(0);

			if (rule_id.equals("")) {
				row.setValue("public_id", pid);
				row.setValue("rule_name", rule_name);
				row.setValue("match_key", match_key);
				row.setValue("cust_match", cust_match);
				db.save(table);

				rule_id = row.getValue("rule_id");// 匹配规则id

				String sql = "select wchat_account from WS_PUBLIC_ACCOUNTS where public_id = "
						+ pid + "";
				MapList list = db.query(sql);
				String wchat_account = list.getRow(0).get("wchat_account");

				TableRow crow = ctable.addInsertRow();
				crow.setValue("public_id", pid);
				crow.setValue("rule_id", rule_id);
				crow.setValue("msg_type", msg_type);
				crow.setValue("from_user_name", wchat_account);
				crow.setValue("create_time",String.valueOf(System.currentTimeMillis()));
				crow.setValue("content", content);
				crow.setValue("title", title);
				crow.setValue("description", description);
				crow.setValue("url", url);
				crow.setValue("article_count", 1);// 图文消息个数
				crow.setValue("msg_status", 2);// 消息状态
				db.save(ctable);
			} else {

				TableRow updataTr = table.addUpdateRow();
				updataTr.setOldValue("rule_id", rule_id);
				updataTr.setValue("rule_name", rule_name);
				updataTr.setValue("match_key", match_key);
				updataTr.setValue("match_moudle", match_moudle);
				updataTr.setValue("cust_match", cust_match);
				db.save(table);

				TableRow cupdataTr = ctable.addUpdateRow();
				cupdataTr.setOldValue("msg_id", msg_id);
				cupdataTr.setValue("rule_id", rule_id);
				cupdataTr.setValue("content", content);
				cupdataTr.setValue("title", title);
				cupdataTr.setValue("description", description);
				cupdataTr.setValue("url", url);
				db.save(ctable);
			}

			ac.getActionResult().addSuccessMessage("保存成功");
		} else {
			super.doAction(db, ac);
		}
	}
}
