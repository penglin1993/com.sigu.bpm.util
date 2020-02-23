package com.sigu.bpm.util;

import com.actionsoft.bpms.server.RequestParams;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.sigu.bpm.util.web.ExportWeb;
import com.sigu.bpm.util.web.ToolsWeb;
import com.sigu.bpm.util.web.UserTaskWeb;

@Controller
public class ToolsController {

	@Mapping("com.sigu.bpm.util.exportWord")
	public String exportWord(UserContext uc, String bindId) {
		ExportWeb web = new ExportWeb();
		String result = web.exportWord(uc, bindId);
		return result;
	}

	@Mapping("com.sigu.bpm.util.exportExcel")
	public String exportExcel(UserContext uc, String bindId) {
		ExportWeb web = new ExportWeb();
		String result = web.exportExcel(uc, bindId);
		return result;
	}

	@Mapping("com.sigu.bpm.util.exportExcelNoTemplate")
	public String exportExcelNoTemplate(UserContext uc, String bindId) {
		ExportWeb web = new ExportWeb();
		String result = web.exportExcelNoTemplate(uc, bindId);
		return result;
	}

	@Mapping("com.sigu.bpm.util.getUserInfo")
	public String getUserInfo(UserContext uc, String userId) {
		ToolsWeb web = new ToolsWeb();
		String result = web.getUserInfo(uc, userId);
		return result;
	}
	
	@Mapping(value = "com.sigu.bpm.util.openNotification", 
			session = false, 
			noSessionEvaluate = "用于集成第三方IM消息提醒", 
			noSessionReason = "用于集成第三方IM消息提醒")
	public String openNotification(RequestParams params) {
		ToolsWeb web = new ToolsWeb();
		String result = web.openNotification(params);
		return result;
	}
	
	@Mapping("com.sigu.bpm.util.batchPrint")
	public String batchPrint(UserContext uc, String taskIds) {
		ToolsWeb web = new ToolsWeb();
		String result = web.batchPrint(uc, taskIds);
		return result;
	}
	
	@Mapping("com.sigu.bpm.util.task.todoPage")
	public String todoPage(UserContext uc, String pageLimit, String currentPage) {
		UserTaskWeb web = new UserTaskWeb();
		String result = web.todoPage(uc, pageLimit, currentPage);
		return result;
	}
	
	@Mapping("com.sigu.bpm.util.task.todoPage.h5")
	public String todoPageH5(UserContext uc, String pageLimit, String currentPage) {
		UserTaskWeb web = new UserTaskWeb();
		String result = web.todoPageH5(uc, pageLimit, currentPage);
		return result;
	}
}
