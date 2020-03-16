package com.sigu.bpm.util.sample.job;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ProcessExecuteQuery;

public class FixSystemTaskJob implements IJob {

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		// 1、中干正职和副职请假流程中的未完成的系统任务
		String sql = "SELECT * FROM WFC_TASK WHERE ACTIVITYDEFID = 'obj_c85e89ea540000018b3c1dd01b3a9950' OR ACTIVITYDEFID = 'obj_c85e8a3d24e000015440541018821cfe'";
		List<RowMap> rows = DBSql.getMaps(sql);

		// 2、用API来complete未完成的系统任务
		for (RowMap row : rows) {
			String uid = "admin";
			String taskInstId = row.getString("ID");
			System.out.println("[" + taskInstId + "]=========================================================");
			ProcessExecuteQuery peq = SDK.getTaskAPI().completeTask(taskInstId, null, uid);
			List<TaskInstance> tasks = peq.fetchActiveTasks();
			for (TaskInstance task : tasks) {
				System.out.println("[" + task.getId() + "]" + task.getTitle());
			}
			System.out.println("[" + taskInstId + "]=========================================================");
		}
	}

}
