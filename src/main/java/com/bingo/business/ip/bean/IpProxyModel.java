package com.bingo.business.ip.bean;

import com.bingo.common.model.PageModel;

import javax.persistence.*;

/**
 * @author huangtw
 * 2017-08-23 16:34:30
 * 对象功能: 系统系列号生成 Model对象
 */
@Entity
@Table(name="t_ip_proxy")
public class IpProxyModel  extends PageModel {


	@Id
	@Column(name = "ip")
	public String ip;

	public Integer prot;
	public String protocol;
	public Integer proxy_type;
	public String loc;
	public Integer speed ;
	public String update_time;
	public Integer check_time ;
	public String src_url;



	/**
	 * 对象构建方法
	 * @return
	 */
	public IpProxyModel(){
	
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getProt() {
		return prot;
	}

	public void setProt(Integer prot) {
		this.prot = prot;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Integer getProxy_type() {
		return proxy_type;
	}

	public void setProxy_type(Integer proxy_type) {
		this.proxy_type = proxy_type;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public Integer getCheck_time() {
		return check_time;
	}

	public void setCheck_time(Integer check_time) {
		this.check_time = check_time;
	}

	public String getSrc_url() {
		return src_url;
	}

	public void setSrc_url(String src_url) {
		this.src_url = src_url;
	}
}
