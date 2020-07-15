package com.am.frame.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 参数集合：如参数：<br /> 
 * key1=value1;key2=value2;key3=value3;
 * 通过此类解析后就可通过Key1来获取到value1的值了
 */
public class ParamList {
	
	/** 参数集合 **/
	private Map<String, String> params = new HashMap<String, String>();
	private List<String> listValueParams = new ArrayList<String>();
	private List<String> listNameParams = new ArrayList<String>();

	/**
	 * 添加参数 ,将参数安全key=value的方式添加到集合中
	 * @param name 参数名称
	 * @param value 参数值
	 */
	public void add(String name, String value) {
		params.put(name, value);
		listValueParams.add(value);
		listNameParams.add(name);
	}

	/**
	 * 通过参数名称获得参数值
	 * @param name参数名称
	 * @return 参数名对应的参数值
	 */
	public String getValueOfName(String name) {
		return params.get(name);
	}

	/**
	 * 通过索引获得参数值
	 * @param index
	 * @return 索引对应的参数值
	 */
	public String getValueOfIndex(int index) {
		return listValueParams.get(index);
	}

	/**
	 * 通过索引获得参数名称
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

	
	/**
	 * toString方法将参数集合还原到
	 * key1=value1;key2=value2;key3=value3;格式
	 */
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
	 * 解析参数字符串到此类中，字符格式应该为
	 * key1=value1;key2=value2;key3=value3;
	 * @param paramStr
	 */
	public void parse(String paramStr){
		
		if (paramStr != null&&paramStr.trim().length()>1){
			
			String[] params = paramStr.split(";");
			
			for(int i=0;i<params.length;i++)
			{
				String[] kvs = params[i].split("=");
				this.add(kvs[0], kvs[1]);
			}
		}
	}
}
