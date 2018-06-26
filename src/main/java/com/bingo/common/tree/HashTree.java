package com.bingo.common.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 基与HashMap的树 用此树解决查询数据库中的树结构时递归调用数据库的性能问题。
 * 用此树数据库中的树只要一次查询，不要使用递归了
 * @author huangtw
 */
public class HashTree {
	private Map map = new HashMap();
	
	public HashTree(){
		
	}
	
	/**
	 * 添加一个节点
	 */
	public void addNode(TreeNode node){
		String key = "obj_" + node.getId();
		if(this.map.containsKey(key)){
			return;
		}else{
			this.map.put(key, node);
		}
		String lkey = "list_"+node.getPid();
		List list = (List)this.map.get(lkey);
		if(list ==null){
			list = new ArrayList();
			this.map.put(lkey, list);
		}
		list.add(node);
	}
	
	/**
	 * 删除一个节点
	 * @param node
	 */
	public void delNode(int id){
		String key = "obj_" + id;
		String lkey = "list_"+id;
		TreeNode node = this.getNode(id);
		if(node==null) return;
		List nextlist = this.getNextNodes(node.getPid());
		this.map.remove(key);
		this.map.remove(lkey);
		if(nextlist==null) return;
		for(int i=0;i<nextlist.size();i++){
			TreeNode _node = (TreeNode)nextlist.get(i);
			if(_node.getId()==id) {
				nextlist.remove(i);
				break;
			}
		}
	}
	
	/**
	 * 取一个节点
	 * @param id
	 * @return
	 */
	public TreeNode getNode(int id){
		String key = "obj_" + id;
		return (TreeNode)this.map.get(key);
	}
	
	
	/**
	 * 删除下级节点
	 * @param pid
	 */
	public void delNextNodes(int pid){
		String lkey = "list_"+pid;
		List nodelist = this.getNextNodes(pid);
		if(nodelist==null) return;
		for(int i=0;i<nodelist.size();i++){
			TreeNode node = ((TreeNode)nodelist.get(i));
			this.map.remove("obj_" + node.getId());
		}
		this.map.remove(lkey);
	}
	
	/**
	 * 取下级节点
	 * @param psid
	 * @return
	 */
	public List getNextNodes(int pid){
		String lkey = "list_"+pid;
		return (List)this.map.get(lkey);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			Entry en = (Entry)it.next();
			if(en.getKey().toString().indexOf("obj_")!=-1){
				TreeNode node = (TreeNode)en.getValue();
				sb.append(node.getId() + "," + node.getPid() + "," + node.getText());
			}
		}
		return sb.toString();
	}

}
