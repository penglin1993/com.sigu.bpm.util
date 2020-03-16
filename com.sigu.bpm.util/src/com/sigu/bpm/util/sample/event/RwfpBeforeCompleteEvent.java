package com.sigu.bpm.util.sample.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;

public class RwfpBeforeCompleteEvent extends InterruptListener {

	public RwfpBeforeCompleteEvent() {
		this.setDescription("节点任务办理完成前自动将任务数据同步生成到任务管理APP中！");
	}

	public boolean execute(ProcessExecutionContext pec) throws Exception {
		try {
			String bindId = pec.getProcessInstance().getId();
			// 1、创建项目 公文流转之收文项目
			String projectId = "14b40c6a-bfa8-43c8-9ff9-f6757b9274fd";
			// 2、创建分类 计划
			String entryId = "7a9f2864-00ae-48b6-9a86-19db10097352";
			// 3、创建任务
			List<BO> list = SDK.getBOAPI().query("BO_EU_PX_TASK_C").bindId(bindId).list();
			for (BO bo : list) {
				// 调用方AppId
				String sourceAppId = "com.awspaas.user.apps.px.hfy";
				// aslp服务地址
				String dizhi = "aslp://com.actionsoft.apps.taskmgt/addProjectTask";
				// 参数定义列表
				Map<String, Object> abc = new HashMap<String, Object>();
				// 项目id,必填
				abc.put("projectId", projectId);
				// 分组id,必填
				abc.put("entryId", entryId);
				// 任务名称,必填
				abc.put("taskContent", bo.getString("BT"));
				// 任务描述,非必填
				abc.put("taskDesc", bo.getString("NR"));
				// 截止时间,非必填
				abc.put("deadline", bo.getString("JSRQ"));
				// 轻重缓急,非必填
				abc.put("priority", 4);
				// 执行人列表,非必填
				abc.put("principal", bo.getString("ZXRUID"));
				// 知情人列表,非必填
				String zqr = getManager(bo.getString("ZXRUID"));
				abc.put("insider", zqr);

				abc.put("taskId", "");

				abc.put("sid", pec.getUserContext().getSessionId());

				AppAPI appAPI = SDK.getAppAPI();
				// 创建任务
				ResponseObject ro = appAPI.callASLP(appAPI.getAppContext(sourceAppId), dizhi, abc);
				System.out.println(ro.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String getManager(String users) {
		String fgzf = "";
		if (users.indexOf(" ") != -1) {
			fgzf = " ";
		} else if (users.indexOf(",") != -1) {
			fgzf = ",";
		} else if (users.indexOf("|") != -1) {
			fgzf = "\\|";
		}
		String usersSql = "";
		String[] userArr = users.split(fgzf);
		for (String user : userArr) {
			if (user.equals("")) {
				continue;
			}
			usersSql = usersSql + "'" + user + "',";
		}
		if (usersSql.endsWith(",")) {
			usersSql = usersSql.substring(0, usersSql.length() - 1);
		}

		String sql = "SELECT USERID FROM ORGUSER WHERE ISMANAGER=1 AND "
				+ "DEPARTMENTID=(SELECT DEPARTMENTID FROM ORGUSER WHERE USERID= ?)";
		Object[] params = new Object[] { usersSql };
		List<RowMap> rows = DBSql.getMaps(sql, params);

		String uids = "";
		for (RowMap row : rows) {
			uids = uids + row.getString("USERID") + ",";
		}
		if (uids.endsWith(",")) {
			uids = uids.substring(0, uids.length() - 1);
		}
		return uids;

	}

}
