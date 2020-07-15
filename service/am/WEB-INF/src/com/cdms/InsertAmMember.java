package com.cdms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.am.sdk.md5.MD5Util;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author 刘扬
 *人员管理  重置密码功能
 */
public class InsertAmMember extends DefaultAction{
	Logger log = Logger.getLogger(InsertAmMember.class);
	public void doAction(DB db, ActionContext ac) throws Exception {
		System.err.println("进入增加人员类");
		Table table = ac.getTable("AM_MEMBER");
		String id=table.getRows().get(0).getValue("id");
		int count=0;
		
		//判断电话号码是否符合规则
		boolean flag = true;
		//获得登录账号
		String loginaccount=table.getRows().get(0).getValue("loginaccount");
		//获得电话
		String contact_number=table.getRows().get(0).getValue("contact_number");
		//判断登录账号不能重复
		if(!Checker.isEmpty(loginaccount)){
			count = isRepeat(db, id, "loginaccount",loginaccount);
		}
		//判断数据库中是否有重复的登录账号和工号
		if (count > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("登录账号不能重复");
		}
		//判断电话号码是否符合规则
		if(!Checker.isEmpty(contact_number)){
			flag=checkMobileNumber(contact_number);
			System.err.println("-----"+flag);
		}
		if(!flag){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("联系电话不符合规则");
		}else{
			db.save(table);
			//设置默认密码
			String password="000000";
			String str=MD5Util.getSingleton().textToMD5L32(password);
			log.info(str);
			String sql1="select id from AM_MEMBER where loginaccount='"+loginaccount+"' ";
			MapList list=db.query(sql1);
			String id1=list.getRow(0).get(0);
			System.err.println("id1---------------------"+id1);
			//更新密码SQL
			String sql="update AM_MEMBER set loginpassword='"+str+"' where id='"+id1+"'";
			log.info(sql);
			db.execute(sql);
		}	
	}
	//判断数据重复功能
	public int isRepeat(DB db, String id, String colName, String colValue)
			throws JDBCException {
		int rValue = 0;
		String sql = "select count(*) from AM_MEMBER where "
				+ colName + "='" + colValue + "' and id <>'" + id + "' ";
		MapList mapList = db.query(sql);
		if (!Checker.isEmpty(mapList)) {
			rValue = mapList.getRow(0).getInt(0, 0);
		}
		return rValue;
	}
	
	//验证电话号码 方法
	 public static boolean checkMobileNumber(String mobileNumber){
		  boolean flag = false;
		  try{
		    Pattern regex = Pattern.compile("[1]\\d{10}");
		    Matcher matcher = regex.matcher(mobileNumber);
		    flag = matcher.matches();
		   }catch(Exception e){
		    flag = false;
		   }
		  return flag;
		 }
	 
}
