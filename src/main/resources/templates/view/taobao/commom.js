

var script=document.createElement("script");
script.type="text/javascript";
script.src="https://code.jquery.com/jquery-3.3.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);

/**
 * 抽取字符串，根据行进行抽取
 * @param start
 * @param is
 * @returns {*}
 */
String.prototype.extractLine = function (start,is) {
    var ret=null;
    if(start<=0){
        start=0;
    }
    var thisstr = new String(this);
    thisstr = thisstr.substring(start);
    thisstr.trim().split('\n').forEach(function(v, j) {
        var isidx = true;
        for(var i=0; i<is.length; i++){

            if(v.indexOf(is[i])==-1){
                isidx =false;
            }
        }
        if(isidx){
            ret = v;
            return;
        }
    })
    return ret;
}

String.prototype.trimStr = function () {
    if(this==null){
        return null;
    }
    var t = new String(this);
    t = t.trim();
    if(t.indexOf("&nbsp;")!=-1){
        t = t.replace("&nbsp;","");
    }
    if(t==""){
        t = null;
    }
    return t;
}

/**
 * 抽取字符串
 * @param start 开始位置
 * @param end 结束位置
 * @returns {*}
 */
String.prototype.extractString = function (start,end) {
    if(this==null){
        return null;
    }
    var template = this;
    var s = template.indexOf(start);
    if(s==-1){
        return null;
    }
    s = s+start.length;
    if(end==null){
        return template.substring(s);
    }
    var e = template.indexOf(end,s);
    if(e==-1){
        return null;
    }
    var ret = template.substring(s,e);
    return ret;
}


/**
 * json转换陈字符串输出
 * @param o
 * @returns {string}
 * @constructor
 */
function JsonToString(o) {
    var arr = [];
    var fmt = function(s) {
        if (typeof s == 'object' && s != null) return JsonToStr(s);
        //return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
        return /^(string)$/.test(typeof s) ? '"' + s + '"' : s;
    }
    for (var i in o)
        arr.push("\"" + i + "\":" + fmt(o[i]));
    return '{' + arr.join(',') + '}';
}