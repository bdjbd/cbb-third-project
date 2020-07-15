package com.am.mall.commodity;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 删除商品，
 * 1、删除商品时需验证商品是否下架
 * 2、验证商品是否有订单未完成的状态的数据，如果存在则不可删除，反之可删除。
 * @author xianlin
 *2017年03月24日
 */

public class MallCommodityDeleteAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_mall_commodity.list");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] selectId = ac.getRequestParameters("mall_commodity.list.id.k");
		//获取商品名称
		String[] commodityName = ac.getRequestParameters("mall_commodity.list.name");
		//商品状态
		String[] commoditystate = ac.getRequestParameters("mall_commodity.list.commoditystate");
		
		
		
		String SQL = "";
		
		MapList maplist = null;
		
		String result = "";
		
		String result_commoditystate_result = "";
		
		String cmName = "";
		
		for (int i = 0; i < select.length; i++) {
			if("1".equals(select[i]))
			{
				if("1".equals(commoditystate[i]))
				{
					
					result_commoditystate_result +=commodityName[i]+",";
					
				}else
				{
//					SQL = "SELECT count(*) AS count_number FROM mall_memberorder "
//						+ "WHERE commodityid='"+selectId[i]+"' "
//						+ "AND orderstate in ('2','3','31','4','5')";
					
					cmName = checkOrderStatus(db, selectId[i]);
					
					if(cmName!=null&&!"null".equals(cmName)){
						result +=cmName+",";
					}else
					{
						SQL = "DELETE FROM mall_commodity WHERE id='"+selectId[i]+"'";
						db.execute(SQL);
					}
				}
			}
		}
		
		
		
		if(!Checker.isEmpty(result) || !Checker.isEmpty(result_commoditystate_result))
		{
			if(!Checker.isEmpty(result) && Checker.isEmpty(result_commoditystate_result))
			{
				result = result.substring(0, result.length()-1);
				ac.getActionResult().addErrorMessage("您所选中的部分商品已删除，由于以下商品的订单交易未完成导致无法删除该商品，商品名称如下："+result);
			}else if(Checker.isEmpty(result) && !Checker.isEmpty(result_commoditystate_result))
			{
				result_commoditystate_result = result_commoditystate_result.substring(0, result_commoditystate_result.length()-1);
				ac.getActionResult().addErrorMessage("您所选中的部分商品已删除，由于以下商品的处于上架状态导致无法删除该商品，商品名称如下："+result_commoditystate_result);
			}else
			{
				result = result.substring(0, result.length()-1);
				ac.getActionResult().addErrorMessage("您所选中的部分商品已删除，由于以下商品的订单交易未完成导致无法删除该商品，商品名称如下："+result);
				result_commoditystate_result = result_commoditystate_result.substring(0, result_commoditystate_result.length()-1);
				ac.getActionResult().addErrorMessage("您所选中的部分商品已删除，由于以下商品的处于上架状态导致无法删除该商品，商品名称如下："+result_commoditystate_result);
			}
			
		}else
		{
			ac.getActionResult().addSuccessMessage("您所选中的商品已删除");
		}
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
		
		return result;
	}
	

}
