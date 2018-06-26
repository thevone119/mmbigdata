/**
 * @description javascript expand class
 * @author LiLiQuan
 * @since 2014-12-18 15:37:303
 * 当使用多个js库文件时，为了避免在调用时不同js库文件的同名函数的冲突，一般会使用命名空间来解决。
 * JavaScript支持同名函数，但只使用最后一个加载的函数（不支持重载，不会考虑参数，只看函数名字）， 
 * 哪一个最后被加载，哪一个就会被调用到。所以不使用命名空间的话，就很容易遇到同名函数冲突的问题。
 * 
 */
var Namespace = new Object();
Namespace.register = function(fnFullPath) {
	var array = fnFullPath.split(".");
	var fn = "";
	for(var i=0; i<array.length; i++) {
		if(i > 0) {
			fn += ".";
		}
		fn += array[i];
		// eval() 函数可计算某个字符串，并执行其中的的 JavaScript 代码。
		eval("if(typeof(" + fn + ") == 'undefined') {" + fn + " = new Object();}");
	}
};