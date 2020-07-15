package com.cdms;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.utils.HttpRequest;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;

/**
 * 违章查询类
 * @author guorenjie
 *
 */
public class IllegalInquiry {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	public static String date=null;
	private static IllegalInquiry instance;
	private IllegalInquiry() {}
	public static IllegalInquiry getInstance(){
		if (instance == null) {  
	        instance = new IllegalInquiry();  
	    }
	      return instance;
	   }
	public void illegal(){
		DBManager db = new DBManager();
		int count = Var.getInt("retry_count", 0);//违章请求失败重试次数
		JSONObject resultJson = null;
		
		String sql = "select id,device_sn_number,license_plate_number,frame_number,engine_number,mobile from cdms_vehiclebasicinformation";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			for (int i = 0; i < mapList.size(); i++) {
				String id = "";
				String device_sn_number = "";
				String license_plate_number = "";//车牌号
				String frame_number = "";//车架号
				String engine_number = "";//发动机号
				String mobile = "";//车主手机
				
				id = mapList.getRow(i).get("id");
				device_sn_number = mapList.getRow(i).get("device_sn_number");
				license_plate_number = mapList.getRow(i).get("license_plate_number");//车牌号
				if(!Checker.isEmpty(mapList.getRow(i).get("frame_number"))){
					frame_number = mapList.getRow(i).get("frame_number");//车架号
				}
				if(!Checker.isEmpty(mapList.getRow(i).get("engine_number"))){
					engine_number = mapList.getRow(i).get("engine_number");//发动机号
				}
				if(!Checker.isEmpty(mapList.getRow(i).get("mobile"))){
					mobile = mapList.getRow(i).get("mobile");//车主手机
				}
				if(!"".equals(frame_number)||!"".equals(engine_number)){
					int frequency = count;//重试次数
					//请求异常，重新请求
					while (frequency>0) {
						String result = requestApi(id,device_sn_number,license_plate_number, frame_number, engine_number,mobile);
						resultJson = new JSONObject(result);
						String code = resultJson.getString("code");
						String msg = resultJson.getString("msg");
						//更新车辆基础信息表中的违章查询结果（illegal_code、illegal_msg字段）
						String updateSql = "update cdms_vehiclebasicinformation set illegal_code='"+code+"',illegal_msg='"+msg+"' where device_sn_number='"+device_sn_number+"'";
						db.execute(updateSql);
						//请求异常，重新请求
						if("接口请求异常".equals(msg)){
							frequency--;
						}else {
							frequency=0;
							break;
						}
					}
				
				}else {
					//车架号、发动机号均无值则不调用接口，并设置获取违章结果信息字段为"车架号或发动机号数据不完整"
					String updateSql = "update cdms_vehiclebasicinformation set illegal_code='3',illegal_msg='车架号或发动机号数据不完整' where device_sn_number='"+device_sn_number+"'";
					db.execute(updateSql);
				}
				
				
			}
			
		}
	}
	
	public String requestApi(String car_id,String device_sn_number,String license_plate_number,String frame_number,String engine_number,String mobile){
		JSONObject rValue = new JSONObject();
		String url = Var.get("illegal_interface");//获取变量中配置的违章接口地址
		//替换变量中的车牌号，车架号，发动机号参数
		try {
			url = url.replace("[isprefix]", URLEncoder.encode(license_plate_number.substring(0, 1), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url = url.replace("[isnum]", license_plate_number.substring(1, license_plate_number.length()));
		url = url.replace("[frameno]", frame_number);
		url = url.replace("[engineno]", engine_number);
		url = url.replace("[mobile]", mobile);
		logger.info("违章接口url==="+url);
		String result = HttpRequest.sendGet(url, "");
		logger.info("违章接口返回值==="+result);
		if(Checker.isEmpty(result)){
			rValue.put("code","1");
        	rValue.put("msg","接口请求异常");
		}else {	
	        JSONObject json = new JSONObject(result);
	        if (json.getInt("status") != 0) {//请求失败
	        	rValue.put("code","1");
	        	rValue.put("msg",json.get("msg"));
	        } else {//请求成功
	            JSONObject resultarr = json.optJSONObject("result");
	            if (resultarr != null) {
	                if (resultarr.opt("list") != null) {
	                    JSONArray list = resultarr.optJSONArray("list");
	                    for (int j = 0; j < list.length(); j++) {
	                        JSONObject list_obj = (JSONObject) list.opt(j);
	                        if (list_obj != null) {
	                            String time = list_obj.getString("time");//时间
	                            String address = list_obj.getString("address");//地点
	                            String content = list_obj.getString("content");//违章内容
	                            String id = list_obj.getString("illegalid"); //违章ID（如果数据库已存在，则不insert）
	                            String score = list_obj.getString("score"); //扣分
	                            String price = list_obj.getString("price"); //罚款
	                            String agency = list_obj.getString("agency"); //处理部门
	                            saveDB(car_id,device_sn_number,id,time, address, content, agency,score,price);
	                            rValue.put("code","2");
	                        	rValue.put("msg","获取车辆违章信息成功");
	                        }
	                    }
	                }else{
	                	rValue.put("code","2");
		            	rValue.put("msg","获取车辆违章信息成功,没有违章");
	                }
	            } else {
	            	rValue.put("code","2");
	            	rValue.put("msg","获取车辆违章信息成功,没有违章");
	            }
	        }
		}
        return rValue.toString();
	}
	/**
	 * 保存违章数据到违章表中
	 * @param id	违章编号
	 * @param time	违章时间
	 * @param address	违章地址
	 * @param content	违章内容
	 * @param agency	处理部门
	 * @param content	扣分
	 * @param agency	罚款
	 */
	public void saveDB(String car_id,String device_sn_number,String id,String time,String address,String content,String agency,String score,String price){
		DBManager db = new DBManager();
		//检查数据库违章ID是否存在
		String checkSql = "select illegal_id from cdms_vehicleviolationrecord where illegal_id='"+id+"'";
		MapList list = db.query(checkSql);
		if(!Checker.isEmpty(list)){
			logger.info("违章ID已存在，不需要再次保存");
		}else {
			String member_id = "";
			// 检查数据库是否存在cdms_vcd_terminalId这张表
			String checkTableExistSql = "select concat('1',(select to_regclass('cdms_vcd_"
					+ device_sn_number + "') is not null)) as result";
			String isTableExist = db.query(checkTableExistSql).getRow(0).get(0);
			// 如果存在则查询
			if (!"1f".equals(isTableExist)) {
				//对接违章接口时应搜索车辆位置运行信息中该时间的3分钟内的车辆绑定人id
				String sql = "select member_id from cdms_vcd_"+device_sn_number+" where positioning_time>=(to_timestamp('"+time+"','YYYY-MM-DD HH24:MI:SS')- interval '1 D') and positioning_time<=(to_timestamp('"+time+"','YYYY-MM-DD HH24:MI:SS')+ interval '1 D')";
				MapList mapList = db.query(sql);
				if(!Checker.isEmpty(mapList)){
					if(!Checker.isEmpty(mapList.getRow(0).get(0))){
						member_id = mapList.getRow(0).get(0);
					}
				}
			}
			//insert id,car_id,time,content,agency,member_id,score,price
			String insertSql = "insert into cdms_vehicleviolationrecord(id,car_id,illegal_time,illegal_content,broadband_access_solutions,member_id,score,price,address,illegal_id) values ("
					+"'"+UUID.randomUUID().toString()+"'"
					+",'"+car_id+"'"
					+",'"+time+"'"
					+",'"+content+"'"
					+",'"+agency+"'"
					+",'"+member_id+"'"
					+",'"+score+"'"
					+",'"+price+"'"
					+",'"+address+"'"
					+",'"+id+"'"
					+ ")";
			db.execute(insertSql);
		}
		
	}
}
