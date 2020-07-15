package com.cdms.carExcelData.action;

/**
 * 
 * 保险导入功能
 */
import java.io.BufferedInputStream;
/**
 * 
 * 保险费
 */
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.carExcelData.pojo.Insurance;
import com.am.frame.webapi.db.DBManager;
import com.cdms.carExcelData.action.DefaultImportDataAction;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

public class ExcelImportInsurance extends DefaultImportDataAction implements ExcelImportService {
	private Logger logger = LoggerFactory.getLogger(ExcelImportInsurance.class);

	@Override
	public Class<?> newTclass(String className)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		log.info("进入车辆基础信息导入类，获得类名" + cls);
		return cls;
	}

	@Override
	public ActionContext saveDB(ActionContext ac, File file, String className, String import_year, String import_rule,
			String menuname) {
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
		List<Insurance> us = ExcelImportUtil.importExcel(file, (Class<?>) cls, importParams);
		System.err.println(us.size());
		log.debug("准备for循环");
		for (Insurance i : us) {
			log.debug("进入for循环");
			UUID id = UUID.randomUUID();
			String insertSql1 = " insert into cdms_Insurance( id ";
			String insertSql2 = " values( '" + id + "'";
			count += count + 1;
			// 获得车牌号
			String license_plate_number = i.getLicense_plate_number();
			if (Checker.isEmpty(license_plate_number)) {
				ac.getActionResult().addErrorMessage("车牌号不能为空");
				ac.getActionResult().setSuccessful(false);
			}
			System.err.println(license_plate_number);
			// 获得保险金额
			String insurance_amount = i.getInsurance_amount();
			if (!Checker.isEmpty(insurance_amount)) {
				insertSql1 += " ,insurance_amount";
				insertSql2 += " ," + insurance_amount + " ";
			}
			System.err.println(insurance_amount);

			// 获取保险缴纳时间
			String insurance_payment_time = i.getInsurance_payment_time();
			String insurance_payment_time111="";
			if (!Checker.isEmpty(insurance_payment_time)) {
				insertSql1 += " ,insurance_payment_time";
				insertSql2 += " ,'" + HSSFDateUtil.getJavaDate(Double.valueOf(insurance_payment_time)) + "' ";
				insurance_payment_time111="='"+HSSFDateUtil.getJavaDate(Double.valueOf(insurance_payment_time)) + "'";
			}else {
				insurance_payment_time111="  is null";
			}
			
			System.err.println(insurance_payment_time);
			// 保险到期时间
			String term_insurance_time = i.getTerm_insurance_time();
			if (!Checker.isEmpty(term_insurance_time)) {
				
				insertSql1 += " ,term_insurance_time";
				insertSql2 += " ,'" + HSSFDateUtil.getJavaDate(Double.valueOf(term_insurance_time)) + "' ";
				
			}
			System.err.println(term_insurance_time);
			// //转化时间
			// SimpleDateFormat str=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String
			// insurance_payment_time1=str.format(insurance_payment_time);
			// 获得备注
			String notes = i.getNotes();
			String notes111="";
			if (!Checker.isEmpty(notes)) {
				insertSql1 += " ,notes";
				insertSql2 += " ,'" + notes + "' ";
				notes111="='"+notes+"'";
			}
			else {
				notes111="  is null";
			}
			System.err.println(notes);

			if (!Checker.isEmpty(license_plate_number)) {
				
				log.debug("进入车辆查询");
				String sql = "select id from cdms_VehicleBasicInformation where license_plate_number='"
						+ license_plate_number + "' ";
				System.err.println(sql);
				MapList mapList = db.query(sql);
				System.err.println(mapList.size());
				System.err.println(mapList.size());
				System.err.println(mapList.size());
				if (mapList.size() <= 0) {
					ac.getActionResult().addErrorMessage("车牌号'"+license_plate_number+"'不存在!请重新输入");
					ac.getActionResult().setSuccessful(false);
				} else {
					String car_id = "";
					for (int j = 0; j < mapList.size(); j++) {
						car_id = mapList.getRow(0).get("id");
					}
					insertSql1 += " ,car_id)";
					insertSql2 += " ,'" + car_id + "')";
					String insertSql = insertSql1 + insertSql2;
					
					
					String sqltool = "select * from  cdms_Insurance where car_id='"+car_id+"' and insurance_amount="+insurance_amount+""
							+ "and insurance_payment_time"+insurance_payment_time111+" and term_insurance_time='"+ HSSFDateUtil.getJavaDate(Double.valueOf(term_insurance_time)) +"'"
							+ " and notes"+notes111+"";
					MapList sqllisttool = db.query(sqltool);
					
					if(sqllisttool.size()>0){
						
						ac.getActionResult().addErrorMessage("数据重复");
						ac.getActionResult().setSuccessful(false);
					}
					else {
					
					
					log.debug("准备进入插入方法");
					count1 += db.execute(insertSql);
					log.debug("退出插入方法");
					// 查詢保险费表
					// 按照降序查詢，將最大的保險提醒時間取出，放入車輛基礎信息表
					String mysql = "select term_insurance_time from cdms_insurance where car_id='"+car_id+"' order by term_insurance_time DESC";
					MapList ml = db.query(mysql);
					Date date = new Date();
					if (!Checker.isEmpty(ml)) {
						String insurance_time = ml.getRow(0).get("term_insurance_time");
						log.debug("查询出最大提醒日期"+insurance_time);
						SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");  
						try {
							date = format.parse(insurance_time);
							log.debug("日期"+date);
						} catch (ParseException e) {
							e.printStackTrace();
						}  
					}
					
					String upCar="update cdms_VehicleBasicInformation set duration_of_insurance= '" + date + "'  where id='"+car_id+"'";
					db.execute(upCar);
					String delsql = "delete from cdms_message where remind_type='3' and car_id='"+ car_id + "'";
					db.execute(delsql);
					}	
					
				}

			}
		}
		logger.debug("for循环结束");
		String msg = "增加行数:" + count1;
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().setUrl("/cdms/cdms_insurance.do?m=s");
		return ac;
	}

}
