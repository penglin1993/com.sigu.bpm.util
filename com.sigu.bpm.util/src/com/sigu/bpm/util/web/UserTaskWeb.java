package com.sigu.bpm.util.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class UserTaskWeb {
	
	private String appId = "com.sigu.bpm.util";

	/**
	 * 查询指定用户的待办任务数据并返回页面显示
	 * 
	 * @param uc
	 *            当前操作的用户上下文
	 * @param myts
	 *            每页显示的数据条数
	 * @param dqys
	 *            当前要显示的页码数
	 * @return
	 */
	public String todoPage(UserContext uc, String myts, String dqys) {
		String sid = uc.getSessionId();
		int total = 0;
		int pageLimit = (myts == null || myts.trim().equals("")) ? 10 : Integer.parseInt(myts);
		int currentPage = (dqys == null || dqys.trim().equals("")) ? 0 : Integer.parseInt(dqys);

		// 从数据库获取指定用户的待办任务
		List<TaskInstance> todoTasks = SDK.getTaskQueryAPI().target(uc.getUID()).userTask().list();
		total = todoTasks.size();

		int startPage = currentPage * pageLimit;
		int endPage = (currentPage + 1) * pageLimit;
		endPage = total < endPage ? total : endPage;
		// 将待办任务数据拼装成html对象
		StringBuffer html = new StringBuffer();
		for (int i = startPage; i < endPage; i++) {
			TaskInstance task = todoTasks.get(i);
			String title = task.getTitle();
			String from = UserContext.fromUID(task.getOwner()).getUserName();
			String date = task.getBeginTime().toString().substring(0, 19);
			String url = SDK.getFormAPI().getFormURL("", sid, task.getProcessInstId(), task.getId(), task.getState(),
					null, null, null);

			html.append("<tr>");
			html.append("<td>" + (i + 1) + "</td>");
			html.append("<td><a target=\"_blank\" href=\"" + url + "\">" + title + "</a></td>");
			html.append("<td>" + from + "</td>");
			html.append("<td>" + date + "</td>");
			html.append("</tr>");
		}
		String lczxUrl = "./w?sid=" + sid + "&cmd=com.actionsoft.apps.workbench_main_page";

		// 将数据和模板上的标签进行映射
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sid", sid);
		map.put("workflowCenterUrl", lczxUrl);
		map.put("taskHtmls", html.toString());
		map.put("total", total);
		map.put("pageLimit", pageLimit);
		map.put("currentPage", currentPage);

		return HtmlPageTemplate.merge(appId, "todoTaskPage.html", map);

	}

	/**
	 * 查询指定用户的待办任务数据并返回移动H5页面显示
	 * 
	 * @param uc
	 *            当前操作的用户上下文
	 * @param myts
	 *            每页显示的数据条数
	 * @param dqys
	 *            当前要显示的页码数
	 * @return
	 */
	public String todoPageH5(UserContext uc, String myts, String dqys) {
		String sid = uc.getSessionId();
		int total = 0;
		int pageLimit = (myts == null || myts.trim().equals("")) ? 10 : Integer.parseInt(myts);
		int currentPage = (dqys == null || dqys.trim().equals("")) ? 0 : Integer.parseInt(dqys);

		// 从数据库获取指定用户的待办任务
		List<TaskInstance> todoTasks = SDK.getTaskQueryAPI().target(uc.getUID()).userTask().list();
		total = todoTasks.size();

		int startPage = currentPage * pageLimit;
		int endPage = (currentPage + 1) * pageLimit;
		endPage = total < endPage ? total : endPage;
		// 将待办任务数据拼装成html对象
		StringBuffer html = new StringBuffer();
		for (int i = startPage; i < endPage; i++) {
			TaskInstance task = todoTasks.get(i);
			String title = task.getTitle();
			String from = UserContext.fromUID(task.getOwner()).getUserName();
			String date = task.getBeginTime().toString().substring(0, 10);
			String url = SDK.getFormAPI().getFormURL("", sid, task.getProcessInstId(), task.getId(), task.getState(),
					null, null, null);

			html.append("<tr>");
			html.append("<td>" + (i + 1) + "</td>");
			html.append("<td><a target=\"_blank\" href=\"" + url + "\">" + title + "</a></td>");
			html.append("<td>" + from + "</td>");
			html.append("<td>" + date + "</td>");
			html.append("</tr>");
		}
		String lczxUrl = "./w?sid=" + sid + "&cmd=com.actionsoft.apps.workbench_main_page";

		// 将数据和模板上的标签进行映射
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sid", sid);
		map.put("workflowCenterUrl", lczxUrl);
		map.put("taskHtmls", html.toString());
		map.put("total", total);
		map.put("pageLimit", pageLimit);
		map.put("currentPage", currentPage);

		return HtmlPageTemplate.merge(appId, "todoTaskPageH5.html", map);

	}

}
