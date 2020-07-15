package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * @author  wz  
 * @descriptions 前后置处理器传递参数
 * @date 创建时间：2016年3月24日 下午3:40:47 
 * @version 1.0   
 */
public class ActionParame {
	
	private String className = "";
	
	private HttpServletRequest request = null;
	
	private HttpServletResponse response = null;
	
	public ActionParame(String classNames,HttpServletRequest requests,HttpServletResponse responses){
		 this.className =	classNames;
		 this.request = requests ;
		 this.response = responses;
	}
	
    public void setParame(String name,String value){
    	
           request.getSession().setAttribute(className+"_"+name, value);
    }

    public String getParame(String name){
    	
    	return request.getSession().getAttribute(className+"_"+name).toString();
    }

}
