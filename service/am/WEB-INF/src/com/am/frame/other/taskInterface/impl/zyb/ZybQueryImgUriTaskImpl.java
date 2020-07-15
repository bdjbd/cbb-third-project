package com.am.frame.other.taskInterface.impl.zyb;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.xml.sax.InputSource;

import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.am.frame.other.taskInterface.impl.zyb.xmHanlder.ZybQueryImgUriXMLHandler;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 智游宝获取二维码任务
 * 
 */
public class ZybQueryImgUriTaskImpl extends ZybTaskImplAbstract{

	@Override
	public void execute(DB db, String taskRecordId) {
		try{
			
			MapList map=getTaskInfoMap(db,taskRecordId);
			
			if(!Checker.isEmpty(map)){
				
					//我们的订单号
					requestTemplate=requestTemplate.replaceAll("\\$am\\{orderCode\\}",busseData);
					
					logger.info("请求参数："+requestTemplate);
					
					String sign=DigestUtils.md5Hex("xmlMsg="+requestTemplate+key);
					
					String responseData=doPost(requestUrl,requestTemplate,sign);

					logger.info(responseData);
					
					taskRetryTime++;
					updateResponseData(db,taskRecordId,
							requestTemplate,responseData,taskRetryTime,
							maxRetryTime,busseData);
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	private void updateResponseData(final DB db,final String taskRecordId,final String requestData,
			final String responseData,final long taskRetryTime,final long maxRetryTime,final String busseData) throws Exception {
		
		ZybQueryImgUriXMLHandler hander=new ZybQueryImgUriXMLHandler(
				 db, taskRecordId, requestData, responseData, taskRetryTime,
				 maxRetryTime,busseData);
		
		hander.setCallBack(new IProcessZybCallBack() {
			

			@Override
			public String process(DB db, String taskRecordId,
					String requestData, String responseData,
					long taskRetryTime, long maxRetryTime, String busseData,
					String result, Map<String, String> extendData) {
				
				try {
					//处理记录
					Table recordTable=new Table(OtherInterfaceTaskManager.DOMAIN, "am_other_task_record");
					TableRow updateRow=recordTable.addUpdateRow();
					
					updateRow.setOldValue("id", taskRecordId);
					updateRow.setValue("request_data", requestData);
					updateRow.setValue("response_data", responseData);
					updateRow.setValue("task_tate", result);
					updateRow.setValue("task_retry_time", taskRetryTime);
				
					db.save(recordTable);
					
					//处理业务
					//1,如果成功，更新业务状态，如果失败，不处理
					if("1".equals(result)){
						//获取二维码成功
						String updateSQL="UPDATE mall_MemberOrder SET "
								+ " query_img_url=? WHERE id=? ";
						
						db.execute(updateSQL,new String[]{
								extendData.get(ZybQueryImgUriXMLHandler.IMG),
								busseData
						},new int[]{
								Type.VARCHAR,Type.VARCHAR
						});
						
						logger.info("订单："+busseData+"\td二维码地址为："+extendData.get(ZybQueryImgUriXMLHandler.IMG));
						
						//发送短信
						//1，酒店类订单，在订单执支付，到带使用状态时，发送短信
						OrderManager orderManager=new OrderManager();
						orderManager.notifyClient(db,busseData);
						
					}
					
				} catch (JDBCException e) {
					e.printStackTrace();
				}
				
				
				return null;
			}
		});
		
		 //创建一个新的字符串  
		SAXParserFactory factory = SAXParserFactory.newInstance();
		  // step 2: 获得SAX解析器实例
        SAXParser parser = factory.newSAXParser();
        // step 3: 开始进行解析
        // 传入待解析的文档的处理器
        StringReader sr = new StringReader(responseData);  
        InputSource is = new InputSource(sr);  
        parser.parse( is,hander);
        
	}

}
