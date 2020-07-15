package com.am.mall.commodity;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * @author YueBin
 * @create 2014年11月12日
 * @version 
 * 说明:<br />
 *	支付方式保存Action 
 */
public class PaymentSetupSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//支付方式主表
		Table paymentSetTable=ac.getTable("mall_paymentsetup");
		//支付方式参数子表
		Table paymentParamentTable=ac.getTable("mall_paymentsetupparame");
		
		//保存主表 支付方式
		db.save(paymentSetTable);
		
		String id=paymentSetTable.getRows().get(0).getValue("id");
		
		if(!Checker.isEmpty(paymentParamentTable)){
			//paymentSetID
			for(int i=0;i<paymentParamentTable.getRows().size();i++){
				paymentParamentTable.getRows().get(i).setValue("paymentsetid", id);
			}
		}
		
		//保存子表 支付方式参数表
		db.save(paymentParamentTable);
		
		//保存文件路径  MALL_PAYMENTSETUP   bdp_iconpath iconpath   文件
		String iconpath=Utils.getFastUnitFilePath("MALL_PAYMENTSETUP", "bdp_iconpath", id);
						
		if(!Checker.isEmpty(iconpath)){
			iconpath=iconpath.substring(0, iconpath.length()-1);
			String updateSql="UPDATE MALL_PAYMENTSETUP  SET iconpath='"
				+iconpath+"'  WHERE id='"+id+"' ";
					
			db.execute(updateSql);
		}
		
		//将ID保存至Session中，在$RS需要获取，list列表和FORM表单
		//am_bdp.mall_paymentsetup.form.id
		ac.setSessionAttribute("am_bdp.mall_paymentsetup.form.id", id);
		
	}
	
}
