package com.cdms.carExcelData.action;
/**
 * 油卡信息导入功能
 */
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.cdms.carExcelData.pojo.OilCard;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

public class ExcelImportOilCard extends DefaultImportDataAction implements ExcelImportService{
	private Logger logger = LoggerFactory.getLogger(ExcelImportInsurance.class);
	@Override
	public Class<?> newTclass(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		log.info("进入车辆基础信息导入类，获得类名" + cls);
		return cls;
	}

	@Override
	public ActionContext saveDB(ActionContext ac, File file, String className, String import_year, String import_rule,String menuname) {
		Class<?> cls = null;
		try {
			cls = newTclass(className);
			logger.debug("执行反射，进入saveDB方法，cls：" + cls);
			logger.debug("saveDB方法className：" + className);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		DBManager db = new DBManager();
		int count1=0;
		String editsql = "";
		logger.debug("开始导入参数设置");
		ImportParams importParams = new ImportParams();// 导入参数设置
														// 走到这一步时，线下报错：找不到工具类ImportParams
		log.debug("已引入参数设置");
		System.err.println("file:" + file);
		System.err.println("importParams:" + importParams);
		List<OilCard> us = ExcelImportUtil.importExcel(file, (Class<?>) cls, importParams);
		System.err.println(us.size());
		log.debug("准备for循环");
		for (OilCard i : us) {
			log.debug("进入for循环");
			UUID id=UUID.randomUUID();
			String insertSql1=" insert into cdms_oilcard( id ";
			String insertSql2=" values('"+id+"' ";
			
			
			String license_plate_number=i.getLicense_plate_number();
			if(Checker.isEmpty(license_plate_number)){
				ac.getActionResult().addErrorMessage("车牌号不能为空");
				ac.getActionResult().setSuccessful(false);
			}
			System.err.println("获得车牌号"+license_plate_number);
			
			String oil_card_number=i.getOil_card_number();
			String oil_card_number11="";
			if(!Checker.isEmpty(oil_card_number)){
				insertSql1+=" ,oil_card_number";
				insertSql2+=" ,'"+oil_card_number+"' ";
				oil_card_number11="='"+oil_card_number+"'";
			}
			else {
				oil_card_number11="  is null";
			}
			System.err.println("获得油卡编号"+oil_card_number);
			
			
			String add_oil =i.getAdd_oil();
			String add_oil11 = "";
			if(!Checker.isEmpty(add_oil)){
				insertSql1+=" ,add_oil";
				insertSql2+=" ,"+add_oil+" ";
				add_oil11="='"+add_oil+"'";
			}else {
				add_oil11=" is null";
			}
			
			System.err.println("获得加油量"+add_oil);
			
			
			String add_oil_time=i.getAdd_oil_time();
			String  add_oil_time111="";
			if(!Checker.isEmpty(add_oil_time)){
				insertSql1+=" ,add_oil_time";
				insertSql2+=" ,'"+HSSFDateUtil.getJavaDate(Double.valueOf(add_oil_time))+"' ";
				add_oil_time111="='"+HSSFDateUtil.getJavaDate(Double.valueOf(add_oil_time))+"'";
			}
			
			else {
				add_oil_time111="  is null";
			}
			
			System.err.println("获得加油时间"+add_oil_time);
			
			
			String driver_name=i.getDriver_name();
			String driver_name11 = "";
			if(!Checker.isEmpty(driver_name)){
				insertSql1+=" ,driver_name";
				insertSql2+=" ,'"+driver_name+"' ";
				driver_name11 = "='"+driver_name+"'";
			}else {
				driver_name11 = " is null";
			}
			System.err.println("获得驾驶员姓名"+driver_name);
			
			
			
			if (!Checker.isEmpty(license_plate_number)) {
				String sql = "select id from cdms_VehicleBasicInformation where license_plate_number='"
						+ license_plate_number + "' ";
				System.err.println(sql);
				MapList mapList = db.query(sql);
				
				if(mapList.size()<=0){
					ac.getActionResult().addErrorMessage("车牌号'"+license_plate_number+"'不存在");
					ac.getActionResult().setSuccessful(false);
				}else{
					String car_id = "";
					for (int j = 0; j < mapList.size(); j++) {
						car_id = mapList.getRow(0).get("id");
					}
					insertSql1+=" ,car_id )";
					insertSql2+=" ,'"+car_id+"')";
					
					String insertSql=insertSql1+insertSql2;
					String sqoil = "select * from cdms_oilcard where car_id='"+car_id+"' and driver_name"+driver_name11+""
							+ " and add_oil_time"+add_oil_time111+" and add_oil"+add_oil11+""
							+ " and oil_card_number"+oil_card_number11+"";
					MapList mapListoil = db.query(sqoil);
					if(mapListoil.size()>0){
						ac.getActionResult().addErrorMessage("数据重复");
						ac.getActionResult().setSuccessful(false);
					}
					else {
					count1+=db.execute(insertSql);
					}
				}		
			}
			else{
				ac.getActionResult().addErrorMessage("车牌号不能为空");
				ac.getActionResult().setSuccessful(false);
			}
		}
		String msg="增加行数:"+count1;
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().setUrl("/cdms/cdms_oilcard.do?m=s");
		return ac;
	}

}
