package com.sigu.bpm.util.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.sigu.bpm.util.util.file.ExportUtil;

/**
 * WORD和EXCEL文档导出示例类
 */
public class ExportWeb {

	private String appId = "com.sigu.bpm.util";

	/**
	 * 根据模板导出数据到Word
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public String exportWord(UserContext uc, String bindId) {
		try {
			// 主表名称
			String boName = "BO_SG_UTIL_EXP_P";
			BO data = SDK.getBOAPI().query(boName).bindId(bindId).detail();
			// 生成的文件名称
			String fileName = "基地首长周会议活动安排（" + data.getString("NF") + "年第" + data.getString("ZS") + "周）.doc";

			// 获取模板文件路径
			String userdir = System.getProperty("user.dir");
			userdir = userdir.substring(0, userdir.length() - 3);
			StringBuffer templatePath = new StringBuffer(userdir);
			templatePath.append("apps" + File.separator);
			templatePath.append("install" + File.separator);
			templatePath.append(appId + File.separator);
			templatePath.append("template" + File.separator);
			templatePath.append("word");

			String templateName = "WeekPlanTemplate.ftl";

			// 获取要填充的数据
			Map<String, Object> dataMap = getExportData(bindId);

			// 调用工具类导出文件
			ResponseObject ro = ExportUtil.exportWord(uc, templateName, templatePath.toString(), fileName, dataMap);
			return ro.toString();
		} catch (Exception e) {
			e.printStackTrace();
			ResponseObject ro = ResponseObject.newErrResponse("导出Word文件失败！" + e.getMessage());
			return ro.toString();
		}
	}
	
	/**
	 * 根据模板导出数据到Excel
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public String exportExcel(UserContext uc, String bindId) {
		try {
			// 主表名称
			String boName = "BO_SG_UTIL_EXP_P";
			BO data = SDK.getBOAPI().query(boName).bindId(bindId).detail();
			// 生成的文件名称
			String fileName = "基地首长周会议活动安排（" + data.getString("NF") + "年第" + data.getString("ZS") + "周）.xls";

			// 获取模板文件路径
			String userdir = System.getProperty("user.dir");
			userdir = userdir.substring(0, userdir.length() - 3);
			StringBuffer templatePath = new StringBuffer(userdir);
			templatePath.append("apps" + File.separator);
			templatePath.append("install" + File.separator);
			templatePath.append(appId + File.separator);
			templatePath.append("template" + File.separator);
			templatePath.append("excel");

			String templateName = "WeekPlanTemplate.xls";

			// 获取要填充的数据
			Map<String, Object> dataMap = getExportData(bindId);

			// 调用工具类导出文件
			ResponseObject ro = ExportUtil.exportExcel(uc, templateName, templatePath.toString(), fileName, dataMap);
			return ro.toString();
		} catch (Exception e) {
			e.printStackTrace();
			ResponseObject ro = ResponseObject.newErrResponse("导出Excel文件失败！" + e.getMessage());
			return ro.toString();
		}
	}

	/**
	 * 获取需要导出的数据
	 * 
	 * @param bindId
	 * @return
	 */
	private Map<String, Object> getExportData(String bindId) {
		Map<String, Object> mapData = new HashMap<>();

		// 主表名称
		String boName = "BO_SG_UTIL_EXP_P";
		BO data = SDK.getBOAPI().query(boName).bindId(bindId).detail();
		// 子表名称
		String subBoName = "BO_SG_UTIL_EXP_C";
		List<BO> bos = SDK.getBOAPI().query(subBoName).bindId(bindId).orderByCreated().asc().list();

		// 组织字符串标签及其数据
		mapData.put("NF", data.getString("NF"));
		mapData.put("ZS", data.getString("ZS"));
		String dateFrom[] = data.getString("DATEFROM").split("-");
		String dateTo[] = data.getString("DATETO").split("-");
		String datetime = dateFrom[0] + "年" + Integer.parseInt(dateFrom[1]) + "月" + Integer.parseInt(dateFrom[2]) + "日-" + Integer.parseInt(dateTo[1]) + "月" + Integer.parseInt(dateTo[2]) + "日";
		mapData.put("RQ", datetime);
		String zbld = data.getString("ZBLD");
		if (zbld.indexOf("<") != -1) {
			zbld = zbld.substring(zbld.indexOf("<") + 1, zbld.length() - 1);
		}
		mapData.put("ZBLD", zbld);

		// 组织Table标签及其数据
		List<Map<String, String>> tableData = new ArrayList<>();
		for (BO bo : bos) {
			Map<String, String> boData = new HashMap<>();
			String week = SDK.getRuleAPI().executeAtScript("@weekDay(" + bo.getString("HDRQ") + ")");
			if ("1".equals(week)) {
				week = "星期天";
			} else if ("2".equals(week)) {
				week = "星期一";
			} else if ("3".equals(week)) {
				week = "星期二";
			} else if ("4".equals(week)) {
				week = "星期三";
			} else if ("5".equals(week)) {
				week = "星期四";
			} else if ("6".equals(week)) {
				week = "星期五";
			} else if ("7".equals(week)) {
				week = "星期六";
			} else {
				week = "待定";
			}
			boData.put("WEEK", week);
			if (!"待定".equals(week)) {
				String rq[] = bo.getString("HDRQ").split("-");
				String date = "(" + Integer.parseInt(rq[1]) + "月" + Integer.parseInt(rq[2]) + "日)";
				boData.put("HDRQ", date);
			}
			boData.put("KSSJ", bo.getString("KSSJ"));
			boData.put("HDNR", bo.getString("HDNR"));
			boData.put("HDDD", bo.getString("HDDD"));
			boData.put("CJLD", bo.getString("CJLD"));
			boData.put("CBDW", bo.getString("CBDW"));

			tableData.add(boData);
		}
		mapData.put("plans", tableData);

		return mapData;
	}

	/**
	 * 导出数据到Excel（没有Excel模板的方式）
	 * 
	 * @param uc
	 * @param bindId
	 * @return
	 */
	public String exportExcelNoTemplate(UserContext uc, String bindId) {
		try {
			// 主表名称
			String boName = "BO_SG_UTIL_EXP_P";
			BO data = SDK.getBOAPI().query(boName).bindId(bindId).detail();
			// 生成的文件名称
			String fileName = "基地首长周会议活动安排（" + data.getString("NF") + "年第" + data.getString("ZS") + "周）.xls";

			// 获取要填充的数据
			List<List<Object>> listData = getExportDataList(bindId);

			// 调用工具类导出文件
			ResponseObject ro = ExportUtil.exportExcelNoTemplate(uc, fileName, listData);
			return ro.toString();
		} catch (Exception e) {
			e.printStackTrace();
			ResponseObject ro = ResponseObject.newErrResponse("导出Excel文件失败！" + e.getMessage());
			return ro.toString();
		}
	}

	/**
	 * 获取需要导出的数据
	 * 
	 * @param bindId
	 * @return
	 */
	private List<List<Object>> getExportDataList(String bindId) {
		List<List<Object>> dataList = new ArrayList<>();

		// 创建Excel标题行数据
		List<Object> data = new ArrayList<>();

		data.add("日期");
		data.add("时间");
		data.add("活动内容");
		data.add("地点");
		data.add("参加领导");
		data.add("承办单位");

		dataList.add(data);

		// 子表名称
		String subBoName = "BO_SG_UTIL_EXP_C";
		List<BO> bos = SDK.getBOAPI().query(subBoName).bindId(bindId).orderByCreated().asc().list();
		// 遍历子表数据
		for (BO bo : bos) {
			data = new ArrayList<>();

			data.add(bo.getString("HDRQ"));
			data.add(bo.getString("KSSJ"));
			data.add(bo.getString("HDNR"));
			data.add(bo.getString("HDDD"));
			data.add(bo.getString("CJLD"));
			data.add(bo.getString("CBDW"));

			dataList.add(data);
		}

		return dataList;
	}

}
