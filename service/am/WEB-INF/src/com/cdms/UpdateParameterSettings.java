package com.cdms;
/**
 * 刘扬
 * 参数设置   类
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class UpdateParameterSettings extends DefaultAction {

	Logger log  = LoggerFactory.getLogger(getClass());
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		String id = ac.getRequestParameter("id");
		log.debug("[id] = " + id);
		String car_ids = ac.getRequestParameter("cdms_vehiclebasicparametersettings.list.car_id");
		log.debug("[car_ids] = " + car_ids);

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);

		// 得到一条版本表中的数据
		String sql1 = "select * from cdms_ParameterSettings where id='" + id + "' ";
		// 将查询出来的数据转化为JSON JSON转化为字符串存入数据库
		DBManager db1 = new DBManager();
		String cmd_content ="{\"DATA\":"+db1.queryToJSON(sql1).toString()+"}";
		log.debug("[cmd_content] = " + cmd_content);
		
		// cmd_type命令类型 1=远程升级 2=参数设置
		String cmd_type = "2";
		//终端流水号
		UUID serial_number=UUID.randomUUID();
		// state 1=待执行；2=执行中；3=执行完成；4=执行失败
		String state = "1";
		//commandid命令ID
		String commandid="10006";
		
		// 获得carID数组
		// 每循环得出一个Car id 执行更新一次车辆基础信息表 升级状态 =升级中 最后升级时间为当前时间 版本为当前版本
		if(!Checker.isEmpty(car_ids)){
			String[] car_id = car_ids.split(",");
			for (int i = 0; i < car_id.length; i++) {
				// 生成ID一条UUID 用来插入终端命令表
				String uuid = UUID.randomUUID().toString();

				//searchSql--->查找终端表有此车辆吗
				String searchSql="select id from cdms_TerminalCommand where car_id='"+car_id[i]+"' and state='2' and  cmd_type='2'  ";
				
				//如果查到车辆 则更新终端表中此车辆状态为 执行失败 
				String updateSql="update cdms_TerminalCommand set state='4' where car_id='"+car_id[i]+"' and  cmd_type='2' ";
				
				// 向终端表中插入数据
				String sql2 = "insert into cdms_TerminalCommand(id,car_id,cmd_type,create_time,cmd_content,state,commandid,serial_number) values("
									+ "'" + uuid + "','" + car_id[i] + "','" + cmd_type + "','" + str + "','" + cmd_content + "','"
									+ state + "','"+commandid+"','"+serial_number+"')";

				//更新车辆基础信息表中车辆的设置状态为设置中
				String sql = "update cdms_VehicleBasicInformation set parameter_setting_state='"
						+ 2 + "',parameter_last_set_time='" + str + "' where id='" + car_id[i] + "' ";
		
				log.debug("[searchSql] = " + searchSql);
				//先判断该车是否是否正在设置参数,是则把当前状态改为失败,再新增一条新的重新设置
				MapList mapList=db.query(searchSql);
				if(!Checker.isEmpty(mapList)){
					db.execute(updateSql);
					log.debug("[updateSql] = " + updateSql);
				}
					db.execute(sql2);
					db.execute(sql);
					log.debug("[sql2] = " + sql2);
					log.debug("[sql] = " + sql);
			}
		}
		
	}
}
