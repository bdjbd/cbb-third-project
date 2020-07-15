package com.fastunit.demo.list;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * @author 张少飞  2017/7/26
 *  fastUnit练习  从列表单元获取选中的主键值
 */
public class ListSelectAction extends DefaultAction {

	protected static final String KEY = "com.fastunit.demo.list.select";  //当前域编号：demo  当前单元编号：list.select

	@Override
	public void doAction(DB db, ActionContext ac) {
		// 获得列表所有行: _s_单元编号    当前单元编号：list.select
		String[] list = ac.getRequestParameters("_s_list.select");
		// 获得列表所有主键: 单元编号.主键元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] bookIds = ac.getRequestParameters("list.select.bookid.k");
		// 筛选选中的id
		List<String> selectedBookIds = new ArrayList<String>();
		if (!Checker.isEmpty(list)) {  //保证列表有值，不是空列表
			for (int i = 0; i < list.length; i++) {
				if ("1".equals(list[i])) {// '1'为选中,'0'为未选中
					selectedBookIds.add(bookIds[i]);
				}
			}
		}
		// 设置Session供ListSelectUI设置页面选中状态
		ac.setSessionAttribute(KEY, selectedBookIds);
		
		// 设置提示消息
		StringBuffer msg = new StringBuffer();
		if (Checker.isEmpty(selectedBookIds)) {
			msg.append("没有选择数据");
		} else {
			msg.append("选择的数据为：");
			for (int i = 0; i < selectedBookIds.size(); i++) {
				msg.append(selectedBookIds.get(i));
				//当 i+1=selectedBookIds.size() 时，提示信息中不再拼接逗号
				if (i < selectedBookIds.size() - 1) {
					msg.append(",");
				}
			}
		}
		//addSuccessMessage 提示信息颜色定为绿色
		ac.getActionResult().addSuccessMessage(msg.toString());
	}

}
