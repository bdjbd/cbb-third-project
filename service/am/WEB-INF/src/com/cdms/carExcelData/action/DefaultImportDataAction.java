package com.cdms.carExcelData.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.carExcelData.action.DefaultImportDataAction;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.context.ActionContext;
import com.fastunit.support.action.DefaultAction;

public class DefaultImportDataAction extends DefaultAction{
	Logger log = LoggerFactory.getLogger(DefaultImportDataAction.class);
	@Override
	public ActionContext execute(ActionContext ac) {
		log.debug("进入通用数据导入类DefaultImportDataAction");
		String actionName = ac.getRequestParameter("data_import.actionname");
		log.debug("获得actionName："+actionName);  //com.am.hc.data.importdata.action.ExcelImportHuiNong
		String className = ac.getRequestParameter("data_import.classname");
		log.debug("获得className："+className);  //com.am.hc.data.importdata.entity.HuiNongFuWu
		String import_year = ac.getRequestParameter("data_import.import_year");
		log.debug("获得import_year："+import_year);  //空  ""
		String import_rule = ac.getRequestParameter("data_import.import_rule");
		log.debug("获得import_rule："+import_rule);  //1
		String car_id=ac.getRequestParameter("data_import.car_id");
		log.debug("获得car_id："+car_id);
		String menuname=ac.getRequestParameter("data_import.menuname");
		log.debug("获得menuname："+menuname);
		try {
			File file = ac.getFile("data_import.import_excel");
			log.debug("获得上传的文件==============：" + file);	 //临时文件 20.Code\service\am_home\t_emp\_u_pload_e242.temp  
			
			ExcelImportService excelImportService = classNameToObject(actionName);
			System.err.println("11111111111111111111111");
			log.debug("获得excelImportService==============：" + excelImportService);	 //实例化对应的Action com.am.hc.data.importdata.action.ExcelImportHuiNong@60393279
			log.debug("调用excelImportService的saveDB方法");	
			return excelImportService.saveDB(ac, file, className, import_year,
					import_rule,menuname);
		}catch(Exception e){
			log.debug("捕获异常e.toString()：" + e.toString());	
			String tips = "上传文件不能为空或文件大小超过限制！";
			ac.getActionResult().addNoticeMessage(tips);
			ac.getActionResult().setSuccessful(true);
			return ac;
		} 
	}

	/**
	 * 依据类名反射出对象
	 * 
	 * @param className
	 * @return
	 */
	private ExcelImportService classNameToObject(String className) {
		ExcelImportService excelImportService = null;

		try {
			excelImportService = (ExcelImportService) Class.forName(className)
					.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return excelImportService;
	}

}
