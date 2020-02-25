package com.sigu.bpm.util.util.file;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.actionsoft.apps.resource.plugin.profile.DCPluginProfile;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.sdk.local.SDK;
import com.sigu.bpm.util.profile.MyFileProcessor;
import com.sigu.bpm.util.util.file.ExcelUtil;
import com.sigu.bpm.util.util.file.JxlsUtils;
import com.sigu.bpm.util.util.file.FreeMakerUtil;

/**
 * WORD和EXCEL文档导出工具类
 */
public class ExportUtil {

	private static String appId = "com.sigu.bpm.util";

	/**
	 * 根据模板导出数据到Word
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public static ResponseObject exportWord(UserContext uc, String templateName, String templatePath, String fileName, Map<String, Object> data) {
		ResponseObject ro = ResponseObject.newOkResponse();
		try {
			// 获取将要生成的word文件存放路径
			String groupValue = "export";
			String fileValue = UUID.randomUUID().toString();
			DCPluginProfile dcProfile = SDK.getDCAPI().getDCProfile(appId, MyFileProcessor.repositoryName);
			DCContext dcContext = new DCContext(uc, dcProfile, appId, groupValue, fileValue, fileName);
			// 判断文件目录是否存在，不存在则创建
			File exportFileDir = new File(dcContext.getPath());
			if (!exportFileDir.exists()) {
				exportFileDir.mkdirs();
			}

			// 调用工具类导出文件
			FreeMakerUtil.exportDoc(data, templatePath.toString(), templateName, dcContext.getFilePath());

			// 获取导出文件的下载地址
			ro.setData(dcContext.getDownloadURL());
		} catch (Exception e) {
			e.printStackTrace();
			ro = ResponseObject.newErrResponse("导出Word文件失败！" + e.getMessage());
		}
		return ro;
	}

	/**
	 * 根据模板导出数据到Excel
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public static ResponseObject exportExcel(UserContext uc, String templateName, String templatePath, String fileName, Map<String, Object> data) {
		ResponseObject ro = ResponseObject.newOkResponse();
		try {
			// 获取将要生成的excel文件存放路径
			String groupValue = "export";
			String fileValue = UUID.randomUUID().toString();
			DCPluginProfile dcProfile = SDK.getDCAPI().getDCProfile(appId, MyFileProcessor.repositoryName);
			DCContext dcContext = new DCContext(uc, dcProfile, appId, groupValue, fileValue, fileName);
			// 判断文件目录是否存在，不存在则创建
			File exportFileDir = new File(dcContext.getPath());
			if (!exportFileDir.exists()) {
				exportFileDir.mkdirs();
			}

			// 调用工具类导出文件
			JxlsUtils.exportExcel(data, templatePath + File.separator + templateName, dcContext.getFilePath());

			// 获取导出文件的下载地址
			ro.setData(dcContext.getDownloadURL());
		} catch (Exception e) {
			e.printStackTrace();
			ro = ResponseObject.newErrResponse("导出Excel文件失败！" + e.getMessage());
		}
		return ro;
	}

	/**
	 * 导出数据到Excel（没有Excel模板的方式）
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public static ResponseObject exportExcelNoTemplate(UserContext uc, String fileName, List<List<Object>> data) {
		ResponseObject ro = ResponseObject.newOkResponse();
		try {
			// 获取将要生成的excel文件存放路径
			String groupValue = "export";
			String fileValue = UUID.randomUUID().toString();
			DCPluginProfile dcProfile = SDK.getDCAPI().getDCProfile(appId, MyFileProcessor.repositoryName);
			DCContext dcContext = new DCContext(uc, dcProfile, appId, groupValue, fileValue, fileName);
			// 判断文件目录是否存在，不存在则创建
			File exportFileDir = new File(dcContext.getPath());
			if (!exportFileDir.exists()) {
				exportFileDir.mkdirs();
			}

			// 调用工具类导出文件
			ExcelUtil.writeExcel(data, dcContext.getFilePath());

			// 获取下载url
			ro.setData(dcContext.getDownloadURL());
		} catch (Exception e) {
			e.printStackTrace();
			ro = ResponseObject.newErrResponse("导出Excel文件失败！" + e.getMessage());
		}
		return ro;
	}

}
