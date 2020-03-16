package com.sigu.bpm.util.sample.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class PxProcessAfterCompleteEvent extends ExecuteListener {

	public PxProcessAfterCompleteEvent() {
		this.setDescription("接收同意请假节点办理完毕后，将数据写入考勤统计表！");
		this.setProvider("hufangyun");
		this.setVersion("1.0");
	}

	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		UserContext uc = pec.getUserContext();
		String bindId = pec.getProcessInstance().getId();
		
		//清除旧的数据
		SDK.getBOAPI().removeByBindId("BO_EU_KQTJ", bindId);
		
		// 获取当前表单的数据对象
		BO bo = SDK.getBOAPI().query("BO_EU_QJLC_YGQJ2").bindId(bindId).detail();

		// 获取需要写入到考勤统计表的数据
		String xm = bo.getString("NAME");
		String zh = bo.getCreateUser();
		String lx = bo.getString("QJLB");
		String kssj = bo.getString("KSSJ");
		String jssj = bo.getString("JSSJ");
		String ts = bo.getString("QJTS");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date1 = null;
		Date date2 = null;
		try {
			date1 = sdf.parse(kssj);
			date2 = sdf.parse(jssj);

			if (date1.equals(date2)) {
				System.out.println(sdf.format(date1) + ":" + ts);
				// 将数据写入考勤统计表
				BO newBo = new BO();
				newBo.setBindId(bindId);
				newBo.set("BINDID", bindId);
				newBo.set("XM", xm);
				newBo.set("ZH", zh);
				newBo.set("LX", lx);
				newBo.set("TS", ts);
				newBo.set("RQ", sdf.format(date1));
				SDK.getBOAPI().createDataBO("BO_EU_KQTJ", newBo, uc);
			} else {
				Double days = Double.parseDouble(ts);
				while (!date1.after(date2)) {
					Double day = days < 1 ? days : 1;
					System.out.println(sdf.format(date1) + " : " + day);
					// 将数据写入考勤统计表
					BO newBo = new BO();
					newBo.set("XM", xm);
					newBo.set("ZH", zh);
					newBo.set("LX", lx);
					newBo.set("TS", day);
					newBo.set("RQ", sdf.format(date1));
					SDK.getBOAPI().createDataBO("BO_EU_KQTJ", newBo, uc);

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date1);
					calendar.add(Calendar.DATE, 1);
					date1 = calendar.getTime();
					days = days - day;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
