/**
 * 对layer.mobile插件的封装，更方便使用。
 * @since   2017年2月17日 16:16:36
 */
Namespace.register("plugin.layer.mobile");

//提示，2秒后消失
plugin.layer.mobile.tip = function(msg) {
	layer.open({
		skin: 'msg',
		time: 2,
		content: msg
	});
};

//居中提示，必须点击“关闭”按钮才能关闭
plugin.layer.mobile.tipCenterWin = function(msg) {
	layer.open({
		type: 0,
		shadeClose: false,
		content: msg,
		btn: ['<font color="#FD482C">关闭</font>']
	});
};

//提示，必须点击“关闭”按钮才能关闭
plugin.layer.mobile.tipWin = function(msg) {
	layer.open({
		type: 0,
		skin: 'footer',
		shadeClose: false,
		content: msg,
		btn: ['<font color="#FD482C">关闭</font>']
	});
};

//提示加载中
plugin.layer.mobile.tipLoading = function(msg) {
	layer.open({
		type: 2,
		content: msg
	});
};

//关闭layer窗口和提示
plugin.layer.mobile.tipClose = function() {
	layer.closeAll();
};