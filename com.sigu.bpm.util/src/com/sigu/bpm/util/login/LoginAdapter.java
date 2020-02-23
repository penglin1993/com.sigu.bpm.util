package com.sigu.bpm.util.login;

import com.actionsoft.bpms.commons.login.LoginAdapterInterface;
import com.actionsoft.bpms.commons.login.constant.LoginConst;
import com.actionsoft.bpms.commons.login.control.LoginContext;
import com.actionsoft.bpms.commons.login.control.LoginResult;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;

/**
 * 自定义单点登陆适配器，需要将格式appId:class path的以下地址
 * com.sigu.bpm.util:com.sigu.bpm.util.login.LoginAdapter
 * 注册到aws_portal.xml文件中，替换原有的登陆适配器地址
 */
public class LoginAdapter implements LoginAdapterInterface {

	public LoginAdapter() {

	}

	@Override
	public LoginResult validate(LoginContext context) {
		// 首先构造一个登录结果对象context
		LoginResult lr = new LoginResult();
		// 获取请求的身份证号码
		String userId = "";

		/**
		 * 保留传统的用户名密码登陆，预留其他登陆入口：
		 * 如果请求参数中没有ext1，表示本次请求不是sso发起的 那么采用正常的用户名密码来校验本次登陆请求
		 */
		if (!context.getParams().containsKey("ext1")) {
			String uid = context.getUid();
			UserModel user = UserCache.getModel(uid);
			if(user==null) {
				// 如果校验失败，不用设置LoginConst，直接设置Msg即可
				lr.setMsg("登录失败！用户名不存在！");
			}else if (context.getMD5Pwd().equals(user.getPassword())) {
				// 如果校验成功，设置LOGIN_STATUS_OK
				lr.setStatus(LoginConst.LOGIN_STATUS_OK);
				// 最后设置登录UID，
				lr.setLocalUID(uid);
			} else {
				// 如果校验失败，不用设置LoginConst，直接设置Msg即可
				lr.setMsg("登录失败！用户名密码不匹配！");
			}
		} else {
			String pid = (String) context.getParams().get("ext1");
			// 采用EXT1字段校验身份证号码对应的用户是否存在
			if (UtilString.isNotEmpty(pid)) {
				String sql = "SELECT USERID FROM ORGUSER WHERE EXT1=?";
				userId = DBSql.getString(sql, new Object[] { pid });
			}

			if (UtilString.isNotEmpty(userId)) {
				// 如果校验成功，设置LOGIN_STATUS_OK
				lr.setStatus(LoginConst.LOGIN_STATUS_OK);
				// 最后设置登录UID，
				lr.setLocalUID(userId);
			} else {
				// 如果校验失败，不用设置LoginConst，直接设置Msg即可
				lr.setMsg("登录失败！身份认证不成功！");
			}
		}

		return lr;
	}

}
