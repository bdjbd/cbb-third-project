package com.cdms.carExcelData.action;

import java.io.File;
import java.util.List;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.sdk.md5.MD5Util;
import com.cdms.carExcelData.pojo.AmMember;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class ExcelImportAmMember extends DefaultImportDataAction implements ExcelImportService{
	
	private Logger logger = LoggerFactory.getLogger(ExcelImportVehicleBasicInformation.class);
	@Override
	public Class<?> newTclass(String className) throws InstantiationException,
	IllegalAccessException, ClassNotFoundException {
		Class<?> cls=Class.forName(className);
		log.info("人员信息导入类，获得类名"+cls);
		return cls;
	}
	
	@Override
	public ActionContext saveDB(ActionContext ac, File file, String className, String import_year, String import_rule,String menuname) {
		Class<?> cls=null;
		try {
			cls=newTclass(className);
			logger.debug("执行反射，进入saveDB方法，cls："+cls);  
			logger.debug("saveDB方法className："+className);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		DBManager db=new DBManager();
		
		int count=0;		//行数
		int count1=0;		//新增行数
		int count2=0;		//修改行数
		String errorMsg = "";// 失败数据
		
		logger.debug("开始导入参数设置");
		ImportParams importParams = new ImportParams();//导入参数设置  走到这一步时，线下报错：找不到工具类ImportParams
		log.debug("已引入参数设置");
		System.err.println("file:"+file);
		System.err.println("importParams:"+importParams);
		List<AmMember> us = ExcelImportUtil.importExcel(file,(Class<?>) cls, importParams);
		System.err.println(us.size());
		log.error(us.toString());
		for (AmMember i : us) {
			log.debug("进入for循环");			
			int count5=0;
			String id=UUID.randomUUID().toString();
			String insertSql1=" insert into am_member (id ";//添加语句
			String insertSql2="  values( '"+id+"' "; //添加语句
			String updateSql=" update am_member set ";
			
			//获取登录账号
			String loginaccount=i.getLoginaccount();	
			System.err.println("loginaccount-----"+loginaccount);
			if(!Checker.isEmpty(loginaccount)){
				insertSql1+=" ,loginaccount ";
				insertSql2+=" ,'"+loginaccount+"'";
				updateSql+=" loginaccount='"+loginaccount+"'";			
			}
			boolean b=true;
			System.err.println(Checker.isEmpty(loginaccount));
			System.err.println(Checker.isEmpty(loginaccount));
			System.err.println(Checker.isEmpty(loginaccount));
			if(import_rule.equals("1")){
				if(Checker.isEmpty(loginaccount)){
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("登录账号不得为空");
					b=false;
				}
			}
			
			String checkSql="select count(*) from AM_member where loginaccount='"+loginaccount+"'";
			if(b){
				MapList mapList =db.query(checkSql);
				count5=mapList.getRow(0).getInt(0, 0);	
			}
			
			System.err.println("count5---->"+count5);
			System.err.println("sql---->"+checkSql);
			if(!import_rule.equals("1")){
				if (count5 > 0) {
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("登录账号不能重复");
				} 	
			}
			
			
			
			//获取名字
			String name=i.getName();
			System.err.println("name-----"+name);
			if(!Checker.isEmpty(name)){
				insertSql1+=" ,name ";
				insertSql2+=" ,'"+name+"'";
				updateSql+=" ,name='"+name+"'";
			}else{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("姓名不能为空");
				b=false;
			}
			
			
			//获取联系电话
			String contact_number=i.getContact_number();
			System.err.println("contact_number-----"+contact_number);
			if(!Checker.isEmpty(contact_number)){
				insertSql1+=" ,contact_number ";
				insertSql2+=" ,'"+contact_number+"'";
				updateSql+=" ,contact_number='"+contact_number+"'";
			}else{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("联系电话不能为空");
				b=false;
			}
			if(!b){
				continue;
			}
			
			//获取身份证识别码
			String id_number2=i.getId_number2();
			
			System.err.println("id_number2-----"+id_number2);
			if(!Checker.isEmpty(id_number2)){
				insertSql1+=" ,id_number2 ";
				insertSql2+=" ,'"+id_number2+"'";
				updateSql+=" ,id_number2='"+id_number2+"' ";
			}
			
			//获取工号
			String job_number=i.getJob_number();
			System.err.println("job_number-----"+job_number);
			if(!Checker.isEmpty(job_number)){
				insertSql1+=" ,job_number ";
				insertSql2+=" ,'"+job_number+"' "; 
				updateSql+=" ,job_number='"+job_number+"' ";
			}
			//更新密码为000000
			String password="000000";
			String str=MD5Util.getSingleton().textToMD5L32(password);
			insertSql1+=" ,loginpassword";
			insertSql2+=" ,'"+str+"'";
			updateSql+=",loginpassword='"+str+"'";
			
			String orgcode=ac.getVisitor().getUser().getOrgId();
			insertSql1+=" ,orgcode";
			insertSql2+=" ,'"+orgcode+"'";
			updateSql+=",orgcode='"+orgcode+"'";
			
			
			insertSql1+=" )";
			insertSql2+=" )";
			updateSql+=" where loginaccount='"+loginaccount+"'";
			
			String insertSql=insertSql1+insertSql2;	
				if(import_rule.equals("1")){
					if(count5<=0){
						count1+=db.execute(insertSql);
					}else{			
						count2+=db.execute(updateSql);
					}
				}else{
					
						if(count5<=0){
							count1+=db.execute(insertSql);
						}else{
							errorMsg += "失败数据：登录账号" + loginaccount + "已存在" +"<br>";
						}
					
				}
			}
					
		//}		
		//影响总行数
		count=count1+count2;
		String msg1="共增加行数:"+count1+"行!";
		String msg2="共更新行数:"+count2+"行!";
		String msg="操作成功!共影响行数:"+count+"行!";
		
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg1);
		ac.getActionResult().addSuccessMessage(msg2);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().addErrorMessage(errorMsg);
		ac.getActionResult().setUrl("/cdms/"+menuname+".do?m=s");
		System.err.println("menuname"+menuname);
		System.err.println("menuname"+menuname);
		System.err.println("menuname"+menuname);
		return ac;
	}
}
