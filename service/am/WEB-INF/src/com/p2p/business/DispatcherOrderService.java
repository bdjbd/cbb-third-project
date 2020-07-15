package com.p2p.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.map.GpsPointDistance;
import com.p2p.service.bt.SendMessageManager;
import com.wisdeem.wwd.WeChat.Utils;

public class DispatcherOrderService {
	
	public static final String tag="DispatcherOrderService";

	/**
	 * 根据订单自动派单
	 * @param orderCode
	 */
	public static void dispatcherOrder(String orderCode)
	{
	DB db=null;	
	try{
			
			db=DBFactory.newDB();
			
//			//检查订单状态是否已完成
//			String checkSQL="SELECT ord.order_code FROM p2p_DispatchRecod AS dr "
//					+ " LEFT JOIN ws_order AS ord ON dr.order_code=ord.order_code "
//					+ " WHERE dr.order_code='"+orderCode+"' "
//					+ " AND ord.data_status NOT IN (1,6,7)";
//			if(Checker.isEmpty(db.query(checkSQL))){
//				Utils.Log(tag, "订单状态无效，订单已完成或取消。订单编号:"+orderCode);
//				return ;
//			}
			
			//0、向数据库写入该订单的订单编号（如订单编号已存在则不写入），接单状态：未接单。
			//将订单表，与订单派单表同步
			updateDispatcherOrder(orderCode ,db);
			
			//选择维修人员的可服务项目=订单服务项目
			//接单状态=待命中
			//距上次接单时间最长的人；
			//返回所有满足以上条件的维修人员集合
			MapList map=getRecvMember(orderCode,db);
			
			//距离最近3公里（可定义）之内的人,当未发现符合条件的接单服务人员时，修改接单状态：无人接单；
			String memberCode=filterMemberByLocation(map,orderCode,db);
			
			//3、给维修端推送派单消息。
			if(memberCode!=null)
			{
				sendOrdMsgToMember(memberCode,orderCode,db);
			}

			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 发送订单消息给维修人员
	 * @param memberCode  维修人员编号
	 * @param orderCode  订单编号
	 * @param db   数据库
	 * @throws JDBCException
	 */
	public static void sendOrdMsgToMember(String memberCode, String orderCode,DB db) throws JDBCException 
	{
		Utils.Log(tag, "sendOrdMsgToMember:"+memberCode+"  "+orderCode);
		
		String sql="SELECT ord.order_code,wcn.name "
				+ " FROM ws_order AS ord "
				+ " LEFT JOIN ws_commodity_name AS wcn "
				+ " ON ord.comdity_id=wcn.comdity_id  "
				+ " WHERE ord.order_code='"+orderCode+"' AND wcn.type=3 ";
		MapList map=db.query(sql);
		
		if(!Checker.isEmpty(map))
		{
			try
			{
				sql="SELECT member_code,bdPushUserID,bdPushChannelID "
						+ " FROM ws_member  WHERE member_code="+memberCode;
				Utils.Log(tag, "sendOrdMsgToMember SQL:"+sql);
				
				map=db.query(sql);
				
				if(!Checker.isEmpty(map))
				{
					
					String userid=map.getRow(0).get("bdpushuserid");
					long channelId=Long.parseLong(map.getRow(0).get("bdpushchannelid"));
					String tContent=memberCode+","+orderCode+","+new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date());
					
					SendMessageManager smm=new SendMessageManager(userid,channelId);
					String rValue=smm.send(tContent, 3);
					
					Utils.Log(tag, "任务推送给会员："+memberCode+";userId："+userid+";channelId:"+channelId
							+";推送消息："+tContent+";"
							+"推送结果：" + rValue);
				}
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
//		JSONObject msg=new JSONObject();
//		try {
//			//"{\"title\":\"Notify_title_danbo\",\"description\":\"Notify_description_content\"}"
//			msg.put("title", memberCode);
//			msg.put("description","服务"+ map.getRow(0).get("name"));
//			
////			Utils.sendMsgToMember(memberCode, msg.toString());
//			MT mt=new MT();
//			mt.setUserId(userId);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 根据系统配置距离(3公里)计算可以接单人
	 * @param map  具有服务能力，并且处理待命状态中，距离接单时间最长的人数据集合。
	 * @param orderCode 订单编号
	 * @param db
	 * @return 可以接单人编号
	 * @throws JDBCException 
	 * @throws ParseException 
	 */
	private static String filterMemberByLocation(MapList map, String orderCode,DB db) throws JDBCException, ParseException 
	{
		//获取订单GPS坐标
		String sql="SELECT  longitud,latitude FROM ws_order WHERE order_code='"+orderCode+"'";
		MapList orderMap=db.query(sql);//获取订单坐标
		
		//lastLogintube，lastLautitube，
		double distance=0.0;
		String memberCode=null;
		
		//会员GPS位置
		double lng1=0.0;//经度 Longitude
		double lat1=0.0;//纬度 Latitude
		
		//订单目标GPS位置
		double lat2=0.0;//纬度
		double lng2=0.0;//经度
		
		//获得标准距离，千米
		double standerDistance=Var.getDouble("AutoDispatcherDistance",2);
		
		Utils.Log(tag, "订单号：" + orderCode + ";可以接单的人有（Map.size）"+map.size() + "个");
		
		for(int i=0;i<map.size();i++)
		{
			//获取维修人员当前坐标
			String memberCodeSQL="SELECT LAST_LOGINTUBE,LAST_LAUTITUBE FROM ws_member WHERE member_code="
					+ map.getRow(i).get("member_code");
			MapList map1=db.query(memberCodeSQL);
			
			//会员GPS位置
			lng1=map1.getRow(0).getDouble("last_logintube",0);//经度 Longitude
			lat1=map1.getRow(0).getDouble("last_lautitube",0);//纬度 Latitude
			
			//订单目标GPS位置
			lat2=orderMap.getRow(0).getDouble("latitude", 0);//纬度
			lng2=orderMap.getRow(0).getDouble("longitud", 0);//经度
			
			//计算距离，千米
			distance=GpsPointDistance.getDistance(lat1, lng1, lat2, lng2);

			Utils.Log(tag, "标准距离:"+standerDistance+"千米;实际距离地址："+distance+"千米;维修人员编号："+map.getRow(i).get("member_code"));
			
			//判断会员地址是否在标准区域地址范围内
			if(standerDistance>=distance)
			{
				memberCode= map.getRow(i).get("member_code");
				Utils.Log(tag, "订单配送给:"+memberCode+";订单号为:"+orderCode);
				
				//判断当前选定的接单维修人员，是否是上次选择的接单维修人员
				//如果是，则判断时间是否超出派单超时时间
				//如果是，则不派单，选择下一位
				if(isMemberTimeOut(memberCode,orderCode)<=0)
				{
					break;
				}
				else
				{
					memberCode=null;
				}

			}
		}
		
		if(memberCode==null)
		{
			//当未发现符合条件的接单服务人员时，修改接单状态：无人接单；
			Utils.Log(tag, "未发现符合条件的接单服务人员时，修改接单状态：无人接单；");
			sql="UPDATE p2p_DispatchRecod "
				+ " SET endtime=null,ORStatus=2 "
				+ " WHERE order_code='"+orderCode+"'";
		}
		else
		{
			//找到符合条件的接单人，更新接单人。
			//更新该接单人的接单时间
			sql="UPDATE p2p_DispatchRecod SET MEMBER_CODE="+memberCode
					+",ORStatus=0,recv_time=now() WHERE order_code='"+orderCode+"'";
		}
		
		Utils.Log(tag, "sql="+sql);
		db.execute(sql);
		
		return memberCode;
	}
	
	//判断当前接单人的接单时间是否超时
	//返回超时分钟
	public static long isMemberTimeOut(String memberCode,String orderCode) throws JDBCException, ParseException
	{
		DB db=null;
		long rValue=0;
		
		try{

			db=DBFactory.newDB();
			
			//超时标准时间，单位分钟
			long bOutTime=Var.getInt("interval", 0);
			
			//距离上次派单时间差，单位分钟
			long thisOutTime=0;
			String tSql="select recv_time from p2p_DispatchRecod where " 
					+ " member_code='" + memberCode +"'"
					+ " and order_code='" + orderCode + "'";
			
			Utils.Log(tag, "isMemberTimeOut.tSql；" + tSql);
			
			MapList map=db.query(tSql);
			
			Utils.Log(tag, "isMemberTimeOut.map.size()；" + map.size());
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String tStr="";
			if(!Checker.isEmpty(map))
			{
				tStr=map.getRow(0).get("recv_time");
				
				Date date1= format.parse(tStr);
				Date date2=new Date();
				
				//获得分钟间隔
				thisOutTime=(date2.getTime()-date1.getTime())/(60*1000);
			}
			
			rValue=thisOutTime-bOutTime;
			
			Utils.Log(tag, "超时时间设置为：" + bOutTime + "；会员：" 
					+ memberCode + "由" + tStr + "接收" + orderCode + "号订单,截止目前已经超时" + thisOutTime + "分钟；");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return rValue;
	}

	/**
	 * 获取可以接单的人员  
	 * 选择维修人员：可服务项目=订单服务项目，接单状态=待命中，距上次接单时间最长的人；
	 * @param orderCode
	 * @param db
	 * @return  MapList 会员编号  key  member_code，会员最近坐标：lastLogintube，lastLautitube，
	 * @throws JDBCException 
	 */
	private static MapList getRecvMember(String orderCode, DB db) throws JDBCException 
	{

		//过滤所有具有接单能力，工作状态=接单的，从未接过单的维修人员
		String sql = "	select a.member_code,'' as recv_time from ws_member a ,p2p_MemberAbility b,ws_order c  "
				+ " where not exists (select * from p2p_DispatchRecod where member_code = a.member_code) "
				+ "	and a.recvstatus=2   "
				+ "	and a.orgid='ekx'   "
				+ "	and a.member_code=b.member_code   "
				+ "	and b.serbver_code=c.comdity_id  "
				+ "	and c.order_code='"
				+ orderCode + "'  ";
		MapList mapFirst=db.query(sql);
		
		Utils.Log(tag, "过滤所有具备" + orderCode + "号订单接单能力，工作状态=接单的，从未接过单的维修人员(map.size=" 
				+ mapFirst.size() + "):"+sql);
		
		//过滤距离上次接单时间最久的人
		
		// 2，所有已接单人的排序
		sql = "select  a.member_code,max(d.RECV_TIME) from ws_member a,p2p_MemberAbility b,ws_order c,p2p_DispatchRecod d "
				+ " where a.member_code=b.member_code "
				+ " and b.serbver_code=c.comdity_id  "
				+ " and a.member_code=d.member_code "
				+ " and a.recvstatus=2 "
				+ " and a.orgid='ekx' "
				+ " and c.order_code='"
				+ orderCode
				+ "' "
				+ " GROUP BY a.member_code "
				+ " order by max(d.RECV_TIME)";
		MapList mapMore = db.query(sql);

		Utils.Log(tag, "过滤距离上次接单时间最久的人:" + mapMore.size() + "个；Sql="+ sql);
		
		MapList rMap=null;
		if(!Checker.isEmpty(mapFirst))
		{
			//将已接单人合并至从未接过单的维修人员集合
			if(Checker.isEmpty(mapMore))
			{
				int tMapMoreCount=mapMore.size();
				
				for(int i=0;i<tMapMoreCount;i++)
				{
					mapFirst.insertRow(mapFirst.size(), mapMore.getRow(i));
				}
			}
			rMap=mapFirst;
		}
		else
			rMap=mapMore;
		
		Utils.Log(tag, "最终符合条件的服务人员:" + rMap.size() + "个；");
		
		
		return rMap;
	}

	/**
	 * 更新订单状态
	 * @param orderCode 订单编号
	 * @param db
	 * @throws JDBCException 
	 */
	private static void updateDispatcherOrder(String orderCode, DB db) throws JDBCException 
	{
		//检查订单是否为服务类型的订单  并且订单状态为已下单的订单
		String checkOrderSQL="SELECT order_code "
				+ " FROM ws_order AS ord "
				+ " LEFT JOIN ws_commodity_name AS wcn ON ord.comdity_id=wcn.comdity_id  "
				+ " WHERE wcn.type=3 AND ord.order_code='"+orderCode+"' AND ord.data_status=2 ";
		MapList mapList=db.query(checkOrderSQL);
		
		if(Checker.isEmpty(mapList))
		{
			Utils.Log(tag,"订单商品类型不为服务类商品 :"+orderCode);
		}
		
		//查询是否存在数据
		String checkExits="SELECT * FROM p2p_DispatchRecod  WHERE order_code='"+orderCode+"' ";
		MapList map=db.query(checkExits);
		
		//更新数据SQL
		String updateSQL="";
		if(!Checker.isEmpty(map))
		{
			//update
			//updateSQL="UPDATE p2p_DispatchRecod SET  ORStatus=0,recv_time=now(),member_code=null,endtime=null "
			//		+ " WHERE order_code='"+orderCode+"' ";
		}
		else
		{
			//insert
			updateSQL="INSERT INTO  p2p_DispatchRecod  "
					+ " ( id, order_code,orstatus,recv_time) VALUES "
					+ " (uuid_generate_v4(),'"+orderCode+"',0,now())";
			db.execute(updateSQL);
		}
		
	}
	
	
	/** 
	 * 订单完成接口
	 * 
	 * 1.更新订单状态为6；
	 * update ws_order set data_status=6 where  order_code ='订单编号'
	 * 2.更新维修人员工作状态为：2=待命中，扣除其现金中的信息费
	 * update ws_member set recvstatus='2' where member_code='人员编码'";
	 * 3.扣除项目接单手续费（=企业基本信息表.项目手续费比例*服务项目金额）
	 * ws_order.InfoCosts=ws_order.Total * ws_org_baseinfo.projectfeeratio
	 * 
	 * @param orgID 组织机构编码
	 * @param OrderCode 订单编码
	 * @param MemberCode 维修人员编码
	 * */
	public static String OverOrder(String orgID,String OrderCode,String MemberCode)
	{
		String rValue="";
		String rMsg="";
		String tSql="";
		int tNum=0;
		DB db=null;
		try 
		{
			db=DBFactory.newDB();
			
//			tSql="select (total * (select projectfeeratio from ws_org_baseinfo where orgid='" + orgID + "')) as infocosts"
//				+" from ws_order where order_code='" + OrderCode + "'";
			
			//更新商品销售数量
			updateModiftyAmount(db,OrderCode);
			
			//更新库存数量 2014-09-12
			updateStore(db,OrderCode);
			
			//根据订单号更新给下单人积分
			updateMemberScore(db, OrderCode);
			
			//20140831 商品信息费需求变更
			tSql="SELECT order_code,orgid,infocosts FROM ws_order WHERE order_code='"+OrderCode+"'";
			MapList map=db.query(tSql);
			
			//信息费
			Double InfoCosts = null;
			
			if(!Checker.isEmpty(map))
				//InfoCosts=map.getRow(0).getDouble("infocosts",4);
				InfoCosts=map.getRow(0).getDouble("infocosts", 0);
			
			rMsg+="本次订单(" + OrderCode + ")信息费为：" + InfoCosts.toString() + "元;\n";
			
			//更新维修人员工作状态为：2=待命中,更新现金扣除信息费
			tSql="update ws_member set recvstatus='2'" 
					+ ",cash=cash-" + InfoCosts.toString() + " where member_code='" + MemberCode + "'";
			tNum=db.execute(tSql);
			
			if(tNum>0) 
				rMsg+="成功更新维修人员(" + MemberCode + ")recvstatus='2',共" + tNum + "条记录;\n";
			
			//更新订单状态为6,扣除项目接单手续费（=企业基本信息表.项目手续费比例*服务项目金额）
			tSql="update ws_order set data_status=6"
				+",InfoCosts=" + InfoCosts.toString()
				+" where order_code='" + OrderCode + "'";
			tNum=db.execute(tSql);
			
			if(tNum>0) 
				rMsg+="成功更订单(" + OrderCode + ")data_status=6,共" + tNum + "条记录;\n";
			
			rValue="true";
		} 
		catch (JDBCException e) 
		{
			e.printStackTrace();
			rValue="false";
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Utils.Log(tag, rMsg);
		
		return rValue.toUpperCase();
	}
	
	
	/**
	 * 跟新库存数量
	 * @param db  数据库
	 * @param orderCode 订单编号
	 * @throws JDBCException 
	 */
	public static void updateStore(DB db, String orderCode) throws JDBCException {
		
		String querySQL="SELECT * FROM p2p_commoditydetail  "
				+ " WHERE comdityformatid=(SELECT cf.id FROM p2p_comdityformat AS cf "
				+ " LEFT JOIN ws_order AS od ON cf.commodityid=od.comdity_id "
				+ " WHERE order_code='"+orderCode+"')";
		MapList storeMap=db.query(querySQL);
		
		
		if(!Checker.isEmpty(storeMap)){
			//MaterialsCode物资编码
			//Quantity数量
			
			String outStoreSQL="INSERT INTO p2p_outstore("
					+ "id, code,  outcode, counts, creattiem,outremark, datatstatus) VALUES "
					+ "(uuid_generate_v1(), ?,  to_char(now(),'yyyymmddhh24mmssSS'),"
					+ " ?,  now(),'订单完成自动出库', 3)";
			
			String updateSQL="UPDATE p2p_materialscode SET amount=(amount-?) WHERE code=?";
			
			List<String[]> paramList=new ArrayList<String[]>();
			List<String[]> updateParamList=new ArrayList<String[]>();
			
			for(int i=0;i<storeMap.size();i++){//遍历每个订单中的每个物质
				//1，插入出库记录数据
				Row row=storeMap.getRow(i);
				paramList.add(new String[]{row.get("materialscode"),row.get("quantity")});
				//2，更新库存量
				updateParamList.add(new String[]{row.get("quantity"),row.get("materialscode")});
			}
			
			db.executeBatch(outStoreSQL, paramList, new int[]{});
			db.executeBatch(updateSQL, updateParamList, new int[]{});
		}
	}

	//向用户终端推送消息
	/**
	 * 向用户终端推送消息
	 * @param userid 百度云推送产生的用户设备id
	 * @param channelid 百度云推送的开发者代码
	 * @param content 推送的内容
	 */
	public static String sendMessageToUserEqurp(String userid,String channelid,String content)
	{
		String rValue="";
		
		
		
		/*long btChannelID=0;
		
		MT mt=new MT();
		mt.setUserId(userid);
		long channelId=-1;
		
		try
		{
			btChannelID=Long.parseLong(channelid);
			
			mt.setChannelId(btChannelID);
			mt.push(content);
			
			rValue="true";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			rValue="ErrorMessage:" + e.toString();
		}*/
		
		return rValue;
	}

	
	/**
	 * 根据订单编号更新销售数量
	 * @param db  DB FastUnit DB
	 * @param orderCode
	 */
	public static void updateModiftyAmount(DB db, String orderCode) {
		String updateSQL="UPDATE ws_commodity_name  SET   amount=COALESCE(amount,0)+1 "
				+ " WHERE comdity_id =("
				+ " SELECT comdity_id FROM ws_order WHERE order_code='"+orderCode+"'"
				+ " )";
		try{
			db.execute(updateSQL);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据订单号更新给下单人积分
	 * @param db
	 * @param orderCode
	 */
	public static void updateMemberScore(DB db,String orderCode){
		//更新下单人的积分
		String sql=
				"UPDATE ws_member SET integration=    "+
				"	COALESCE(integration+(SELECT score FROM ws_commodity_name WHERE comdity_id=(  "+
				"	SELECT comdity_id FROM ws_order WHERE order_code='"+orderCode+"' )),integration)   "+
				"	WHERE member_code=(SELECT member_code FROM  ws_order WHERE order_code='"+orderCode+"') ";
		try {
			db.execute(sql);
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
}
