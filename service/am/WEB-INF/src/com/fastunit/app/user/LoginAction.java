package com.fastunit.app.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fastunit.LangUtil;
import com.fastunit.MapList;
import com.fastunit.User;
import com.fastunit.app.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.context.ActionResult;
import com.fastunit.context.LocalContext;
import com.fastunit.framework.util.RequestUtil;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.TransactionAction;
import com.fastunit.user.UserFactory;
import com.fastunit.user.login.ValidateCode;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;

public class LoginAction extends TransactionAction {
	private static final String LOG_LAST = "update AUSER set lasttime=logintime,lastip=loginip where userid=?";
	private static final String LOG_CURR = "update AUSER set logintime=?,loginip=? where userid=?";
	private static final int[] TYPES_CURR = new int[] { Type.TIMESTAMP,
			Type.VARCHAR, Type.VARCHAR };

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String unitId = ActionContextHelper.getActionUnitId(ac);
		//检查空间使用是否过期
		String org = ac.getRequestParameter(unitId + ".orgid");
		
		//版本
		String version=ac.getRequestParameter("version");
		
		System.out.println(LocalContext.getLocalContext().getHttpServletRequest().getRemoteHost());
		
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
		String userId = ac.getRequestParameter(unitId + ".userid");
		String password = ac.getRequestParameter(unitId + ".password");
		User user = UserFactory.getUser(ac, userId, password);
		if (user == null) {
			ac.getActionResult().setSuccessful(false);
			String backUrl="/app/login.do?orgid="+org;
			if(Checker.isEmpty(version)){
				backUrl+="&version="+version;
			}
			ac.getActionResult().setUrl(backUrl);
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
			//return;
		}
		ac.getSessionAttribute("test", "ABCDEW");
		// 3.4 其他定制检查
		// if (false) {
		// ActionResult ar = ac.getActionResult();
		// ar.setSuccessful(false);
		// ar.addErrorMessage("");
		// return;
		// }
		
		if(!Checker.isEmpty(org)){	
		  DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  
		  String SQL="SELECT due_time from lxny_space_usage_fee where status='1' and org_code=(select orgid from auser where userid='"+ userId +"') ORDER BY due_time DESC LIMIT 1";
		  MapList list = db.query(SQL);
		  
		  if(!Checker.isEmpty(list)&&!Checker.isDate(list.getRow(0).get("due_time"))){
			  Date due_time = format.parse(list.getRow(0).get("due_time"));
			  
			  if(new Date().getTime()>due_time.getTime()){
				  ActionResult ar = ac.getActionResult();
					ar.setSuccessful(false);
					ar.addErrorMessage("您的空间使用时间过期，请充值!");
					String backUrl="/app/login.do?orgid="+org;
					if(Checker.isEmpty(version)){
						backUrl+="&version="+version;
					}
					ac.getActionResult().setUrl(backUrl);
					return;
			  }
		  }else{
			  ActionResult ar = ac.getActionResult();
				ar.setSuccessful(false);
				ar.addErrorMessage("您的空间使用时间过期，请充值!");
				String backUrl="/app/login.do?orgid="+org;
				if(Checker.isEmpty(version)){
					backUrl+="&version="+version;
				}
				ac.getActionResult().setUrl(backUrl);
				return;
		  }
		  
		}
		// 四、单点登录-登录（没有单点登录时此部分可删除）
		/*String result = SSOUtil.login(userId, password);
		if (result != null) {
			ActionResult ar = ac.getActionResult();
			ar.setSuccessful(false);
			ar.addErrorMessage(result);
			return;
		}*/

		// 五、登陆成功
		// 5.1 User对象设置到Session中
		ac.getVisitor().setUser(user);		
		String ssql = "select * from auser where userid = '"+userId+"'";
		MapList list =db.query(ssql);
		if(!Checker.isEmpty(list))
		{		
		
			ac.setSessionAttribute("am_fastunti_auser_loginuser_department_id", list.getRow(0).get("department_id"));
		}
		
		// 5.2 在线用户、重复登陆设置
	//	OnlineUtil.login(ac, user);
		// 5.3 更新登录日志
		db.execute(LOG_LAST, userId, Type.VARCHAR);
		String loginTime = DateUtil.getCurrentDatetime();
		String[] values = new String[] { loginTime, clientIp, userId };
		db.execute(LOG_CURR, values, TYPES_CURR);
		
		
		//理性农业业务处理，更新机构对应pad的收货情况，
		String orgId=ac.getVisitor().getUser().getOrgId();
		String updateSQL="UPDATE am_terminal_order_manager SET order_status=4 WHERE org_id=? AND order_status=3 ";
		db.execute(updateSQL, orgId,Type.VARCHAR);
		
	}

}
