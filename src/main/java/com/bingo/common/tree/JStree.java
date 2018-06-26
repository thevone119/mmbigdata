package com.bingo.common.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对（js树）的便捷处理,为了提高性能结合HashTree一起使用
 * 为了方便，使用充血的类模型
 * 支持 后台的菜单树/dhtmlxtree/dtree/BStree/MzTreeView的js的生成
 * 支持树形下拉框的html的生成
 * 支持树形结构的数据的很多计算操作,如取第一个有效节点、取节点完整信息、取节点的所有子节点、所有父节点等等。
 * 
 * dhtmlxtree是功能最强的树,但是速度比较慢
 * dtree是最常用的树，操作和性能都不错，但不支持复选树
 * BStree是后台之前用的复选树。操作和性能都不太好
 * MzTreeView是速度最快的树，可以支持3-5W个节点
 * Ext.tree性能一般，功能还行
 * @author huangtw
 */
public class JStree {
	private HashTree tree = new HashTree();				//树的数据域
	private String treeName = "tree";						//树的名称,用于需要js操作时访问js树
	private int rootId = 0;								//树的根id
	private int currNodeId = 0;							//当前节点的id
	private String divId = "TreeId";						//树容器的id
	private String optionChar = "/";						//下拉框中节点的连接符
	private String selectIds = "";							//已选择的id 针对对复选树,用逗号(,)隔开。
	private boolean isCheckBox =  true;					//是否复选树,对dhtmlxtree复选树的js有效
	private boolean enableThreeStateCheckboxes =  false;	//是否级联复选,对dhtmlxtree复选树的js有效
	private boolean openAll  = false;						//是否全部节点展开
	private int deep = -1;									//树的深度,-1：默认无限深

	private String  basePath = "/";
	
	public JStree(){
	}

	/**
	 * 测试方法
	 * @param args
	 */
	public static void main(String args[]){
		JStree tree = new JStree();
		tree.addNode(-2, -1, "[根节点]");
		tree.addNode(2, -2, "[二级节点1]");
		tree.addNode(4, 2, "[三级节点1]");
		tree.addNode(5, 2, "[三级节点2]");
		tree.addNode(6, 5, "[四级节点1]");
		tree.addNode(7, 5, "[四级节点2]");
		tree.addNode(3, -2, "[二级节点2]");
		tree.setRootId(-2);
		tree.setDeep(2);
		//tree.deleteOutDeepNodes();
		tree.setDeep(-1);
		System.out.println(tree.getOptionHtml());

	}
	
	/**
	 * 添加一个节点
	 * @param id	节点id
	 * @param psid	父节点id
	 * @param text	节点内容
	 */
	public void addNode(int id,int pid,String text){
		TreeNode node = new TreeNode(id,pid,text,null,null);
		tree.addNode(node);
	}
	
	/**
	 * 添加一个节点
	 * @param node
	 */
	public void addNode(TreeNode node){
		tree.addNode(node);
	}
	
	/**
	 * 添加一个节点
	 * @param id
	 * @param pid
	 * @param text
	 * @param url
	 * @param target
	 */
	public void addNode(int id,int pid,String text,String url,String target){
		TreeNode node = new TreeNode(id,pid,text,url,target);
		tree.addNode(node);
	}
	
	/**
	 * 删除一个节点
	 * @param id
	 * @param pid
	 */
	public void delNode(int id){
		tree.delNode(id);
	}
	
	/**
	 * 删除下级节点
	 * @param pid
	 */
	public void delNextNodes(int pid){
		tree.delNextNodes(pid);
	}
	
	/**
	 * 取树的第一个有效节点的id
	 * @return
	 */
	public int getTreeFristNodeId() {
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist!=null&&rootlist.size()>=0){
			return ((TreeNode)rootlist.get(0)).getId();
		}
		return 0;
	}
	
	public String toString(){
		return tree.toString();
	}
	
	/**
	 * 取节点在树中排列的位置
	 */
	public int getNodeIndex(int id){
		StringBuffer index = new StringBuffer();
		this.getNextNodeIndex(id, rootId,index);
		return index.length();
	}
	private void getNextNodeIndex(int id,int pid,StringBuffer index){
		List rootlist = tree.getNextNodes(pid);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			if(index.indexOf("2")!=-1){
				break;
			}
			TreeNode node = (TreeNode)rootlist.get(i);
			if(node.getId()==id){
				index.append("2");
				break;
			}else{
				index.append("1");
			}
			if(index.indexOf("2")==-1){
				this.getNextNodeIndex(id,node.getId(),index);
			}
		}
	}
	
	/**
	 * 取节点的完整的信息如：[根节点]/[二级节点1]
	 * 支持 optionChar
	 * @param id
	 * @return
	 */
	public String getNodeFullText(int id){
		String ftext = "";
		TreeNode node = tree.getNode(id);
		if(node!=null){
			ftext = this.getOptionText(node);
		}
		return ftext;
	}
	
	
	/**
	 * 取selectIds节点中有效的复选节点，如 1,2,3 3个节点中2和3是1的子节点的话，则有效的复选节点是1。
	 * @return
	 */
	public String getEnableCheckBoxIds(String selectIds){
		String ids[] = selectIds.split(",");
		if(ids==null ||ids.length==0){
			return "";
		}
		//把selectIds放入到临时map中方便处理
		Map map = new HashMap();
		for(int i=0; i<ids.length;i++){
			if(ids[i]!=null&&!"".equals(ids[i])){
				map.put(ids[i], "1");
			}
		}
		//递归处理map中节点数据（把多余的子节点设置为null）
		for(int i=0; i<ids.length;i++){
			if(ids[i]!=null&&!"".equals(ids[i])){
				if(map.get(ids[i])!=null){
					this.getEnableCheckBoxIds(map,Integer.parseInt(ids[i]),2);
				}
			}
		}
		//取出有效的节点
		StringBuffer retIds = new StringBuffer(",");
		for(int i=0; i<ids.length;i++){
			if(ids[i]!=null&&!"".equals(ids[i])){
				if(map.get(ids[i])!=null){
					retIds.append(ids[i]+",");
				}
			}
		}
		return retIds.toString();
	}
	private void getEnableCheckBoxIds(Map map,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String key = node.getId()+"";
			if(map.containsKey(key)){
				map.put(key, null);
				this.getEnableCheckBoxIds(map,node.getId(),deep+1);
			}
		}
	}
	
	/**
	 * 删除超出深度的所有节点
	 * 支持rootId/deep的设置
	 * @return
	 */
	public void deleteOutDeepNodes(){
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			return ;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			this.deleteOutDeepNodes(node.getId(),2);
		}
	}
	private void deleteOutDeepNodes(int id,int deep){
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			if(this.deep!=-1 && deep>this.deep){
				rootlist.remove(i);
				i--;
			}
			this.deleteOutDeepNodes(node.getId(),deep+1);
		}
	}
	
	/**
	 * 删除节点
	 * 支持rootId的设置
	 * @return
	 */
	public void deleteNodes(String nodeIds){
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			return ;
		}
		nodeIds = "," + nodeIds + ",";
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			if(nodeIds.indexOf("," + node.getId() + ",")!=-1){
				rootlist.remove(i);
				i--;
			}else{
				this.deleteNodes(nodeIds,node.getId(),2);
			}
		}
	}
	private void deleteNodes(String nodeIds,int id,int deep){
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			if(nodeIds.indexOf("," + node.getId() + ",")!=-1){
				rootlist.remove(i);
				i--;
			}else{
				this.deleteNodes(nodeIds,node.getId(),deep+1);
			}
		}
	}
	

	/**
	 * 取selectIds节点的所有父节点,用逗号（,）串起来 前后都带了逗号 如",12,13,"
	 * @return
	 */
	public String getAllParentIds(String selectIds){//取所有父节点
		selectIds = "," + selectIds + ",";
		StringBuffer pIds = new StringBuffer(",");//所有父节点id
		String ids[] = selectIds.split(",");
		if(ids==null ||ids.length==0){
			return "";
		}
		for(int i=0; i<ids.length;i++){
			if(ids[i]!=null&&!"".equals(ids[i])){
				TreeNode node = this.tree.getNode(Integer.parseInt(ids[i]));
				if(node!=null){
					this.getAllParentIds(selectIds,pIds, node);
				}
			}
		}
		return pIds.toString();
	}
	private void getAllParentIds(String selectIds,StringBuffer pIds,TreeNode node){
		TreeNode pnode = this.tree.getNode(node.getPid());
		if(pnode!=null){
			String pix = ","+pnode.getId()+",";
			if(pIds.indexOf(pix)==-1){
				pIds.append(pnode.getId()+",");
			}
			if(selectIds.indexOf(pix)==-1){
				this.getAllParentIds(selectIds,pIds, pnode);
			}
		}
	}
	
	/**
	 * 取selectIds节点的所有子节点的id，用逗号串起来 前后都带了逗号 如",12,13,"
	 * @return
	 */
	public String getAllChildIds(String selectIds){
		StringBuffer cIds = new StringBuffer(",");//所有子节点id
		String ids[] = selectIds.split(",");
		if(ids==null ||ids.length==0){
			return "";
		}
		for(int i=0; i<ids.length;i++){
			if(ids[i]!=null&&!"".equals(ids[i])){
				TreeNode node = this.tree.getNode(Integer.parseInt(ids[i]));
				if(node==null) continue;
				List rootlist = tree.getNextNodes(node.getId());
				if(rootlist==null||rootlist.size()==0){
					continue;
				}
				for(int j=0;j<rootlist.size();j++){
					TreeNode cNode = (TreeNode)rootlist.get(j);
					String pix = ","+cNode.getId()+",";
					if(cIds.indexOf(pix)==-1 && selectIds.indexOf(pix)==-1){
						cIds.append(cNode.getId()+",");
						this.getAllChildIds(selectIds,cIds, cNode.getId(),2);
					}
				}
			}
		}
		return cIds.toString();
	}
	private void getAllChildIds(String selectIds,StringBuffer cIds,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String pix = ","+node.getId()+",";
			if(cIds.indexOf(pix)==-1 && selectIds.indexOf(pix)==-1){
				cIds.append(node.getId()+",");
				this.getAllChildIds(selectIds,cIds, node.getId(),deep+1);
			}
		}
	}
	
	
	
	/**
	 * 取dhtmlxtree树，支持单选，复选，级联复选。（更多的功能可通过直接在js里扩展）
	 * 主要用在简单的复选树。
	 * 默认打开所有选中的。
	 * 支持 treeName/currNodeId/selectIds/divId/isCheckBox/enableThreeStateCheckboxes 的设置
	 * @return
	 */
	public String getDhtmlxtreeJS(){
		if(!selectIds.startsWith(",")){
			selectIds = ","+selectIds;
		}
		if(!selectIds.endsWith(",")){
			selectIds = selectIds+",";
		}
		StringBuffer js = new StringBuffer("");
		StringBuffer openIds = new StringBuffer("");//需要打开的节点id
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			System.out.println("根节点id=" + rootId + "下无数据，如果需要重新定位根，请调用setRootId()方法！");
			return "";
		}
		js.append(treeName + "=new dhtmlXTreeObject(document.getElementById('"+ divId +"'),'100%','100%',0); ");
		js.append(treeName + ".setImagePath('"+ basePath +"js/dhtmlxTree/imgs/'); ");	
		if(isCheckBox){
			js.append(treeName + ".enableCheckBoxes(1);");	
		}
		if(enableThreeStateCheckboxes){
			js.append(treeName + ".enableThreeStateCheckboxes(true); ");	
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String state = "";
			if(currNodeId==node.getId()){
				state += "SELECT";
			}
			if(selectIds.indexOf(","+node.getId()+",")!=-1){
				state += ",CHECKED";
			}
			js.append(treeName + ".insertNewChild("+  node.getPid()+","+ node.getId() +",'"+node.getText()+"',0,0,0,0,'"+ state +"'); ");
			this.getDhtmlxtreeJS(js,openIds, node.getId(),2);
		}
		if(!openAll){//打开选中的父节点
			js.append(treeName + ".closeAllItems(0);");
			String checkPids[] = openIds.toString().split(",");
			for(int i=0; i<checkPids.length;i++){
				if(checkPids[i]!=null && !"".equals(checkPids[i])){
					js.append(treeName + ".openItem("+ checkPids[i] +");");
				}
			}
		}
		return js.toString();
	}
	private void getDhtmlxtreeJS(StringBuffer js,StringBuffer openIds,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String state = "CHILD";
			if(currNodeId==node.getId()){
				state += ",SELECT";
			}
			if(selectIds.indexOf(","+node.getId()+",")!=-1){
				state += ",CHECKED";
				if(openIds.indexOf(","+node.getPid()+",")==-1){
					openIds.append(","+node.getPid()+",");
				}
			}
			if(this.currNodeId==node.getId()&&openIds.indexOf(","+node.getPid()+",")==-1){
				openIds.append(","+node.getPid()+",");
			}
			//state = "CHILD,CHECKED";
			js.append(treeName + ".insertNewChild("+  node.getPid()+","+ node.getId() +",'"+node.getText()+"',0,0,0,0,'"+ state +"'); ");
			this.getDhtmlxtreeJS(js, openIds , node.getId(),deep+1);
		}
	}
	
	/**
	 * 取BS树，支持复选/级联复选 （复选和级连复选等设置需要在js中设置）。
	 * 主要用在简单的复选树。
	 * 默认打开所有选中的。
	 * 支持 treeName/selectIds 的设置
	 * @return
	 */
	public String getBStreeJS(){
		if(!selectIds.startsWith(",")){
			selectIds = ","+selectIds;
		}
		if(!selectIds.endsWith(",")){
			selectIds = selectIds+",";
		}
		StringBuffer js = new StringBuffer("");
		StringBuffer openIds = new StringBuffer("");
		if(!this.openAll){
			openIds.append(this.getAllParentIds(selectIds));//需要打开的节点id
		}
		
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			System.out.println("根节点id=" + rootId + "下无数据，如果需要重新定位根，请调用setRootId()方法！");
			return "";
		}
		js.append(treeName + "= new Array;\n");
		String parent = treeName;
		
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			js.append(parent + "[" + i + "] = new Array;\n");
			js.append(parent + "[" + i + "]['caption'] = '" + node.getText() + "';\n");
			//js.append(parent + "[" + i + "]['url'] = '';");
			//js.append(parent + "[" + i + "]['target'] = '" + this.target + "';\n");
			if(selectIds.indexOf("," + node.getId() + ",")!=-1){
				js.append(parent + "[" + i + "]['isChecked'] = 2;");//2和0
			}
			if(this.openAll||openIds.indexOf("," + node.getId() + ",")!=-1){
				js.append(parent + "[" + i + "]['isOpen'] = " + true + ";\n");
			}
			js.append(parent + "[" + i + "]['checkboxName'] = '"+ treeName + "_" + node.getId() + "';\n");
			List nextlist = tree.getNextNodes(node.getId());
			if(nextlist!=null&&nextlist.size()!=0){
				js.append(parent + "[" + i + "]['children'] = new Array;\n");
				this.getBStreeJS(js,openIds, parent + "[" + i + "]['children']",nextlist,2);
			}
		}
		return js.toString();
	}
	private void getBStreeJS(StringBuffer js,StringBuffer openIds,String parent,List nextlist,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		for(int i=0;i<nextlist.size();i++){
			TreeNode node = (TreeNode)nextlist.get(i);
			js.append(parent + "[" + i + "] = new Array;\n");
			js.append(parent + "[" + i + "]['caption'] = '" + node.getText() + "';\n");
			//js.append(parent + "[" + i + "]['url'] = '';\n");
			//js.append(parent + "[" + i + "]['target'] = '" + this.target + "';\n");
			//js.append(parent + "[" + i + "]['isOpen'] = " + true + ";\n");
			if(selectIds.indexOf("," + node.getId() + ",")!=-1){
				js.append(parent + "[" + i + "]['isChecked'] = 2;");//2和0
			}
			if(this.openAll||openIds.indexOf("," + node.getId() + ",")!=-1){
				js.append(parent + "[" + i + "]['isOpen'] = " + true + ";\n");
			}
			js.append(parent + "[" + i + "]['checkboxName'] = '"+ treeName + "_" + node.getId() + "';\n");
			List _nextlist = tree.getNextNodes(node.getId());
			if(_nextlist!=null&&_nextlist.size()!=0){
				js.append(parent + "[" + i + "]['children'] = new Array;\n");
				this.getBStreeJS(js, openIds , parent + "[" + i + "]['children']",_nextlist,deep+1);
			}
		}
	}
	
	/**
	 * 取EXT的树
	 * 
	 * var root = new Ext.tree.TreeNode({
		    id : 'root',
		    text : '树根',
		    checked : false
		});
		root.appendChild(leaf1);
	 * @return
	 */
	public String getExtTreeJS(){
		StringBuffer js = new StringBuffer("");
		TreeNode rootNode = tree.getNode(rootId);//取根节点
		if(rootNode==null){
			System.out.println("没找到树的标题（请传一个id为"+rootId+"的节点进来做树的根！）");
			return "";
		}
		js.append("var root = new Ext.tree.TreeNode({");
		js.append("id : '"+rootNode.getId()+"'");
		js.append(",text : '"+rootNode.getText()+"'");
		if(isCheckBox){
			if(rootNode.isChecked()){
				js.append(",checked : true");
			}else{
				js.append(",checked : false");
			}
		}
		js.append("});");
		this.getExtTreeJS(js,rootId,1);
		return js.toString();
	}
	
	private void getExtTreeJS(StringBuffer js,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			js.append("var t"+node.getId()+" = new Ext.tree.TreeNode({");
			js.append("id : '"+node.getId()+"'");
			js.append(",text : '"+node.getText()+"'");
			if(isCheckBox){
				if(node.isChecked()){
					js.append(",checked : true");
				}else{
					js.append(",checked : false");
				}
			}
			js.append("});");
			if(node.getPid()==rootId){//根节点特殊
				js.append("root.appendChild(t"+node.getId()+");");
			}else{
				js.append("t"+node.getPid()+".appendChild(t"+node.getId()+");");
			}
			this.getExtTreeJS(js, node.getId(),deep+1);
		}
	}
	
	/**
	 * 取dtree的js，不支持复选
	 * 支持nodeBaseUrl/idParamName/target的设置
	 * @return
	 */
	public String getDtreeJS(){
		StringBuffer js = new StringBuffer("");
		TreeNode rootNode = tree.getNode(0);//标题
		if(rootNode==null||rootNode.getPid()!=-1){
			System.out.println("没找到树的标题（请传一个id为0，pid为-1的节点进来做树的标题！）");
			return "";
		}
		js.append("d = new dTree('d');");
		js.append("d.add(0, -1, '"+ rootNode.getText() +"','','','','"+ basePath +"images/index.gif');");
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			System.out.println("根节点id=" + rootId + "下无数据，如果需要重新定位根，请调用setRootId()方法！");
			return js.toString();
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String url = node.getUrl()!=null? node.getUrl():"";
			String target = node.getTarget()!=null?node.getTarget():"";
			js.append("d.add(" + node.getId() + "," + node.getPid()+",'" + node.getText() + "','" + url + "','','" + target+"','"+ basePath +"images/bookclose.gif','"+ basePath +"images/bookopen.gif',"+ this.openAll +");");
			this.getDtreeJS(js, node.getId(),2);
		}
		js.append("document.write(d);");
		return js.toString();
	}
	private void getDtreeJS(StringBuffer js,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String url = node.getUrl()!=null? node.getUrl():"";
			String target = node.getTarget()!=null?node.getTarget():"";
			js.append("d.add(" + node.getId() + "," + node.getPid()+",'" + node.getText() + "','" + url + "','','" + target+"','"+ basePath +"images/endnode.gif','"+ basePath +"images/h1.gif',"+ this.openAll +");");
			this.getDtreeJS(js, node.getId(),deep+1);
		}
	}
	
	/**
	 * 取Option的html
	 * 支持currNodeId/optionChar的设置
	 * @return
	 */
	public String getOptionHtml(){
		StringBuffer html = new StringBuffer("");
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			return "";
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			html.append("<option value='" + node.getId() + "'");
			if(node.getId()==currNodeId){
				html.append(" selected ");
			}
			html.append(">" + this.optionChar +  node.getText() + "</option>");
			this.getOptionHtml(html, node.getId(),2);
		}
		return html.toString();
	}
	private void getOptionHtml(StringBuffer html,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String text = this.getOptionText(node);
			html.append("<option value='" + node.getId() + "'");
			if(node.getId()==currNodeId){
				html.append(" selected ");
			}
			html.append(">" + text + "</option>");
			this.getOptionHtml(html, node.getId(),deep+1);
		}
	}
	
	/**
	 * 取MzTreeView的js，支持复选/非复选
	 * 支持rootId,selectIds的设置
	 * @return
	 */
	public String getMzTreeViewJS(){
		if(!selectIds.startsWith(",")){
			selectIds = ","+selectIds;
		}
		if(!selectIds.endsWith(",")){
			selectIds = selectIds+",";
		}
		StringBuffer js = new StringBuffer("var data={};");
		List rootlist = tree.getNextNodes(rootId);
		if(rootlist==null||rootlist.size()==0){
			return "";
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			js.append("data['" + node.getPid() + "_" + node.getId() + "']='text: " + node.getText());
			if(selectIds.indexOf("," + node.getId()+",")!=-1){
				js.append(";checked:true");
			}
			if(node.getTarget()!=null){
				js.append(";target:" + node.getTarget());
			}
			if(node.getUrl()!=null){
				js.append(";url:" + node.getUrl());
			}
			js.append("';");
			this.getMzTreeViewJS(js, node.getId(),2);
		}
		return js.toString();
	}
	private void getMzTreeViewJS(StringBuffer js,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			js.append("data['" + node.getPid() + "_" + node.getId() + "']='text: " + node.getText());
			if(selectIds.indexOf("," + node.getId()+",")!=-1){
				js.append(";checked:true");
			}
			if(node.getTarget()!=null){
				js.append(";target:" + node.getTarget());
			}
			if(node.getUrl()!=null){
				js.append(";url:" + node.getUrl());
			}
			js.append("';");
			this.getMzTreeViewJS(js, node.getId(),deep+1);
		}
	}

	
	
	/**
	 * 取菜单的js
	 * 支持的设置
	 * @return
	 */
	public String getMenuJS(){
		StringBuffer js = new StringBuffer("");
		js.append("stm_bm(['mu',400,'','images/dot.gif',0,'','',0,0,0,0,0,1,0,0]);");
		js.append("stm_bp('p0',[0,4,0,0,2,2,0,0,100,'',-2,'',-2,90,0,0,'#000000','transparent','',3,0,0,'#000000']);");		
		js.append("stm_ai('p0i0',[0,'','','',-1,-1,0,'','_self','','','','',0,0,0,'','',0,0,0,0,1,'#f1f2ee',1,'#cccccc',1,'','',3,3,0,0,'#fffff7','#000000','#000000','#000000','9pt 宋体','9pt 宋体',0,0]);");
		this.getMenuJS(js,rootId,1);//递归
		return js.toString();
	}
	private void getMenuJS(StringBuffer js,int id,int deep){
		if(this.deep!=-1 && deep>this.deep) return;
		List rootlist = tree.getNextNodes(id);
		if(rootlist==null||rootlist.size()==0){
			return;
		}
		String strPLayer = "p0i0";
		for(int i=0;i<rootlist.size();i++){
			TreeNode node = (TreeNode)rootlist.get(i);
			String strCurLayer = "p" + (deep-1) + "i" + (i + 1);
			if(tree.getNextNodes(node.getId())!=null){//存在下级节点
				if(deep==1){//第一级
					js.append("stm_aix('" + strCurLayer + "','" + strPLayer + "',[0,' " + node.getText() + " ','','',-1,-1,0,'','mainframe','','','','',0,0,0,'','',0,0,0,0,1,'#ffffff',1,'#666666',0,'','',3,3,0,0,'#dddddd','#dddddd','#000000','#ffffff','9pt 宋体','9pt 宋体']);");
					js.append("stm_bp('p1',[1,4,0,0,2,3,6,7,100,'',-2,'',-2,67,2,0,'#888888','#ffffff','',3,1,1,'#666666']);");
				}else{
					js.append("stm_aix('" + strCurLayer + "','" + strPLayer + "',[0,'" + node.getText() + "','','',-1,-1,0,'','" + node.getTarget() + "','','','','',0,0,0,'" + basePath + "images/muar1.gif','" + basePath + "images/muar2.gif',0,0,0,0,1,'#000000',1,'#666666',0,'','',3,3,0,0,'#cccccc','#cccccc','#000000','#ffffff','9pt 宋体']);");
					js.append("stm_bpx('p2','p1',[1,2,-2,-3,2,3,0,0]);");
				}
				this.getMenuJS(js, node.getId(),deep+1);
			}else{
				if(deep==2){
					js.append("stm_aix('" + strCurLayer + "','" + strPLayer + "',[0,'" + node.getText() + "','','',-1,-1,0,'" + basePath + node.getUrl() + "','" + node.getTarget() + "','','','','',0,0,0,'','',0,0,0,0,1,'#000000',1,'#666666',0,'','',3,3,0,0,'#cccccc','#cccccc','#000000','#ffffff','9pt 宋体']);");
				}else{
					js.append("stm_aix('" + strCurLayer + "','" + strPLayer + "',[0,' " + node.getText() + " ','','',-1,-1,0,'" + basePath + node.getUrl() + "','" + node.getTarget() + "','','','','',0,0,0,'','',0,0,0,0,1,'#ffffff',1,'#666666',0,'','',3,3,0,0,'#dddddd','#dddddd','#000000','#ffffff','9pt 宋体','9pt 宋体']);");
				}
			}
		}
		if(deep==1){
			js.append("stm_em();");
		}else{
			js.append("stm_ep();");
		}
	}
	
	
	
	private String getOptionText(TreeNode node){
		StringBuffer text = new StringBuffer();
		TreeNode pnode = tree.getNode(node.getPid());
		if(pnode!=null){
			this.getOptionText(text, pnode);
		}
		text.append(this.optionChar + node.getText());
		return text.toString();
	}
	private void getOptionText(StringBuffer text,TreeNode node){
		TreeNode pnode = tree.getNode(node.getPid());
		if(pnode!=null){
			this.getOptionText(text, pnode);
		}
		text.append(this.optionChar + node.getText());
	}
	
	/**get/set方法**/
	public String getDivId() {
		return divId;
	}
	public void setDivId(String divId) {
		this.divId = divId;
	}
	public boolean isCheckBox() {
		return isCheckBox;
	}
	public void setCheckBox(boolean isCheckBox) {
		this.isCheckBox = isCheckBox;
	}
	public String getSelectIds() {
		return selectIds;
	}
	public void setSelectIds(String selectIds) {
		this.selectIds = selectIds;
	}
	public String getTreeName() {
		return treeName;
	}
	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public String getOptionChar() {
		return optionChar;
	}

	public void setOptionChar(String optionChar) {
		this.optionChar = optionChar;
	}

	public int getCurrNodeId() {
		return currNodeId;
	}
	public void setCurrNodeId(int currNodeId) {
		this.currNodeId = currNodeId;
	}
	public HashTree getTree() {
		return tree;
	}
	public void setTree(HashTree tree) {
		this.tree = tree;
	}
	public boolean isEnableThreeStateCheckboxes() {
		return enableThreeStateCheckboxes;
	}
	public void setEnableThreeStateCheckboxes(boolean enableThreeStateCheckboxes) {
		this.enableThreeStateCheckboxes = enableThreeStateCheckboxes;
	}
	public int getRootId() {
		return rootId;
	}
	public void setRootId(int rootId) {
		this.rootId = rootId;
	}
	public boolean isOpenAll() {
		return openAll;
	}
	public void setOpenAll(boolean openAll) {
		this.openAll = openAll;
	}
	public int getDeep() {
		return deep;
	}
	public void setDeep(int deep) {
		this.deep = deep;
	}
	
}
