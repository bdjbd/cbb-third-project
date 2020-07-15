package com.wisdeem.wwd.WeChat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.tools.DatabaseAccess;
import com.wisdeem.wwd.Constant;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 说明: 微商店业务处理Server类
 * 
 * @creator 岳斌
 * @create Nov 18, 2013
 * @version $Id
 */
public class WeShopServer {

	private DB db = null;
	private PrintWriter out;
	private static final String tag="WeShopServer:";

	private static WeShopServer weShopServer;
	
	
	/**
	 * 微商店服务类 
	 * @return
	 */
	public static WeShopServer getInstance(){
		if(weShopServer==null){
			weShopServer=new WeShopServer();
		}
		return weShopServer;
	}
	
	private  WeShopServer() {
		try {
			db = DBFactory.getDB();
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据会员编号获取对应的订单
	 * 
	 * @param memberCode
	 */
	public JSONArray getOrdersByMemberCode(String memberCode) {
//		String sql = "SELECT * FROM ws_order WHERE member_code=" + memberCode;
		String sql=/*"SELECT orders.member_code,order_code,recv_detail,recv_name,recv_phone,recv_postal_code," +
				"	orders.amount,orders.total,orders.explain,"+
				"	CASE orders.data_status                                                                "+
				"	WHEN 1 THEN '起草' WHEN 2 THEN '已提交' WHEN 3 THEN '已交易' END AS orderstatus        "+
				"	,cmn.comdity_id,cmn.name,cmn.description_text,cmn.price,                               "+
				"	zo.zonename,city.cityname,pro.proname,                                                 "+
				"	CASE pro.proremark                                                                     "+
				"	WHEN '特别行政区' THEN 1                                                               "+
				"	WHEN '自治区' THEN 2                                                                   "+
				"	WHEN '省份' THEN 3                                                                     "+
				"	WHEN '直辖市' THEN 4 END                                                               "+
				"	FROM ws_order AS orders                                                                "+
				"	LEFT JOIN ws_member AS member ON orders.member_code=member.member_code                 "+
				"	LEFT JOIN ws_commodity_name AS cmn ON orders.comdity_id=cmn.comdity_id                 "+
				"	LEFT JOIN ws_province AS pro ON orders.recv_province=to_number(pro.prosort,'999999999999D9999999') "+
				"	LEFT JOIN ws_city AS city ON orders.recv_city=to_number(city.citysort,'999999999999D9999999')  "+
				"	LEFT JOIN ws_zone AS zo ON orders.recv_area=zo.zoneid                                  "+
				"	WHERE orders.member_code="+memberCode+" ORDER BY order_code DESC                 ";*/
				"SELECT orders.member_code,org.orgname,                                                                        "+
				"		CASE WHEN ordss.order_code IS NULL OR  ordss.order_code='' THEN orders.order_code                     "+
				"		ELSE  ordss.order_code END AS order_code,                                                             "+
				"		CASE WHEN ordss.comdity_name IS NULL OR ordss.comdity_name='' THEN cmn.name                           "+
				"		ELSE ordss.comdity_name END AS name ,                                                                 "+
				"		CASE WHEN ordss.description_text IS NULL OR ordss.description_text='' THEN cmn.description_text       "+
				"		ELSE ordss.description_text END AS description_text                                                   "+
				"		 ,recv_detail,recv_name,recv_phone,recv_postal_code,                                                  "+
				"	      	orders.amount,orders.total,orders.explain,                                                        "+
				"	      	CASE orders.data_status                                                                	          "+
				"	      	WHEN 1 THEN '起草' WHEN 2 THEN '已提交' WHEN 3 THEN '已交易' END AS orderstatus        	          "+
				"	      	,cmn.comdity_id,cmn.price,                        	                                              "+
				"	      	zo.zonename,city.cityname,pro.proname,                                                 	          "+
				"	      	CASE pro.proremark                                                                     	          "+
				"	      	WHEN '特别行政区' THEN 1                                                               	          "+
				"	      	WHEN '自治区' THEN 2                                                                   	          "+
				"	      	WHEN '省份' THEN 3                                                                     	          "+
				"	      	WHEN '直辖市' THEN 4 END                                                               	          "+
				"	      	FROM ws_order AS orders                                                                	          "+
				"	      	LEFT JOIN ws_member AS member ON orders.member_code=member.member_code                 	          "+
				"	      	LEFT JOIN ws_commodity_name AS cmn ON orders.comdity_id=cmn.comdity_id                 	          "+
				"	      	LEFT JOIN ws_province AS pro ON orders.recv_province=to_number(pro.prosort,'999999999999D9999999')"+
				"	      	LEFT JOIN ws_city AS city ON orders.recv_city=to_number(city.citysort,'999999999999D9999999')  	  "+
				"	      	LEFT JOIN ws_zone AS zo ON orders.recv_area=zo.zoneid                                             "+
				"	      	LEFT JOIN ws_orderdetail AS ordss ON ordss.order_code=orders.order_code                           "+
				"	      	LEFT JOIN aorg AS org ON orders.orgid=org.orgid                                                "+
				"	      	WHERE orders.member_code="+memberCode+" ORDER BY order_code DESC                                  ";
		
		JSONArray result=DatabaseAccess.query(sql);
		
		return result;
	}

	/**
	 * 根据会员编号获取对应的地址
	 * 
	 * @param memberCode
	 */
	public JSONArray getAddressByMemberCode(String memberCode) {
		String sql=
				"SELECT addr.*,pro.proname,city.cityname,zo.zonename FROM ws_addres AS addr                              "+
				"				LEFT JOIN ws_province AS pro ON addr.recv_province=to_number(pro.prosort,'999999999999D9999999') "+
				"        LEFT JOIN ws_city AS city ON addr.recv_city=to_number(city.citysort,'999999999999D9999999')  	 "+
				"        LEFT JOIN ws_zone AS zo ON addr.recv_area=zo.zoneid                                  	         "+
				"        WHERE member_code=" + memberCode+" ORDER BY addr.addres_id DESC";

		return DatabaseAccess.query(sql);
	}

	/**
	 * 获取省信息
	 * 
	 * @return
	 */
	public JSONArray loadProvinecs() {
		String getProvncesSQL = "SELECT prosort,proname FROM ws_province";
		return DatabaseAccess.query(getProvncesSQL);
	}

	/**
	 * 获取市信息 根据省份ID
	 * 
	 * @param parameter
	 * @return
	 */
	public JSONArray loadCityByProvince(String provinecid) {
		String getCitySQL = "SELECT  citysort,cityname FROM ws_city WHERE proid='"
				+ provinecid + "' ORDER BY citysort";
		return DatabaseAccess.query(getCitySQL);
	}

	/**
	 * 获取市信息 根据城市ID
	 * 
	 * @param parameter
	 * @return
	 */
	public JSONArray loadZoneByCityId(String cityId) {
		String getZoneSQL = "SELECT zoneid,zonename FROM ws_zone WHERE cityid='"
				+ cityId + "' ORDER BY zoneid ";
		return DatabaseAccess.query(getZoneSQL);
	}

	/**
	 * 保存会员地址
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return
	 */
	public JSONArray saveMemberAddr(HttpServletRequest request) {
		JSONArray result = null;
		String type = "";
		try {
			Table addrTable = new Table("wwd", "WS_ADDRES");
			TableRow tr = null;
			type = request.getParameter("type");
			if ("insert".equals(type)) {
				tr = addrTable.addInsertRow();
			} else {
				tr = addrTable.addUpdateRow();
				tr.setOldValue("addres_id", request.getParameter("ADDRESID"));
			}
			result = new JSONArray();

			tr.setValue("member_code", request.getParameter("MEMBERCODE"));
			tr.setValue("recv_province", request.getParameter("PROVINCE"));
			tr.setValue("recv_city", request.getParameter("CITY"));
			tr.setValue("recv_area", request.getParameter("AREA"));
			String detail = request.getParameter("DETAIL");
			String recvName = request.getParameter("RECVNAME");
			if (detail != null) {
				tr.setValue("recv_detail", URLDecoder.decode(detail, "UTF-8"));
			}
			if (recvName != null) {
				tr.setValue("recv_name", URLDecoder.decode(recvName, "UTF-8"));
			}
			tr.setValue("recv_phone", request.getParameter("PHONE"));
			tr.setValue("recv_postal_code", request.getParameter("POSTCODE"));
			db.save(addrTable);
			result.put(0, "success");
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	/**
	 * 根据公众帐号ID获取对应的商品
	 * 
	 * @param token
	 * @param orgid
	 * @return
	 */
	public JSONArray getShopListView(String token,String orgid,String currentPage) {
		String root = Path.getRootPath();
		JSONArray result = new JSONArray();
		try {
			String getFilePath=null;
			if(orgid==null){
				getFilePath= "SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
						+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
						+ " CASE WHEN cn.hot IS NULL THEN -1 ELSE cn.hot END AS odbHot,"
						+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
						+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,cn.payment,"
						+ " wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath "
						+ "	FROM ws_commodity_name AS cn                                                 "
						+ "	LEFT JOIN ws_public_accounts AS wa                                           "
						+ "	ON cn.orgid=wa.orgid WHERE wa.token='"+ token+ "' AND cn.data_status=2 " 
						+ " ORDER BY odbHot DESC, comdity_id ASC  "+
						"   LIMIT 5 OFFSET "+currentPage+"*5 ";
			}else{
				getFilePath= "SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
						+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
						+ " CASE WHEN cn.hot IS NULL THEN -1 ELSE cn.hot END AS odbHot,"
						+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
						+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,cn.payment,"
						+ " wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath "
						+ "	FROM ws_commodity_name AS cn                                                 "
						+ "	LEFT JOIN ws_public_accounts AS wa                                           "
						+ "	ON cn.orgid=wa.orgid WHERE wa.token='"+ token+ "' AND cn.data_status=2 " 
						+ " AND cn.orgid='"+orgid+"' "
						+ " ORDER BY odbHot DESC, comdity_id ASC  "+
						"   LIMIT 5 OFFSET "+currentPage+"*5 ";
			}
			MapList map = db.query(getFilePath);
			List<String> fileNames = new ArrayList<String>();
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					fileNames.add(Utils.getFileNamer(root
							+ map.getRow(i).get("filepath")));
				}
			}
			result.put(0, DatabaseAccess.query(getFilePath));
			JSONObject filePaths = new JSONObject();
			for (int i = 0; i <fileNames.size(); i++) {
				filePaths.put(i + "", fileNames.get(i) + "");
			}
			result.put(1, filePaths);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据商品ID获取商品信息
	 * 
	 * @param cid
	 * @return
	 */
	public JSONArray getComdityById(String cid) {
		JSONArray result = new JSONArray();
		try {
			JSONObject fileJb = new JSONObject();
			String sql = "SELECT  "
					+ "comdity_id,orgid,name,comdity_class_id,price,description_text, "
					+ "data_status,added_time,hot,payment,comdity_norm,comdity_code,"
					+ "CASE WHEN orders IS NULL THEN 0 ELSE orders END AS orders, "
					+ "CASE WHEN hot IS NULL THEN -1 ELSE hot END AS odbhot,"
					+ "'/files/WS_COMMODITY_NAME/'||comdity_id AS filepath "
					+ " FROM ws_commodity_name  " + " WHERE comdity_id=" + cid
					+ "  ORDER BY odbhot DESC ";
			MapList map = db.query(sql);
			String filePath = Path.getRootPath();
			if (!Checker.isEmpty(map)) {
				filePath += map.getRow(0).get("filepath");
			}
			// productimage1
			for (int i = 1; i < 7; i++) {
				String temp = filePath + "/productimage" + i;
				fileJb.put(i+"", "/files/WS_COMMODITY_NAME/"+cid+"/productimage" +i+"/"+Utils.getFileNamer(temp));
			}
			result.put(0, DatabaseAccess.query(sql));
			result.put(1, fileJb);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 提交订单
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return
	 */
	public JSONArray submitOrder(HttpServletRequest req) {
		JSONArray reslut=new JSONArray();
		try {
			String orgid =req.getParameter("ORGIDP");
			String cid=req.getParameter("COMDITY_ID");
			
			String getPrice="SELECT price FROM ws_commodity_name  WHERE comdity_id="+cid;
			MapList priceMap=db.query(getPrice);
			String price=null;
			if(!Checker.isEmpty(priceMap)){
				price=priceMap.getRow(0).get(0);
			}
			Double priced=0d;
			if(price!=null){
				priced=Double.parseDouble(price);
			}
			Table table = new Table("wwd", "WS_ORDER");
			TableRow tr = table.addInsertRow();	
			tr.setValue("order_code", Utils.getOrderCode(orgid));
			tr.setValue("orgid", orgid);
			tr.setValue("member_code",req.getParameter("MEMBER_CODE"));
			tr.setValue("comdity_id", cid);
			tr.setValue("recv_province",req.getParameter("RECVPROVINCE"));
			tr.setValue("recv_city", req.getParameter("RECVCITY"));
			tr.setValue("recv_area", req.getParameter("RECVZONE"));
			tr.setValue("recv_detail", URLDecoder.decode(req.getParameter("RECVDETAIL"),"UTF-8"));
			tr.setValue("recv_name", URLDecoder.decode(req.getParameter("RECVNAME"),"UTF-8"));
			tr.setValue("explain",URLDecoder.decode(req.getParameter("EXPALIN"),"UTF-8"));
			tr.setValue("recv_phone", req.getParameter("RECVPHONE"));
			tr.setValue("recv_postal_code", req.getParameter("RECVPOSTCODE"));
			tr.setValue("data_status", 2);
			//支付方式
			tr.setValue("payment", 1);
//			tr.setValue("orders", Integer.parseInt(orders)+1);
			//订单数量
			String amount=req.getParameter("AMOUNT");
			tr.setValue("amount", amount);
			tr.setValue("total",priced*Integer.parseInt(amount));
			
			db.save(table);
			String getOrders="SELECT orders FROM ws_commodity_name WHERE comdity_id="+cid+" AND orgid='"+orgid+"'";
			MapList ordMap=db.query(getOrders);
			String orders=null;
			if(!Checker.isEmpty(ordMap)){
				orders=ordMap.getRow(0).get(0);
			}
			//订单编号
			String id=tr.getValue("order_code");
			//更新商品的订单量
			Table comdityTable=new Table("wwd","ws_commodity_name");
			TableRow updataRow=comdityTable.addUpdateRow();
			updataRow.setOldValue("comdity_id", cid);
			if(orders==null||orders.equals("0")){
				orders="0";
			}
			updataRow.setValue("orders", Integer.parseInt(orders)+Integer.parseInt(amount));
			db.save(comdityTable);
			//将订单信息保存到订单详情中，以后在可以购买多个商品是可以在此扩展。
			Table detailTable = new Table("wwd", "WS_ORDERDETAIL");
			TableRow detailTr = detailTable.addInsertRow();
			//获取商品信息，订单编号，商品名称，上面分类名称，商品单价，商品描述，热度，购买方式，这信息非索引.
			String getShopInfoSQL=
							"SELECT ord.order_code,cn.name,cn.price,cn.description_text,cn.hot,     "+
							"	cn.payment,cn.comdity_norm,cc.class_name  FROM ws_order AS ord        "+
							"	LEFT JOIN ws_commodity_name AS cn ON cn.comdity_id=ord.comdity_id     "+
							"	LEFT JOIN ws_commodity AS cc ON cn.comdity_class_id=cc.comdy_class_id "+
							"	WHERE ord.order_code='"+id+"'  ";
			MapList orderShopInfoMap=db.query(getShopInfoSQL);
			if(Checker.isEmpty(orderShopInfoMap)){
				throw new  JDBCException("订单保存错误！");
			}
			//目前只支持一个商品一条订单
			detailTr.setValue("order_code",orderShopInfoMap.getRow(0).get("order_code"));
			detailTr.setValue("comdity_name",orderShopInfoMap.getRow(0).get("name"));
			detailTr.setValue("class_name",orderShopInfoMap.getRow(0).get("class_name"));
			detailTr.setValue("price",orderShopInfoMap.getRow(0).get("price"));
			detailTr.setValue("amount",amount);
			detailTr.setValue("subtotal",new Double(orderShopInfoMap.getRow(0).get("price"))*new Double(amount));
			detailTr.setValue("description_text",orderShopInfoMap.getRow(0).get("description_text"));
			detailTr.setValue("hot",orderShopInfoMap.getRow(0).get("hot"));
			detailTr.setValue("payment",orderShopInfoMap.getRow(0).get("payment"));
			detailTr.setValue("comdity_norm",orderShopInfoMap.getRow(0).get("comdity_norm"));
			detailTr.setValue("comdity_norm",orderShopInfoMap.getRow(0).get("comdity_norm"));
			db.save(detailTable);
			reslut.put(0,id);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return reslut;
	}

	/**
	 * 加载商品分类
	 * @param token 公众帐号token
	 * @param parentId 父ID
	 * @param sceneid 场景ID
	 * @return
	 */
	public JSONArray loadShopClass(String token,String parendId,String orgid) {
		String sql=null;
		sql="SELECT orgid,class_name,comdy_class_id,parent_id " +
					" FROM ws_commodity WHERE orgid='"+orgid+"'"+
					" AND parent_id IS NOT NULL AND parent_id="+parendId+" ORDER BY class_name ";
		return DatabaseAccess.query(sql);
	}

	/**
	 * 根据商品分类ID获取商品
	 * @param pid
	 * @param cid
	 * @return
	 */
	public JSONArray loadShopListByShopClass(String token,String cid){
		String root = Path.getRootPath();
		JSONArray result = new JSONArray();
		try {
			String getFilePath = "SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
					+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
					+ " CASE WHEN cn.hot IS NULL THEN -1 ELSE cn.hot END AS odbHot,"
					+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
					+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,"
					+ " wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath "
					+ "	FROM ws_commodity_name AS cn                                                 "
					+ "	LEFT JOIN ws_public_accounts AS wa                                           "
					+ "	ON cn.orgid=wa.orgid WHERE wa.token='"+ token+ "'"
					+ " AND cn.comdity_class_id="+cid+" AND cn.data_status=2 ORDER BY odbHot DESC, comdity_id ASC  ";
			MapList map = db.query(getFilePath);
			List<String> fileNames = new ArrayList<String>();
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					fileNames.add(Utils.getFileNamer(root
							+ map.getRow(i).get("filepath")));
				}
			}
			result.put(0, DatabaseAccess.query(getFilePath));
			JSONObject filePaths = new JSONObject();
			for (int i = 0; i <fileNames.size(); i++) {
				filePaths.put(i + "", fileNames.get(i) + "");
			}
			result.put(1, filePaths);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据价格排序商品
	 * @param token
	 * @param sceneid
	 * @return
	 */
	public JSONArray loadShopByPrice(String token,String orderby,String orgid) {
		String root = Path.getRootPath();
		JSONArray result = new JSONArray();
		try {
			String 
				getFilePath = "SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
						+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
						+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
						+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,"
						+" wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath "
						+ "	FROM ws_commodity_name AS cn                                                 "
						+ "	LEFT JOIN ws_public_accounts AS wa                                           "
						+ "	ON cn.orgid=wa.orgid WHERE wa.token='"+ token+ "' AND cn.data_status=2 " 
						+ " AND cn.orgid ='"+orgid+"'"
						+ " ORDER BY cn.price  "+orderby;
			
			MapList map = db.query(getFilePath);
			List<String> fileNames = new ArrayList<String>();
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					fileNames.add(Utils.getFileNamer(root
							+ map.getRow(i).get("filepath")));
				}
			}
			result.put(0, DatabaseAccess.query(getFilePath));
			JSONObject filePaths = new JSONObject();
			for (int i = 0; i <fileNames.size(); i++) {
				filePaths.put(i + "", fileNames.get(i) + "");
			}
			result.put(1, filePaths);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据订购量排序商品
	 * @param token
	 * @param orderby 排序方式
	 * @param orgid  场景ID
	 * @return
	 */
	public JSONArray loadShopByAmount(String token,String orderby,String orgid) {
		String root = Path.getRootPath();
		JSONArray result = new JSONArray();
		try {
			String getFilePath= "SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
						+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
						+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
						+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,"
						+" wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath, "
						+ " CASE  WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS ordrbys "
						+ "	FROM ws_commodity_name AS cn                                                 "
						+ "	LEFT JOIN ws_public_accounts AS wa                                           "
						+ "	ON cn.orgid=wa.orgid WHERE wa.token='"+ token+ "' AND cn.data_status=2 " 
						+ " AND cn.orgid ='"+orgid+"'"
						+ " ORDER BY ordrbys "+orderby;
			MapList map = db.query(getFilePath);
			List<String> fileNames = new ArrayList<String>();
			if (!Checker.isEmpty(map)) {
				for (int i = 0; i < map.size(); i++) {
					fileNames.add(Utils.getFileNamer(root
							+ map.getRow(i).get("filepath")));
				}
			}
			result.put(0, DatabaseAccess.query(getFilePath));
			JSONObject filePaths = new JSONObject();
			for (int i = 0; i <fileNames.size(); i++) {
				filePaths.put(i + "", fileNames.get(i) + "");
			}
			result.put(1, filePaths);
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	

	/**
	 * 商品搜索
	 * 搜索规则：多个关键字用逗号分隔中英文不区分(未实现)，搜索优先级为：
	 * 商品名称，描述，规格进行匹配 当一次匹配数据量超过50条数据时不再向下搜索
	 * @param key
	 * @param token
	 * @param orgid
	 * @return 搜索结果
	 */
	public JSONArray getSearchShopList(String key,String token,String orgid) {
		String root = Path.getRootPath();
		JSONArray result = new JSONArray();
		if(key==null||key.trim().length()==0){
			return new JSONArray();
		}else{
			try{
				key=key.trim();
				String searchSQL=null;
					searchSQL="SELECT SUBSTRING(cn.description_text FROM 0 FOR 40) AS summary ,"
							+ " CASE WHEN cn.orders IS NULL THEN 0 ELSE cn.orders END AS orders,"
							+ " cn.comdity_id,cn.orgid,cn.name,cn.comdity_class_id,cn.price,cn.description_text,cn.data_status,"
							+ " cn.added_time,cn.hot,cn.payment,cn.comdity_norm,cn.comdity_code,"+
							" wa.*, '/files/WS_COMMODITY_NAME/'||comdity_id||'/productscover/' AS filepath "+
							" 	FROM ws_commodity_name AS cn       "+
							" 	LEFT JOIN ws_public_accounts AS wa ON cn.orgid=wa.orgid  "+
							" 	LEFT JOIN ws_commodity AS cc ON cn.comdity_id=cc.comdy_class_id   "+
							" 	WHERE wa.token='"+token+"' AND cn.data_status=2   "+
							"   AND cn.orgid ='"+orgid+"'"+
							" 	AND ( cn.name like '%"+key+"%'      "+
							" 	OR cn.description_text like '%"+key+"%'    "+
							" 	OR cn.comdity_norm like '%"+key+"%' )   "+
							" 	LIMIT 50       ";
				MapList map = db.query(searchSQL);
				List<String> fileNames = new ArrayList<String>();
				if (!Checker.isEmpty(map)) {
					for (int i = 0; i < map.size(); i++) {
						fileNames.add(Utils.getFileNamer(root
								+ map.getRow(i).get("filepath")));
					}
				}
				result.put(0, DatabaseAccess.query(searchSQL));
				JSONObject filePaths = new JSONObject();
				for (int i = 0; i <fileNames.size(); i++) {
					filePaths.put(i + "", fileNames.get(i) + "");
				}
				result.put(1, filePaths);
			}catch(Exception e){
				e.printStackTrace();
			}
		return result;
		}
	}

	/**
	 * 获取机构名
	 * @param token  token
	 * @param orgid
	 * @return
	 */
	private JSONArray getOrgnameByToken(String token,String orgid) {
		String getOrgSQL="SELECT org.orgname FROM ws_public_accounts AS pacc " +
					"	LEFT JOIN aorg AS org ON pacc.orgid=org.orgid " +
					"	WHERE pacc.token='"+token+"'"+
					"   AND org.orgid ='"+orgid+"'"+
					"   limit 1 ";
		return DatabaseAccess.query(getOrgSQL);
	}

	/**
	 * 注册用户
	 * @param openid
	 * @param token
	 * @return
	 */
	public JSONArray registerUser(String openid, String token) {
		try{
			String orgid=Utils.getOrgId(token);
			String findViewSQL=
					"SELECT acc.*,member.*,member.data_status AS uStatus,org.orgname FROM ws_member AS member "+
							"         	LEFT JOIN ws_public_accounts AS acc             "+
							"         	ON acc.orgid=member.orgid                       "+
							"         	LEFT JOIN aorg AS org                           "+
							"         	ON org.orgid=acc.orgid                          "+
							"         	WHERE  member.openid='"+openid+"' 	            "+
							"         	AND acc.token='"+token+"'                       ";
			MapList map=db.query(findViewSQL);
			String getPublicIdSQL="SELECT public_id FROM  ws_public_accounts WHERE token='"+token+"' ";
			if(Checker.isEmpty(map)){
				MapList publicidMap=db.query(getPublicIdSQL);
				String paswd=Utils.getRandomStr(5);
				Table table=new Table("wwd","WS_MEMBER" );
				TableRow tr=table.addInsertRow();
				tr.setValue("orgid", orgid);
				tr.setValue("wshop_name", openid);
				tr.setValue("openid", openid);
				tr.setValue("group_id",1);
				tr.setValue("wshaop_password", DigestUtils.md5Hex(paswd));
				tr.setValue("data_status",1);
				tr.setValue("public_id", publicidMap.getRow(0).get(0));
				//System.out.println("注册新用户!"+publicidMap.getRow(0).get(0));
				db.save(table);
			}
			return DatabaseAccess.query(findViewSQL);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 *@param request  HttpServletRequest
	 * @return  保存成功返回true，否则返回false
	 */
	private boolean saveMemberByMemberCode(HttpServletRequest request){
		Table table=new Table("wwd","WS_MEMBER");
		TableRow editRow=table.addUpdateRow();
		editRow.setOldValue("member_code", request.getParameter("member_code"));
		editRow.setValue("nickname",request.getParameter("nickname"));
		editRow.setValue("age", request.getParameter("age"));
		editRow.setValue("gender", request.getParameter("gender"));
		editRow.setValue("phone", request.getParameter("phone"));
		editRow.setValue("email", request.getParameter("email"));
		editRow.setValue("postal_code", request.getParameter("postal_code"));
		editRow.setValue("openid", request.getParameter("openid"));
		editRow.setValue("mem_name", request.getParameter("memname"));
		
		Map map=request.getParameterMap();
		String temp="";
		for(int i=1;i<9;i++){
			String key="hobby"+i;
			if(map.containsKey(key)){
				temp+=i+",";
			}
		}
		String hobby="";
		if(temp.length()>0){
			hobby=temp.substring(0, temp.length());
		}
		
		editRow.setValue("hobby",hobby);
		
		try {
			db.save(table);
		} catch (JDBCException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/***
	 * 根据用户OpenID获取用户关注的企业
	 * @param openid 微信OpenId
	 * @return 机构信息
	 */
	public MapList getOrgByOpenid(String openid){
		String orgByOpenidSQL="SELECT orgname,orgid FROM aorg WHERE orgid IN ( " +
				" SELECT detail.care_orgid FROM ws_member AS member" +
				" RIGHT JOIN ws_mbdeatil AS detail " +
				" ON member.member_code=detail.member_code " +
				" WHERE member.openid=? AND detail.data_status!=4 )";
		MapList map=null;
		try {
			map=db.query(orgByOpenidSQL,new String[]{openid},new int[]{Type.VARCHAR});
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/***
	 * 根据Token获取机构编号 
	 * @param token
	 * @return
	 */
	public MapList getOrgidByToken(String token){
		String sql="SELECT org.orgname,wpa.* FROM ws_public_accounts AS wpa " +
				" LEFT JOIN aorg AS org ON wpa.orgid=org.orgid " +
				" WHERE wpa.token='"+token+"' ";
		MapList map=null;
		try {
			map = db.query(sql);
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 根据机构ID获取机构名称
	 * @param orgid
	 * @return
	 */
	public String getOrgnameByOrgid(String orgid){
		String sql="SELECT orgname FROM aorg WHERE orgid='"+orgid+"'";
		new String(orgid.getBytes(),Charset.forName("UTF-8"));
		MapList map=null;
		try {
			map = db.query(sql);
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return map.getRow(0).get("orgname");
	}
	public String getMemberinfo(String openid){
		String getOrgidSQL="SELECT * FROM ws_member WHERE openid='"+openid+"'";
		return DatabaseAccess.query(getOrgidSQL).toString();
	}
	
	/**
	 *  根据OPENID和机构名称获取用户状态
	 * @param orgid
	 * @param openid
	 * @return
	 */
	public String getMemberStatus(String orgid,String openid){
		String result="1";
		try{
			String accType="SELECT account_belong FROM ws_public_accounts WHERE orgid='"+orgid+"' ";
			MapList map=db.query(accType);
			String statusSQL="";
			if(!Checker.isEmpty(map)&&"1".equals(map.getRow(0).get("account_belong"))){
				statusSQL="SELECT md.data_status FROM ws_member AS member RIGHT JOIN ws_mbdeatil AS md ON           "+
						  "	  member.member_code=md.member_code WHERE member.openid='"+openid+"'    "+
						  "   AND md.care_orgid='"+orgid+"'                                                                 ";

			}else{
				statusSQL="SELECT data_status FROM ws_member WHERE openid='"+openid+"'";
			}
			map=db.query(statusSQL);
			if(!Checker.isEmpty(map)){
				result=map.getRow(0).get("data_status");
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据openid获取membecode
	 * @param openid
	 * @return
	 */
	public String getMemberCodeByOpenid(String openid){
		String memberCode="";
		String sql="SELECT member_code FROM ws_member WHERE openid='"+openid+"' ";
		try {
			MapList map=db.query(sql);
			if(!Checker.isEmpty(map)){
				memberCode=map.getRow(0).get("member_code");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return memberCode;
	}
	/**
	 * 取消关注
	 * @param openid  客户openid
	 * @param orgid  取下关注机构orgid
	 * @return
	 */
	private JSONArray unscributeOrg(String openid, String orgid) {
		JSONArray result=new JSONArray();
		Table table=new Table("wwd","WS_MBDEATIL");
		String sql="SELECT detail.detail_code FROM ws_member AS member "+
				" LEFT JOIN ws_mbdeatil AS detail ON member.member_code=detail.member_code "+ 
				" WHERE member.openid='"+openid+"' AND member.data_status=1 "+ 
				" AND detail.care_orgid='"+orgid+"' ";
	
		try {
			MapList map= db.query(sql);
			if(!Checker.isEmpty(map)){
				TableRow updateTr=table.addUpdateRow();
				String detailCode=map.getRow(0).get("detail_code");
				updateTr.setOldValue("detail_code",detailCode);
				updateTr.setValue("data_status",4);
				db.save(table);
				result.put(0,new JSONObject("{\"RESULT\":\"1\"}"));
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 更新用户昵称 
	 * @param nickName 昵称
	 * @param token token
	 * @param openid openid
	 * @param flage  是否始终更新,当选择否是，只有在用户第一次注册是更新。
	 * 
	 */
	public void updateUserNickname(String token,String openid,String nickName,boolean flage){
		try{
			//查询会员是否存在SQL
			String checkSQL="SELECT * FROM ws_member AS wm LEFT JOIN             "+
					"	ws_public_accounts AS wpa ON wm.public_id=wpa.public_id  "+
					"	WHERE wpa.token='"+token+"'                                "+
					"	AND wm.openid='"+openid+"'   ";
			//更新昵称SQL
			String updateSQL="UPDATE ws_member SET nickname='"+nickName+"' " +
					" WHERE openid='"+openid+"' " +
					" AND public_id =(SELECT public_id FROM ws_public_accounts WHERE token='"+token+"')";
			
			MapList map=db.query(checkSQL);
			
			if(Checker.isEmpty(map)){
				registerUser(openid, token);
				db.execute(updateSQL);
			}else{
				if(flage){
					db.execute(updateSQL);
				}
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据CID获取企业详细信息
	 * @param parameter
	 * @return
	 */
	private JSONArray getNewsDetail(String nid) {
		JSONArray result=new JSONArray();
		try{
			String sql="SELECT c.cid,c.cinfo,d.title,d.content, "+
					"   to_char(d.release_date,'yyyy-MM-dd HH24:MI:ss') AS release_date "+
					"	FROM newscategory AS c RIGHT JOIN newsdetail AS d    "+
					"	ON c.cid=d.cid                                       "+
					"	WHERE  d.nid="+nid;
			List<String> images=Utils.getImageByTable("NEWSDETAIL", nid, false);
			result=DatabaseAccess.query(sql);
			if(images!=null&&images.size()>0){
				result.put(1,images.toString());
			}
		}catch(JSONException e){
			e.printStackTrace();
		}finally{
			return result;
		}
	}
	
	/**
	 * 加载更多信息
	 * @param parameter
	 * @param parameter2
	 * @return
	 */
	private JSONArray loadMoreInfo(String cid, String page,String orgid) {
		String sql="SELECT c.cid,c.cinfo,d.nid,d.title,substring(content,0,20) AS abstract "+
				"	FROM newscategory AS c RIGHT JOIN newsdetail AS d                     "+
				"	ON c.cid=d.cid                                                        "+
				"	WHERE d.datastatus=2 AND c.orgid='"+orgid+"'"+
				"   AND c.cid="+cid+"   ORDER BY d.createdate DESC "+
				"   LIMIT 10 OFFSET "+page+"*10 ";
		return DatabaseAccess.query(sql);
	}
	
	
	/**
	 * 微商店业务处理
	 * @param request
	 * @param response
	 */
	public void shopServer(HttpServletRequest request,HttpServletResponse response,String action)
			throws ServletException, IOException{
		out=new PrintWriter(response.getOutputStream());
		if("member".equals(action)){//会员页面请求
			String openid=request.getParameter("openid");
			String token=request.getParameter("token");
			String orgid=Utils.getOrgId(token);
			String findMemberSQL=
					"SELECT acc.*,member.*,member.data_status AS uStatus,org.orgname FROM ws_member AS member "+
							"         	LEFT JOIN ws_public_accounts AS acc             "+
							"         	ON acc.orgid=member.orgid                       "+
							"         	LEFT JOIN aorg AS org                           "+
							"         	ON org.orgid=acc.orgid                          "+
							"         	WHERE  member.openid='"+openid+"' 	            "+
							"         	AND acc.token='"+token+"'                       ";
			out.print(DatabaseAccess.query(findMemberSQL).toString());
		}else if("weshop".equals(action)){//商店请求
			
		}else if("getOrders".equals(action)){//订单处理
			JSONArray result=getOrdersByMemberCode(request.getParameter("membercode"));
			out.print(result.toString());
		}else if("getAddress".equals(action)){//地址处理
			JSONArray result=getAddressByMemberCode(request.getParameter("membercode"));
			out.print(result.toString());
		}else if("editMember".equals(action)){//保存用户数据
			if(saveMemberByMemberCode(request)){
				String url="/domain/wwd/weshop/member.jsp?openid="+
						request.getParameter("openid")+"&token="+request.getParameter("token");
				String orgidp=(String)request.getSession().getAttribute("CURRENT_ORG");
				if(orgidp!=null){
					url+="&orgidp="+orgidp;
				}
				response.sendRedirect(url);
			};
		}else if("loadProvinces".equals(action)){//获取省信息
			JSONArray result=loadProvinecs();
			out.print(result.toString());
		}else if("loadCityByProvinec".equals(action)){//获取市信息
			JSONArray result=loadCityByProvince(request.getParameter("provinceid"));
			out.print(result);
		}else if("loadZoneByCityId".equals(action)){//获取区信息
			JSONArray result=loadZoneByCityId(request.getParameter("cityId"));
			out.print(result);
		}else if("saveMemberAddr".equals(action)){//保存用户地址
			JSONArray result=saveMemberAddr(request);
			out.print(result);
		}else if("loadShopListView".equals(action)){//获取商品列表
			String orgid=request.getParameter("orgidp");
			JSONArray result=getShopListView(request.getParameter("PID"),orgid,request.getParameter("CURRENTPAGE"));
			if(orgid!=null){
				request.getSession().setAttribute(Constant.CURRENT_ORG,orgid);
				Utils.Log(tag,"绑定机构编号到Session中。"+orgid);
			}
			out.print(result);
		}else if("getComdityById".equals(action)){//根据商品ID获取商品信息
			JSONArray result=getComdityById(request.getParameter("CID"));
			out.print(result);
		}else if("submitOrder".equals(action)){//提交订单
			JSONArray result=submitOrder(request);
			out.print(result);
		}else if("loadShopClass".equals(action)){//加载商品分类
			JSONArray result=loadShopClass(request.getParameter("PID"),request.getParameter("PARENTID"),request.getParameter("orgidp"));
			out.print(result);
		}else if("loadShopListViewByClass".equals(action)){
			JSONArray result=loadShopListByShopClass(request.getParameter("PID"),request.getParameter("CID"));
			out.print(result);
		}else if("loadShopByPrice".equals(action)){//根据价格排序商品
			JSONArray result=loadShopByPrice(request.getParameter("PID"),request.getParameter("ORDERBY"),request.getParameter("orgidp"));
			out.print(result);
		}else if("loadShopByAmount".equals(action)){//根据订购量排序商品
			JSONArray result=loadShopByAmount(request.getParameter("PID"),request.getParameter("ORDERBY"),request.getParameter("orgidp"));
			out.print(result);
		}else if("registerUser".equals(action)){//注册用户
			JSONArray result=registerUser(request.getParameter("openid"),request.getParameter("token"));
			out.print(result);
		}else if("getOrgnameByToken".equals(action)){//获取机构名
			JSONArray result=getOrgnameByToken(request.getParameter("PID"),request.getParameter("orgidp"));
			out.print(result);
		}else if("getSearchShopList".equals(action)){
			JSONArray result=getSearchShopList(request.getParameter("KEY"),request.getParameter("TOKEN"),request.getParameter("orgidp"));
			out.print(result);
		}else if("unscribtOrg".equals(action)){//取消关注企业
			JSONArray result=unscributeOrg(request.getParameter("OPENID"),request.getParameter("ORGID"));
			out.print(result);
		}else if("getUserStatus".equals(action)){
			String result=getMemberStatus(request.getParameter("ORGIDP"), request.getParameter("OPENID"));
			Utils.Log(tag, action+" "+result);
			out.print(result);
		}else if("getNewsDetail".equals(action)){//根据CID获取企业详细信息
			JSONArray result=getNewsDetail(request.getParameter("NID"));
			Utils.Log(tag, action+" "+result);
			out.print(result);
		}else if("loadMoreInfo".equals(action)){
			JSONArray result=loadMoreInfo(request.getParameter("CID"),request.getParameter("PAGE"),request.getParameter("ORGID"));
			Utils.Log(tag, action+" "+result);
			out.print(result);
		}else {
			out.write("<h1>西安触角电子科技有限公司 </h1>");     
		}
		out.flush();
		out.close();
	}

}
