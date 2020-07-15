package com.am.mall;

import java.util.ArrayList;
import java.util.List;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * @author Mike
 * @create 2014年11月10日
 * @version 
 * 说明:<br />
 * 商品保存类
 */
public class CommoditySaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//商品表
		Table commodityTable=ac.getTable("mall_commodity");
		
		//上奖励规则
		Table rewardRuleTable=ac.getTable("mall_commodityrewardrule");
		
		//规格表
		Table commoditySpecificay=ac.getTable("mall_commodityspecifications");
		//商品参数表
		Table commodityParame=ac.getTable("mall_commodityparame");
		//配套推荐表
		Table commodityRecommend=ac.getTable("mall_commodityrecommend");
		//邮费表
		Table commodityPostage=ac.getTable("mall_commoditypostage");
		
		//商品标签
		Table commodyMarks = ac.getTable("mall_commoditymarks");
	
		
		String commoditId="";
		

		//保存主表数据
		db.save(commodityTable);
	
			
		//获取主表主键
		TableRow tr=commodityTable.getRows().get(0);
		commoditId=tr.getValue("id");
		ac.setSessionAttribute("mall_commodity.form.id", commoditId);
		//保存奖励规则
		saveTables(db, rewardRuleTable, "commodityid", commoditId);
		
		//保存规格数据
		saveTables(db, commoditySpecificay, "commodityid", commoditId);
		
		//保存商品参数
		saveTables(db, commodityParame, "commodityid", commoditId);
			
		//保存配套推荐
		saveTables(db, commodityRecommend, "thiscommodityid", commoditId);
		
		//保存邮费
		saveTables(db, commodityPostage, "commodityid", commoditId);
		
		//保存商品标签
		saveTables(db, commodyMarks, "commodityId", commoditId);
		
		//保存商品的标签
		String ids="";
		for (int i = 0; i < commodyMarks.getRows().size(); i++) {
			TableRow row=commodyMarks.getRows().get(i);
			
			if(!row.isDeleteRow()){
				ids += row.getValue("id") + ",";	
			}
		}	
		
		if(ids.contains(",")){
			ids = ids.substring(0,ids.length()-1);
			
			String updateSQL="UPDATE MALL_COMMODITY SET marks = '"+ids+"' WHERE id = '"+commoditId+"'";
			
			db.execute(updateSQL);
		}
		
		//保存商品主图  mainimages   bdp_mainimages  MALL_COMMODITY 多文件
//		String mainimages=Utils.getFastUnitFilePath("MALL_COMMODITY", "bdp_mainimages", commoditId);
		String mainimages=new FileUtils().getFastUnitFilePathJSON("MALL_COMMODITY", "bdp_mainimages", commoditId);
		String updateSql="UPDATE MALL_COMMODITY  SET mainimages='"
				+mainimages+"'  WHERE id='"+commoditId+"' ";
			
		db.execute(updateSql);
		
		//保存PC商品主图  pcmainimages   bdp_pcmainimages  MALL_COMMODITY 多文件
		String pcmainimages=Utils.getFastUnitFilePath("MALL_COMMODITY", "bdp_pcmainimages", commoditId);
		
		
		if(!Checker.isEmpty(pcmainimages)){
			pcmainimages=pcmainimages.substring(0, pcmainimages.length()-1);
		}
		updateSql="UPDATE MALL_COMMODITY  SET pcmainimages='"
				+pcmainimages+"'  WHERE id='"+commoditId+"' ";
			
		db.execute(updateSql);
		
		
		//保存商品图文详情  listimage   bdp_listimage  MALL_COMMODITY 多文件
		String bdp_listimage=Utils.getFastUnitFilePath("MALL_COMMODITY", "bdp_listimage", commoditId);
		if(!Checker.isEmpty(bdp_listimage)){
			bdp_listimage=bdp_listimage.substring(0, bdp_listimage.length()-1);
		}
		updateSql="UPDATE MALL_COMMODITY  SET listimage='"
				+bdp_listimage+"'  WHERE id='"+commoditId+"' ";
				
		db.execute(updateSql);
		
		
		
		//保存商品图文详情  pclistimage   bdp_pclistimage  MALL_COMMODITY 多文件
		String bdp_pclistimage=Utils.getFastUnitFilePath("MALL_COMMODITY", "bdp_pclistimage", commoditId);
		
		if(!Checker.isEmpty(bdp_listimage)&&bdp_pclistimage.length()>1){
			bdp_pclistimage=bdp_pclistimage.substring(0, bdp_pclistimage.length()-1);
		}
		
		String shelfimage=new FileUtils().getFastUnitFilePathJSON("MALL_COMMODITY", "bdp_shelf_image", commoditId);
		
		updateSql="UPDATE MALL_COMMODITY  SET pclistimage='"
				+bdp_pclistimage+"',shelf_image='"+shelfimage+"'  WHERE id='"+commoditId+"' ";
				
		db.execute(updateSql);
		
		
		//保存规格图片
		String updateSQL="UPDATE MALL_COMMODITYSPECIFICATIONS  SET specimages=?  WHERE id=? ";
		List<String[]> specIds = new ArrayList<String[]>();
		
		for(int i=0;i<commoditySpecificay.getRows().size();i++){
			String id=commoditySpecificay.getRows().get(i).getValue("id");
			
			String specimages=Utils.getFastUnitFilePath("MALL_COMMODITYSPECIFICATIONS", "bdp_specimages", id);
			
			if(specimages!=null&&specimages.length()>1){
				specimages=specimages.substring(0, specimages.length()-1);
			
				specIds.add(new String[]{specimages,id});
		}
		
			db.executeBatch(updateSQL, specIds, new int[]{Type.VARCHAR,Type.VARCHAR});
		
		}
	}
	
	
	private void saveTables(DB db,Table table,String tableField,String value) throws JDBCException{
		
		for(int i=0;i<table.getRows().size();i++){
			table.getRows().get(i).setValue(tableField, value);
		}
		
		db.save(table);
	}

}

	
	
