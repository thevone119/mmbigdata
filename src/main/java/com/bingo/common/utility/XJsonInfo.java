package com.bingo.common.utility;


import com.bingo.common.model.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xxx on 2017-7-26.
 */
public class XJsonInfo {
    private Long total;
    private Integer totalPages;
    private Boolean success;
    private Integer code;
    private String msg;
    private Object data;

    public Long getTotal() {
        return total;
    }

    public XJsonInfo setTotal(Long total) {
        this.total = total;
        return this;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getSuccess() {
        return success;
    }

    public XJsonInfo setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public static XJsonInfo success() {
        return new XJsonInfo(true);
    }

    public static XJsonInfo error() {
        return new XJsonInfo(false);
    }

    /**
     * Instantiates a new X json info.
     */
    public XJsonInfo() {
        this(true);
    }

    /**
     * Instantiates a new X json info.
     *
     * @param p_success the p success
     */
    public XJsonInfo(boolean p_success) {
        this.success = p_success;
        Init();
    }

    /**
     * Instantiates a new X json info.
     *
     * @param p_success the p success
     * @param p_msg     the p msg
     */
    public XJsonInfo(boolean p_success, String p_msg) {
        this.success = p_success;
        this.msg = p_msg;
        Init();
    }

    private void Init() {
        if (this.msg == null) {
            if (this.success) {
                this.code = 200;
                this.msg = "操作成功";
            } else {
                this.code = 500;
                this.msg = "操作失败";
            }
        }
    }

    public <T> XJsonInfo setPageData(Page<T> page) {
        this.total = page.getTotalCount();
        this.data = page.getResult();
        return this;
    }

    /**
     * 设置返回的正文数据
     *
     * @param p_obj 正文内容
     */
    public XJsonInfo setData(Object p_obj) {
        if (p_obj == null) {
            return this;
        }

        this.data = p_obj;

        if (p_obj.getClass().isArray()) {
            Object[] os = (Object[]) p_obj;
            this.total = (long) os.length;
        } else if (p_obj instanceof ArrayList) {
            ArrayList os = (ArrayList) p_obj;
            this.total = (long) os.size();
        } else {
            this.total = 1L;
        }

        return this;
    }

    /**
     * 设置返回的正文数据，一般分页会用到
     *
     * @param p_obj   正文内容
     * @param p_total 数据总行数
     */
    public XJsonInfo setData(Object p_obj, Long p_total) {
        setData(p_obj);
        this.total = p_total;
        return this;
    }

    public XJsonInfo setData(Object p_obj, Long p_total, int p_totalPages) {
        setData(p_obj);
        this.total = p_total;
        this.totalPages = p_totalPages;
        return this;
    }

    public XJsonInfo setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public XJsonInfo setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 设置json文本
     *
     * @param raw 文本
     */
    public XJsonInfo setJsonData(String raw) throws IOException {
        setData(JsonClass.fromJSON(raw, Map.class));
        this.total = 1L;
        return this;
    }

    /**
     * 转为JSON格式
     *
     * @return JSON化后的实体
     */
    public String toJson() {
        return JsonClass.toJSON(this);
    }

    /**
     * 正文转为实体
     *
     * @param <T>        the type parameter
     * @param modelclass 实体的类型
     * @return the t
     */
    public <T> T toDataModel(Class<T> modelclass) {
        //return JsonClass.fromJSON(this.attr.toString(), modelclass);
        return (T) this.data;
    }

    public String toDataString() {
        try {
            return this.data.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 正文转为动态实体
     * REVIEW 这里要注意，如果data直接就是对象，这样tostring会出问题
     * @return the t
     */
    public Map toDataMap() {
        return JsonClass.fromJSONToMap(this.data.toString());
    }

    /**
     * 正文转为集合
     * 只针对data是string的类型
     * @return the list
     */
    public List toDataList() {
        return JsonClass.fromJSON(this.data.toString(), List.class);
    }

}
