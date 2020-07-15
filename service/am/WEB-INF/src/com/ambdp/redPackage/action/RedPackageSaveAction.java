package com.ambdp.redPackage.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月14日
 *@version
 *说明：红包设置保存Action
 */
public class RedPackageSaveAction extends DefaultAction{
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 红包管理
		Table table=ac.getTable("mall_red_packets");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		// 金额
		String moneyValue = ac.getRequestParameter("mall_red_packets.form.money");
		if (!Checker.isEmpty(moneyValue)) {
			//将金额转化为分
			String updateSql = "UPDATE mall_red_packets  SET money_value =" + moneyValue+"*100  WHERE id='" + id + "' ";
			db.execute(updateSql);
		}
		//保存文件路径  enclosure   bdp_enclosure 
		String enclosure=com.wisdeem.wwd.WeChat.Utils.getFastUnitFilePath("MALL_RED_PACKETS", "bdp_enclosure", id);
		logger.info("保存文件路径  "+enclosure);
		
		if(!Checker.isEmpty(enclosure)){
			enclosure=enclosure.substring(0, enclosure.length()-1);
			String updateEnclosureSql="UPDATE mall_red_packets  SET enclosure='"
				+enclosure+"'  WHERE id='"+id+"' ";
			
			logger.info("保存文件路径updateEnclosureSql:"+updateEnclosureSql);
			
			db.execute(updateEnclosureSql);
		}
	}
}
