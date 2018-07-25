(function () {
    var fn = function () {
        return new fn.prototype.init();
    };

    fn.prototype = {
        init: function () {
            return this;
        },

        extend: function (target, source, deep) {
            target = target || {};
            var sType = typeof source, i = 1, options;
            if (sType === 'undefined' || sType === 'boolean') {
                deep = sType === 'boolean' ? source : false;
                source = target;
                target = this;
            }
            if (typeof source !== 'object' && Object.prototype.toString.call(source) !== '[object Function]')
                source = {};
            while (i <= 2) {
                options = i === 1 ? target : source;
                if (options != null) {
                    for (var name in options) {
                        var src = target[name], copy = options[name];
                        if (target === copy)
                            continue;
                        if (deep && copy && typeof copy === 'object' && !copy.nodeType)
                            target[name] = this.extend(src ||
                                (copy.length != null ? [] : {}), copy, deep);
                        else if (copy !== undefined)
                            target[name] = copy;
                    }
                }
                i++;
            }
            return target;
        },

        getnikename: function () {
            var nikename = this.getCookie("nikename");
            if (!nikename) {
                //
                return null;
            }
            return nikename;
        },
        getuserid: function () {
            var userid = this.getCookie("userid");
            if (!userid) {
                //
                return null;
            }
            return userid;
        },

        setLoginUser: function (d) {
            this.setCookie('userid', d.userid+"");
            this.setCookie('nikename', d.nikename+"");
        },
        setCookie:function(cname,cvalue,exdays){
            var d = new Date();
            d.setTime(d.getTime()+(exdays*8*60*60*1000));
            var expires = "expires="+d.toGMTString();
            document.cookie = cname+"="+cvalue+"; "+expires;
        },
        getCookie:function(cname){
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for(var i=0; i<ca.length; i++) {
                var c = ca[i].trim();
                if (c.indexOf(name)==0) { return c.substring(name.length,c.length); }
            }
            return "";
        }
    };
    fn.prototype.init.prototype = fn.prototype;
    window.USER = fn();
})();



