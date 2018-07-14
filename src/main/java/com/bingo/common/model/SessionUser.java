package com.bingo.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018-07-04.
 * 回话用户，保存到回话中。
 * 1.保存用户基础信息
 * 2.保存用户的角色信息
 * 3.保存用户的资源信息
 */
public class SessionUser implements Serializable {
    /**
     * 1.用户基础信息
     */
    public String  useracc;//用户名，登录账号
    public Integer  usertype=0;//用户类型，0：普通用户，1：管理员 ,2：支付商户
    public Long  userid;//userid
    public String  nikename;//昵称，中文名称

    /**
     * 2.用户角色信息（保存用户的角色ID，角色编码）
     */
    public List<Long> roleids;
    public List<String> rolecodes;

    /**
     * 3.用户的资源信息（保存用户的资源ID）
     */
    public List<Long> resids;


    public String getUseracc() {
        return useracc;
    }

    public void setUseracc(String useracc) {
        this.useracc = useracc;
    }

    public Integer getUsertype() {
        return usertype;
    }

    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public List<Long> getRoleids() {
        return roleids;
    }

    public void setRoleids(List<Long> roleids) {
        this.roleids = roleids;
    }

    public List<String> getRolecodes() {
        return rolecodes;
    }

    public void setRolecodes(List<String> rolecodes) {
        this.rolecodes = rolecodes;
    }

    public List<Long> getResids() {
        return resids;
    }

    public void setResids(List<Long> resids) {
        this.resids = resids;
    }
}
