package com.am.cro.designatedDriver;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 代驾费结算审核状态
 * @author 王成阳
 * 2017-9-6
 */
public class Review extends DefaultAction{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		logger.debug("-----------------------------------进入代驾费结算审核状态方法-----------------------------------");
		//获取页面id
		String Id=ac.getRequestParameter("id");
		logger.debug("-----------------------------------获取页面主键-----------------------------------" + Id);
		//获得审核人
		String driving_settlement_audit_man = ac.getVisitor().getUser().getName();
		logger.debug("-----------------------------------获取审核人-----------------------------------" + driving_settlement_audit_man);
		//获得审核时间
		Date date = new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String driving_settlement_audit_time = format.format(date);
		logger.debug("-----------------------------------获取审核时间-----------------------------------" + driving_settlement_audit_time);
		//声明数据库管理
		DB db=DBFactory.getDB();
		//根据id查找单条数据
		String checkSQL="SELECT * FROM CRO_CARREPAIRORDER  WHERE id='"+Id+"'";
		//执行查询SQL
		MapList map = db.query(checkSQL);
		//如果查询结果不为空
		if(!Checker.isEmpty(map)){
			//获得当前数据的审核状态
			String dataStatus=map.getRow(0).get("driving_settlement_audit_state");
			String updateSQL="";
				//如果状态为1(待审核)就把状态改为2(审核通过)
				if("1".equals(dataStatus)){
					//审核状态 driving_settlement_audit_state	审核人driving_settlement_audit_man		审核时间driving_settlement_audit_time
					updateSQL="UPDATE CRO_CARREPAIRORDER SET driving_settlement_audit_state='2' , driving_settlement_audit_man = '"+driving_settlement_audit_man+"' , driving_settlement_audit_time = '"+driving_settlement_audit_time+"' WHERE id='"+Id+"'";
				}
			//如果修改sql不为空就执行修改SQL
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
