package com.am.frame.other.taskInterface.impl.zyb.xmHanlder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 智游宝获取二维码XML解析类
 */
public class ZybQueryImgUriXMLHandler extends ZybSubmitProcessXMLHandler {
	
	public ZybQueryImgUriXMLHandler(DB db, String taskRecordId,
			String requestData, String responseData, long taskRetryTime,
			long maxRetryTime, String busseData) {
		
		super(db, taskRecordId, requestData, responseData, taskRetryTime, maxRetryTime,
				busseData);
	}


	/** 智游宝获取二维码地址 key**/
	public final static String IMG="ZybQueryImgUriXMLHandler.IMG";
	
	

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		String tag = stack.peek();
		String value=new String(ch,start,length).trim();
		
		if("code".equals(tag)){
			if("0".equals(value)){
				logger.info("订单下单接口成功,订单ID:"+busseData);
				result="1";
			}else if(value!=null&&!"".equals(value.trim())
					&&!"1".equals(result)){
				logger.info("下单失败,订单ID:"+busseData);
				if(taskRetryTime<maxRetryTime){
					result="0";
				}else{
					result="3";
				}
			}
		}
		
		if("img".equals(tag)){
			if(value!=null&&value.length()>1){
				logger.info("获取二维码地址信息:"+value);
				extendData.put(IMG, value);
			}
		}
	}
	
}
