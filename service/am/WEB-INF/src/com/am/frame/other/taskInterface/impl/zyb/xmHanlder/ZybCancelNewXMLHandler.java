package com.am.frame.other.taskInterface.impl.zyb.xmHanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.fastunit.jdbc.DB;


/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * 取消订单XML处理类
 */
public class ZybCancelNewXMLHandler extends ZybSubmitProcessXMLHandler {
	
	/**订单取消 退单情况查询编号**/
	public static final String RETREAT_BATCH_NO="ZybCancelNewXMLHandler.RETREAT_BATCH_NO";
	
	public ZybCancelNewXMLHandler(DB db, String taskRecordId,
			String requestData, String responseData, long taskRetryTime,
			long maxRetryTime, String busseData) {
		
		super(db, taskRecordId, requestData, responseData, taskRetryTime, maxRetryTime, busseData);
	}

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		String tag = stack.peek();
		String value=new String(ch,start,length).trim();
		
		if("code".equals(tag)){
			if("0".equals(value)){
				logger.info("订单取消接口成功,订单ID:"+busseData);
				result="1";
			}else if(value!=null&&!"".equals(value.trim())
					&&!"1".equals(result)){
				logger.info("订单取消失败,订单ID:"+busseData);
				if(taskRetryTime<maxRetryTime){
					result="0";
				}else{
					result="3";
				}
			}
		}
		
		if("retreatBatchNo".equals(tag)){
			if(value!=null&&value.length()>1){
				logger.info("退单情况查询编号:"+value);
				extendData.put(RETREAT_BATCH_NO, value);
			}
		}
	}
	
}
