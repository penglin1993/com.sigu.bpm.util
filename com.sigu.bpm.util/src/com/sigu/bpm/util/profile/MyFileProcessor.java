package com.sigu.bpm.util.profile;

import java.util.Map;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.fs.AbstFileProcessor;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.server.fs.dc.DCMessage;
import com.actionsoft.bpms.util.UtilFile;
import com.sigu.bpm.util.web.ImportWeb;

/**
 * 当前应用的自定义文件处理器
 */
public class MyFileProcessor extends AbstFileProcessor {
	/**声明当前应用的文档仓库目录名称*/
	public static String repositoryName = "cd";
	
	@Override
	public boolean uploadReady(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		System.out.println("准备上传文件--" + context.getPath() + context.getFileName());
		return true;
	}

	@Override
	public void uploadError(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		System.out.println("上传失败--" + context.getPath() + context.getFileName());
	}

	@Override
	public void uploadBeforeEncrypt(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		System.out.println("已上传明文，准备加密前--" + context.getPath() + context.getFileName());
	}

	@Override
	public void uploadSuccess(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		context.setDCMessage(DCMessage.OK, "");
		context.getDCMessage().addAttr("fileName", context.getFileName());
		context.getDCMessage().addAttr("url", context.getDownloadURL());
		System.out.println("上传成功--" + context.getPath() + context.getFileName());
		
		UtilFile tmpFile = new UtilFile(context.getFilePath());
		// 处理文件 导入Excel数据
		ImportWeb web = new ImportWeb();
		ResponseObject ro = web.importExcelData(tmpFile, context.getSession(), context.getExtParam());
		if (ro.isOk()) {
			context.setDCMessage(DCMessage.OK, ro.getMsg());
		} else {
			context.setDCMessage(DCMessage.ERROR, ro.getMsg());
		}
		// 处理完成后删除文件
		tmpFile.delete();
	}

	@Override
	public boolean downloadValidate(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		System.out.println("下载校验--" + context.getPath() + context.getFileName());
		return true;
	}

	@Override
	public void downloadComplete(Map<String, Object> param) {
		DCContext context = (DCContext) param.get("DCContext");
		System.out.println("下载结束--" + context.getPath() + context.getFileName());
	}
}
