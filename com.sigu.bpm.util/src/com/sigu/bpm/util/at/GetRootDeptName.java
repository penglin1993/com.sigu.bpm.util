package com.sigu.bpm.util.at;

import java.sql.Connection;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

/**
 * 获取用户的根部门名称
 */
public class GetRootDeptName extends AbstExpression {

	public GetRootDeptName(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) {
		// 取第1个参数
		String uid = getParameter(expression, 1);
		if (uid == null || uid.trim().equals("")) {
			uid = getExpressionContext().getUserContext().getUID();
		} else {
			uid = uid.trim();
		}

		// 查询用户的根部门名称（所属单位名称）
		Connection conn = null;
		try {
			conn = DBSql.open();
			String sql = "SELECT DEPARTMENTID FROM ORGUSER WHERE USERID=?";
			String deptId = DBSql.getString(conn, sql, new Object[] { uid });
			String rootDeptName = getDeptName(conn, deptId);
			return rootDeptName;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBSql.close(conn);
		}
		return "";
	}

	/**
	 * 递归查询上级部门，直到找到根部门
	 * @param conn
	 * @param deptId
	 * @return
	 */
	private String getDeptName(Connection conn, String deptId) {
		if (deptId == null || deptId.trim().equals("")) {
			return "";
		}
		String sql = "SELECT PARENTDEPARTMENTID, DEPARTMENTNAME FROM ORGDEPARTMENT WHERE ID =?";
		RowMap row = DBSql.getMap(conn, sql, new Object[] { deptId });
		String pid = row.getString("PARENTDEPARTMENTID");
		if ("0".equals(pid)) {
			return row.getString("DEPARTMENTNAME");
		} else {
			return getDeptName(conn, pid);
		}
	}

}
