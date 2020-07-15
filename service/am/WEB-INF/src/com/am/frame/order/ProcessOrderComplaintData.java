package com.am.frame.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年5月25日
 * @version 
 * 说明:<br />
 * 订单投诉处理
 */
public class ProcessOrderComplaintData implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String orderId=request.getParameter("ID");//订单id
		String complaintContent=request.getParameter("COMPLAINT_CONTENT");//投诉内容
		String complaintAttach=request.getParameter("COMPLAINT_ATTACH");//投诉附件
		
		JSONObject result=new JSONObject();
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			//1,查询验证投诉订单是否已经存在
			MapList orderMap=getOrderInfoByOrderId(db,orderId);
			if(!Checker.isEmpty(orderMap)){
				//2,保存投诉数据
				saveComplaintData(db,orderId,complaintContent,complaintAttach,orderMap);
				//3,返回接口处理结果
				result.put("ERRORCODE", 0);
				result.put("MSG","投诉成功，正在处理中。");
			}else{
				result.put("ERRORCODE", 2900);
				result.put("MSG","此订单无法投诉。");
			}
			
		}catch(Exception e){
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		return result.toString();
	}
		
	
	/**
	 * 投诉结果处理
	 * @param db
	 * @param orderId
	 * @param complaintContent
	 * @throws JDBCException 
	 */
	private void saveComplaintData(DB db, String orderId,
			String complaintContent,String complaintAttach,MapList orderMap) throws JDBCException {
		Row orderRow=orderMap.getRow(0);
		
		Table complTable=new Table("am_bdp","MALL_COMPLAINT_MANAGER");
		TableRow compInsertRow=complTable.addInsertRow();
//		Name	Code	Data Type	Length	Precision	Primary	Foreign Key	Mandatory
//		id	id	VARCHAR(36)	36		TRUE	FALSE	TRUE
//		订单id	order_id	VARCHAR(36)	36		FALSE	FALSE	FALSE
//		投诉人id	member_id	VARCHAR(36)	36		FALSE	FALSE	FALSE
//		投诉内容	complaint_content	VARCHAR(1000)	1000		FALSE	FALSE	FALSE
//		投诉附件	complaint_attach	VARCHAR(500)	500		FALSE	FALSE	FALSE
//		create_time	create_time	TIMESTAMP WITH TIME ZONE			FALSE	FALSE	FALSE
//		退款信息id	refund_id	VARCHAR(36)	36		FALSE	FALSE	FALSE
		compInsertRow.setValue("order_id", orderId);
		compInsertRow.setValue("member_id", orderRow.get("memberid"));
		compInsertRow.setValue("complaint_content", complaintContent);
		compInsertRow.setValue("complaint_attach", complaintAttach);
		
		db.save(complTable);
	}


	/**
	 * 获取订单信息，根据订单ID
	 * @param db
	 * @param orderId
	 * @return
	 * @throws JDBCException 
	 */
	private MapList getOrderInfoByOrderId(DB db, String orderId) throws JDBCException {
		MapList result=null;
		String querOrderSQL="SELECT * FROM mall_MemberOrder WHERE id=? AND OrderState IN ('5','8','81') ";
		result=db.query(querOrderSQL,orderId,Type.VARCHAR);
		
		return result;
	}

}
