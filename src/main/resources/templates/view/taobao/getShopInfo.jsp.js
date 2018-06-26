/**
 *
 * https://shop.taobao.com/getShopInfo.htm?shopId=108533315&_ksTS=1523263113062_62&callback=jsonp63
 * Created by Administrator on 2018-04-09.
 */

//1.加入jquery 方便页面进行操作
var script=document.createElement("script");
script.type="text/javascript";
script.src="https://code.jquery.com/jquery-3.3.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);
var cuurTime = new Date().getTime();
//2.定义需要循环抓取的内容
var shopids = [58032330,58114335,59758757,61057727,65936725,66517087,66744119,67326913,70118733,70756511,100232527,105669227,112068440,112344349,113967864,115352807,116050076,117642281,117871771,119284587,119603731,123052663,126025383,141075181,142135236,144320070,155993044,166103363,258806829,403637843,546116992,587006620];
//var shopids = [672177,10209286,58114335]
//3.循环进行数据抓取
for(var i=0;i<shopids.length;i++){
    //闭包异步调用
    (function () {
        var sid = shopids[i];
        var url = "https://shop.taobao.com/getShopInfo.htm?shopId="+sid;
        $.ajax({ url: url, dataType:"json",cache:false, success: function(msg){
            document.write(sid+":"+msg.data.starts);
            document.write("<br>");
            //console.log(new Date().getTime()-cuurTime);
        }});
    })();
}

