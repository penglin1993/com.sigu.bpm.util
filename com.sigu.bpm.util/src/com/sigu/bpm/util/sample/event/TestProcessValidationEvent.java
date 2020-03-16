package com.sigu.bpm.util.sample.event;

import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.model.def.ActivityModel;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class TestProcessValidationEvent extends InterruptListener {

	public TestProcessValidationEvent() {
		this.setDescription("办理时校验触发器示例！包含触发器编程的常用API示例！");
		this.setProvider("hufangyun");
		this.setVersion("1.0");
	}

	@Override
	public boolean execute(ProcessExecutionContext pec) throws Exception {
		// 取系统上下文字段值
		String bindId = pec.getProcessInstance().getId();// 流程实例ID
		String taskId = pec.getTaskInstance().getId();// 任务实例ID
		String uid = pec.getUserContext().getUID();// 当前操作用户
		String sid = pec.getUserContext().getSessionId();// 当前操作用户的SID

		String processDefId = pec.getProcessDef().getId();// 流程模型UUID
		String activityDefId = pec.getTaskInstance().getActivityDefId();// 节点模型UUID
		ActivityModel am = SDK.getRepositoryAPI().getActivityModel(processDefId, activityDefId);
		String customId = am.getCustomUniqueId();// 节点模型用户自定义ID

		// 取表单业务字段值
		// 取主表BO表的当前数据
		String boName = "BO_EU_XX";
		BO bo = SDK.getBOAPI().query(boName).bindId(bindId).detail();
		String xxxx = bo.getString("APPLYUSER");

		// 取子表BO表的当前数据
		String subBoName = "BO_EU_XX_C";
		List<BO> bos = SDK.getBOAPI().query(subBoName).bindId(bindId).list();
		for (int i = 0; i < bos.size(); i++) {
			BO subBo = bos.get(i);
			String xxx = subBo.getString("BH");
		}
		for (BO subBo : bos) {
			String xxx = subBo.getString("BH");
		}

		// 通过SQL语句查询需要的数据
		String sql = "SELECT AA, BB FROM BO_XX_XX WHERE BINDID = ? AND XXTYPE = ? ";
		List<RowMap> rows = DBSql.getMaps(sql, new Object[] { bindId, "xx" });
		for (RowMap row : rows) {
			String xxx = row.getString("AA");
		}
		RowMap row = DBSql.getMap(sql, new Object[] { bindId, "xx" });

		// 逻辑示例
		String showMsg = "";
		try {
			// 取待办任务
			List<TaskInstance> todoTasks = SDK.getTaskQueryAPI().target(uid).userTask().list();

			// 取已办任务
			List<HistoryTaskInstance> doneTasks = SDK.getHistoryTaskQueryAPI().target(uid).list();

		} catch (Exception e) {
			e.printStackTrace();
			showMsg = "校验发生异常，请联系管理员！" + e.getMessage();
			// 通过抛出BPMNError直接返回提示信息
			throw new BPMNError("msg", showMsg);
		}
		// // 如果返回的信息需要再处理或者多次处理，那么可以选择在catch块后面再抛出处理好的msg
		// if (!showMsg.equals("")) {
		// throw new BPMNError("msg", showMsg);
		// }

		return false;
	}

}
