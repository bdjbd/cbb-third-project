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
import com.cdms.carExcelData.pojo.DangerousTools;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

public class ExcelImportDangerousTools extends DefaultImportDataAction implements ExcelImportService{
	private Logger logger = LoggerFactory.getLogger(ExcelImpoertCarRepairRrecords.class);
	@Override
	public Class<?> newTclass(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		log.info("进入工具导入类，获得类名" + cls);
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
		List<DangerousTools> us = ExcelImportUtil.importExcel(file, (Class<?>) cls, importParams);
		System.err.println(us.size());
		log.debug("准备for循环");
		for (DangerousTools i : us) {
			log.debug("进入for循环");
			UUID id=UUID.randomUUID();
			String insertSql1=" insert into cdms_DangerousTools ( id ";
			String insertSql2=" values ('"+id+"' ";
			String insertSql="";
			String license_plate_number=i.getLicense_plate_number();
			if(Checker.isEmpty(license_plate_number)){
				ac.getActionResult().addErrorMessage("车牌号不能为空");
				ac.getActionResult().setSuccessful(false);
			}
			System.err.println("车牌号码"+license_plate_number);
			
			String tool_name=i.getTool_name();
			if(!Checker.isEmpty(tool_name)){
				insertSql1+=" ,tool_name";
				insertSql2+=" ,'"+tool_name+"' ";
			}
			System.err.println("工具名称"+tool_name);
			
			
			String tool_number=i.getTool_number();
			if(!Checker.isEmpty(tool_number)){
				insertSql1+=" ,tool_number";
				insertSql2+=" ,"+tool_number+" ";
			}
			System.err.println("工具数量"+tool_number);
			
			
			String receive_time=i.getReceive_time();
			if(!Checker.isEmpty(receive_time)){
				insertSql1+=" ,receive_time";
				insertSql2+=" ,'"+HSSFDateUtil.getJavaDate(Double.valueOf(receive_time))+"' ";
			}
			System.err.println("领取时间"+receive_time);
			
			
			String principal=i.getPrincipal();
			if(!Checker.isEmpty(principal)){
				insertSql1+=" ,principal";
				insertSql2+=" ,'"+principal+"' ";
			}
			System.err.println("负责人"+principal);
			
			
			String change_log=i.getChange_log();
			String  change_log111="";
			if(!Checker.isEmpty(change_log)){
				change_log111="='"+change_log+"'";
				insertSql1+=" ,change_log";
				insertSql2+=" ,'"+change_log+"' ";
			}
			else {
				change_log111=" is null";
			}
			System.err.println("变更记录"+change_log);
			
			//判断如果车牌号不为空  则获取car_id
			if (license_plate_number != null) {
				String sql = "select id from cdms_VehicleBasicInformation where license_plate_number='"
						+ license_plate_number + "' ";
				System.err.println(sql);
				MapList mapList = db.query(sql);
				if(mapList.size()<=0){
					ac.getActionResult().addErrorMessage("车牌号'"+license_plate_number+"'不存在");
					ac.getActionResult().setSuccessful(false);
				}else{
					String car_id = "";
					//遍历mapList
					for (int j = 0; j < mapList.size(); j++) {
						car_id = mapList.getRow(0).get("id");
					}
					System.err.println("car_idcar_idcar_idcar_idcar_id" + car_id);
					insertSql1+=" ,car_id)";
					insertSql2+=" ,'"+car_id+"')";
					insertSql=insertSql1+insertSql2;
					
					String sqltool = "select * from  cdms_DangerousTools where car_id='"+car_id+"' and tool_name='"+tool_name+"' "
							+ "and receive_time='"+HSSFDateUtil.getJavaDate(Double.valueOf(receive_time))+"' and tool_number="+tool_number+""
							+ " and principal='"+principal+"' and change_log" + change_log111+"";
					MapList sqllisttool = db.query(sqltool);
					
					if(sqllisttool.size()>0){
						
						ac.getActionResult().addErrorMessage("数据重复");
						ac.getActionResult().setSuccessful(false);
					}
					else {
					
					count1+=db.execute(insertSql);
					}
				}	
			}
		}
		
		String msg="增加行数:"+count1;
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().setUrl("/cdms/cdms_dangeroustools.do?m=s");
		return ac;
	}

}
