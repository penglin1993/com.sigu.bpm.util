package com.sigu.bpm.util;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.ASLPPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.DCPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.ProcessPublicEventPluginProfile;
import com.sigu.bpm.util.aslp.ExportExcelAslp;
import com.sigu.bpm.util.aslp.ExportExcelNoTemplateAslp;
import com.sigu.bpm.util.aslp.ExportWordAslp;
import com.sigu.bpm.util.aslp.RtxNotificationAslp;
import com.sigu.bpm.util.aslp.SimNotificationAslp;
import com.sigu.bpm.util.at.GetRootDeptId;
import com.sigu.bpm.util.at.GetRootDeptName;
import com.sigu.bpm.util.profile.MyFileProcessor;
import com.sigu.bpm.util.profile.MyProcesssListener;

/**
 * 该JAVA类需要注册到当前应用的配置属性中
 */
public class Plugins implements PluginListener {

	@Override
	public List<AWSPluginProfile> register(AppContext context) {
		// 存放本应用的全部插件扩展点描述
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();

		// 注册AT公式
		list.add(new AtFormulaPluginProfile("自定义", "@getRootDeptId(uid)", GetRootDeptId.class.getName(),
				"获取用户所属单位(根部门)ID", "返回用户所属单位(根部门)ID"));
		list.add(new AtFormulaPluginProfile("自定义", "@getRootDeptName(uid)", GetRootDeptName.class.getName(),
				"获取用户所属单位(根部门)名称", "返回用户所属单位(根部门)名称"));

		// 注册DC Customize Document
		list.add(new DCPluginProfile(MyFileProcessor.repositoryName, MyFileProcessor.class.getName(), "自定义文件处理器", false));
		
		// 注册流程全局事件监听器
       list.add(new ProcessPublicEventPluginProfile(MyProcesssListener.class.getName(), "自定义流程全局监听器"));
        
        //注册ASLP
        list.add(new ASLPPluginProfile("exportWord", ExportWordAslp.class.getName(), "Word模板导出", null));
        list.add(new ASLPPluginProfile("exportExcel", ExportExcelAslp.class.getName(), "Excel模板导出", null));
        list.add(new ASLPPluginProfile("exportExcelNoTemplate", ExportExcelNoTemplateAslp.class.getName(), "Excel无模板直接导出", null));
        list.add(new ASLPPluginProfile("simNotification", SimNotificationAslp.class.getName(), "腾讯通消息提醒推送接口", null));
        list.add(new ASLPPluginProfile("rtxNotification", RtxNotificationAslp.class.getName(), "密信通消息提醒推送接口", null));

		return list;
	}

}
