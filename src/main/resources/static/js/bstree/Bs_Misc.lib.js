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
var ie = document.all != null;var moz = !ie && document.getElementById != null && document.layers == null;function bs_isNull(theVar) {
if (typeof(theVar) == 'undefined') return true;if (theVar == null) return true;return false;}
function bs_isObject(theVar) {
ret = false;if (typeof(theVar) == 'object') {
ret = !bs_isNull(theVar);}
return ret;}
function bs_isEmpty(theVar) {
if (bs_isNull(theVar)) return true;if (theVar == '') return true;return false;}
function bs_typeOf(theVar) {
ret = 'undefined';switch (typeof(theVar)) {
case 'boolean':  ret = 'boolean';  break;case 'number':   ret = 'number';   break;case 'string':   ret = 'string';   break;case 'function': ret = 'function'; break;case 'object':
if (bs_isNull(theVar)) {
ret = 'null';break;}
if (theVar.concat && theVar.join && theVar.sort && theVar.pop) {
ret = 'array';break;}
break;case 'undefined':
default:
ret = 'undefined';}
return ret;}
function bs_isTrue(value) {
var trueVals = new Array('true','on','y','yes',1,'1','ja','oui');if (value == '') return false;if (typeof(value) == 'string') value = value.toLowerCase();if (value == true) return true;for (var i=0; i<trueVals.length; i++) {
if (value == trueVals[i].toLowerCase()) return true;}
return false;}
function instanceOf(object, constructor) {
while (object != null) {
if (object == constructor.prototype) return true;object = object.__proto__;}
return false;}
function bs_arrayMerge(obj1, obj2) {
if (!bs_isObject(obj1) || !bs_isObject(obj2)) return false;for (var key in obj2) {obj1[key] = obj2[key];}
return obj1;}
function bs_arrayFlip(aArray) {
var aHash = new Object();type = bs_typeOf(aArray);if (type == 'array') {
for (var i=0; i<aArray.length; i++) {
aHash[aArray[i]] = true;}
} else if (type == 'string') {
if (aArray != '') {
aHash[aArray] = true;}
}
return aHash;}
function queryStringToHash(queryString) {
if (typeof(queryString) == 'undefined') {
var queryString = window.location.search;}
var ret = new Array;if (bs_isEmpty(queryString)) return ret;queryString = queryString.substr(1);if (bs_isEmpty(queryString)) return ret;var junks = queryString.split('&');for (var i=0; i<junks.length; i++) {
var x = junks[i].split('=');if (x.length == 2) {
ret[x[0]] = x[1];} else {
ret[x[0]] = '';}
}
return ret;}
function dump(theVar, doReturn, showFunctions, _out, _indent, _numCall) {
if (!_indent) {
_indent  = ' ';_bsDumpOverallNumCall = 1;} else {
_indent  += ' ';_bsDumpOverallNumCall++;}
if (_bsDumpOverallNumCall < 8) {
if (_out) {
var isInternal = true;} else {
_out = '';_numCall = 1;}
var goOn = true;if (_numCall > 10) {
goOn = false;if (!doReturn) {
goOn = confirm("There have been 10 recursive calls so far. Maybe you have an endless loop. Do you want to continue?");}
if (!goOn) {
_out += _indent + "error/warning: nesting levels too deep (>10 times)!\n";} else {
_numCall = 0;}
}
if (goOn) {
switch (typeof(theVar)) {
case 'object':
for (var key in theVar) {
switch (typeof(theVar[key])) {
case 'function':
if (typeof(showFunctions) == 'boolean') {
if (showFunctions) {
_out += _indent + 'function "' + key + '" => ' + theVar[key] + "\n";} else {
_out += _indent + 'function "' + key + "\n";}
} else {
if (showFunctions == 2) {
_out += _indent + 'function "' + key + '" => ' + theVar[key] + "\n";} else if (showFunctions == 1) {
_out += _indent + 'function "' + key + "\n";} else {
}
}
break;case 'undefined':
break;case 'object':
_out += _indent + key;if (instanceOf(theVar[key], Array)) {
_out += ' (Array) => \n';} else if (instanceOf(theVar[key], Date)) {
_out += ' (Date) => '+ theVar[key] +'\n';} else {
_out += ' (Object) => \n';}
_out = dump(theVar[key], doReturn, showFunctions, _out, _indent + "    ", _numCall+1);break;case 'number':
if (instanceOf(theVar, Date)) alert('date');default:
_out += _indent + typeof(theVar[key]) + ' "' + key + '" => ' + theVar[key] + "\n";}
}
break;default:
_out += _indent + typeof(theVar) + ' => ' + theVar + "\n";}
}
}
if (isInternal || doReturn) {
return _out;} else {
alert(_out);return;}
}
function Position(x, y) {
this.x = x;this.y = y;};function getAbsolutePos(el, stopIfAbsolute) {
if (bs_isNull(el)) {
return {x:0, y:0};}
var res = {x:el.offsetLeft, y:el.offsetTop};if (el.offsetParent) {
if (el.offsetParent.currentStyle && el.offsetParent.currentStyle.position) {
var position = el.offsetParent.currentStyle.position;var overflow = el.offsetParent.currentStyle.overflow;} else if (document.defaultView) {
var position = document.defaultView.getComputedStyle(el, null).getPropertyValue("position");var overflow = document.defaultView.getComputedStyle(el, null).getPropertyValue("overflow");} else {
return false;}
if ((stopIfAbsolute != true ) || ((position != 'absolute') && (position != 'relative') && (overflow != 'auto') && (overflow != 'scroll'))) {
var tmp = getAbsolutePos(el.offsetParent, stopIfAbsolute);res.x += tmp.x;res.y += tmp.y;}
}
return res;};function getElementDimensions(elm, stopIfAbsolute) {
var ret = getAbsolutePos(elm, stopIfAbsolute);if (!ret) return ret;ret.w = elm.offsetWidth;ret.h = elm.offsetHeight;return ret;}
function bs_findBackgroundColor(elm) {
if (typeof(elm) == 'string') {
elm = document.getElementById(elm);}
if (typeof(elm) == 'undefined') return false;if (moz) {
try {
var col = document.defaultView.getComputedStyle(elm, null).getPropertyValue("background-color");} catch (e) {
return false;}
} else {
if (typeof(elm.currentStyle) == 'undefined') return false;var col = elm.currentStyle.backgroundColor;}
if ((typeof(col) != 'undefined') && (col != 'transparent') && (col != '')) {
return col;} else {
return bs_findBackgroundColor(elm.parentNode);}
}
function bs_toggleVisibility(show, tags) {
try {
if (typeof(tags) == 'undefined') tags = new Array('select', 'iframe');for (var tag in tags) {
var elms = document.getElementsByTagName(tags[tag]);for (var e = 0; e < elms.length; e++) {
elms[e].style.visibility = (show) ? 'visible' : 'hidden';}
}
} catch (e) {
}
}
Function.prototype.method = function (name, func) {
this.prototype[name] = func;return this;};Function.method('inherits', function (parent) {
var d = 0, p = (this.prototype = new parent());this.method('uber', function uber(name) {
var f, r, t = d, v = parent.prototype;if (t) {
while (t) {
v = v.constructor.prototype;t -= 1;}
f = v[name];} else {
f = p[name];if (f == this[name]) {
f = v[name];}
}
d += 1;r = f.apply(this, Array.prototype.slice.apply(arguments, [1]));d -= 1;return r;});return this;});Function.method('swiss', function (parent) {
for (var i = 1; i < arguments.length; i += 1) {
var name = arguments[i];this.prototype[name] = parent.prototype[name];}
return this;});Function.method('extend', function (object) {
for (property in object.prototype) {
this.prototype[property] = object.prototype[property];}
});if ("undefined" != typeof(HTMLElement)) {
if ("undefined" == typeof(HTMLElement.insertAdjacentElement)) {
HTMLElement.prototype.insertAdjacentElement = function(where, parsedNode)	{
switch(where){
case 'beforeBegin':
this.parentNode.insertBefore(parsedNode,this)
break;case 'afterBegin':
this.insertBefore(parsedNode,this.firstChild);break;case 'beforeEnd':
this.appendChild(parsedNode);break;case 'afterEnd':
if (this.nextSibling) this.parentNode.insertBefore(parsedNode,this.nextSibling);else this.parentNode.appendChild(parsedNode);break;}
}
}
}
if (moz) {
extendEventObject();emulateAttachEvent();emulateEventHandlers(["click", "dblclick", "mouseover", "mouseout",
"mousedown", "mouseup", "mousemove",
"keydown", "keypress", "keyup"]);emulateCurrentStyle(["left", "right", "top", "bottom", "width", "height"]);emulateHTMLModel();Event.LEFT = 1;Event.MIDDLE = 2;Event.RIGHT = 3;}
else {
Event = {};Event.LEFT = 1;Event.MIDDLE = 4;Event.RIGHT = 2;}
function extendEventObject() {
Event.prototype.__defineSetter__("returnValue", function (b) {
if (!b) this.preventDefault();return b;});Event.prototype.__defineSetter__("cancelBubble", function (b) {
if (b) this.stopPropagation();return b;});Event.prototype.__defineGetter__("srcElement", function () {
var node = this.target;while (node.nodeType != 1) node = node.parentNode;return node;});Event.prototype.__defineGetter__("fromElement", function () {
var node;if (this.type == "mouseover")
node = this.relatedTarget;else if (this.type == "mouseout")
node = this.target;if (!node) return;while (node.nodeType != 1) node = node.parentNode;return node;});Event.prototype.__defineGetter__("toElement", function () {
var node;if (this.type == "mouseout")
node = this.relatedTarget;else if (this.type == "mouseover")
node = this.target;if (!node) return;while (node.nodeType != 1) node = node.parentNode;return node;});Event.prototype.__defineGetter__("offsetX", function () {
return this.layerX;});Event.prototype.__defineGetter__("offsetY", function () {
return this.layerY;});}
function emulateAttachEvent() {
HTMLDocument.prototype.attachEvent =
HTMLElement.prototype.attachEvent = function (sType, fHandler) {
		var shortTypeName = sType.replace(/on/, "");
fHandler._ieEmuEventHandler = function (e) {
window.event = e;return fHandler();};this.addEventListener(shortTypeName, fHandler._ieEmuEventHandler, false);};HTMLDocument.prototype.detachEvent =
HTMLElement.prototype.detachEvent = function (sType, fHandler) {
		var shortTypeName = sType.replace(/on/, "");
if (typeof fHandler._ieEmuEventHandler == "function")
this.removeEventListener(shortTypeName, fHandler._ieEmuEventHandler, false);else
this.removeEventListener(shortTypeName, fHandler, true);};}
function emulateEventHandlers(eventNames) {
for (var i = 0; i < eventNames.length; i++) {
document.addEventListener(eventNames[i], function (e) {
window.event = e;}, true);}
}
function emulateAllModel() {
var allGetter = function () {
var a = this.getElementsByTagName("*");var node = this;a.tags = function (sTagName) {
return node.getElementsByTagName(sTagName);};return a;};HTMLDocument.prototype.__defineGetter__("all", allGetter);HTMLElement.prototype.__defineGetter__("all", allGetter);}
function extendElementModel() {
HTMLElement.prototype.__defineGetter__("parentElement", function () {
if (this.parentNode == this.ownerDocument) return null;return this.parentNode;});HTMLElement.prototype.__defineGetter__("children", function () {
var tmp = [];var j = 0;var n;for (var i = 0; i < this.childNodes.length; i++) {
n = this.childNodes[i];if (n.nodeType == 1) {
tmp[j++] = n;if (n.name) {
if (!tmp[n.name])
tmp[n.name] = [];tmp[n.name][tmp[n.name].length] = n;}
if (n.id)
tmp[n.id] = n
}
}
return tmp;});HTMLElement.prototype.contains = function (oEl) {
if (oEl == this) return true;if (oEl == null) return false;return this.contains(oEl.parentNode);};}
function emulateCurrentStyle(properties) {
HTMLElement.prototype.__defineGetter__("currentStyle", function () {
var cs = {};var el = this;for (var i = 0; i < properties.length; i++) {
cs.__defineGetter__(properties[i], encapsulateObjects(el, properties[i]));}
return cs;});}
function encapsulateObjects(el, sProperty) {
return function () {
return document.defaultView.getComputedStyle(el, null).getPropertyValue(sProperty);};}
function emulateHTMLModel() {
function convertTextToHTML(s) {
		s = s.replace(/\&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g, "<BR>");
while (/\s\s/.test(s))
			s = s.replace(/\s\s/, "&nbsp; ");
		return s.replace(/\s/g, " ");
}
HTMLElement.prototype.insertAdjacentHTML = function (sWhere, sHTML) {
var df;var r = this.ownerDocument.createRange();switch (String(sWhere).toLowerCase()) {
case "beforebegin":
r.setStartBefore(this);df = r.createContextualFragment(sHTML);this.parentNode.insertBefore(df, this);break;case "afterbegin":
r.selectNodeContents(this);r.collapse(true);df = r.createContextualFragment(sHTML);this.insertBefore(df, this.firstChild);break;case "beforeend":
r.selectNodeContents(this);r.collapse(false);df = r.createContextualFragment(sHTML);this.appendChild(df);break;case "afterend":
r.setStartAfter(this);df = r.createContextualFragment(sHTML);this.parentNode.insertBefore(df, this.nextSibling);break;}
};HTMLElement.prototype.__defineSetter__("outerHTML", function (sHTML) {
var r = this.ownerDocument.createRange();r.setStartBefore(this);var df = r.createContextualFragment(sHTML);this.parentNode.replaceChild(df, this);return sHTML;});HTMLElement.prototype.__defineGetter__("canHaveChildren", function () {
switch (this.tagName) {
case "AREA":
case "BASE":
case "BASEFONT":
case "COL":
case "FRAME":
case "HR":
case "IMG":
case "BR":
case "INPUT":
case "ISINDEX":
case "LINK":
case "META":
case "PARAM":
return false;}
return true;});HTMLElement.prototype.__defineGetter__("outerHTML", function () {
var attr, attrs = this.attributes;var str = "<" + this.tagName;for (var i = 0; i < attrs.length; i++) {
attr = attrs[i];if (attr.specified)
str += " " + attr.name + '="' + attr.value + '"';}
if (!this.canHaveChildren)
return str + ">";return str + ">" + this.innerHTML + "</" + this.tagName + ">";});HTMLElement.prototype.__defineSetter__("innerText", function (sText) {
this.innerHTML = convertTextToHTML(sText);return sText;});var tmpGet;HTMLElement.prototype.__defineGetter__("innerText", tmpGet = function () {
var r = this.ownerDocument.createRange();r.selectNodeContents(this);return r.toString();});HTMLElement.prototype.__defineSetter__("outerText", function (sText) {
this.outerHTML = convertTextToHTML(sText);return sText;});HTMLElement.prototype.__defineGetter__("outerText", tmpGet);HTMLElement.prototype.insertAdjacentText = function (sWhere, sText) {
this.insertAdjacentHTML(sWhere, convertTextToHTML(sText));};}
function encodeFilename(filename, e) {
if (typeof(e) == 'undefined') e = '_';var ret      = '';for (var i=0; i<filename.length; i++) {
var chr = filename.substr(i,1);if (chr == e) {
ret += chr;continue;}
ord = chr.charCodeAt(1);if ((ord < 48) || (ord > 122) || ((ord > 57) && (ord < 65)) || ((ord > 90) && (ord < 97))) {
if (ord < 10) {
ret += e + '00' + ord;} else if (ord < 100) {
ret += e + '0' + ord;} else {
ret += e + ord;}
} else {
ret += chr;}
}
return ret;}

function _Bs_Path(){
	var t=document.getElementsByTagName("SCRIPT");
	t=(System.scriptElement=t[t.length-1]).src.replace(/\\/g, "/");
	 var ret = (t.lastIndexOf("/")<0)?".":t.substring(0, t.lastIndexOf("/"));
	 return ret;
}

Bs_Path = _Bs_Path();
