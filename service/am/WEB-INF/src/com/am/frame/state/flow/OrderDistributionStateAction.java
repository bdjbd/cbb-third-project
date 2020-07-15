package com.am.frame.state.flow;

import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.transactions.virement.VirementManager;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author Mike
 * @create 2014年11月29日
 * @version 
 * 说明:<br />
 * 订单配送	S:4  SO:5 FO:无操作
 * 订单配功能
 */
public class OrderDistributionStateAction extends DefaultOrderStateAction {

	private Logger logger=Logger.getLogger(com.am.frame.state.flow.OrderDistributionStateAction.class);
	
	public static final String OUT_STORE_ACTION="出库作业";
	
	public static final String OPERATER="出库操作人";
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		DB db=null;
		try{
			//更新状态
			String res=super.execute(ofp);
			
			db=DBFactory.newDB();
			
			//检查是否出库
			if("TRUE".equalsIgnoreCase(ofp.paramList.getValueOfName(OUT_STORE_ACTION))){
				//出库操作
				logger.info("订单配送，出库操作,订单ID："+ofp.orderId);
				
				outStore(ofp, db);
			}
			
			result=new JSONObject(res);
		}catch(Exception e){
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result.toString();
	}
	
	
	
	
	private void outStore(OrderFlowParam ofp,DB db) throws Exception{
		
		// 出库
		//获取订单信息
		MemberOrder order =new OrderManager().getMemberOrderById(ofp.orderId, db);
		
		String specid=order.getSpecId();
		String saleNumber=order.getSaleNumber()+"";
		//记录出库记录
		String salePrice =order.getSalePrice()+"";
		//查询物资编码
		String commidDetalSQL = "select MaterialsCode from P2P_COMMODITYDETAIL"
				+ " where comdityformatid='"
				+ specid + "'";
				
		ResultSet commidDetalRs = db.getResultSet(commidDetalSQL);
				
		String code = "";//物资编码
				
		while(commidDetalRs.next()){
			code = commidDetalRs.getString("materialscode");
		}
				
		//修改物资数量
		String updateCommodSQL = "update p2p_MaterialsCode set amount=(amount-"
				+ saleNumber +") where code='"
				+ code + "'";
		db.execute(updateCommodSQL);
		
		//mall_out_store
		//记录出口记录
		Table table=new Table("am_bdp", "MALL_OUT_STORE");
		TableRow inTrableRow=table.addInsertRow();
		inTrableRow.setValue("materialscode", code);
		inTrableRow.setValue("outprice", VirementManager.changeY2F(salePrice));//分
		inTrableRow.setValue("outprice_yuan", salePrice);//元
		inTrableRow.setValue("counts", saleNumber);//数量
		
		db.save(table);
		
//		String insertSQL = "INSERT INTO p2p_outstore(id,"
//				+ " code,  outcode, outprice, counts, operar, creattiem"
//				+ ",outremark, datatstatus)VALUES (";
//		
//				insertSQL += "'" + UUID.randomUUID().toString() + "',";
//				insertSQL += "'" + code + "',";
//			    insertSQL += "'" + System.currentTimeMillis()
//			    		+ RandomUtil.getRandomString(3).toUpperCase() + "',";
//			    insertSQL += "" + salePrice + ",";
//			    insertSQL += "'" + saleNumber + "',";
//			    insertSQL += "'"+ofp.paramList.getValueOfName(OPERATER)+ "',";
//			    insertSQL += "now(),";
//			    insertSQL += "'订单号：" +ofp.orderId + "   出库记录。',";
//			    insertSQL += "3)";
			    
	   
//	   db.execute(insertSQL);
	}
}
