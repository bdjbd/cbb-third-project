package com.fastunit.app.user;

import com.fastunit.UserSupport;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.framework.sso.SSOUtil;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.user.config.UserLevel;
import com.fastunit.user.table.AUser;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;

public class SaveUserAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String password = null;
		Table table = ac.getTables().get(0);
		TableRow tr = table.getRows().get(0);
		// 一、新增时加密密码（修改时不考虑：密码元素被禁用）
		if (tr.isInsertRow()) {
			password = tr.getValue(AUser.PASSWORD);
			tr.setValue(AUser.PASSWORD, UserSupport.encodePassword(password));
		}
		// 二、过期状态
		String expired = "0";
		int userLevel = tr.getValueInt(AUser.USER_LEVEL, UserLevel.USER);
		if (userLevel < UserLevel.SUPER_USER) {
			String expiredDate = tr.getValue(AUser.EXPIRED_DATE);
			if (!Checker.isEmpty(expiredDate)
					&& DateUtil.getMilliseconds(expiredDate, DateUtil.getCurrentDate()) >= 0) {
				expired = "1";
			}
		} else {
			// 超级用户及以上级别，不受过期日期限制
			tr.setValue(AUser.EXPIRED_DATE, "");
		}
		tr.setValue(AUser.EXPIRED, expired);
		// 三、保存到数据库
		super.doAction(db, ac);
		// 四、修改后同步关联数据：用户编号变化时才有同步的必要
		if (tr.isUpdateRow()) {
			String newId = tr.getValue(AUser.USER_ID);
			String oldId = tr.getOldValue(AUser.USER_ID);
			if (!oldId.equals(newId)) {
				UserSupport.updateUser(db, newId, oldId);
			}
		}

		// 五、单点登录-用户同步（没有单点登录时以下可删除）
		// 密码设为明文
		tr.setValue(AUser.PASSWORD, password);
		// 修改时，补充“用户编号”
		if (tr.isUpdateRow()) {
			tr.setValue(AUser.USER_ID, tr.getOldValue(AUser.USER_ID));
		}
		String result = SSOUtil.addOrUpdateUser(tr);
		if (result != null) {
			ActionResult ar = ac.getActionResult();
			ar.setSuccessful(false);
			ar.addErrorMessage(result);
			return;
		}

	}

}
