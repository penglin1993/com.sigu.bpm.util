package com.sigu.bpm.util.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class WordUtil {
	private static Configuration configuration = new Configuration();

	/**
	 * 通过FreeMkaer导出Word文档
	 * 
	 * @param dataMap
	 * @param templatePath
	 * @param templateName
	 * @param fileName
	 *            导出文件的路径加文件名
	 */
	public static void exportDoc(Map<String, Object> dataMap, String templatePath, String templateName, String fileName)
			throws Exception {
		// 创建一个Configuration对象
		// Configuration configuration = new Configuration();
		// 设置默认字符集utf-8.
		configuration.setDefaultEncoding("utf-8");
		// 设置模板文件所在的路径
		configuration.setDirectoryForTemplateLoading(new File(templatePath));
		// 加载指定的模板对象
		Template template = configuration.getTemplate(templateName);

		FileOutputStream fos = new FileOutputStream(new File(fileName));
		OutputStreamWriter oWriter = new OutputStreamWriter(fos, "UTF-8");
		Writer out = new BufferedWriter(oWriter);

		// 调用模板对象的process方法输出文件
		template.process(dataMap, out);
		// 关闭流
		out.close();
		fos.close();
	}

}
