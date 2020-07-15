package com.p2p.commodity;

import java.sql.Connection;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 商品保存SaveAction
 * @author Administrator
 *
 */
public class CommoditySaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table=ac.getTable("WS_COMMODITY_NAME");
//		super.doAction(db, ac);
		db.save(table);
		String comdityId=table.getRows().get(0).getValue("comdity_id");
		//主图PC MainImage   productscover
		String fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productscover", comdityId);
		Connection conn=db.getConnection();
		String updateSql="SELECT now() ";
		if(fileName!=null&&fileName.length()>1){
		fileName=fileName.substring(0, fileName.length()-1);
		updateSql="UPDATE WS_COMMODITY_NAME  SET MainImage='"
		+fileName+"'  WHERE comdity_id="+comdityId+" ";
		System.out.println(fileName);
		 
		conn.createStatement().execute(updateSql);
		}
		//主图手机   MMainImage   productimage1
		fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productimage1", comdityId);
		if(fileName!=null&&fileName.length()>1){
		fileName=fileName.substring(0, fileName.length()-1);
		updateSql="UPDATE WS_COMMODITY_NAME  SET MMainImage='"
		+fileName+"'  WHERE comdity_id="+comdityId+" ";
		conn.createStatement().execute(updateSql);
		}
		//列表PC   listimage  productimage2
		fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productimage2", comdityId);
		if(fileName!=null&&fileName.length()>1){
		fileName=fileName.substring(0, fileName.length()-1);
		updateSql="UPDATE WS_COMMODITY_NAME  SET listimage='"
		+fileName+"'  WHERE comdity_id="+comdityId+" ";
		conn.createStatement().execute(updateSql);
		}
		//列表手机  Mlistimage  productimage3
		fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productimage3", comdityId);
		if(fileName!=null&&fileName.length()>1){
			fileName=fileName.substring(0, fileName.length()-1);
			updateSql="UPDATE WS_COMMODITY_NAME  SET Mlistimage='"
			+fileName+"'  WHERE comdity_id="+comdityId+" ";
			System.out.println(fileName);
			conn.createStatement().execute(updateSql);
		}
		
		//详图PC   CommdityImages   productimage4
		
		fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productimage4", comdityId);
		if(fileName!=null&&fileName.length()>1){
		fileName=fileName.substring(0, fileName.length()-1);
		updateSql="UPDATE WS_COMMODITY_NAME  SET CommdityImages='"
		+fileName+"'  WHERE comdity_id="+comdityId+" ";
		System.out.println(fileName);
		conn.createStatement().execute(updateSql);
		}
		//详图手机  mcommdityimages   productimage5
		fileName=Utils.getFastUnitFilePath("WS_COMMODITY_NAME", "productimage5", comdityId);
		if(fileName!=null&&fileName.length()>1){
		fileName=fileName.substring(0, fileName.length()-1);
		updateSql="UPDATE WS_COMMODITY_NAME  SET mcommdityimages='"
		+fileName+"'  WHERE comdity_id="+comdityId+" ";
		System.out.println(fileName);
		conn.createStatement().execute(updateSql);
		}
		conn.commit();
	}
}
