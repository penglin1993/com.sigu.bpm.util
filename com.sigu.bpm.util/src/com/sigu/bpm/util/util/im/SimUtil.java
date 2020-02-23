package com.sigu.bpm.util.util.im;

import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.sigu.bpm.util.util.HttpClientUtil;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;

public class SimUtil {
	private static final String appId = "com.sigu.bpm.util.util";

	/**
	 * 普通消息提醒
	 * @param receiver
	 * @param msg
	 */
	public static void sendMsgNotification(String receiver, String msg) {
		try {
			// 判断是否开启了消息推送
			String flag = SDK.getAppAPI().getProperty(appId, "SimEnabled");
			if (!"true".equals(flag)) {
				return;
			}
			// 参数解析
			String account = SDK.getAppAPI().getProperty(appId, "account");// 获取身份认证用户账号
			String pwd = SDK.getAppAPI().getProperty(appId, "pwd");// 获取身份认证用户密码
			String authKey = getKey(account, pwd);// 获取身份认证的key
			// 判断获取authKey是否失败
			if (UtilString.isNotEmpty(authKey) && !authKey.equals("-1")) {
				msg = URLEncoder.encode(msg, "UTF-8");

				// 接口调用
				String simUrl = SDK.getAppAPI().getProperty(appId, "SimSendMsgUrl");
				Map<String, String> params = new HashMap<String, String>();
				params.put("authKey", authKey);
				params.put("sender", account);
				params.put("receiver", receiver);
				params.put("msgContent", msg);

				HttpClientUtil.doPost(simUrl, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 待办任务提醒
	 * @param task
	 */
	public static void sendTaskNotification(TaskInstance task) {
		try {
			// 判断是否开启了消息推送
			String flag = SDK.getAppAPI().getProperty(appId, "SimEnabled");
			if (!"true".equals(flag)) {
				return;
			}
			// 自己发给自己的不做提醒
			if (task.getOwner().equals(task.getTarget())) {
				return;
			}
			// 参数解析
			String account = SDK.getAppAPI().getProperty(appId, "account");// 获取身份认证用户账号
			String pwd = SDK.getAppAPI().getProperty(appId, "pwd");// 获取身份认证用户密码
			String authKey = getKey(account, pwd);// 获取身份认证的key
			// 判断获取authKey是否失败
			if (UtilString.isNotEmpty(authKey) && !authKey.equals("-1")) {
				String sender = UserContext.fromUID(task.getOwner()).getUserName();// 发送者名字
				String receiver = task.getTarget();// 接收者账号

				String bpmPortalHost = SDK.getPlatformAPI().getPortalUrl();
				bpmPortalHost = bpmPortalHost.endsWith("/") ? bpmPortalHost + "r/w" : bpmPortalHost + "/r/w";
				String url = bpmPortalHost + "?cmd=com.sigu.bpm.util.util.openNotification&type=todo&taskId=" + task.getId();

				String msg = task.getTitle();

				if (msg.indexOf("[") != -1) {
					msg = msg.replace("[", "(");
				}
				if (msg.indexOf("]") != -1) {
					msg = msg.replace("]", ")");
				}
				msg = "<a href=\"" + url + "\">[" + sender + "]发来了任务:[" + msg + "] ,请及时办理！</a>";
				msg = URLEncoder.encode(msg, "UTF-8");

				// 接口调用
				String simUrl = SDK.getAppAPI().getProperty(appId, "SimSendMsgUrl");
				Map<String, String> params = new HashMap<String, String>();
				params.put("authKey", authKey);
				params.put("sender", account);
				params.put("receiver", receiver);
				params.put("msgContent", msg);

				HttpClientUtil.doPost(simUrl, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getKey(String authUser, String password) {
		// 获取身份认证接口地址
		String authUrl = SDK.getAppAPI().getProperty(appId, "AuthKeyUrl");
		// 请求参数
		String passwordMD5 = getMD5String(password);

		Map<String, String> params = new HashMap<String, String>();
		params.put("account", authUser);
		params.put("password", passwordMD5);
		String authKey = HttpClientUtil.doPost(authUrl, params);

		return authKey;

	}

	/**
	 * 取得字符串对应的MD5值的字符串。
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public static String getMD5String(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		String strSummary = null;
		try {
			byte[] bytes = str.getBytes("GB2312");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] answer = md5.digest();
			// 二进制转换成字符串
			strSummary = byteToString(answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strSummary;
	}

	/**
	 * 将二进制转换成字符串。
	 * 
	 * @param b
	 *            byte[] 二进制
	 * @return String
	 */
	public static String byteToString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			if ((0xff & b[i]) < 0x10) {
				hexString.append("0" + Integer.toHexString((0xFF & b[i])));
			} else {
				hexString.append(Integer.toHexString(0xFF & b[i]));
			}
		}
		return hexString.toString();
	}

}
