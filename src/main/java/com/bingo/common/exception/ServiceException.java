package com.bingo.common.exception;

/**
 * @Copyright:广州市品高软件股份有限公司
 * @Author:李丽全
 * @Email:15119575223@139.com
 * @Telephone:15119575223
 * @Date:2017年6月22日 17:14:41
 * @Description:自定义异常类：Service层异常
 */
public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	protected ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}