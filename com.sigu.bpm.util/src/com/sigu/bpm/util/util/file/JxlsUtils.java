package com.sigu.bpm.util.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

public class JxlsUtils {

	public static void exportExcel(Map<String, Object> dataMap, String templatePath, String exportPath)
			throws Exception {
		File template = getTemplate(templatePath);
		if (template == null) {
			throw new Exception("Excel 模板未找到！");
		}

		Context context = PoiTransformer.createInitialContext();
		if (dataMap != null) {
			for (String key : dataMap.keySet()) {
				context.putVar(key, dataMap.get(key));
			}
		}

		InputStream is = new FileInputStream(templatePath);
		OutputStream os = new FileOutputStream(exportPath);
		JxlsHelper jxlsHelper = JxlsHelper.getInstance();
		Transformer transformer = jxlsHelper.createTransformer(is, os);

		// JexlExpressionEvaluator evaluator =
		// (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
		// //设置静默模式，不报警告
		// evaluator.getJexlEngine().setSilent(true);
		// // 函数强制，自定义功能
		// Map<String, Object> funcs = new HashMap<String, Object>();
		// funcs.put("utils", new JxlsUtils()); //添加自定义功能
		// evaluator.getJexlEngine().setFunctions(funcs);

		jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
		is.close();
		os.close();
	}

	// 获取jxls模版文件
	public static File getTemplate(String path) {
		File template = new File(path);
		if (template.exists()) {
			return template;
		}
		return null;
	}

}