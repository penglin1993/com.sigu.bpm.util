package com.sigu.bpm.util.util;

import java.util.ArrayList;
import java.util.List;
import com.actionsoft.bpms.bo.design.cache.BOCache;
import com.actionsoft.bpms.bo.design.model.BOModel;
import com.actionsoft.bpms.bpmn.engine.cache.ProcessDefCache;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.model.def.ProcessDefinition;
import com.actionsoft.bpms.bpmn.engine.model.def.UserTaskModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ext.BOPropertyModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ext.FormSetModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 更新BO表中的流程状态
 */
public class ProcessStatusChangeUtil {
	/**
	 * 更新流程第一个节点绑定的储存主表的状态字段(FLOWSTATUS)的值 主动处理审批中和已退回两种状态
	 * 对于已办结和已否决,如果用户已经更新了值,那么该方法中不做处理 `
	 * 
	 * @param eventName
	 * @param task
	 * @param pec
	 */
	public static void updateFlowStatus(String eventName, TaskInstance task, ProcessExecutionContext pec) {
		if ("TASK_CREATE".equals(eventName)) {
			if (task.getState() != 1) {
				return;
			}
			String bindId = task.getProcessInstId();
			String processDefId = task.getProcessDefId();
			String taskDefId = task.getActivityDefId();
			// 获取需要修改的表
			ArrayList<String> boNameList = checkStateIsExists(processDefId);
			if (boNameList.size() == 0) {
				return;
			}
			String state = checkInstanceState(bindId, taskDefId);
			modifyTableState(boNameList, state, bindId);
		}
		if ("PROCESS_COMPLETE".equals(eventName)) {
			String bindId = pec.getProcessInstance().getId();
			String processDefId = pec.getProcessDef().getId();
			ArrayList<String> boNameList = checkStateIsExists(processDefId);
			if (boNameList.size() == 0) {
				return;
			}
			String ext6 = pec.getProcessInstance().getExt6();
			if ("DELETE_PORTAL".equals(ext6)) {
				// TODO 工具栏作废、审核菜单作废 都无法识别
				modifyTableState(boNameList, "已作废", bindId);

			} else if (SDK.getTaskAPI().isChoiceActionMenu(pec.getTaskInstance(), "否决")) {
				//TODO 如果审核菜单否决 配置的是终止而不是自然结束 那么这里也无法判断
				modifyTableState(boNameList, "已否决", bindId);
			} else if ("terminate".equals(pec.getProcessInstance().getControlState())) {
				//TODO 实际为终止的流程
				modifyTableState(boNameList, "已否决", bindId);
			} else {
				modifyTableState(boNameList, "已办结", bindId);
			}
		}

	}

	/**
	 * @param bindId
	 * @param taskDefId
	 * @return
	 */
	private static String checkInstanceState(String bindId, String taskDefId) {
		String state = "未提交";
		try {
			String sql = "SELECT T.ID, T.ACTIVITYDEFID FROM (SELECT ID,ACTIVITYDEFID,BEGINTIME FROM WFC_TASK WHERE PROCESSINSTID=? UNION SELECT ID,ACTIVITYDEFID,BEGINTIME FROM WFH_TASK WHERE PROCESSINSTID=?) T ORDER BY T.BEGINTIME ASC";
			List<RowMap> ids = DBSql.getMaps(sql, new Object[] { bindId, bindId });
			if (ids != null && ids.size() > 1) {
				state = "审批中";
				if (ids.get(0).get("ACTIVITYDEFID").equals(taskDefId)) {
					state = "已退回";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * 
	 * @param boNameList
	 * @param state
	 * @param bindId
	 */
	private static void modifyTableState(ArrayList<String> boNameList, String state, String bindId) {
		try {
			for (int i = 0; i < boNameList.size(); i++) {
				String boName = boNameList.get(i).toString();
				String sql = "UPDATE " + boName + " SET FLOWSTATUS=? WHERE BINDID=? AND FLOWSTATUS NOT IN('已否决','已办结')";
				DBSql.update(sql, new Object[] { state, bindId });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param processDefId
	 * @return
	 */
	private static ArrayList<String> checkStateIsExists(String processDefId) {
		ArrayList<String> list = new ArrayList<String>();
		// 流程模型对象
		ProcessDefinition pm = ProcessDefCache.getInstance().get(processDefId);
		List<UserTaskModel> tms = pm.getUserTaskList();
		// 节点模型
		for (UserTaskModel tm : tms) {
			List<FormSetModel> fsms = tm.getFormSets();
			// 表单模型
			for (FormSetModel fsm : fsms) {
				List<BOPropertyModel> bpms = fsm.getBoPropertys();
				// 表单字段
				for (BOPropertyModel bpm : bpms) {
					if ("FLOWSTATUS".equals(bpm.getFieldName())) {
						// 储存模型对象
						BOModel bm = BOCache.getInstance().getModel(bpm.getBoDefId());
						list.add(bm.getName());
						break;
					}
				}
				break;
			}
			break;
		}
		return list;
	}
}
