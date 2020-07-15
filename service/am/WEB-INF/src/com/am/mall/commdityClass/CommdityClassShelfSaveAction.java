package com.am.mall.commdityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * @author Mike
 * @create 2014年10月24日
 * @version 
 * 说明:<br />
 * 货架分类保存Action
 */
public class CommdityClassShelfSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
				//分类表
				Table table=ac.getTable("mall_commodityclass");
				//分类商品映射表
				Table mapTable=ac.getTable("mall_commodityclassrelationship");
				
				//保存主表(分类)信息
				db.save(table);
				
				//当前分类ID
				String classId=table.getRows().get(0).getValue("id");
				
			
				String inserSQL="INSERT INTO mall_commodityclassrelationship( id, commodityclassid, commodityid) VALUES (?, ?, ?)";
				String updateSQL="UPDATE mall_commodityclassrelationship SET commodityclassid=?, commodityid=? WHERE id=?";
				String deleteSQL="DELETE FROM mall_commodityclassrelationship WHERE id=?";
				
				List<String[]> inserList=new ArrayList<String[]>();
				List<String[]> updateList=new ArrayList<String[]>();
				List<String[]> deleteList=new ArrayList<String[]>();
				
				
				if(!Checker.isEmpty(mapTable)){
					
					for(int i=0;i<mapTable.getRows().size();i++){
						TableRow row=mapTable.getRows().get(i);
						
						if(row.isInsertRow()){
							inserList.add(new String[]{
									UUID.randomUUID().toString(),
									classId,
									row.getValue("commodityid")
							});
						}
						if(row.isUpdateRow()){
							updateList.add(new String[]{classId,row.getValue("commodityid"),row.getValue("id")});
						}
						if(row.isDeleteRow()){
							System.out.println(row.getOldValue("id")+"\n"+row.getValue("id"));
							deleteList.add(new String[]{row.getOldValue("id")});
						}
					}
				}
				
				
				db.executeBatch(inserSQL, inserList, new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
				db.executeBatch(updateSQL, updateList, new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
				db.executeBatch(deleteSQL, deleteList, new int[]{Type.VARCHAR});
				
				
				//保存文件路径  listimage   listimagefs  MALL_COMMODITY 多文件
				String listimage=Utils.getFastUnitFilePath("MALL_COMMODITYCLASS", "listimagefs", classId);
						
				if(!Checker.isEmpty(listimage)){
					listimage=listimage.substring(0, listimage.length()-1);
					String updateSql="UPDATE MALL_COMMODITYCLASS  SET listimage='"
						+listimage+"'  WHERE id='"+classId+"' ";
							
					db.execute(updateSql);
				}
				
				Ajax ajax=new Ajax(ac);
				ajax.addScript("window.parent.location.reload()");
				ajax.send();
			}

	
}
