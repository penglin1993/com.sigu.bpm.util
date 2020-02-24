package com.sigu.bpm.util.util.im;

import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.sigu.bpm.util.util.HttpClientUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;

public class RtxUtil {

	private static String appId = "com.sigu.bpm.util.util";

	/**
	 * 普通消息提醒
	 * 
	 * @param receiver
	 * @param msg
	 */
	public static void sendMsgNotification(String receiver, String msg) {
		try {
			// 判断是否开启了RTX消息推送
			String flag = SDK.getAppAPI().getProperty(appId, "RtxEnabled");
			if (!"true".equals(flag)) {
				return;
			}

			String title = "BPM消息提醒";
			title = URLEncoder.encode(title, "GBK");
			msg = URLEncoder.encode(msg, "GBK");

			// 接口调用
			String rtxUrl = SDK.getAppAPI().getProperty(appId, "RtxSendMsgUrl");
			Map<String, String> params = new HashMap<String, String>();
			params.put("receiver", receiver);
			params.put("title", title);
			params.put("msg", msg);

			HttpClientUtil.doGetMap(rtxUrl, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 待办任务提醒
	 * 
	 * @param task
	 */
	public static void sendTaskNotification(TaskInstance task) {
		try {
			// 判断是否开启了RTX消息推送
			String flag = SDK.getAppAPI().getProperty(appId, "RtxEnabled");
			if (!"true".equals(flag)) {
				return;
			}

			// 自己发给自己的不做提醒
			if (task.getOwner().equals(task.getTarget())) {
				return;
			}
			// 参数解析
			String owner = UserContext.fromUID(task.getOwner()).getUserName();
			String bpmPortalHost = SDK.getPlatformAPI().getPortalUrl();
			bpmPortalHost = bpmPortalHost.endsWith("/") ? bpmPortalHost + "r/w" : bpmPortalHost + "/r/w";
			String url = bpmPortalHost + "?cmd=com.sigu.bpm.util.openNotification&type=todo&taskId=" + task.getId();

			String title = "BPM任务提醒";
			title = URLEncoder.encode(title, "GBK");
			String msg = task.getTitle();
			if (msg.indexOf("[") != -1) {
				msg = msg.replace("[", "(");
			}
			if (msg.indexOf("]") != -1) {
				msg = msg.replace("]", ")");
			}
			msg = "您有一个" + owner + "发来的任务[" + msg + "|" + url + "]，请及时办理！";
			 // RTX的接口参数使用GBK而不是UTF8进行编码
			msg = URLEncoder.encode(msg, "GBK");

			// 接口调用
			String rtxUrl = SDK.getAppAPI().getProperty(appId, "RtxSendMsgUrl");
			Map<String, String> params = new HashMap<String, String>();
			params.put("receiver", task.getTarget());
			params.put("title", title);
			params.put("msg", msg);

			HttpClientUtil.doGetMap(rtxUrl, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
