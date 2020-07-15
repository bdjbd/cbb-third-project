package com.cdms.carExcelData.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.cdms.carExcelData.pojo.CarRepairRrecords;
import com.cdms.carExcelData.pojo.Insurance;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

/**
 * 
 * 维修导入功能
 * @author Administrator
 *
 */
public class ExcelImpoertCarRepairRrecords extends DefaultImportDataAction implements ExcelImportService{
	private Logger logger = LoggerFactory.getLogger(ExcelImpoertCarRepairRrecords.class);

	@Override
	public Class<?> newTclass(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Class<?> cls = Class.forName(className);
		log.info("进入维修导入类，获得类名" + cls);
		return cls;
	}

	@Override
	public ActionContext saveDB(ActionContext ac, File file, String className, String import_year, String import_rule,String menuname) {
		// TODO Auto-generated method stub
		
		
		Class<?> cls = null;
		try {
			cls = newTclass(className);
			logger.debug("执行反射，进入saveDB方法，cls：" + cls);
			logger.debug("saveDB方法className：" + className);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		DBManager db = new DBManager();

		String editsql = "";
		int count = 0; // 行数
		int count1 = 0; // 新增行数
		int count2 = 0; // 修改行数
		String errorMsg = "";// 失败数据
		logger.debug("开始导入参数设置");
		ImportParams importParams = new ImportParams();// 导入参数设置
		// 走到这一步时，线下报错：找不到工具类ImportParams
		log.debug("已引入参数设置");
		System.err.println("file:" + file);
		System.err.println("importParams:" + importParams);
		List<CarRepairRrecords> us = ExcelImportUtil.importExcel(file, (Class<?>) cls, importParams);
		System.err.println(us.size());
		log.debug("准备for循环");
		for (CarRepairRrecords i : us) {
			UUID id=UUID.randomUUID();
			log.debug("进入for循环");
			String insertSql1=" insert into cdms_CarRepairRrecords ( id ";
			String insertSql2=" values ( '"+id+"'";
			
			String license_plate_number=i.getLicense_plate_number();
			if(Checker.isEmpty(license_plate_number)){	
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("车牌号不能为空");
			}
			System.err.println("获得车牌号------>"+license_plate_number);
			
			
			String repair_fees=i.getRepair_fees();
			String repair_fees11 = "";
			if(!Checker.isEmpty(repair_fees)){
				insertSql1+=" ,repair_fees";
				insertSql2+=" ,"+repair_fees+"";
				repair_fees11 = repair_fees;
			}else {
				repair_fees11 = " is null";
			}
			System.err.println("获得维修费用------>"+repair_fees);
			
			String repair_time=i.getRepair_time();
			String repair_time111="";
			if(!Checker.isEmpty(repair_time)){
				insertSql1+=" ,repair_time";
				insertSql2+=" ,'"+HSSFDateUtil.getJavaDate(Double.valueOf(repair_time))+"'";
				repair_time111="='"+HSSFDateUtil.getJavaDate(Double.valueOf(repair_time))+"'";
			}
			else {
				repair_time111="  is null";
				
			}
			System.err.println("获得维修时间------>"+repair_time);
			
			
			
			String repair_type=i.getRepair_type();
			String repair_type11 = "";
			if(!Checker.isEmpty(repair_type)){
				
				if(repair_type.equals("小保")){
					repair_type="1";
					repair_type11 = "='1'";
				}
				if(repair_type.equals("大保")){
					repair_type="2";
					repair_type11 = "='2'";
				}
				if(repair_type.equals("维修")){
					repair_type="3";
					repair_type11 = "='3'";
				}	
				
				insertSql1+=" ,repair_type";
				insertSql2+=" ,'"+repair_type+"'";	
			}else {
				repair_type11 = " is null";
			}
			System.err.println("获得维修类型------>"+repair_type);
				
			
			String  member_id=i.getMember_id();
			String member_id11 = "";
			if(!Checker.isEmpty(member_id)){
				insertSql1+=" ,member_id";
				insertSql2+=" ,'"+member_id+"'";	
				member_id11 = "='"+member_id+"'";
			}else {
				member_id11 = " is null";
			}
			System.err.println("获得维修人员名称------>"+member_id);
			
			
			//判断如果车牌号不为空  则获取car_id
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
					System.err.println("进入方法------>");
					//遍历mapList
					for (int j = 0; j < mapList.size(); j++) {
						car_id = mapList.getRow(0).get("id");
					}		
					insertSql1+=" ,car_id)";
					insertSql2+=" ,'"+car_id+"')";
					String insertSql=insertSql1+insertSql2;	
					System.err.println("准备执行查询sql------>");
					
					String sqlwork = "select * from  cdms_CarRepairRrecords where car_id='"+car_id+"' and repair_fees"+repair_fees11+""
							+ " and repair_time"+repair_time111+" and repair_type"+repair_type11+""
							+ " and member_id"+member_id11+"";
					System.err.println("执行查询sql------>");
					MapList sqllistwork = db.query(sqlwork);
					if(sqllistwork.size()>0){
						System.err.println("数据重复------>");
						ac.getActionResult().addErrorMessage("数据重复");
						ac.getActionResult().setSuccessful(false);
					}
					else {
						System.err.println("执行插入------>");
					count1+=db.execute(insertSql);
					}
				}
			}else{
				ac.getActionResult().addErrorMessage("车牌号不能为空");
				ac.getActionResult().setSuccessful(false);
			}
		}	
		logger.debug("for循环结束");
		String msg="增加行数:"+count1;
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().setUrl("/cdms/cdms_carrepairrrecords.do?m=s");
		return ac;
	}
	
	
	
}
