package com.bingo.common.exception;

/**
 * @Copyright:广州市品高软件股份有限公司
 * @Author:李丽全
 * @Email:15119575223@139.com
 * @Telephone:15119575223
 * @Date:2017年6月22日 17:14:17
 * @Description:自定义异常类：DAO层异常
 */
public class DaoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DaoException() {
		super();
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}
}