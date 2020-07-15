package com.am.frame.other.taskInterface.impl.zyb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.other.taskInterface.ITaskOtherInterface;
import com.am.frame.util.GetThirdPartyConfig;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 智游宝接口处理抽象类 
 * 
 */
public abstract class ZybTaskImplAbstract implements ITaskOtherInterface {

	protected Logger logger =LoggerFactory.getLogger(getClass());
	//请求地址
	protected String requestUrl;
	//私钥
	protected String key;
	//结构配置信息
	protected JSONObject configData;
	
	//业务数据
	protected String busseData;
	//部分退票数量
	protected String return_num;
	// 退单ID
	protected String thirdReturnCode;
	// IDCARD
	protected String idCards;
	
	//任务当前重试次数
	protected long taskRetryTime;//
	//订单最大重试次数 
	protected long maxRetryTime;
	
	//请求参数模板
	protected String requestTemplate;
	
	
	/**
	 * 获取任务记录集任务信息 并且初始化requestUrl和key
	 * @param db DB
	 * @param taskRecordId  任务记录ID
	 * @return
	 * @throws JDBCException
	 */
	protected MapList getTaskInfoMap(DB db ,String taskRecordId) throws Exception {

		MapList map=null;
		
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("SELECT impl.task_code,impl.task_name,impl.class_path,impl.request_template,  ");
		querySQL.append("	rec.task_tate,request_data,busse_data,impl.other_config_data,rec.task_retry_time, ");
		querySQL.append("   rec.max_retry_time,rec.busse_data ");
		querySQL.append("	FROM am_other_task_record AS rec                                           ");
		querySQL.append("	LEFT JOIN am_other_task_impl AS impl ON rec.other_task_impl_code=impl.id   ");
		querySQL.append("	WHERE rec.access_direction=1 AND rec.task_tate=0                           ");
		querySQL.append("	AND rec.id=?                                                               ");
		
		map=db.query(querySQL.toString(),taskRecordId, Type.VARCHAR);
		
		
		if(!Checker.isEmpty(map)){
			Row  dataRow=map.getRow(0);
			
			//请求参数解析
//			configData=new JSONObject(dataRow.get("other_config_data"));
			configData=new JSONObject(GetThirdPartyConfig.getIstance().getZybIntefaceParame(dataRow.get("other_config_data")));
			
			//请求参数解析
			requestUrl=configData.getString("hostUrl");
			key=configData.getString("key");
			
			//智游宝下单业务总，业务数据保存的为订单ID
			//订单请求模板数据
			requestTemplate=dataRow.get("request_template");
			requestTemplate=StringEscapeUtils.unescapeHtml4(requestTemplate);
			
			requestTemplate = requestTemplate.replaceAll("\\$am\\{userName\\}", GetThirdPartyConfig.getIstance().getZybuserName());
			
			//请求时间
			requestTemplate=requestTemplate.replaceAll("\\$am\\{requestTime\\}",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			logger.info(requestTemplate);
			//任务当前重试次数
			taskRetryTime=dataRow.getLong("task_retry_time", 0);
			//订单最大重试次数 
			maxRetryTime=dataRow.getLong("max_retry_time", 0); 
			//业务数据
			busseData=dataRow.get("busse_data");
			
			// 获取退票数量
			return_num = getReturn_num(busseData,db);
			// 获取退单表ID
			thirdReturnCode = getReturn_table_id(busseData,db);
			// IDCARD
			idCards = getIdCard(busseData,db);
			
		}
		return map;
	}
	
	
	
	public String nullRepalce(String value){
		if(value==null){
			return "";
		}else{
			return value;
		}
	}

	
	public String doPost(String hostUrl,String xmlMsg,String sign){  
        String result = "";  
        HttpPost httpRequst = new HttpPost(hostUrl);//创建HttpPost对象  
          
        List <NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair("xmlMsg", xmlMsg));  
        params.add(new BasicNameValuePair("sign", sign));  
          
        try {  
            httpRequst.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);  
            if(httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);//取出应答字符串  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            result = e.getMessage().toString();  
        } 
        
        return result;  
    }  
	
	public  String getReturn_num(String id,DB db){
		
		String sql = "select return_num from mall_memberorder where id = '" +id+ "'";
		
		MapList map = null;
		
		String num = null;
		try {
			map = db.query(sql);
			
			if(!Checker.isEmpty(map)){
				num = map.getRow(0).get("return_num");
			}
			
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return num;
	}
	
	public  String getReturn_table_id(String id,DB db){
		
		String sql = "select id from mall_refund where orderid = '" +id+ "'";
		
		MapList map = null;
		String refund_id = null;
		
		try {
			map = db.query(sql);
			
			if(!Checker.isEmpty(map)){
				refund_id = map.getRow(0).get("id");
			}
			
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return refund_id;
	}
	
	public  String getIdCard(String id,DB db){
		
		String sql = "select identitycardnumber from mall_memberorder as mm,am_member as am where mm.memberid=am.id and mm.id = '" +id+ "'";
		
		MapList map = null;
		String identitycardnumber = null;
		
		try {
			map = db.query(sql);
			
			if(!Checker.isEmpty(map)){
				identitycardnumber = map.getRow(0).get("identitycardnumber");
			}
			
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return identitycardnumber;
	}
}
