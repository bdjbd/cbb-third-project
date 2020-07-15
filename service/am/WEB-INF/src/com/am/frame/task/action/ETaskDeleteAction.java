package com.am.frame.task.action;

import java.util.ArrayList;
import java.util.List;

import com.am.frame.task.task.TaskEngine;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月17日
 * @version 
 * 说明:<br />
 */
public class ETaskDeleteAction extends DefaultAction {
	
	
	@Override
	public void doAction(DB db, ActionContext ac) {
		// 获得选择列: _s_单元编号
			String[] select = ac.getRequestParameters("_s_p2p_enterprisetask.list");
			// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
			String[] bookId = ac.getRequestParameters("p2p_enterprisetask.list.id.k");
			List<String> selectedBookId = new ArrayList<String>();
			if (!Checker.isEmpty(select)) {
				for (int i = 0; i < select.length; i++) {
					if ("1".equals(select[i])) {// 1为选中
						selectedBookId.add(bookId[i]);
					}
				}
			}
			
			for(String taskId:selectedBookId){
				//1,停用任务
				TaskEngine taskEngine=TaskEngine.getInstance();//=ITaskManager
				taskEngine.deleteTaskToUsers(taskId);
				//2，删除企业任务
				String delSQL="DELETE FROM am_ENTERPRISETASK WHERE id=?  ";
				try {
					db.execute(delSQL, taskId, Type.VARCHAR);
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			
			
		}
	
}
