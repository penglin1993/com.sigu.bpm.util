package com.sigu.bpm.util.sample.event;

import java.util.List;

import com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.org.cache.RoleCache;
import com.actionsoft.bpms.org.model.RoleModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class DataWindowFormatSQLEvent implements DataWindowFormatSQLEventInterface {
	private static final String GSLD = "公司领导";
	private static final String ZGZZ = "中干正职";
	private static final String ZGFZ = "中干副职";

	public String formatSQL(UserContext me, DataView view, String sql) {
		String where = getCondition(me);
		sql = sql.replace("1=1", where);
		return sql;
	}

	private String getCondition(UserContext me) {
		String where = "(";
		UserModel user = me.getUserModel();
		String roles = getRoleNamesOfUser(user);

		// 总经办秘书和总经办正职可见公司领导
		boolean leader = false;
		if (me.getDepartmentModel().getName().equals("总经理办公室") && user.getExt2().equals(ZGZZ)) {
			leader = true;
		}
		if (roles.indexOf("总经理办公室秘书") != -1) {
			leader = true;
		}

		// 党群工作部干部考勤员和党群工作部正职可见所有正副职和公司领导
		boolean leaderAndZg = false;
		if (me.getDepartmentModel().getName().equals("党群工作部") && user.getExt2().equals(ZGZZ)) {
			leaderAndZg = true;
		}
		if (roles.indexOf("党群工作部党委秘书") != -1) {
			leaderAndZg = true;
		}

		// 公司领导和人力资源部考勤人员和人力资源部正副职可见所有人员
		boolean all = false;
		if (user.getExt2().equals(GSLD)) {
			all = true;
		}
		if (me.getDepartmentModel().getName().equals("人力资源部")
				&& (user.getExt2().equals(ZGZZ) || user.getExt2().equals(ZGFZ))) {
			all = true;
		}
		if (roles.indexOf("人力资源部员工培训") != -1) {
			all = true;
		}

		// 部门考勤人员和部门正副职可见本部门所有人员
		boolean dept = false;
		if (user.getExt2().equals(ZGZZ) || user.getExt2().equals(ZGFZ)) {
			dept = true;
		}
		if (roles.indexOf("考勤员") != -1) {
			dept = true;
		}

		// 将为true的条件转换为where子句
		if (leader) {
			where += " ZW='" + GSLD + "' OR ";
		}
		if (leaderAndZg) {
			where += " ZW='" + GSLD + "' OR ZW='" + ZGZZ + "' OR ZW='" + ZGFZ + "' OR ";
		}
		if (dept) {
			where += " BM='" + me.getDepartmentModel().getName() + "' OR ";
		}
		if (all) {
			where += " 1=1 OR ";
		}
		where += " NAME='" + me.getUserName() + "') ";

		return where;

	}

	private String getRoleNamesOfUser(UserModel user) {
		StringBuffer roles = new StringBuffer();
		List<UserMapModel> usermaps = SDK.getORGAPI().getUserMaps(user.getUID());
		for (UserMapModel userMap : usermaps) {
			RoleModel roleModel = RoleCache.getModel(userMap.getRoleId());
			if (roleModel != null) {
				String fullName = roleModel.getCategoryName() + "/" + roleModel.getName();
				roles.append(fullName);
				roles.append(" ");
			}
		}
		RoleModel roleModel = RoleCache.getModel(user.getRoleId());
		if (roleModel != null) {
			String fullName = roleModel.getCategoryName() + "/" + roleModel.getName();
			roles.append(fullName);
			roles.append(" ");
		}
		return roles.toString();
	}

}
