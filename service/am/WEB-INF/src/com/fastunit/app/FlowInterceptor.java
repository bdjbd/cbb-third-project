package com.fastunit.app;

import com.fastunit.MapList;
import com.fastunit.Page;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.UserUtil;
import com.fastunit.context.ActionContext;
import com.fastunit.flow.FlowContextUtil;
import com.fastunit.flow.config.FlowStyle;
import com.fastunit.flow.config.FlowTrackMode;
import com.fastunit.flow.config.TaskID;
import com.fastunit.flow.meta.FlowMeta;
import com.fastunit.flow.meta.FlowMetaFactory;
import com.fastunit.flow.meta.task.ManualTaskMeta;
import com.fastunit.flow.meta.task.TaskMeta;
import com.fastunit.flow.processor.ActionProcessor;
import com.fastunit.flow.table.IFlowOwner;
import com.fastunit.flow.table.IFlowTask;
import com.fastunit.framework.config.AppConfig;
import com.fastunit.framework.config.Domains;
import com.fastunit.framework.expression.LangExpression;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
import com.fastunit.view.unit.UnitComponent;
import com.fastunit.view.unit.UnitFactory;

/**
 * 给非流程单元绑定审批过程等
 */
public class FlowInterceptor implements UnitInterceptor {

	private static final String OWNER_SQL = "select " + IFlowOwner.OWNER
			+ " from " + IFlowOwner.TABLENAME + " where " + IFlowOwner.TASK_INST_ID
			+ "=?";
	private static final String DONE = "flow.track";
	private static final String TODO = "flow.todo";

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 流程实例编号
		String flowInstId = ac.getRequestParameter("flowinstid");
		// 流程编号
		String flowId = ac.getRequestParameter("flowid");

		// 补充流程信息
		if (!Checker.isEmpty(flowInstId) && !Checker.isEmpty(flowId)) {
			UnitComponent uc = (UnitComponent) unit;
			Page page = uc.getPage();
			String domain = uc.getDomain();
			FlowMeta fm = FlowMetaFactory.getFlowMeta(domain, flowId);
			StringBuffer html = new StringBuffer();
			// 1、单元前：流程编号、执行过程，需要接受flowinstid
			setTopUnit(html, ac, page);
			// 2、单元本身
			html.append(uc.write(ac));
			// 3、单元后：已办
			setTrackUnit(html, ac, fm, page);
			// 4、单元后：待办
			setTodoUnit(html, ac, fm, page);
			return html.toString();
		} else {
			return unit.write(ac);
		}
	}

	private void setTopUnit(StringBuffer html, ActionContext ac, Page page)
			throws Exception {
		String unitId = AppConfig.getFlowTopUnit();
		if (!Checker.isEmpty(unitId)) {
			Unit unit = UnitFactory.getUnit(ac, Domains.ADM, unitId, page);
			html.append(unit.write(ac));
		}
	}

	private void setTrackUnit(StringBuffer html, ActionContext ac,
			FlowMeta flowMeta, Page page) throws Exception {
		int switchMode = AppConfig.getFlowDoneTrackMode();
		if (switchMode == FlowTrackMode.NONE) {
			return;
		}
		Unit trackUnit = UnitFactory.getUnit(ac, Domains.ADM, DONE, page);
		trackUnit.setSwitchMode(switchMode);

		MapList data = trackUnit.getData();
		if (Checker.isEmpty(data)) {
			return;
		}

		// 设置任务名称、动作状态、办理意见
		for (int i = 0; i < data.size(); i++) {
			Row row = data.getRow(i);
			String taskId = row.get(IFlowTask.TASK_ID);
			if (TaskID.JUMP.equals(taskId)) {
				data.put(i, IFlowTask.TASK_ID, FlowContextUtil.getJumpName(ac));
				data.put(i, IFlowTask.ACTION_ID, getJumpToTaskName(ac, flowMeta, row));
			} else {
				TaskMeta taskMeta = flowMeta.getTaskMeta(taskId);
				if (taskMeta != null) {// 用来适应流程变更
					data.put(i, IFlowTask.TASK_ID, taskMeta.getTaskName(ac));
					if (taskMeta instanceof ManualTaskMeta) {
						data.put(i, IFlowTask.ACTION_ID,
								getActionState(ac, (ManualTaskMeta) taskMeta, row));
					} else {
						trackUnit.setRowCss(i, FlowStyle.FLOW_TRACT_SPLIT);
					}
				}
			}
			// 办理意见（分支任务会设未解析的任务名称）
			String opinion = row.get(IFlowTask.OPINION);
			data.put(i, IFlowTask.OPINION, LangExpression.resolve(ac, opinion));
		}
		html.append(trackUnit.write(ac));
	}

	private void setTodoUnit(StringBuffer html, ActionContext ac,
			FlowMeta flowMeta, Page page) throws Exception {
		int switchMode = AppConfig.getFlowTodoTrackMode();
		if (switchMode == FlowTrackMode.NONE) {
			return;
		}
		Unit todoUnit = UnitFactory.getUnit(ac, Domains.ADM, TODO, page);
		todoUnit.setSwitchMode(switchMode);
		MapList data = todoUnit.getData();
		if (Checker.isEmpty(data)) {
			return;
		}
		DB db = DBFactory.getDB();
		for (int i = 0; i < data.size(); i++) {
			Row row = data.getRow(i);
			// 1、设置任务名称
			String taskId = row.get(IFlowTask.TASK_ID);
			ManualTaskMeta taskMeta = (ManualTaskMeta) flowMeta.getTaskMeta(taskId);
			if (taskMeta != null) {// 用来适应流程变更
				data.put(i, IFlowTask.TASK_ID, taskMeta.getTaskName(ac));
			}
			// 2、设置执行人名称
			String taskInstId = row.get(IFlowTask.TASK_INST_ID);
			data.put(i, "executors", getExecutorNames(db, taskInstId));
		}
		html.append(todoUnit.write(ac));
	}

	private String getExecutorNames(DB db, String taskInstId) throws Exception {
		MapList list = db.query(OWNER_SQL, taskInstId, Type.BIGINT);
		StringBuffer users = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String owner = list.getRow(i).get(IFlowOwner.OWNER);
			users.append(UserUtil.getUserName(db, owner));
			if (i < list.size() - 1) {
				users.append(",");
			}
		}
		return users.toString();
	}

	private String getJumpToTaskName(ActionContext ac, FlowMeta flowMeta, Row row) {
		String name = "-> ";
		String jumpToTaskId = row.get(IFlowTask.SKIP_TO_TASK_ID);
		if (!Checker.isEmpty(jumpToTaskId)) {
			TaskMeta taskMeta = flowMeta.getTaskMeta(jumpToTaskId);
			if (taskMeta != null) {// 用来适应流程变更
				name += taskMeta.getTaskName(ac);
			}
		}
		return name;
	}

	private String getActionState(ActionContext ac, ManualTaskMeta taskMeta,
			Row row) {
		String actionId = row.get(IFlowTask.ACTION_ID);
		if (!Checker.isEmpty(actionId)) {
			ActionProcessor action = taskMeta.getAction(actionId);
			if (action != null) {// 用来适应流程变更
				return action.getState(ac);
			}
		}
		return null;
	}
}
