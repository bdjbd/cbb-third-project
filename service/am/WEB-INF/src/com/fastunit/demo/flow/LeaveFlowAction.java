package com.fastunit.demo.flow;

import com.fastunit.context.ActionContext;
import com.fastunit.flow.FlowContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.FlowAction;
import com.fastunit.util.DateUtil;

public class LeaveFlowAction extends FlowAction {

	@Override
	public void doFlow(DB db, ActionContext ac, FlowContext fc) throws Exception {
		// 可获取的流程数据：
		// String caller = fc.getCaller();// 发起人用户编号
		// String actionId = fc.getActionId();// Action元素编号
		// String flowInstId = fc.getFlowInstId();// 流程实例编号
		// int taskId = fc.getTaskId();// 当前任务编号
		// String a = fc.getVar("a");// 获得变量a的值
		// String opinion = fc.getOpinion();// 获得提交的审批意见

		// 可控制的流程数据：
		// fc.setVar("a", "x");// 变量
		// fc.setContent("y");// 内容
		// fc.setOpinion("z");// 审批意见

		// 设置天数
		TableRow tr = ac.getTables().get(0).getRows().get(0);
		double days = getDays(tr);
		tr.setValue("days", days);// 设置数据库
		fc.setVar("days", days);// 设置流程变量
		// 调用父类方法：执行命令行（如果没有设置命令行，不必调用）
		super.doFlow(db, ac, fc);
	}

	private double getDays(TableRow tr) {
		String beginDate = tr.getValue("begindate");
		int beginTime = tr.getValueInt("begintime", 1);
		String endDate = tr.getValue("enddate");
		int endTime = tr.getValueInt("endtime", 1);
		double days = Double.parseDouble(Long.toString(DateUtil.getDays(beginDate,
				endDate)));
		if (endTime > beginTime) {
			days += 0.5;
		} else if (endTime < beginTime) {
			days -= 0.5;
		}
		return days += 0.5;
	}

}
