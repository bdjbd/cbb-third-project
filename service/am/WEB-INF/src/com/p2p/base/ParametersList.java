package com.p2p.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Mike 2014年7月16日 说明：参数类表对象
 * 
 **/
public class ParametersList {
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
	 * @pdOid d1d318f5-a411-4ec2-8a06-0a9f1749a97d
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
	 * @pdOid 7d66cd7b-2555-476e-9c54-019cafa9cdaa
	 */
	public String getValueOfName(String name) {
		return params.get(name);
	}

	/**
	 * 通过索引获得参数值
	 * 
	 * @param index
	 * @pdOid ebf42173-504e-43d1-909b-bd0d15c63209
	 */
	public String getValueOfIndex(int index) {
		return listParams.get(index);
	}

	/**
	 * 通过索引获得参数名称
	 * 
	 * @param index
	 * @pdOid 9b5a1521-508a-43bb-b238-efcbdae5d6a3
	 */
	public String getName(int index) {
		return listNameParams.get(index);
	}

	/**
	 * 获得总参数个数
	 * 
	 * @pdOid 140309a9-1980-483c-a897-975171f477eb
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
}
