package com.sigu.bpm.util.aslp;

import java.util.Map;

import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilString;
import com.alibaba.fastjson.JSONObject;
import com.sigu.bpm.util.util.file.ExportUtil;

/**
 * excel文件按模板导出的aslp接口
 */
public class ExportExcelAslp implements ASLP {

	public ExportExcelAslp() {}

	@Override
	@Meta(parameter = { "name:'sid',required:true,desc:'sessionId'", 
			"name:'templateName',required:true,desc:'模板文件名称'", 
			"name:'templatePath',required:true,desc:'模板文件路径（不包含名称）'", 
			"name:'fileName',required:true,desc:'导出文件的名称'", 
			"name:'exportData',required:true,desc:'JSON格式的导出数据，使用JSONObject.toJSONString()进行格式转换。'" })
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		if (params == null) {
			ro.err("不接受参数为空的调用!");
			return ro;
		}
		UserContext uc = null;
		String sid = params.containsKey("sid") ? params.get("sid").toString() : "";
		if (UtilString.isEmpty(sid)) {
			ro.err("sid不能为空!");
			return ro;
		} else {
			uc = UserContext.fromSessionId(sid);
		}
		String templateName = params.containsKey("templateName") ? params.get("templateName").toString() : "";
		String templatePath = params.containsKey("templatePath") ? params.get("templatePath").toString() : "";
		String fileName = params.containsKey("fileName") ? params.get("fileName").toString() : "";
		String exportData = params.containsKey("exportData") ? params.get("exportData").toString() : "";

		try {
			// 将接口传入的字符串转换为map对象
			Map<String, Object> mapData = getMapData(exportData);
			ResponseObject result = ExportUtil.exportExcel(uc, templateName, templatePath, fileName, mapData);
			if (result.isOk()) {
				// 获取下载地址
				ro.setData(result.getData());
			} else {
				// 获取导出失败的提示信息
				ro.err(ro.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			ro.err("文档生成失败！" + e.getMessage());
		}
		return ro;
	}

	
	private Map<String, Object> getMapData(String data) {
		Map<String, Object> mapData = JSONObject.parseObject(data, Map.class);
		return mapData;
	}

}
