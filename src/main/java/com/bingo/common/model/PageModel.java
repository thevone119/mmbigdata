package com.bingo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @description   <分页模型>
 * @about   所有model对象必须继承该对象。
 * @author   李丽全
 * @since   2016年9月8日 13:28:17
 */
public abstract class PageModel implements Serializable {
	private static final long serialVersionUID = 5388400428076673992L;
	//计算公式：start = (pageNo - 1) * pageSize;
	private int start;  //记录开始的行数，不等于pageNo（当前页）
	private int limit;  //每页记录数，等价于pageSize
	private String order;  //排序方式，desc或者asc
	private String sort;  //排序字段

	private long totalCount = 0;
	
	private int pageNo;// 当前页
	private int pageSize;// 每页记录数

	public int getStart() {
		if(pageNo > 0) {
			start = (pageNo - 1) * pageSize;
		}
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		if(pageSize > 0) {
			limit = pageSize;
		}
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 复制不为空的字段到目标对象，只复制不为空的字段
	 * @param src
	 * @return true:发生变化  false:没发生变化
	 * @throws IllegalAccessException
	 */
	public  boolean copyPropertiesIgnoreNull(Object src) throws IllegalAccessException {
		final org.springframework.beans.BeanWrapper _src = new org.springframework.beans.BeanWrapperImpl(src);
		final org.springframework.beans.BeanWrapper _target = new org.springframework.beans.BeanWrapperImpl(this);
		java.beans.PropertyDescriptor[] pds = _src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = _src.getPropertyValue(pd.getName());
			Object targetValue = _target.getPropertyValue(pd.getName());
			if (srcValue == null){
				emptyNames.add(pd.getName());
			}else{
				if(srcValue.equals(targetValue)){
					emptyNames.add(pd.getName());
				}
			}
		}
		if(emptyNames.size()==pds.length){
			return false;
		}
		String[] result = new String[emptyNames.size()];

		org.springframework.beans.BeanUtils.copyProperties(src, this, emptyNames.toArray(result));
		return true;
	}

	/**
	 * @description   <排序>
	 * @about   bootstrap-table专用的排序功能。
	 * @return
	 */
	@JsonIgnore
	@Transient
	public String getOrderBy() {
		if(sort != null && order != null) {
			return sort + " " + order;
		}
		return null;
	}
}