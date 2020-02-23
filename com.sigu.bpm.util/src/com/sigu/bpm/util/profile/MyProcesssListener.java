package com.sigu.bpm.util.profile;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ProcessPubicListener;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.sigu.bpm.util.util.im.RtxUtil;
import com.sigu.bpm.util.util.im.SimUtil;

/**
 * 流程全局监听事件（一般用于集成IM即时通讯发送待办任务提醒）
 */
public class MyProcesssListener extends ProcessPubicListener {

	@Override
	public void call(String eventName, TaskInstance taskInst, ProcessExecutionContext pec) {
		// TASK_CREATE 任务创建后，新任务到达（待办） * TASK_COMPLETE 任务执行完，转成历史任务（已办）
		if (eventName.equals("TASK_CREATE")) {
			RtxUtil.sendTaskNotification(taskInst);
			SimUtil.sendTaskNotification(taskInst);
		}

	}

}
