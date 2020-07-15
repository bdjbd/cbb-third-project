package com.am.frame.task.taskParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.am.frame.task.IUserTask;


/**
 * 任务参数集合<br>
 * 保存任务解析参数<br>
 * 保存格式：Key=Value 获取，可以通过Key获取Value，也可以通过index获取对于的ke或者value
 * 
 * @author Administrator
 * 
 */
public class TaskParameterSet {
	/** 参数Map集合 **/
	private Map<String, String> paramerets = new HashMap<String, String>();
	private List<String> paraNames = new ArrayList<String>();
	private List<String> paraValues = new ArrayList<String>();

	/**
	 * 添加参数
	 * @param name
	 *            参数名称
	 * @param value
	 *            参数值
	 * @pdOid d1d318f5-a411-4ec2-8a06-0a9f1749a97d
	 */
	public void add(String name, String value) {
		if (paramerets.put(name, value) == null) {
			paraNames.add(name);
			paraValues.add(value);
		} else {
			int index = paraNames.indexOf(name);
			paraValues.set(index, value);
		}
	}

	/**
	 * 通过参数名称获得参数值
	 * 
	 * @param name
	 *            参数名称
	 * @pdOid 7d66cd7b-2555-476e-9c54-019cafa9cdaa
	 */
	public String getValueOfName(String name) {
		return paramerets.get(name);
	}

	/**
	 * 通过索引获得参数值
	 * 
	 * @param index
	 * @pdOid ebf42173-504e-43d1-909b-bd0d15c63209
	 */
	public String getValue(int index) {
		return paraValues.get(index);
	}

	/**
	 * 通过索引获得参数名称
	 * 
	 * @param index
	 * @pdOid 9b5a1521-508a-43bb-b238-efcbdae5d6a3
	 */
	public String getName(int index) {
		return paraNames.get(index);
	}

	/**
	 * 获得总参数个数
	 * 
	 * @pdOid 140309a9-1980-483c-a897-975171f477eb
	 */
	public int size() {
		return paramerets.size();
	}

	@Override
	public String toString() {
		String str = "";
		for (Map.Entry<String, String> entry : this.paramerets.entrySet()) {
			str += entry.getKey() + "=" + entry.getValue() +IUserTask.TASK_PARAMTERE_DECOLLATOR;
		}
		if (str.length() > 1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		paramerets.clear();
		paraNames.clear();
		paraValues.clear();
		
	}
}
