package com.bingo.common.controller;

import com.bingo.common.model.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description   <公共的controller>
 * @about   定义成abstract抽象类，防止别人new这个对象，必须使用继承。
 * @author   李丽全
 * @since   2016年9月8日 22:00:35
 */
public abstract class BaseController {
	public static final String SUCCESS = "操作成功";
	public static final String ERROR = "操作失败";
	
	public Map<String, Object> success() {
		return model(true, SUCCESS);
	}
	
	public Map<String, Object> success(String msg) {
		return model(true, msg);
	}
	
	public Map<String, Object> error() {
		return model(false, ERROR);
	}
	
	public Map<String, Object> error(String msg) {
		return model(false, msg);
	}
	
	/**
	 * @about   
	 * 与@ResponseBody一起使用。
	 * 直接返回Map，经过json转换，会变成json数据返回到前端。
	 * @param success
	 * @param msg
	 * @return
	 */
	public Map<String, Object> model(boolean success, String msg) {
		//HashMap默认起始容量是16，指定初始大小，可以节省资源。
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("success", success);
		map.put("msg", msg);
		return map;
	}
	
	public Map<String, Object> model(boolean success, String key, String value) {
		//HashMap默认起始容量是16，指定初始大小，可以节省资源。
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("success", success);
		map.put("msg", SUCCESS);
		map.put(key, value);
		return map;
	}
	
	public Map<String, Object> success(String key, String value) {
		return model(true, key, value);
	}
	
	public Map<String, Object> error(String key, String value) {
		return model(false, key, value);
	}
	
	/**
	 * @description   <页面跳转>
	 * @about   系统内部的页面重定向
	 * @param viewName
	 * @return
	 */
	public ModelAndView redirect(String viewName) {
		return new ModelAndView("redirect:/" + viewName);
	}
	
	/**
	 * @description   <页面跳转>
	 * @about   系统外部的页面重定向
	 * @param url   例如：http://xxx，一定要在地址前面添加http://
	 * @return
	 */
	public ModelAndView redirectOut(String url) {
		return new ModelAndView("redirect:" + url);
	}
	
	public ModelAndView getModel(String viewName, Map<String, Object> map) {
		return new ModelAndView(viewName, map);
	}
	
	public <T> Map<String, Object> getGridModel(Page<T> page) {
		Map<String, Object> map = null;
		if(page != null) {
			map = getGridModel(page.getTotalCount(), page.getResult());
		} else {
			map = new HashMap<String, Object>(2);
			map.put("total", 0);
			map.put("rows", "");
		}
		return map;
	}
	
	public <T> Map<String, Object> getGridModel(long total, List<T> rows) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("total", total);
		if(rows == null) {
			map.put("rows", "");
		} else {
			map.put("rows", rows);
		}
		return map;
	}
}