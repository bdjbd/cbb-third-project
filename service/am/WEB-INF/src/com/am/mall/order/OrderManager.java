package com.am.mall.order;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.frame.webapi.db.DBManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.utils.AmEmailSend;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.email.EmailSettings;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.tools.sms.SMSIdentifyingCode;

/**
 * @author Mike
 * @create 2014年11月17日
 * @version 
 * 说明:<br />
 * 订单管理类，完成对订单的CRUD操作 
 * 
 */
public class OrderManager {

	private  Logger logger=LoggerFactory.getLogger(getClass());
	
	
	/**
	 * 更新订单的状态
	 * @param orderId 订单ID
	 * @param state  订单状态
	 * @return 更新成功返回TRUE,失败返回FALSE	 
	 */
	public boolean  updateOrderState(String orderId,String state){
		boolean result=false;
		
		try{
			String updateSQL="UPDATE mall_memberorder SET orderstate=? WHERE id=? ";
			
			DB db=DBFactory.getDB();
			
			if(db.execute(updateSQL,new String[]{state,orderId},new int[]{Type.VARCHAR,Type.VARCHAR})>0){
				result=true;
			};
			
		}catch(Exception e){
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	
	
	/**
	 * 获取订单根据订单ID
	 * @param orderId 订单ID
	 * @return 订单，如果不存在，则返回NULL
	 * @throws JDBCException 
	 */
	public MapList getMemberOrderMapById(String orderId,DB db) throws JDBCException{
		String getSQL="SELECT trim(to_char(saleprice,'9999999990D99')) AS fsaleprice,"
				+ " trim(to_char(totalprice,'9999999990D99')) AS ftotalprice,"
				+ " to_char(start_date,'yyyy-MM-dd') AS fstart_date, "
				+ " * FROM mall_MemberOrder WHERE id=? ";
			
		MapList map=db.query(getSQL, orderId, Type.VARCHAR);
			
		return map;
	}
	
	
	/**
	 * 获取订单根据订单ID
	 * @param orderId 订单ID
	 * @return 订单，如果不存在，则返回NULL
	 * @throws JDBCException 
	 */
	public MemberOrder getMemberOrderById(String orderId,DB db) throws JDBCException{
		MemberOrder memberOrder=null;
		String getSQL="SELECT * FROM mall_MemberOrder WHERE id=? ";
			
		MapList map=db.query(getSQL, orderId, Type.VARCHAR);
			
		if(!Checker.isEmpty(map)){
			memberOrder=new MemberOrder(map);
		}
		return memberOrder;
	}
	
	/**
	 * 获取订单根据支付ID
	 * @param payId 订单ID
	 * @return 订单，如果不存在，则返回NULL
	 * @throws JDBCException 
	 */
	public MemberOrder getMemberOrderPayId(String payId,DB db) throws JDBCException{
		MemberOrder memberOrder=null;
		String getSQL="SELECT * FROM mall_MemberOrder WHERE pay_id=? ";
			
		MapList map=db.query(getSQL, payId, Type.VARCHAR);
			
		if(!Checker.isEmpty(map)){
			memberOrder=new MemberOrder(map);
		}
		return memberOrder;
	}
	
	
	/**
	 * 获取订单根据订单ID
	 * @param orderId 订单ID
	 * @return 订单，如果不存在，则返回NULL
	 * @throws JDBCException 
	 */
	public MemberOrder getMemberOrderById(String orderId) throws JDBCException{

		MemberOrder memberOrder=null;
		
		String getSQL="SELECT * FROM mall_MemberOrder WHERE id=? ";
		
		DB db=DBFactory.getDB();
		
		MapList map=db.query(getSQL, orderId, Type.VARCHAR);
			
		if(!Checker.isEmpty(map)){
			memberOrder=new MemberOrder(map);
		}
		return memberOrder;
	}

	/**
	 * 更新订单完成时间
	 * @param id 订单id
	 * @param db  DB
	 * @throws JDBCException 
	 */
	public void updateOrderCompleteDate(String id, DB db) throws JDBCException {
		String updateSQL="UPDATE mall_MemberOrder SET CompleteDate=now() WHERE id=? ";
		db.execute(updateSQL,id, Type.TIMESTAMP);
	}


	
	/**
	 * 配送人员接单
	 * @param db DB
	 * @param orderId 订单ID
	 * @return JSONObject {CODE:"0接单成，其它值，接单失败，消息见MSG","MSG":"xxx"}
	 * @throws JDBCException 
	 */
	public JSONObject recvOrder(DB db, String orderId) throws Exception {
		JSONObject result=new JSONObject();
		//获取会员订单
		MemberOrder order=getMemberOrderById(orderId, db);
		//根据流程状态ID获取商品信息
		Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
		//获取派单ID
		Map<String,String> otherParam=new HashMap<String,String>();
		otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"403");
		StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"402",order.getId(), otherParam);
		
		result.put("CODE", 0);
		result.put("MSG","接单成功！");
		
		return result;
	}
	
	
	/**
	 * 订单放货接口
	 * @param db
	 * @param orderId
	 * @return
	 * @throws Exception 
	 */
	public JSONObject distOrderRelease(DB db,String orderId) throws Exception{
		JSONObject result=new JSONObject();
		//获取会员订单
		MemberOrder order=getMemberOrderById(orderId, db);
		//根据流程状态ID获取商品信息
		Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
		//获取派单ID
		Map<String,String> otherParam=new HashMap<String,String>();
		StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"403",order.getId(), otherParam);
		
		return result;
	}


	/**
	 * 配送人员拒绝接单
	 * @param db  DB
	 * @param orderId  订单ID
	 * @return JSONObject {CODE:"0接单成，其它值，接单失败，消息见MSG","MSG":"xxx"}
	 * @throws JDBCException 
	 */
	public JSONObject refuseOrder(DB db, String orderId) throws Exception {
		JSONObject result=new JSONObject();
		//获取会员订单
		MemberOrder order=getMemberOrderById(orderId, db);
		//根据流程状态ID获取商品信息
		Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
		//获取派单ID
		Map<String,String> otherParam=new HashMap<String,String>();
		otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"401");
		StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"402",order.getId(), otherParam);
	
		result.put("CODE", 0);
		result.put("MSG","拒绝接单成功！");
		
		return result;
	}

	
	/**
	 * 
		1、商品表增加平均评星数、总评论数，字段名同店铺表
		商品表、店铺表、用户表增加：晒图数、差评数、好评数
		晒图数，只要有图就+1
		差评数，只要1星就+1
		好评数，只要4星及以上就+1
		
		评论表增加字段，晒图（有=1，无=0）
		
		2、评论时向商品表及店铺表写值：
		商品表
		平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
		总评论数++;
		
		店铺表
		平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
		总评论数++;
	 * @param db
	 * @param evalId
	 * @param memberId
	 * @throws JDBCException 
	 */
	public void processEval(DB db, String evalId, String memberId) throws JDBCException {
		//1，获取评论信息
		String querEvalDataSQL=
				"SELECT eval.id AS evalid/**评论ID**/,od.id AS odid/**顶点ID**/,cm.id AS cmid/**商品ID**/ "+
				"    ,eval.eval_images,eval.starvalue  "+
				"    ,store.id AS storeid /**店铺ID**/  "+
				"    FROM mall_CommodityOrderEvaluation AS eval   "+
				"    LEFT JOIN mall_MemberOrder AS od ON eval.OrderID=od.id "+
				"    LEFT JOIN mall_Commodity AS cm ON cm.id=od.commodityid  "+
				"    LEFT JOIN mall_Store AS store ON store.id=cm.store "+
				"    WHERE eval.id=? ";
		
		MapList map=db.query(querEvalDataSQL,evalId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			Row evalRow=map.getRow(0);
			//		1、商品表增加平均评星数、总评论数，字段名同店铺表
			//		商品表、店铺表、用户表增加：晒图数、差评数、好评数
			//		晒图数，只要有图就+1
			//		差评数，只要1星就+1
			//		好评数，只要4星及以上就+1
			//晒图
			String showImgs="0";
			//差评数
			String badComments="0";
			//好评数
			String goodComments="0";
			
			if(!Checker.isEmpty(evalRow.get("eval_images"))){
				//图片不为空
				showImgs="1";
			}
			
			int starVale=evalRow.getInt("starvalue", 0);
			if(starVale>=4){
				goodComments="1";
			}
			if(starVale==1){
				badComments="1";
			}
			//1.1商品表、店铺表、用户表增加 晒图数 show_imgs、差评数 bad_comments 、好评数good_comments
			String updateSQL="UPDATE "
					+ "  mall_commodity "  //商品表
					+ " SET show_imgs=COALESCE(show_imgs,0)+?,"
					+ "  bad_comments=COALESCE(bad_comments,0)+?,"
					+ "  good_comments=COALESCE(good_comments,0)+? "
					+ "  WHERE id=?";
			db.execute(updateSQL,new String[]{
					showImgs,badComments,goodComments,evalRow.get("cmid")
			},new int[]{
					Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR
			});
			
			//更新店铺数据库字段
			updateSQL="UPDATE "
					+ "  mall_Store " //店铺表
					+ " SET show_imgs=COALESCE(show_imgs,0)+?,"
					+ "  bad_comments=COALESCE(bad_comments,0)+?,"
					+ "  good_comments=COALESCE(good_comments,0)+? "
					+ "  WHERE id=?";
			db.execute(updateSQL,new String[]{
					showImgs,badComments,goodComments,evalRow.get("storeid")
			},new int[]{
					Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR
			});
			
			//更新用户数据库字段
			updateSQL="UPDATE "
					+ "  am_member " //用户表
					+ " SET show_imgs=COALESCE(show_imgs,0)+?,"
					+ "  bad_comments=COALESCE(bad_comments,0)+?,"
					+ "  good_comments=COALESCE(good_comments,0)+? "
					+ "  WHERE id=?";
			db.execute(updateSQL,new String[]{
					showImgs,badComments,goodComments,memberId
			},new int[]{
					Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR
			});
			
			
			//		评论表增加字段，晒图（有=1，无=0）//前端已经处理
			//		2、评论时向商品表及店铺表写值：
			//		商品表
			//		平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
			//		总评论数++;
			updateSQL="UPDATE "
					+ "  mall_commodity "  //商品表
					+ " SET PjPxDf=trim(to_char(((COALESCE(PjPxDf,0)*COALESCE(QjZs,0))+?)/(COALESCE(QjZs,0)+1),'990D9'))::double precision,"
					+ "  QjZs=COALESCE(QjZs,0)+1 "
					+ "  WHERE id=? ";
			
			db.execute(updateSQL,new String[]{starVale+"",evalRow.get("cmid")},
					new int[]{Type.INTEGER,Type.VARCHAR});
			
			//		店铺表
			//		平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
			//		总评论数++;
			updateSQL="UPDATE "
					+ "  mall_Store " //店铺表
					+ " SET PjPxDf=trim(to_char(((COALESCE(PjPxDf,0)*COALESCE(QjZs,0))+?)/(COALESCE(QjZs,0)+1),'990D9'))::double precision,"
					+ "  QjZs=COALESCE(QjZs,0)+1 "
					+ "  WHERE id=?";
			db.execute(updateSQL,new String[]{starVale+"",evalRow.get("storeid")},
					new int[]{Type.INTEGER,Type.VARCHAR});
			
				//			用户表
				//		平均评星数=((平均评星数*总评论数)+当前评星数)/(总评论数+1)
				//		总评论数++;
				//
				updateSQL="UPDATE "
						+ "  am_Member " //用户表
						+ " SET PjPxDf=trim(to_char(((COALESCE(PjPxDf,0)*COALESCE(QjZs,0))+?)/(COALESCE(QjZs,0)+1),'990D9'))::double precision,"
						+ "  QjZs=COALESCE(QjZs,0)+1 "
						+ "  WHERE id=?";
				db.execute(updateSQL,new String[]{starVale+"",memberId},
						new int[]{Type.INTEGER,Type.VARCHAR});
			
		}
	}

	
	/**
	 * 处理呼旅订单信息 
	 * @param db  DB
	 * @param orderIds  订单ID，多个ID使用逗号分隔
	 * @param extendData  订单扩展信息  数据结构 {
	 * 				"contact":"联系人",
	 * 				"contactPhone":"联系电话","groupOrderInfo":
	 * 					[{"vname":"游客名称","identity_code":"游客省份证号","contactPhone":"游客联系电话"}],
	 * 				"vesterNumber":1 /××游客数量××/,"childeDataShow":true}
	 * @throws JSONException 
	 * @throws JDBCException 
	 */
	public void processExendsOrder(DB db, String orderIds, String extendData) throws JSONException, JDBCException {
		
		if(!Checker.isEmpty(extendData)&&!Checker.isEmpty(orderIds)){
			//获取商品商城分类
			String querySQL="SELECT st.mallclass_id "+
							"    FROM  mall_MemberOrder AS od "+
							"    LEFT JOIN mall_Commodity AS cmd ON cmd.id=od.commodityid "+
							"    LEFT JOIN mall_store AS st ON st.id=cmd.store "+
							"    WHERE od.id=? ";
			
			JSONObject extendDataJs=new JSONObject(extendData);
			String updateSQL="UPDATE mall_MemberOrder SET Contact=?,ContactPhone=? WHERE id=? ";
			
			for(String orderId:orderIds.split(",")){
				//联系人
				String contact=extendDataJs.getString("contact");
				//联系电话
				String contactPhone=extendDataJs.getString("contactPhone");
				//跟团游信息
				JSONArray groupOrderInfo=extendDataJs.getJSONArray("groupOrderInfo");
				
				if(!Checker.isEmpty(contact)&&!Checker.isEmpty(contactPhone)){
					//根据订单ID更新订单 联系人，联系电话信息
					db.execute(updateSQL,new String[]{
							contact,contactPhone,orderId
					},new int[]{
							Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
					});
				}
				
				MapList map=db.query(querySQL,orderId,Type.VARCHAR);
				
				if(!Checker.isEmpty(map)){
					//店铺分类
					String mallClass=map.getRow(0).get("mallclass_id");
					
					if(groupOrderInfo!=null&&"8".equals(mallClass)){//更团游，处理子订单
						Table amGroupOrderTable=new Table("am_bdp", "am_group_order");
						for(int j=0;j<groupOrderInfo.length();j++){
							JSONObject item=groupOrderInfo.getJSONObject(j);
							
							TableRow inserRow=amGroupOrderTable.addInsertRow();
							
							inserRow.setValue("order_id",orderId);
							inserRow.setValue("vname", item.getString("vname"));
							inserRow.setValue("identity_code", item.getString("identity_code"));
							inserRow.setValue("contactphone", item.getString("contactPhone"));
						}
						
						db.save(amGroupOrderTable);
					}
				}
				
				
			}
		}
	}


	/**
	 * 根据订单编号和退货批次号，查询订单
	 * @param orderId   订单ID
	 * @param retreatBatchNo 退货批次编号
	 * @param db DB
	 * @throws JDBCException 
	 */
	public MapList getMemberOrderMapIdOrRetreatBatchNo(String orderId,
			String retreatBatchNo, DB db) throws JDBCException {
		MapList result=null;
		
		String querSQL="SELECT * FROM mall_MemberOrder WHERE id=? AND retreat_Batch_No=? ";
		
		result=db.query(querSQL, new String[]{
				orderId,retreatBatchNo
		}, new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		return result;
	}


	
	/**
	 * 根据订单ID更新订单对应的支付ID
	 * @param orderId
	 * @param pay_id
	 */
	public void updatePayId(String orderId, String pay_id) {
		String updateSQL="UPDATE mall_MemberOrder SET pay_id=? WHERE id=? ";
		DBManager dbManager=new DBManager();
		dbManager.execute(updateSQL,new String[]{
				pay_id,orderId
		},
		new int[]{Type.VARCHAR,Type.VARCHAR});
	}


	
	/**
	 * 交易支付时间限制 
	 * @param db  DB
	 * @param orderIds  订单ID，多个用逗号分隔 
	 * @throws JDBCException 
	 */
	public void setOrderCloseTime(DB db, String orderIds,String mallClass) throws JDBCException{
		if(!Checker.isEmpty(orderIds)&&("2".equals(mallClass)||"6".equals(mallClass))){
			
			//mall_class  2 酒店
			//mall_class  6 租车
			
			//，1获取变量设置
			String hotelCloseTime=Var.get("hotel_trand_limit_time");
			String rentalCarCloseTime=Var.get("car_rental_trand_limit_time");
			
			String currentCloseTime=hotelCloseTime;
			
			if("6".equals(mallClass)){
				currentCloseTime=rentalCarCloseTime;
			}
			
			//，2设置交易关闭时间
			for(String orderId:orderIds.split(",")){
				String updateSQL="UPDATE mall_memberOrder SET CLOSE_TRAND_TIME=(SELECT now() + '"+
						currentCloseTime+" min' ) WHERE id='"+orderId+"' ";
				
				db.execute(updateSQL);
			}
		}
		
		
	}
	
	
	/**
	 * 短息通知客户
	 * @param db
	 * @param orderId
	 * @throws JDBCException
	 */
	public void smsNotifyClient(DB db, String orderId) throws JDBCException {
		
	}
	
	
	/**
	 * 邮箱通知客户
	 * @param db
	 * @param orderId
	 * @throws JDBCException
	 */
	public void emaliNotifyClient(DB db, String orderId) throws JDBCException {
		
	}
	

	/**
	 * 订单完成，通知客户
	 * @param db  DB
	 * @param orderId  订单ID
	 * @throws JDBCException 
	 */
	public void notifyClient(DB db, String orderId) throws JDBCException {
		//,1 查询订单类型及订单相关信息。
		String queryOrderSQL="SELECT to_char(start_date,'yyyy-mm-dd') AS startDate,"+
		" to_char(leave_time,'yyyy-mm-dd') AS leaveTime, "+
		" st.store_name,od.* "+
		" FROM mall_MemberOrder AS od "+
		" LEFT JOIN mall_commodity AS mc ON mc.id=od.commodityid "+
		" LEFT JOIN mall_store AS st ON mc.store=st.id  "+
		" WHERE od.id=? "+
		" ORDER BY od.createdate desc";
		//，2发送短信
		MapList orderMap=db.query(queryOrderSQL,orderId,Type.VARCHAR);
		
		String  smsVarCode="order_complete_msg_templ_";
		
		if(!Checker.isEmpty(orderMap)){
			Row row=orderMap.getRow(0);
			
			String smsTempVar=smsVarCode+row.get("mall_class");
			
			//短信模板内容
			String smsContent=Var.get(smsTempVar);
			
			if(!Checker.isEmpty(smsContent))
			{
				for(int i=0;i<row.size();i++)
				{
					String key=row.getKey(i);
					if(key!=null)
					{
						String value=row.get(key);
						value=value==null?"":value;
						
						if(!Checker.isEmpty(smsContent)&&row!=null&&key!=null)
						{
							logger.info("--------- i=" + i);
							logger.info("key=" + key);
							logger.info("value=" + value);
							logger.info("smsContent=" + smsContent);
							smsContent=smsContent.replaceAll("\\$am\\{"+key.toUpperCase()+"\\}",value);
						}
					}
					
				}
			}
			
			logger.info("订单通知对象："+row.get("contactphone")+",订单通知短信内容："+smsContent);
			
			
			//短息通知
			try{
				SMSIdentifyingCode sic = new SMSIdentifyingCode("");
				String phone=row.get("contactphone");
				sic.getCode(smsContent,phone);
			}catch(Exception e){
				logger.error("发送短信失败。");
				e.printStackTrace();
				
			}
			
			//邮箱通知
			String emails=Var.get("order_recv_emails");
			if(!Checker.isEmpty(emails)){
				String[] arrEmails=emails.split(",");
				for(String email:arrEmails){
					try {
						AmEmailSend.send(email, 
								EmailSettings.getUser(),EmailSettings.getUserName(),"【呼旅网】订单通知",
								smsContent);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	

}
