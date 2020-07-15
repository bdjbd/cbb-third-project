package com.am.cro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.Validator;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 汽修厂管理-保存action
 * 
 * @author guorenjie
 *
 */
public class LeaseState extends DefaultAction implements Validator {
	private Logger log = LoggerFactory.getLogger(getClass()); 
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		LeaseState leaseState2 = new LeaseState();
		Table table = ac.getTable("AORG");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		// id
		String orgid = tr.getValue("orgid");
		//汽修厂名称
		String carrepairname = tr.getValue("carrepairname");
		// 租期到期日期
		String leaseexpiredate = tr.getValue("leaseexpiredate");
		// 租期状态
		String leasestate = tr.getValue("leasestate");
		//救援电话
		String rescue_call = tr.getValue("rescue_call");
		//通知电话
		String notification_call = tr.getValue("notification_call");
		//判断电话是否为空
		if (!Checker.isEmpty(rescue_call)) {
			//如果救援电话不为空，且长度不大于11位，则判断格式
			leaseState2.validate(ac, rescue_call, "", 0);
			log.info("执行了救援电话验证方法");			
		}else {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("救援电话不能为空");
		}
		if (!Checker.isEmpty(notification_call)) {
			//如果通知电话不为空，则判断格式
			leaseState2.validate(ac, notification_call, "", 1);
			log.info("执行了通知电话验证方法");
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("通知电话不能为空");
		}
		// 如果停用，用户过期日期设为前一天，否则清空过期日期
		if(!Checker.isEmpty(leasestate)){
			if (leasestate.equals("0")) {
				String date = getYesterday();
				String updateSql2 = "UPDATE AUSER set expireddate='" + date
						+ "',expired=1 WHERE orgid like '" + orgid + "%'";
				db.execute(updateSql2);
			} else {
				String date = null;
				String updateSql3 = "UPDATE AUSER set expireddate=" + date
						+ ",expired=0 WHERE orgid like '" + orgid + "%'";
				db.execute(updateSql3);
			}
		}
		

	}
	/**
	 * 获取昨天的日期
	 * @return
	 */
	public static String getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(cal
				.getTime());
		return yesterday;
	}
	/**
	 * 判断电话格式
	 */
	@Override
	public String validate(ActionContext ac, String value, String arg2,
			int type) {
		String reg = "";
		//判断救援电话格式
				if(type==0){
					reg="1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";
					boolean is = value.matches(reg);
					if (!is) {
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage("救援电话格式不正确");
					}
					
				}
		//判断通知电话格式是否正确
		if(type==1){
			reg = "^(1([38]\\d|4[57]|5[0-35-9]|7[06-8])\\d{8})(;(1([38]\\d|4[57]|5[0-35-9]|7[06-8])\\d{8}))*$";
			boolean is = value.matches(reg);
			if (!is) {
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("通知电话格式不正确");
			}
		}
		
		
		return null;
	}
	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return null;
	}
}
