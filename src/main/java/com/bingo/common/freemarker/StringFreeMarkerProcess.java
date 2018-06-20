package com.bingo.common.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 基于字符串的FreeMarker处理
 */
public class StringFreeMarkerProcess {
	private static Configuration  cfg = new Configuration();
	private  Object model;
	private String template;
	private String templatecode= "myTemplate";
	public StringFreeMarkerProcess(String template,  Object model){
		this.template = template;
		this.model = model;
	}

	public StringFreeMarkerProcess(String templatecode,String template, Object model){
		this.templatecode = templatecode;
		this.template = template;
		this.model = model;
	}

	
	/**
	 * 生成相应的文件
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public String toMarkString() throws Exception {
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate(templatecode,template);
		cfg.setTemplateLoader(stringLoader);
		//cfg.setTemplateUpdateDelay(500); //模版缓存500秒
		cfg.setDefaultEncoding("utf-8");  //设置模版的编码
		Template template = cfg.getTemplate(templatecode,"utf-8");
		return FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
	}

	public static void main(String[] args) throws Exception {
		/*SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		KDOrder order = new KDOrder();
		KDOrder reqorder = new KDOrder();
		order.setCrtTime(formatter.parse("2017-12-10 11:02:06"));
		reqorder.setYyTime(formatter.parse("2017-12-24 16:15:00"));
		Integer p_timeout = new Integer(72);//超时，小时
		Calendar cal = Calendar.getInstance();
		cal.setTime(order.getCrtTime());
		cal.add(Calendar.HOUR,p_timeout);
		boolean isguaqi = false;//是否挂起
		if(cal.getTime().before(reqorder.getYyTime())){//预约超时,自动转管控回访
			isguaqi = true;
		}
		//提前预约不挂起
		if(order.getYyTime()!=null && order.getYyTime().after(reqorder.getYyTime())){
			isguaqi = false;
		}
		//当天不挂起
		if(reqorder.getYyTime().getDay()==new Date().getDay()){
			isguaqi = false;
		}
		System.out.println(reqorder.getYyTime().getDay());
		System.out.println(new Date().getDay());
		System.out.println(isguaqi);*/

	}
}
