package com.sigu.bpm.util.sample.extend;

import java.util.List;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ProcessExecuteQuery;

public class StartProcessByJs extends ValueListener {

	public StartProcessByJs() {
		this.setDescription("JS提交的方式启动流程，并返回任务办理界面！");
	}

	@Override
	public String execute(ProcessExecutionContext pec) throws Exception {
		String page = "";
		// 启动流程
		//String processDefId = "obj_08857707a3b34a5da6713d2fb6d74d3c";//直接写死在代码的uuid
		//通过应用的参数来获取外部配置的uuid
		String processDefId = SDK.getAppAPI().getProperty("com.awspaas.user.apps.px.hfy", "PC_UUID");
		
		String uid = pec.getUserContext().getUID();
		String title = "";
		ProcessInstance process = SDK.getProcessAPI().createProcessInstance(processDefId, uid, title);
		ProcessExecuteQuery peq = SDK.getProcessAPI().start(process);
		List<TaskInstance> tasks = peq.fetchActiveTasks();
		TaskInstance taskInst = tasks.get(0);

		// 通过SQL语句查询待办任务
		// String sql = "SELECT ID FROM WFC_TASK WHERE PROCESSINSTID = ?";
		// String taskId = DBSql.getString(sql, new Object[] {process.getId()});
		// TaskInstance taskInst = SDK.getTaskQueryAPI().detailById(taskId);

		// 返回待办任务的表单界面
		UserContext userContext = pec.getUserContext();
		
		//String bpmPortalHost = SDK.getPlatformAPI().getPortalUrl();//取的是aws_portal.xml中的url值
		String bpmPortalHost = "";//为空可以以相对路径的方式访问
		String sid = userContext.getSessionId();
		String processInstId = process.getId();
		String taskInstId = taskInst.getId();
		int openState = taskInst.getState();
		page = SDK.getFormAPI().getFormURL(bpmPortalHost, sid, processInstId, taskInstId, openState, null, null, null);
		
		
		ResponseObject ro = ResponseObject.newOkResponse();
		//ResponseObject ro = ResponseObject.newErrResponse("返回失败提示！");
		ro.setData(page);
		
		return ro.toString();
	}

}
