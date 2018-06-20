package com.bingo.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description   <分页>
 * @about
 * @author   李丽全
 * @since   2016年9月8日 15:02:23
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = 5388400428076673992L;
	//返回结果
	private List<T> result = new ArrayList<>();
	private long totalCount = 0;

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
}