package com.am.mall.beans.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月17日
 * @version 
 * 说明:<br />
 * 流程状态定义参数类
 * 格式 KEY1=value1;Key2=value2;
 */
public class ParamList {
	
	/** 参数集合 **/
	private Map<String, String> params = new HashMap<String, String>();
	private List<String> listParams = new ArrayList<String>();
	private List<String> listNameParams = new ArrayList<String>();

	/**
	 * 添加参数
	 * 
	 * @param name
	 *            参数名称
	 * @param value
	 *            参数值
	 */
	public void add(String name, String value) {
		params.put(name, value);
		listParams.add(value);
		listNameParams.add(name);
	}

	/**
	 * 通过参数名称获得参数值
	 * 
	 * @param name
	 *            参数名称
	 */
	public String getValueOfName(String name) {
		return params.get(name);
	}

	/**
	 * 通过索引获得参数值
	 * 
	 * @param index
	 */
	public String getValueOfIndex(int index) {
		return listParams.get(index);
	}

	/**
	 * 通过索引获得参数名称
	 * 
	 * @param index
	 */
	public String getName(int index) {
		return listNameParams.get(index);
	}

	/**
	 * 获得总参数个数
	 * 
	 */
	public int getCoutnt() {
		return params.size();
	}

	@Override
	public String toString() {
		String str="";
		for(Map.Entry<String, String> entry:this.params.entrySet()){
			str+=entry.getKey()+"="+entry.getValue()+";";
		}
		if(str.length()>1){
			str=str.substring(0, str.length()-1);
		}
		return str;
	}
	
	
	/**
	 * 解析一个 key1=value1;key2=value2;key3=value3;
	 * 的字符串格式为ParamList实例
	 * @param nativeParams
	 */
	public void parse(String nativeParams){
		if(!Checker.isEmpty(nativeParams)){
			String[] params=nativeParams.split(";");
			
			for(String kev:params){
				String[] kv=kev.split("=");
				this.add(kv[0],kv[1]);
			}
		}
	}
}
