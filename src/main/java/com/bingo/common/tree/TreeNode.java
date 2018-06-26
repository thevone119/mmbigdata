package com.bingo.common.tree;

/**
 * 树的节点对象。
 * @author huangtw
 */
public class TreeNode {
	private int id;
	private int pid;
	private boolean checked=false;//是否选中
	private String text;
	private String url;
	private String target;
	
	public TreeNode(){
		
	}
	
	public TreeNode(int id,int pid,String text,String url,String target){
		this.id = id;
		this.pid = pid;
		this.text = text;
		this.url = url;
		this.target = target;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	

}
