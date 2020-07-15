package com.p2p.elect;

import java.sql.Connection;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;


/**
 * 电子券保存Action
 * @author Administrator
 *
 */
public class ElectTicketSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
//		super.doAction(db, ac);
		
		//电子券类型
		String electtickettype=ac.getRequestParameter("p2p_eterpelectticket.form.electtickettype");
		//积分值
		String scorevalue=ac.getRequestParameter("p2p_eterpelectticket.form.scorevalue");
		
		// 1,兑换券；2，抵现券
		if("1".equals(electtickettype)&&Checker.isEmpty(scorevalue)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("兑换必须填入兑换积分");
		}else{
			Table table=ac.getTable("P2P_ETERPELECTTICKET");
			db.save(table);
			
			//获取数据主键
			String id=table.getRows().get(0).getValue("id");
			
			//获取附件路径地址
			String fileName=Utils.getFastUnitFilePath("P2P_ETERPELECTTICKET", "iconpathf", id);
			if(!Checker.isEmpty(fileName)&&fileName.length()>1){
				fileName=fileName.substring(0, fileName.length()-1);
				
				//更新路径地址到数据库
				String sql="UPDATE p2p_eterpelectticket  SET iconPath='"+fileName+"'  WHERE id='"+id+"' ";
				Connection conn=db.getConnection();
				conn.createStatement().execute(sql);
				conn.commit();
			}
		}
		
	}
}
