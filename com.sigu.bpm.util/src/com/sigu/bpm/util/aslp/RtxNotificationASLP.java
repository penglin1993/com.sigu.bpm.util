package com.sigu.bpm.util.aslp;

import java.util.Map;

import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.sigu.bpm.util.util.im.RtxUtil;

/**
 * RTX腾讯通消息提醒的aslp接口
 */
public class RtxNotificationASLP implements ASLP {

	public RtxNotificationASLP() {
	}

	@Meta(parameter = { "name:'type',required:true,desc:'消息类型task或者msg，task表示可打开表单的任务提醒，msg表示纯消息提醒。'", 
			"name:'content',required:true,desc:'如果type等于task,那么该值为任务实例ID，否则为提示消息。'",
			"name:'receiver',required:true,desc:'消息接收人的IM账号，如果type等于msg则该值必填。'"})
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		if (params == null) {
			ro.err("不接受参数为空的调用!");
			return ro;
		}
		String type = params.containsKey("type") ? params.get("type").toString() : "";
		String content = params.containsKey("content") ? params.get("content").toString() : "";
		String receiver = params.containsKey("receiver") ? params.get("receiver").toString() : "";

		try {
			if (type.equals("task")) {
				TaskInstance task = SDK.getTaskAPI().getInstanceById(content);
				if (task != null) {
					// 待办任务提醒
					RtxUtil.sendTaskNotification(task);
				} else {
					ro.err("消息推送失败，未找到任务实例！");
				}
			} else {
				if (UtilString.isNotEmpty(receiver)) {
					// 普通消息提醒
					RtxUtil.sendMsgNotification(receiver, content);
				} else {
					ro.err("receiver消息接收人账号不能为空！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ro.err("消息推送失败！" + e.getMessage());
		}
		return ro;
	}

}
