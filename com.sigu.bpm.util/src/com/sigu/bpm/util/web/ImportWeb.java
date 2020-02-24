package com.sigu.bpm.util.web;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;
import com.sigu.bpm.util.util.file.ExcelUtil;

/**
 * Excel导入逻辑处理示例类
 * Excel导入文件的接收在自定义的文件处理器MyFileProcessor中
 */
public class ImportWeb {

	/**
	 * Excel导入数据处理示例 
	 * 该示例处理规则为：如果任意一条数据导入失败，那么整体回滚
	 * 
	 * @param file
	 * @param uc
	 */
	public ResponseObject importExcelData(File file, UserContext uc, String extParam) {
		Connection conn = null;
		try {
			conn = DBSql.open();
			conn.setAutoCommit(false);

			JSONObject params = JSONObject.parseObject(extParam);
			String bindId = params.getString("bindId");

			List<List<Object>> result = ExcelUtil.readExcel(file);
			// i等于1开始表示跳过第一行(一般第一行为标题行所以不处理)
			for (int i = 1; i < result.size(); i++) {
				List<Object> row = result.get(i);
				BO bo = new BO();
				bo.set("HDRQ", row.get(0));
				bo.set("KSSJ", row.get(1));
				bo.set("HDNR", row.get(2));
				bo.set("HDDD", row.get(3));
				bo.set("CJLD", row.get(4));
				bo.set("CBDW", row.get(5));

				SDK.getBOAPI().create("BO_SG_UTIL_EXP_C", bo, bindId, uc.getUID(), conn);
			}
			conn.commit();
		} catch (Exception e) {
			// 回滚BO表业务数据
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// 返回失败信息
			ResponseObject ro = ResponseObject.newErrResponse("Excel数据导入失败！" + e.getMessage());
			return ro;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DBSql.close(conn);
		}

		ResponseObject ro = ResponseObject.newOkResponse();
		return ro;
	}

}
