package com.cdms;
/**
 * 刘扬
 * 升级版本类
 * 
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class RemoteUpgradeManagement extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取单元
		// Table table = ac.getTable("cdms_remoteupgrade");

		String id = ac.getRequestParameter("id");
		System.err.println(id);
		String bdp_car_id = ac.getRequestParameter("cdms_remoteupgrade.list.bdp_car_id");
		System.err.println(bdp_car_id);
		
		
		// 获取时间戳
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		/*
		 * 
		 * 向终端命令表中插入数据
		 */
		// 得到一条版本表中的数据
		String sql1 = "select * from cdms_RemoteUpgradeManagement where id='" + id + "' ";

		// 将查询出来的数据转化为JSON JSON转化为字符串存入数据库
		DBManager db1 = new DBManager();

		// CmdContent的值
		String cmd_content = "{\"DATA\":" + db1.queryToJSON(sql1).toString() + "}";
		System.err.println(cmd_content);

		// cmd_type命令类型为1 1=远程升级 2=参数设置
		String cmd_type = "1";

		// 终端流水号
		UUID serial_number = UUID.randomUUID();

		// state 为1 1=待执行；2=执行中；3=执行完成；4=执行失败
		String state = "1";
		
		// commandid命令id
		String commandid = "10008";
		
		// 获得carID数组
		// 每循环得出一个Car id 执行更新一次车辆基础信息表 升级状态 =升级中 最后升级时间为当前时间 版本为当前版本
		String[] car_id = bdp_car_id.split(",");
		
		for (int i = 0; i < car_id.length; i++) {
			// 生成ID一条UUID 用来插入终端命令表
			String uuid = UUID.randomUUID().toString();
			
			//searchSql--->查找终端表有此车辆吗
			String searchSql="select id from cdms_TerminalCommand where car_id='"+car_id[i]+"' and state='2' and  cmd_type='1'  ";
			
			//如果查到车辆 则更新终端表中此车辆状态为 执行失败 
			String updateSql="update cdms_TerminalCommand set state='4' where car_id='"+car_id[i]+"'   and  cmd_type='1' ";
			
			//想终端表中插入一条车辆升级信息   总包数 和 当前包数  都为0;
			String sql2 = "insert into cdms_TerminalCommand(id,car_id,cmd_type,create_time,cmd_content,state,commandid,serial_number,total_package,current_package) values("
					+ "'" + uuid + "','" + car_id[i] + "','" + cmd_type + "','" + str + "','" + cmd_content + "','"
					+ state + "','" + commandid + "','"+serial_number+"' ,"+0+","+0+")";
			//更新车辆基础信息表中车辆的升级状态为升级中
			String sql = "update cdms_VehicleBasicInformation set current_version_number='" + id + "',upgrade_status='"
					+ 2 + "',last_upgrade_time='" + str + "' where id='" + car_id[i] + "' ";
			
			MapList mapList=db.query(searchSql);
			if(mapList.size()>0){			
				db.execute(updateSql);
				db.execute(sql2);
				db.execute(sql); 
			}else{			
				db.execute(sql2);
				db.execute(sql); 
			}
			
			
		}

	}

}
