package com.p2p.tools.sms;


import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;




/**
 * 宅可返回结果处理类
 * */
public class SmsResultManager4ZK implements ISmsResultManager{

	private HashMap ResultList=new HashMap();
	private String mMessageString="";
	
	@Override
	public void init(String Message) {
		//宅可返回结果是xml格式，使用dom解析xml
		Document doc = null;
		try{
			doc = DocumentHelper.parseText(Message);
			Document rootElt = doc.getDocument();
			Element ele = rootElt.getRootElement();
			
			//采用广度优先算法，获取子节点
			List<Element> list  =  ele.elements();
			for(Element element:list){
				ResultList.put(element.getName(), element.getData());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String getMessageString() {
		
		return mMessageString;
	}

	@Override
	public String getValue(String name) {
		
		String rValue=ResultList.get(name).toString();
		
		return rValue;
	}
	
	
}
