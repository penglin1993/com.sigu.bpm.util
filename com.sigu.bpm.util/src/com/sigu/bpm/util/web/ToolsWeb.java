package com.sigu.bpm.util.web;

import java.util.HashMap;
import java.util.Map;

import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.RequestParams;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;
import com.sigu.bpm.util.util.SessionUtil;

public class ToolsWeb {

	private String appId = "com.sigu.bpm.util";

	/**
	 * 根据userId获取用户信息
	 * 
	 * @param uc
	 * @param userId
	 * @return
	 */
	public String getUserInfo(UserContext uc, String userId) {
		ResponseObject ro = ResponseObject.newOkResponse();
		if (UtilString.isEmpty(userId)) {
			ro = ResponseObject.newErrResponse("userId不能为空！");
			return ro.toString();
		}
		if (userId.indexOf("<") != -1) {
			userId = userId.substring(0, userId.indexOf("<"));
		}
		try {
			UserContext user = UserContext.fromUID(userId);
			if (user != null) {
				String rootDeptId = SDK.getRuleAPI().executeAtScript("@getRootDeptId(" + user.getUID() + ")");
				String rootDeptName = SDK.getRuleAPI().executeAtScript("@getRootDeptName(" + user.getUID() + ")");

				JSONObject json = new JSONObject();
				json.put("uid", user.getUserModel().getUID());
				json.put("userName", user.getUserName());
				json.put("deptId", user.getDepartmentModel().getId());
				json.put("deptName", user.getDepartmentModel().getName());
				json.put("rootDeptId", rootDeptId);
				json.put("rootDeptName", rootDeptName);
				json.put("roleId", user.getRoleModel().getId());
				json.put("roleName", user.getRoleModel().getName());
				json.put("roleNo", user.getRoleModel().getNo());
				json.put("roleGroup", user.getRoleModel().getCategoryName());
				json.put("userNo", user.getUserModel().getUserNo());
				json.put("positionNo", user.getUserModel().getPositionNo());
				json.put("positionName", user.getUserModel().getPositionName());
				json.put("positionLayer", user.getUserModel().getPositionLayer());
				json.put("email", user.getUserModel().getEmail());
				json.put("mobile", user.getUserModel().getMobile());
				json.put("officeTel", user.getUserModel().getOfficeTel());
				json.put("officeFax", user.getUserModel().getOfficeFax());
				json.put("ext1", user.getUserModel().getExt1());
				json.put("ext2", user.getUserModel().getExt2());
				json.put("ext3", user.getUserModel().getExt3());
				json.put("ext4", user.getUserModel().getExt4());
				json.put("ext5", user.getUserModel().getExt5());

				ro.setData(json);
			} else {
				ro = ResponseObject.newErrResponse("未找到对应用户！");
				return ro.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ro = ResponseObject.newErrResponse("获取用户信息失败！" + e.getMessage());
			return ro.toString();
		}

		return ro.toString();
	}

	/**
	 * 打开第三方IM的消息提醒
	 * 
	 * @param params
	 * @return
	 */
	public String openNotification(RequestParams params) {
		String url = "";
		String type = params.get("type");
		if (UtilString.isNotEmpty(type)) {
			// 消息是待办任务
			if (type.trim().equals("todo")) {
				String taskId = params.get("taskId");
				TaskInstance task = SDK.getTaskAPI().getInstanceById(taskId);
				// 参数解析
				String target = task.getTarget();
				String sid = SessionUtil.getUserSid(target);
				url = SDK.getFormAPI().getFormURL("", sid, task.getProcessInstId(), task.getId(), task.getState(), "", "", "");
			}
		}

		// 将数据和模板上的标签进行映射
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", url);
		return HtmlPageTemplate.merge(appId, "im.notification.html", map);
	}

	/**
	 * 批量打印
	 * 
	 * @param uc
	 * @param taskIds
	 *            待打印的单据任务实例ID
	 * @return
	 */
	public String batchPrint(UserContext uc, String taskIds) {
		if (UtilString.isEmpty(taskIds)) {
			return "待打印单据的任务实例ID不能为空！";
		}

		// 解析每一个任务实例ID对应的表单URL
		StringBuffer iframes = new StringBuffer("<div id='printMsg'>");
		String[] ids = taskIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			String taskId = ids[i];
			TaskInstance task = SDK.getTaskAPI().getTaskInstance(taskId);
			String url = SDK.getFormAPI().getFormURL("", uc.getSessionId(), task.getProcessInstId(), task.getId(), UserTaskRuntimeConst.STATE_TYPE_SYSTEM_NOTIFY, "", "", "",false);
			url = url.replace("displayToolbar=true", "displayToolbar=false");//FormAPI没隐藏工具栏需要手动设置一下参数隐藏
			iframes.append("<div><iframe id='iframe" + i + "' style='background-color=transparent; width:100%; height:500px;' frameborder='0' scrolling='no'");
			iframes.append("src='" + url + "'></iframe></div>");
		}
		iframes.append("</div>");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sid", uc.getSessionId());
		map.put("iframes", iframes);
		return HtmlPageTemplate.merge(appId, "batchPrint.html", map);
	}

}
