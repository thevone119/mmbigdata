package com.bingo.common.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * FreeMarker发布模板，用于通过模板文件生成文件处理
 */
public class FreeMarkerRelease {
	private Map map;
	private String templatepath;
	private String tempPath;
	private String targetPath;
	private boolean isReplace = false;
	public FreeMarkerRelease(String templatepath,String tempPath,String targetPath,Map map){
		this.tempPath = tempPath;
		this.templatepath = templatepath;
		this.targetPath = targetPath;
		this.map = map;
	}
	
	public FreeMarkerRelease(String templatepath,String tempPath,String targetPath,Map map,boolean isReplace){
		this.tempPath = tempPath;
		this.templatepath = templatepath;
		this.targetPath = targetPath;
		this.map = map;
		this.isReplace = isReplace;
	}
	
	/**
	 * 生成相应的文件
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public void release() throws IOException, TemplateException{
		File file = new File(targetPath);
		if(file.exists()&&!isReplace){
			System.out.println("ERROR:File is Exists Exception:"+targetPath);
			return;
		}
		file.getParentFile().mkdirs();
		System.out.println("create File:"+targetPath);
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(this.getClass(), "/"+templatepath+"/");
		//cfg.setTemplateUpdateDelay(500); //模版缓存500秒
		cfg.setDefaultEncoding("utf-8");  //设置模版的编码 
		Template temp = cfg.getTemplate(tempPath);
		FileOutputStream fos = new FileOutputStream(targetPath);
		Writer out = new OutputStreamWriter(fos,"UTF-8");
        temp.process(map, out);
        out.flush();
        out.close();
	}
}
