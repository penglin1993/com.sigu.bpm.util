package com.sigu.bpm.util.sample.web;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;

public class MaskWeb {

	public String maskForm(UserContext uc) {
		ResponseObject ro = ResponseObject.newOkResponse();
		try {
			// 模拟后台的代码逻辑执行花了5秒钟
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ro.err("执行异常！" + e.getMessage());
		}
		ro.msg("执行成功！");
		return ro.toString();
	}

}
