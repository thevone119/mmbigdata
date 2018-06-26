/********************************************************************************************
* BlueShoes Framework; This file is part of the php application framework.
* NOTE: This code is stripped (obfuscated). To get the clean documented code goto 
*       www.blueshoes.org and register for the free open source *DEVELOPER* version or 
*       buy the commercial version.
*       
*       In case you've already got the developer version, then this is one of the few 
*       packages/classes that is only available to *PAYING* customers.
*       To get it go to www.blueshoes.org and buy a commercial version.
* 
* @copyright www.blueshoes.org
* @author    sam blum <sam-at-blueshoes>
* @author    Andrej Arn <andrej-at-blueshoes>
*/
	var ie = document.all != null;
	var moz = !ie && document.getElementById != null && document.layers == null;
	if (!Bs_Objects)
	{
		var Bs_Objects         = [];
	};
	if (!Bs_XmlRequestQueue)
	{
		var Bs_XmlRequestQueue = [];
	};
	
	function bsTree_loadNodeChildrenFromServer_callback() 
	{
		for (var bsObjectID in Bs_XmlRequestQueue) 
		{
			if (typeof(Bs_XmlRequestQueue[bsObjectID]) == 'object') 
			{
				for (var nodeID in Bs_XmlRequestQueue[bsObjectID]) 
				{
					if (typeof(Bs_XmlRequestQueue[bsObjectID][nodeID]) == 'object') 
					{
						if (Bs_XmlRequestQueue[bsObjectID][nodeID].readyState == 4) 
						{
							if (Bs_XmlRequestQueue[bsObjectID][nodeID].status == 200) 
							{
								eval(Bs_XmlRequestQueue[bsObjectID][nodeID].responseText);
								delete Bs_XmlRequestQueue[bsObjectID][nodeID];
								var elm = Bs_Objects[bsObjectID].getElement(nodeID);
								elm._unloadedChildren = false;
								elm._checkedForChildren = true;
								elm.addChildrenByArray(childrenData, false);
								if (elm._level == 0) 
								{
									myTree._afterImgPreload();
								}
								else 
								{
								}
							}
							else 
							{
								alert("There was a problem retrieving the XML data:\n" + Bs_XmlRequestQueue[bsObjectID][nodeID].statusText);
							}
						}
					}
				}
			}
		}
	}
	

	
	function Bs_Tree() 
	{
		this._id;this._objectId;
		this.autoCollapse      = false;
		this.lookAhead = 2;
		this.loadAhead = 2;
		this.initAhead = 2;
		this.renderAhead = 2;
		this._workStack = new Array();
		this.rememberState = false;
		this.captionBgColor    = "";
		this.linkStyle;
		this.divStyle = 'font-family: Arial, Helvetica, sans-serif; font-size: 12px;';
		this.showPseudoElement = false;
		this.useCheckboxSystem = false;
		this.checkboxSystemWalkTree = 3;
		this.checkboxSystemIfPartlyThenFull = true;
		this.checkboxSystemImgDir;
		this.checkboxSystemGuiNochange;
		this.useRadioButton = false;
		this.radioButtonName;
		this.imageDir = Bs_Path+'/img/win98/';
		this.imageHeight = 16;
		this.useFolderIcon = true;
		this.useLeaf = true;
		this.walkTree = true;
		this.useAutoSequence = true;
		this.draggable = false;
		this._clearingHouse = new Array;
		this._pseudoElement;
		this._currentActiveElement;
		this._elementSequence = 0;
		this._errorArray;
		this.stopWatch;
		this.simple = false;
	
		this._constructor = function()
		{
			this._id = Bs_Objects.length;
			Bs_Objects[this._id] = this;
			this._objectId = "Bs_Tree_" + this._id;
			var a = [];
			a['id']               = 'pseudoElement001';
			a['caption']          = "root";
			a['url']              = "";
			a['target']           = "";
			a['isOpen']           = true;
			this._pseudoElement = this._createTreeElement(a, 0);
		}
		
		this.loadSkin = function(skinName) 
		{
			switch (skinName) 
			{
				case 'win2k':
				case 'win98':
					this.imageDir = Bs_Path+'/img/win98/';
					this.imageHeight = 16;
					break;
				case 'winxp':
					this.imageDir = Bs_Path+'/img/winXp/';
					this.imageHeight = 17;
					break;
				case 'bobby-blue':
					this.imageDir      = Bs_Path+'/img/bobby/blue/';
					this.imageHeight   = 16;
					this.useFolderIcon = false;
					break;
				default:
					return false;	
			}
			return true;
		}

		this.setDisabled = function(b) 
		{
			if (typeof(b) == 'undefined') b = !this._disabled;
			this._disabled = b;
			for (var elementId in this._clearingHouse) 
			{
				if (this.useRadioButton) 
				{
					var radioId  = this.getRadioButtonName() + '_' + elementId;
					var radioElm = document.getElementById(radioId);
					if (radioElm) 
					{
						radioElm.readonly = this._disabled;
						radioElm.disabled = this._disabled;
					}
				}
				else if (typeof(this._clearingHouse[elementId]._checkboxObject) != 'undefined') 
				{
					this._clearingHouse[elementId]._checkboxObject.setDisabled(this._disabled);
				}
			}
		}

		this.initByArray = function(arr)
		{
			this._pseudoElement._unloadedChildren = false;
			this._pseudoElement._checkedForChildren = true;
			if (arr.length > 0)
			{
				for (var i=0; i<arr.length; i++) 
				{
					var e = this._createTreeElement(arr[i], 1);
					if (e == false)
					{
						return false;
					}
					this._pseudoElement.addChild(e);
				}
			}
			else
			{
				for (var key in arr) 
				{
					if (typeof(arr[key]) == 'object') 
					{
						var e = this._createTreeElement(arr[key], 1);
						if (e == false) 
						{
							return false;
						}
						this._pseudoElement.addChild(e);
					}
				}
			}
			return true;
		}

		this.getActiveElement = function() 
		{
			if (typeof(this._currentActiveElement) != 'undefined') return this._currentActiveElement;
			return false;
		}
		
		this.setActiveElement = function(treeElement) 
		{
			this._currentActiveElement = treeElement;
		}
		
		this.hasNodeLoader = function() 
		{
			return (typeof(this._nodeLoaderType) != 'undefined');
		}
		this.setNodeLoader = function(type, url) 
		{
			this._nodeLoaderType = type;
			this._nodeLoaderUrl  = url;
			if (!this._pseudoElement.hasChildren()) 
			{
				this._pseudoElement._unloadedChildren = true;
			}
		}

		this.loadNodeChildrenFromServer = function(nodeID) 
		{
			if (typeof(Bs_XmlRequestQueue[this._id]) == 'undefined') 
			{
				Bs_XmlRequestQueue[this._id] = new Array();
			}
			else 
			{
				if (typeof(Bs_XmlRequestQueue[this._id][nodeID]) != 'undefined') return;
			}
			if (window.ActiveXObject) 
			{
				Bs_XmlRequestQueue[this._id][nodeID] = new ActiveXObject("Microsoft.XMLHTTP");
			}
			else if (window.XMLHttpRequest) 
			{
				Bs_XmlRequestQueue[this._id][nodeID] = new XMLHttpRequest();
			}
			else 
			{
				return false;
			}
			var url = this._nodeLoaderUrl + '?bsObjectID=' + this._id + '&nodeID=' + nodeID;
			Bs_XmlRequestQueue[this._id][nodeID].onreadystatechange = bsTree_loadNodeChildrenFromServer_callback;
			Bs_XmlRequestQueue[this._id][nodeID].open("GET", url, true);
			Bs_XmlRequestQueue[this._id][nodeID].send();
		}
		
		this._createTreeElement = function(arr, level) 
		{
			if (typeof(level) == 'undefined') level = 1;
			var e = new Bs_TreeElement();
			var status = e.initByArray(arr, this, level);
			if (!status) 
			{
				this._addError(e.getLastError());
				return false;
			}
			this._clearingHouse[e.id] = e;
			if (typeof(arr['children'])=='object') 
			{
				e._unloadedChildren   = false;
				e._checkedForChildren = true;
				if ((this.useCheckboxSystem && (this.checkboxSystemWalkTree >= 2)) || e.isOpen || ((this.lookAhead +2) > level) || (this.lookAhead == -1) || ((typeof(e.parent) == 'object') && (e.parent.isOpen))) 
				{
					if (arr['children'].length > 0) 
					{
						for (var i=0, n=arr['children'].length; i<n; i++) 
						{
							var newE = this._createTreeElement(arr['children'][i], level +1);
							if (!newE) return false;
							e.addChild(newE);
						}
					} 
					else 
					{
						for (key in arr['children']) 
						{
							var newE = this._createTreeElement(arr['children'][key], level +1);
							if (!newE) return false;e.addChild(newE);
						}
					}
				}
				else 
				{
					e._undoneChildren = arr['children'];
				}
			}
			return e;
		}

		this.getElement = function(elementId) 
		{
			if (elementId == 0) return this._pseudoElement;
			if (typeof(this._clearingHouse[elementId]) == 'object') 
			{
				return this._clearingHouse[elementId];
			} 
			else 
			{
				return false;
			}
		}

		this.getElementByCaptionPath = function(data) 
		{
			var elm = this._pseudoElement;
			for (var i=0, n=data.length; i<n; i++) 
			{
				var newElm = null;
				for (var j=0, jn=elm._children.length; j<jn; j++) 
				{
					if (elm._children[j].caption == data[i]) 
					{
						newElm = elm._children[j];
						elm = newElm;
						if (typeof(elm._undoneChildren) == 'object') 
						{
							for (var k=0, kn=elm._undoneChildren.length; k<kn; k++) 
							{
								var newE = this._createTreeElement(elm._undoneChildren[k], elm._level +1);
								elm.addChild(newE);
							}
							elm._undoneChildren = false;
						}
						break;
					}
				}
				if (newElm == null) return false;
			}
			return newElm;
		}

		this.removeElement = function(elementId) 
		{
			if (typeof(this._clearingHouse[elementId]) == 'undefined') return false;
			var elm = this._clearingHouse[elementId];
			if ((typeof(elm.parent) == 'object') && (typeof(elm.parent._children) == 'object')) 
			{
				for (var i=0, n=elm.parent._children.length; i<n; i++) 
				{
					if (elm.parent._children[i].id == elementId) 
					{
						elm.parent._children.deleteItem(i);break;
					}
				}
			}
			this._clearingHouse.deleteItemHash(elementId);
			for (var i=0, n=elm._children.length; i<n; i++) 
			{
				this._clearingHouse.deleteItemHash(elm._children[i].id);
			}
			if ((typeof(elm.parent) == 'object') && (elm.parent._isOutrendered)) 
			{
				elm.parent.render(true, true);
			}
			return true;
		}
		
		this.expandAll = function() 
		{
			this._pseudoElement.expandAll();
		}
		
		this.collapseAll = function() 
		{
			this._pseudoElement.collapseAll(false);
		}
		
		this.draw = function() 
		{
			if (this.simple) 
			{
				var content = this._pseudoElement.renderSimple();
			} 
			else 
			{
				var content = this._pseudoElement.render();
			}
			document.writeln(content[0]);eval(content[1]);
		}

		this.toHtml = function() 
		{
			if (this.hasNodeLoader()) 
			{
				alert("Webmaster: Sorry, the toHtml() method cannot be used together with a node loader. Use drawInto() or draw().");
				return;
			}
			if (this.simple) 
			{
				return this._pseudoElement.renderSimple();
			} 
			else 
			{
				return this._pseudoElement.render();
			}
		}
		
		this.executeOnElement = function(id, func, params) 
		{
			if (this._clearingHouse[id]) 
			{
				if (this._clearingHouse[id][func]) 
				{
					if (params) 
					{
						switch (params.length) 
						{
							case 1:
								return this._clearingHouse[id][func](params[0]);
								break;
							case 2:
								return this._clearingHouse[id][func](params[0], params[1]);
								break;
							case 3:
								return this._clearingHouse[id][func](params[0], params[1], params[2]);
								break;
							case 4:
								return this._clearingHouse[id][func](params[0], params[1], params[2], params[3]);
								break;
						}
					}
					else 
					{
						return this._clearingHouse[id][func]();
					}
				}
		}
		return;
	}
	
	this.getJavascriptCode = function() 
	{
		return this._pseudoElement.getJavascriptCode('a', true);
	}
	
	this.elementToggleOpenClose = function(id) 
	{
		this._clearingHouse[id].toggleOpenClose();
	}
	
	this.elementOpenWalkUp = function(id) 
	{
		if (typeof(this._clearingHouse[id]) != 'undefined') 
		{
			var elm = this._clearingHouse[id];
			elm.open(true);
			if (typeof(elm.parent) != 'undefined') this.elementOpenWalkUp(elm.parent.id);
		} 
		else 
		{
			return false;
		}
		return true;
	}
	
	this.elementCloseWalkUp = function(id) 
	{
		if (typeof(this._clearingHouse[id]) != 'undefined') 
		{
			var elm = this._clearingHouse[id];
			elm.close(true);
			if (typeof(elm.parent) != 'undefined') this.elementCloseWalkUp(elm.parent.id);
		} 
		else 
		{
			return false;
		}
		return true;
	}
	
	this.elementCloseWalkDown = function(id) 
	{
		if (typeof(id) == 'undefined') 
		{
			var elm = this._pseudoElement;
		}
		else if (typeof(this._clearingHouse[id]) != 'undefined') 
		{
			var elm = this._clearingHouse[id];elm.close(true);
		} 
		else 
		{
			return false;
		}
		if (typeof(elm._children) != 'undefined') 
		{
			for (var i=0; i<elm._children.length; i++) 
			{
				this.elementCloseWalkDown(elm._children[i].id);
			}
		}
		return true;
	}
	
	this.elementOpen = function(id) 
	{
		if (typeof(this._clearingHouse[id]) != 'undefined') 
		{
			this._clearingHouse[id].open();
		}
	}
	
	this.elementClose = function(id) 
	{
		this._clearingHouse[id].close();
	}
	
	this.openPath = function(data, valueType) 
	{
		var elm = this.getElementByCaptionPath(data);
		if (elm == false) return false;
		this.elementOpenWalkUp(elm.id);
		return true;
	}
	
	this.elementCheckboxEvent = function(id, value) 
	{
		this._clearingHouse[id].checkboxEvent(value);
	}
	
	this.applyState = function() 
	{
		if (typeof(getCookie) == 'undefined') 
		{
			alert('Webmaster: please make sure core/lang/Bs_Cookie.lib.js is included for the rememberState/applyState feature.');
			return false;
		}
		var name = this._objectId;
		var data = getCookie(name);
		for (treeElementId in data) 
		{
			var treeElm = this.getElement(treeElementId);
			for (action in data[treeElementId]) 
			{
				if (data[treeElementId][action]) 
				{
					treeElm.open();
				} 
				else 
				{
					treeElm.close();}
				}
			}
			return true;
		}
		
		this._updateStateCookie = function(treeElementId, action, value)
		{
			if (typeof(setCookie) == 'undefined') 
			{
				alert('Webmaster: please make sure core/lang/Bs_Cookie.lib.js is included for the rememberState/applyState feature.');
				return false;
			}
			var name = this._objectId;
			var data = getCookie(name);
			if ((typeof(data) != 'object') || (data == null)) data = new Object();
			if ((typeof(data[treeElementId]) != 'object') || (typeof(data[treeElementId]) == null)) 
			{
				data[treeElementId] = new Object();
			}
			if (typeof(data[treeElementId][action]) == 'undefined') 
			{
				data[treeElementId][action] = value;
			} 
			else 
			{
				if (data[treeElementId][action] != value) 
				{
					delete data[treeElementId][action];delete data[treeElementId];
				}
			}
			setCookie(name, data);
			return true;
		}
		
		this.getRadioButtonName = function () 
		{
			if (typeof(this.radioButtonName) != 'undefined') 
			{
				return this.radioButtonName;
			} 
			else 
			{
				return 'bsTreeRad_' + this._objectId;
			}
		}
		
		this.debugDumpTree = function(elm, indent) 
		{
			if (typeof(elm) == 'undefined') 
			{
				elm    = this._pseudoElement;indent = '';
				var firstCall = true;
			}
			var ret = '';
			if (typeof(elm._children) == 'object') 
			{
				for (var i=0; i<elm._children.length; i++) 
				{
					ret += indent + i + ': ' + elm._children[i].id + ': ' + elm._children[i].caption + "\n";
					ret += this.debugDumpTree(elm._children[i], indent + '  ');
				}
			}
			if (firstCall) 
			{
				alert(ret);
			} 
			else 
			{
				return ret;
			}
		}
		
		this._addError = function(str) 
		{
			if (typeof(this._errorArray) == 'undefined') 
			{
				this._errorArray = new Array(str);
			} 
			else 
			{
				this._errorArray[this._errorArray.length] = str;
			}
		}
		
		this.getLastError = function() 
		{
			if (typeof(this._errorArray) != 'undefined') 
			{
				if (this._errorArray.length > 0) 
				{
					return this._errorArray[this._errorArray.length -1];
				}
			}
			return false;
		}
		
		this.old_drawInto = function(id) 
		{
			if (this.simple) 
			{
				var content = this._pseudoElement.renderSimple();
			} 
			else 
			{
				var content = this._pseudoElement.render();
			}
			var e       = document.getElementById(id);
			if (e) 
			{
				e.innerHTML = content[0];
				if ('' != content[1]) eval(content[1]);
			}
		}
		
		this._imgPreload = function() 
		{
			var id = this.globalId;
			var e  = document.getElementById(id);
			var ii = 0;
			var outTemp = new Array();
			outTemp[ii++] = '<img src="' + this.imageDir + 'line1.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'line2.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'line3.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'minus1.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'minus2.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'minus3.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'plus1.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'plus2.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'plus3.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'line3.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'empty.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'leaf.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'folderClosed.gif" border="0" style="display:none;">';
			outTemp[ii++] = '<img src="' + this.imageDir + 'folderOpen.gif" border="0" style="display:none;">';
			if (e) e.innerHTML = outTemp.join('');
		}
		
		this._afterImgPreload = function() 
		{
			var id = this.globalId;
			if (this.hasNodeLoader() && !this._pseudoElement._checkedForChildren && !this.showPseudoElement)
			{
				this.loadNodeChildrenFromServer(this._pseudoElement.id);
				return;
			}
			if (this.simple) 
			{
				var content = this._pseudoElement.renderSimple();
			} 
			else 
			{
				var content = this._pseudoElement.render();
			}
			var e       = document.getElementById(id);
			if (e) 
			{
				e.innerHTML = content[0];
				if ('' != content[1]) 
				{
					eval(content[1]);
				}
				this._afterRender();
			}
		}
		
		this._afterRender = function() 
		{
			if (typeof(this._workStack) == 'object') 
			{
				for (var i=0; i<this._workStack.length; i++) 
				{
					eval(this._workStack[i]);
					delete this._workStack[i];
				}
			}
		}
		
		this.drawInto = function(id) 
		{
			this.globalId = id;
			setTimeout('Bs_Objects['+this._id+']._imgPreload()', 0);
			setTimeout('Bs_Objects['+this._id+']._afterImgPreload()', 5);
		}
		
		this._constructor();
	}
