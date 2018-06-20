package com.bingo.common.utility;

import org.springframework.context.ApplicationContext;

/**
 * @Copyright:广州市品高软件股份有限公司
 * @Author:李丽全
 * @Email:15119575223@139.com
 * @Telephone:15119575223
 * @Date:2017年7月26日 15:40:58
 * @Description:spring boot工具
 */
public class SpringBootUtils {
	private static ApplicationContext applicationContext = null;
	
	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringBootUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(String bean, Class<T> clazz) {
		return applicationContext.getBean(bean, clazz);
	}
}