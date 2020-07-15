package com.fastunit.app.user;
import com.fastunit.LangUtil;
import com.fastunit.User;
import com.fastunit.app.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.context.ActionResult;
import com.fastunit.context.LocalContext;
import com.fastunit.framework.sso.SSOUtil;
import com.fastunit.framework.util.RequestUtil;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.TransactionAction;
import com.fastunit.user.UserFactory;
import com.fastunit.user.login.OnlineUtil;
import com.fastunit.user.login.ValidateCode;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;

public class SsonAction extends TransactionAction {
	private static final String LOG_LAST = "update AUSER set lasttime=logintime,lastip=loginip where userid=?";
	private static final String LOG_CURR = "update AUSER set logintime=?,loginip=? where userid=?";
	private static final int[] TYPES_CURR = new int[] { Type.TIMESTAMP,
			Type.VARCHAR, Type.VARCHAR };

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		ac=LocalContext.getLocalContext().getActionContext();
		String unitId = ActionContextHelper.getActionUnitId(ac);
		// 一、语言设置
		String lang = ac.getRequestParameter(unitId + ".lang");
		if (!Checker.isEmpty(lang)) {
			ac.getVisitor().setLocaleString(lang);
		}
		// 二、记录打开窗口方式
		String clean = ac.getRequestParameter(unitId + ".clean");
		ac.setSessionAttribute("app.login.clean", "1".equals(clean) ? "1" : "0");

		// 三、验证
		// 3.1、用户名、密码、有效期检查
		String userId = ac.getRequestParameter("userid");
		String password = ac.getRequestParameter("password");
/*		String userId = ac.getRequestParameter(unitId + ".userid");
		String password = ac.getRequestParameter(unitId + ".password");
*/		String url=ac.getRequestParameter("toindex");
		User user = UserFactory.getUser(ac, userId, password);
		if (user == null) {
			ac.getActionResult().setSuccessful(false);
			return;
		}

		// 3.2 IP地址检查
		String clientIp = RequestUtil.getIp();
		String ipConfig = user.get("ipconfig");
		if (!IPChecker.check(clientIp, ipConfig)) {
			ActionResult ar = ac.getActionResult();
			ar.setSuccessful(false);
			String[] parameters = { clientIp, ipConfig };
			ar.addErrorMessage(LangUtil.get(ac, AdmLang.DOMAIN,
					AdmLang.LOGIN_IP_ERROR, parameters));
			return;
		}
		// 3.3 验证码检查
		if (!ValidateCode.validate(ac, ac.getRequestParameter(unitId + ".code"))) {
			ActionResult ar = ac.getActionResult();
			ar.setSuccessful(false);
			ar.addErrorMessage(LangUtil.get(ac, AdmLang.DOMAIN,
					AdmLang.LOGIN_CODE_ERROR));
			return;
		}
		// 3.4 其他定制检查
		// if (false) {
		// ActionResult ar = ac.getActionResult();
		// ar.setSuccessful(false);
		// ar.addErrorMessage("");
		// return;
		// }

		// 四、单点登录-登录（没有单点登录时此部分可删除）
		String result = SSOUtil.login(userId, password);
		ActionResult ar = ac.getActionResult();
		if (result != null) {
			ar.setSuccessful(false);
			ar.addErrorMessage(result);
			return;
		}

		// 五、登陆成功
			ac.getVisitor().setUser(user);
		// 5.2 在线用户、重复登陆设置
		OnlineUtil.login(ac, user);
		// 5.3 更新登录日志
		db.execute(LOG_LAST, userId, Type.VARCHAR);
		String loginTime = DateUtil.getCurrentDatetime();
		String[] values = new String[] { loginTime, clientIp, userId };
		db.execute(LOG_CURR, values, TYPES_CURR);
		//登陆成功后转向指定的URL
		ar.setUrl(url);

	}
	
	}
