package com.sigu.bpm.util.at;

import java.sql.Connection;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;

/**
 * 获取用户的根部门ID（一般用于集团型组织架构中获取用户的所属单位）
 */
public class GetRootDeptId extends AbstExpression {

	public GetRootDeptId(ExpressionContext atContext, String expressionValue) {
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

		// 查询用户的根部门ID（所属单位ID）
		Connection conn = null;
		try {
			conn = DBSql.open();
			String sql = "SELECT DEPARTMENTID FROM ORGUSER WHERE USERID=?";
			String deptId = DBSql.getString(conn, sql, new Object[] { uid });
			String rootDeptId = getDeptId(conn, deptId);
			return rootDeptId;
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
	private String getDeptId(Connection conn, String deptId) {
		if (deptId == null || deptId.trim().equals("")) {
			return "";
		}
		String sql = "SELECT PARENTDEPARTMENTID FROM ORGDEPARTMENT WHERE ID=?";
		String pid = DBSql.getString(conn, sql, new Object[] { deptId });
		if ("0".equals(pid)) {
			return deptId;
		} else {
			return getDeptId(conn, pid);
		}
	}

}
