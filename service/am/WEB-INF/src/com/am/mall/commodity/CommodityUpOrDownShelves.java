package com.am.mall.commodity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

/**
 * @author YueBin
 * @create 2014年11月11日
 * @version 
 * 说明:<br />
 * 商品上架，下架Action
 */
public class CommodityUpOrDownShelves extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//获取自定义参数
		String operation=ac.getActionParameter();
		
		String commoditSate="0";
		
		if("down".equals(operation)){//下架
			commoditSate="0";
		}
		
		if("up".equals(operation)){//上架
			commoditSate="1";
		}
		
		//获取商品列表选择列
		String[] commodityList=ac.getRequestParameters("_s_mall_commodity.list_scan");
		//获取主键
		String[] commodityIds=ac.getRequestParameters("mall_commodity.list_scan.id.k");
		
		List<String[]> selectedId=new ArrayList<String[]>();
		
		//下架产品
		List<String> noOrderComplete=new ArrayList<String>();
		
		
		if(!Checker.isEmpty(commodityList)){
			for(int i=0;i<commodityList.length;i++){
				if("1".equals(commodityList[i])){
					selectedId.add(new String[]{commoditSate,commodityIds[i]});
					
					if("0".equals(commoditSate)){
						String cmName=checkOrderStatus(db, commodityIds[i]);
						if(cmName!=null&&!"null".equals(cmName)){
							noOrderComplete.add(cmName);
						}
						
					}
				}
			}
		}
		
		List<String> checkReult=null;
		
		if("1".equals(commoditSate)){
			//检查商品上架规则,在上架是，下架不检查
			checkReult=judgeUp(selectedId,db);
		}
		
		
		
		
		Ajax ajax=new Ajax(ac);
		
		if(checkReult!=null&&checkReult.size()>0){
			
			ac.getActionResult().setSuccessful(false);
			String errorMsg=Arrays.toString(checkReult.toArray());
			
			ajax.addScript("document.getElementById('errorMsg').innerHTML='"+errorMsg+"';");
		}else if(!Checker.isEmpty(noOrderComplete)){
			ac.getActionResult().setSuccessful(false);
			String errorMsg="商品订单未完成，无法下架！"+Arrays.toString(noOrderComplete.toArray());
			ajax.addScript("document.getElementById('errorMsg').innerHTML='"+errorMsg+"';");
		}else{
			String updateSQL= "";
			//更新商品状态；
			if("0".equals(commoditSate))
			{
				updateSQL="UPDATE mall_commodity SET commodityState=? WHERE id=? ";
			}
			else
			{
				updateSQL="UPDATE mall_commodity SET commodityState=?,last_time=now() WHERE id=? ";
			} 
			db.executeBatch(updateSQL, selectedId, new int[]{Type.VARCHAR,Type.VARCHAR});
			
			ajax.addScript("location.reload();");
			ac.getActionResult().setSuccessful(true);
		}
		
		ajax.send();
	}

	/**
	 * 判断集合中的商品是否有满足上架条件<br />
	 * 上架规则：<br />
	 * 判断是否满足上架条件：最少拥有一个规格；如果是不包邮则判断至少有一条商品邮费记录。
	 * @param selectedId
	 * @return List<String>
	 * @throws JDBCException 
	 */
	private List<String> judgeUp(List<String[]> selectedId,DB db) throws JDBCException {
		
		List<String> result=new ArrayList<String>();
		
		if(!Checker.isEmpty(selectedId)){
			
			//检查邮费
			//IsPostage  0 不包邮，1，包邮
			String checkPostSQL="SELECT c.id,c.name,cp.id AS cpid "
					+ " FROM mall_Commodity AS c "
					+ " LEFT JOIN mall_CommodityPostage AS cp ON c.id=cp.commodityid "
					+ " WHERE c.id=? AND IsPostage='0' ";
			//检查规格
			String checkSPecSQL="SELECT c.id,c.name,p.id AS pid "
					+ " FROM mall_Commodity AS c "
					+ " LEFT JOIN mall_CommoditySpecifications AS p ON c.id=p.commodityid "
					+ " WHERE c.id=?";
			
			MapList map=null;
			
			for(int i=0;i<selectedId.size();i++){
				
				//检查邮费
				map=db.query(checkPostSQL,selectedId.get(i)[1],Type.VARCHAR);
				
				if(!Checker.isEmpty(map)&&Checker.isEmpty(map.getRow(0).get("cpid"))){

					//map不为空，不包邮邮费为空
					result.add("<p>"+map.getRow(0).get("name")+"&nbsp;&nbsp;&nbsp;不包邮，需要选择邮费。</p>");
				}
				//检查规格
				map=db.query(checkSPecSQL,selectedId.get(i)[1],Type.VARCHAR);
				
				if(!Checker.isEmpty(map)&&Checker.isEmpty(map.getRow(0).get("pid"))){
					//map不为空，无商品规格
					result.add("<p>"+map.getRow(0).get("name")+"</span>&nbsp;&nbsp;需要设置商品规格。</p>");
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 检查产品是有未完成状态的
	 * @param db
	 * @param commodityId 商品id
	 * @return false 没有未完成的，true 未完成；
	 * @throws JDBCException
	 */
	public String checkOrderStatus(DB db,String commodityId) throws JDBCException{
		String result=null;
		//商品下架检查，如果商品下架，所有相关订单的状态都必须完成。
		String orderCompleteState=Var.get("order_deafult_complete_state","");
		String checkSQL="SELECT ord.id,mc.id,mc.name FROM mall_memberorder AS ord  "
				+ " LEFT JOIN mall_commodity AS mc ON ord.commodityid=mc.id "
				+ " WHERE  ord.orderstate NOT IN ("+orderCompleteState+") "
				+ "  AND mc.id=? ";
		
		MapList comdMap=db.query(checkSQL,new String[]{
				commodityId
		},new int[]{Type.VARCHAR});
		
		if(!Checker.isEmpty(comdMap)){
			result=comdMap.getRow(0).get("name");
		}
		
		return null;
	}
	
}
