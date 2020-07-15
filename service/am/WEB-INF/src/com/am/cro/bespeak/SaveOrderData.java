package com.am.cro.bespeak;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.sdk.sms.SendTemplateContent;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 保存预约数据
 * 
 * @author guorenjie
 *
 */
public class SaveOrderData implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DBManager db = new DBManager();
		// 0=失败；1=接车预约，2=普通预约,3=优惠预约
		JSONObject rValue = new JSONObject();
		String rCode = "";
		String rMsg = "";

		// 获得参数
		String id = request.getParameter("id");// 主键ID
		String orgcode = request.getParameter("orgcode");// 所属机构
		String memberid = request.getParameter("memberid");// 会员ID
		String orderstate = request.getParameter("orderstate");// 订单状态 0 待分配
		String createdate = request.getParameter("createdate");// 创建时间
		String orderdate = request.getParameter("orderdate");// 预约日期
		String ordertime = request.getParameter("ordertime");// 预约时间
		String contacts = request.getParameter("contacts");// 联系人
		String contactsphone = request.getParameter("contactsphone");// 联系电话
		String carplatenumber = request.getParameter("carplatenumber");// 车牌号
		String carframenumber = request.getParameter("carframenumber");// 车架号
		String carenginenumber = request.getParameter("carenginenumber");// 发动机号
		String attachment = request.getParameter("attachment");// 附件
		String paystate = request.getParameter("paystate");// 支付状态
		String discountmoney = request.getParameter("discountmoney");// 优惠金额
		String totalmoney = request.getParameter("totalmoney");// 订单金额
		String orderexplain = request.getParameter("orderexplain");// 订单说明
		String repair_class = request.getParameter("repair_class");// 预约类型
		String is_shuttle_service = request.getParameter("is_shuttle_service");// 是否需要接送车服务
		String discountid = request.getParameter("discountid");// 优惠ID
		
		//根据memberid查询会员所享受的折扣
		String queryDiscount = "select discount from cro_MemberLevel where id = (select member_level_id from am_member where id = '"+memberid+"')";
		double orderDiscount =0;//订单折扣
		double amountPaid = 0;//实付金额
		if(Checker.isEmpty(db.query(queryDiscount))){
			amountPaid = Double.parseDouble(totalmoney);
		}else{
			orderDiscount = db.query(queryDiscount).getRow(0).getDouble(0, 0);
			if(orderDiscount==0){
				amountPaid = Double.parseDouble(totalmoney);
			}else{
				amountPaid = Double.parseDouble(totalmoney)*orderDiscount;
			}
			
		}
		//查询车牌号是否存在
		String checkExists = "SELECT * FROM cro_CarManager where carplatenumber ='"+carplatenumber+"' and orgcode ='"+orgcode+"'";
		
		MapList carMapList= db.query(checkExists);
		
		// 查询车辆的预约预约状态
		String checkCarStateSql = "SELECT car_state FROM cro_CarManager WHERE 1=1"
				+ " and carplatenumber = '"
				+ carplatenumber
				+ "' "
				+ " and orgcode = '"
				+ orgcode
				+ "'"
				+ " and memberid = '"
				+ memberid + "'";
		
		MapList carStatemMapList = db.query(checkCarStateSql);
		String car_state = "";
		if (carStatemMapList.size() > 0) {
			car_state = carStatemMapList.getRow(0).get("car_state");
		}
		
		//查询预约时间是否已满
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int weekindex = 0;
		try {
			weekindex = dateFormat.parse(orderdate).getDay();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String checkBooktimesql = "select booktime,maxbooknum from cro_bookingmanagement where "
				+ "orgcode = '"
				+ orgcode
				+ "' "
				+ "and weekday = '"
				+ weekindex
				+ "' "
				+ "and booktime = '"
				+ ordertime
				+ "' "
				+ "and maxbooknum>(select count(id) from cro_carrepairorder where orgcode='"
				+ orgcode
				+ "' "
				+ "and ordertime='"
				+ ordertime
				+ "'"
				+ "and orderdate = '"
				+ orderdate
				+ "') "
				+ "and to_timestamp('"
				+ orderdate
				+ " "
				+ ordertime
				+ "', 'yyyy-mm-dd hh24:mi:ss')>now()";
		MapList booktimeMapList = db.query(checkBooktimesql);
		
		// 保存数据之前检查车辆是否已预约、当前时间预约是否已满
		if (car_state.equals("2")) {
			rCode = "0";
			rMsg = "车辆已经预约，请重新选择";
		} else if (booktimeMapList.size()==0) {
			rCode = "0";
			rMsg = "预约已满，请重新选择预约时间";
		}else if (carMapList.size()==0) {
			rCode = "0";
			rMsg = "车牌号不存在，请重新选择或填写车牌号";
		}  else {

			// 保存预约单
			// 拼接保存数据的insert语句
			String tSql = "insert into cro_CarRepairOrder (" + "id"
					+ ",orgcode" + ",memberid" + ",orderstate" + ",createdate"
					+ ",orderdate" + ",ordertime" + ",contacts"
					+ ",contactsphone" + ",carplatenumber" + ",carframenumber"
					+ ",carenginenumber" + ",attachment" + ",paystate"
					+ ",discountmoney" + ",totalmoney" + ",orderexplain"
					+ ",repair_class" + ",is_shuttle_service" + ",orderdiscount" + ",amountPaid) values "
					+ "(" + "'"
					+ id
					+ "',"
					+ "'"
					+ orgcode
					+ "',"
					+ "'"
					+ memberid
					+ "',"
					+ "'"
					+ orderstate
					+ "',"
					+ "'"
					+ createdate
					+ "',"
					+ "'"
					+ orderdate
					+ "',"
					+ "'"
					+ ordertime
					+ "',"
					+ "'"
					+ contacts
					+ "',"
					+ "'"
					+ contactsphone
					+ "',"
					+ "'"
					+ carplatenumber
					+ "',"
					+ "'"
					+ carframenumber
					+ "',"
					+ "'"
					+ carenginenumber
					+ "',"
					+ "'"
					+ attachment
					+ "',"
					+ "'"
					+ paystate
					+ "',"
					+ ""
					+ discountmoney
					+ ","
					+ ""
					+ totalmoney
					+ ","
					+ "'"
					+ orderexplain
					+ "',"
					+ "'"
					+ repair_class
					+ "',"
					+ "'"
					+ is_shuttle_service
					+ "',"
					+ orderDiscount
					+","
					+amountPaid
					+ ")";
			//判断是否预约成功及预约类型
			JSONObject resultJson = db.update(tSql);
			try {
				if (resultJson.get("CODE").equals("0")) {
					if (is_shuttle_service.equals("1")) {
						rCode = "1";
						rMsg = "需要接车服务";
					} else if (!discountid.equals("")) {
						rCode = "3";
						rMsg = "优惠预约";
					} else {
						rCode = "2";
						rMsg = "预约成功";
					}
				} else {
					rCode = "0";
					rMsg = "预约失败";
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		

		// 设置车辆状态为预约中
		String updateCarStateSql = "update cro_CarManager set car_state = '2' where orgcode='"
				+ orgcode
				+ "' and memberid = '"
				+ memberid
				+ "' and carplatenumber='" + carplatenumber + "'";
		db.update(updateCarStateSql);

		// 发送短信
		// 1、获得当前机构的通知号码
		String getNotification_callSql = "select notification_call from aorg where orgid='"
				+ orgcode + "'";
		MapList qycMapList = db.query(getNotification_callSql);

		String notification_call = "";
		if (qycMapList.size() > 0) {
			notification_call = qycMapList.getRow(0).get("notification_call");
		}

		// 2、调用短信类，发送短信
		// {'code':'SMS_80840010',content:{'CODE':'[CODE]'}}
		SendTemplateContent stc = new SendTemplateContent("NOTESMS_SEND");

		// 用来替换短信变量的maplist
		String sql = "select * from cro_carrepairorder where id = '" + id + "'";
		MapList mapList = db.query(sql);
		if (is_shuttle_service.equals("0")) {
			is_shuttle_service = "预约单";
		} else {
			is_shuttle_service = "接送车服务预约单";
		}
		mapList.getRow(0).put("is_shuttle_service", is_shuttle_service);
		mapList.getRow(0).put("orderdate", orderdate.substring(0, 10));

		stc.send(notification_call, mapList);
		}
		try {
			rValue.put("code", rCode);
			rValue.put("msg", rMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rValue.toString();
	}

}
