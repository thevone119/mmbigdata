(function () {
    var fn = function () {
        return new fn.prototype.init();
    };

    fn.prototype = {
        init: function () {
            return this;
        },
        log: function (msg) {
            if (msg && msg.hasOwnProperty('success')) {
                var opt = msg;
                console.log(msg);
                //alert(opt.msg);
            } else {
                console.log(msg);
                //alert(msg);
            }
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
        getPara: function (name) {
            var r = new RegExp("(\\?|#|&)" + name + "=([^&#]*)(&|#|$)");
            var m = location.href.match(r);
            return (!m ? "" : m[2]);
        },
        alert: function (msg, type) {
            if (msg && msg.hasOwnProperty('success')) {
                msg = msg.msg;
            }
            switch (type){
                case 'info':
                    new Vue().$Modal.info({
                        title: '提示',
                        content: msg
                    });
                    break;
                case 'success':
                    new Vue().$Modal.success({
                        title: '提示',
                        content: msg
                    });
                    break;
                case 'warning':
                    new Vue().$Modal.warning({
                        title: '提示',
                        content: msg
                    });
                    break;
                case 'confirm':
                    new Vue().$Modal.confirm({
                        title: '提示',
                        content: msg
                    });
                    break;
                default:
                    new Vue().$Modal.error({
                        title: '提示',
                        content: msg
                    });
                    break;
            }
        },
        toast: function (msg, msgType) {
            msgType = msgType || 'info';
            let config = {
                content: msg
            };
            switch (msgType) {
                case 'info':
                    new Vue().$Message.info(config);
                    break;
                case 'success':
                    new Vue().$Message.success(config);
                    break;
                case 'warning':
                    new Vue().$Message.warning(config);
                    break;
                case 'error':
                    new Vue().$Message.error(config);
                    break;
                case 'loading':
                    new Vue().$Message.loading(config);
                    break;
            }
        },
        getRandomStr(strLength) {
            let chars = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
            let res = "";
            for (let i = 0; i < strLength; i++) {
                let id = Math.ceil(Math.random() * 35);
                res += chars[id];
            }
            return res;
        },
        confirm: function (msg, yesFunc, noFunc) {
            new Vue().$Modal.confirm({
                title: '确认对话框',
                content: msg,
                onOk: () => {
                    if (typeof yesFunc == 'function')
                        setTimeout(yesFunc, 1000);
                },
                onCancel: () => {
                    if (typeof noFunc == 'function')
                        noFunc()
                }
            });
        },
        getLoginUserToken: function () {
            var dbUser = PB.getLoginUser();
            if (!dbUser) {
                return '';
            }
            return dbUser.token;
        },
        getLoginUser: function () {
            var dbUser = PB.getStorage('user');
            if (!dbUser) {
                return null;
            }
            return dbUser;
        },
        setLoginUser: function (d) {
            this.setStorage('user', d);
        },
        showLoading: function (msg) {

            msg = msg || '加载中...';
            if (!window.isShowLoading) {
                window.showLoadingCOM = new Vue().$Message.loading({
                    content: msg,
                    duration: 0
                });
                window.isShowLoading = true;
            }
        },
        hideLoading: function () {
            if (window.isShowLoading) {
                //new Vue().$Message.destroy();
                window.setTimeout(window.showLoadingCOM, 1);
                window.isShowLoading = false;
            }
        },
        ajaxJson: function (url, data, p_successfunc) {// 通过JSON提交ajax数据
            $.ajax({
                type: "post",
                url:url,
                async: true, // 是否异步
                data: data,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: p_successfunc
            });
        },
        ajax: function (p_route, p_option, p_successfunc) {
            let def = {
                url: '',
                contentType: 'application/x-www-form-urlencoded', // 好像埋下了一个大坑，现在主流应该是用json化传递
                data: '',
                type: 'POST',
                async: true,
                timeout: 1000 * 120,
                headers: {},

                isInFramePage: false,
                showLoading: true,
                loadingContent: '数据加载中',
                cacheKey: null,
                cacheTime: 0, // 分钟
                myBeforeSend: null,
                success: null,
                myFail: null,
                complete: null
            };

            let loginUser = PB.getLoginUser();
            if (loginUser) {
                def.headers.loginusertoken = loginUser.token;
            }

            if (arguments.length === 3) {
                def.url = p_route.uri;
                def.data = p_route.body;
                def.type = p_route.method;
                PB.extend(def, p_option, true);
            }
            else {
                //判断用户传递的是1和2参数，还是2和3参数
                if (arguments[0].uri) { // 有uri就是传的1和2
                    def.url = p_route.uri;
                    def.data = p_route.body;
                    def.type = p_route.method;
                    p_successfunc = p_option;
                }
                else {
                    let cb = p_option;
                    p_option = p_route;
                    p_successfunc = cb;
                }
                PB.extend(def, p_option, true);
            }

            if (def.cacheKey && def.cacheTime) {
                let cData = PB.getStorage(def.cacheKey, true);
                if (cData) {
                    return p_successfunc(cData);
                }
            }

            def.complete = def.complete || function () {
                if (def.showLoading) {
                    PB.hideLoading();
                }
            };

            def.beforeSend = function () {
                if (typeof def.myBeforeSend === 'function') {
                    if (!def.myBeforeSend()) {
                        return;
                    }
                }

                if (def.showLoading) {
                    PB.showLoading(def.loadingContent);
                }
            };

            def.error = function (xhr, errorType, err) {
                if (typeof def.myFail === 'function') {
                    def.myFail();
                }
                else {
                    if (def.isInFramePage === true) {
                        PB.formPageSaveError();
                    }
                    if (xhr.response) {
                        try {
                            let errObj = JSON.parse(xhr.response);
                            PB.alert(errObj.msg);
                        } catch (ex) {
                            PB.alert(xhr.response);
                        }
                        console.log(xhr.response);
                    }
                    else {
                        console.log(xhr.message ? xhr.message : xhr.response.data);
                    }
                }
            };


            if (typeof p_successfunc === 'function') {
                def.success = function (res) {
                    let d = res; // 不同ajax框架可能返回值不一样，这里多一步，最个转换
                    if (d.success) {
                        if (def.cacheKey && def.cacheTime) {
                            PB.setStorage(def.cacheKey, d, def.cacheTime);
                        }
                        p_successfunc(d)
                    }
                    else {
                        PB.alert(d);
                        if (typeof def.myFail == 'function') {
                            def.myFail(d);
                        }
                        if (def.isInFramePage === true) {
                            PB.formPageSaveError(d);
                        }
                        console.log(d);
                    }
                }
            }

            $.ajax(def);
        },
        fmtUri: function (p_uri) {
            let newUri = p_uri;
            // 因为后台是session和token同时使用，所以就算页面不传token，应该问题不大
            // 考虑一下，还是让它带上token尾巴，这样postman调试比较容易
            // let token = this.getLoginUserToken();
            // if (token) {
            //     if (~p_uri.indexOf('?')) {
            //         newUri += '&token=' + token;
            //     }
            //     else {
            //         newUri += '?token=' + token;
            //     }
            // }
            return newUri;
        },
        fmtTimestamp: function (timesp) {
            let date = new Date(timesp);
            let y = date.getFullYear();
            let m = date.getMonth() + 1;
            m = m < 10 ? ('0' + m) : m;
            let d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            let h = date.getHours();
            let minute = date.getMinutes();
            minute = minute < 10 ? ('0' + minute) : minute;
            return y + '-' + m + '-' + d + ' ' + h + ':' + minute;
        },
        open: function (p_url) {
            let url = this.fmtUri(p_url);
            window.location.href = url;
        },
        setStorage: function (key, value, times) {
            if (window.localStorage) {
                if (value == null) {
                    window.localStorage.removeItem(key);
                }
                else {
                    if (times) {
                        value = {
                            myOutDate: new Date().getTime() + 1000 * 60 * times,
                            data: value
                        };
                    }

                    window.localStorage.setItem(key, JSON.stringify(value));
                }
            }
        },
        getStorage: function (key, chkTimes) {
            if (!window.localStorage) {
                return null;
            }
            let cData = window.localStorage.getItem(key);
            if (!cData || cData.toLowerCase() == "null") {
                return null;
            }
            cData = JSON.parse(cData);
            if (cData && chkTimes) {
                if (cData.myOutDate > new Date().getTime()) {
                    return cData.data;
                }
                else {
                    return null;
                }
            }
            else {
                if (cData.myOutDate) {
                    return cData.data;
                }
                else {
                    return cData;
                }
            }
        },
        showTopFormPage: function (uri, title, options) {
            window.top.showFormPage(uri, title, options);
        },
        showFormPage: function (p_uri, p_title, p_options) {
            if (p_options && p_options.width && p_options.width < 100) {
                p_options.width = window.innerWidth * p_options.width * 0.01;
            }
            if (p_options && p_options.height && p_options.height < 100) {
                p_options.height = (window.innerHeight - 100) * p_options.height * 0.01;
            }
            let def = {
                width: window.innerWidth * 0.8,
                height: window.innerHeight * 0.8,
                readOnly: false,
                showFooter: true
            };

            PB.extend(def, p_options);

            def.url = PB.fmtUri('/view/' + p_uri);
            def.title = p_title;

            if (window.pbMyModel) {
                window.pbMyModel.init(def);
                window.pbMyModel.open();
            }
            else {
                let myModel = Vue.extend({
                    template: `
                        <Modal el-model="formPageModel" :width="formPageWidth" :styles="{top: '20px'}" :mask-closable="false">
                            <p slot="header" class="g-model-title">
                                <span>{{formPageTitle}}</span>
                            </p>
                            <iframe id="ifrFormPage" :src="formPageURL" frameborder="0"
                                    :style="{'width':'100%','height':getFormPageHeight}"></iframe>
                        
                                <div slot="footer" v-show="formPageFooter">
                                    <el-button type="text" @click="cancelFormPage">取消</el-button>
                                    <el-button type="primary" :loading="formPageSaving" @click="saveFormPage"
                                              v-if="!formPageReadOnly">确定
                                    </el-button>
                                </div>
                           
                        </Modal>
                    `,
                    data() {
                        return {
                            formPageTitle: '',
                            formPageModel: true,
                            formPageURL: '',
                            formPageWidth: 0,
                            formPageReadOnly: false,
                            formPageSaving: false,
                            formPageFooter: true,
                        }
                    },
                    created() {
                        this.init(def);
                    },
                    computed: {
                        getFormPageHeight() {
                            if (this.formPageFooter) {
                                return this.formPageHeight - 50 + 'px';
                            }
                            else {
                                return this.formPageHeight - 20 + 'px';
                            }

                        }
                    },
                    methods: {
                        init(p_def) {
                            this.formPageTitle = p_def.title;
                            this.formPageURL = '';
                            this.formPageWidth = p_def.width;
                            this.formPageHeight = p_def.height;
                            this.formPageReadOnly = p_def.readOnly;
                            this.formPageFooter = p_def.showFooter;

                            setTimeout(() => {
                                this.formPageURL = p_def.url;
                            }, 1);
                        },
                        open() {
                            this.formPageSaving = false;
                            this.formPageModel = true;
                        },
                        saveFormPage() {
                            document.getElementById('ifrFormPage').contentWindow.save();
                            this.formPageSaving = true;
                        },
                        cancelFormPage() {
                            this.saved();
                            this.formPageModel = false;
                            this.formPageURL = '';
                        },
                        saved() {
                            setTimeout(() => {
                                this.formPageSaving = false;
                            }, 200);

                        }
                    }
                });
                window.pbMyModel = new myModel();
                document.getElementById('app').appendChild(window.pbMyModel.$mount().$el);
            }
        },
        formPageSaveOK(d) {
            let pbmy = window.parent.pbMyModel;
            if (pbmy) {
                if (!d) {
                    pbmy.cancelFormPage();
                }
                else {
                    if (d.success) {
                        PB.toast('操作成功');
                        pbmy.cancelFormPage();
                        if (typeof window.parent.saveOK == 'function') {
                            window.parent.saveOK(d);
                        }
                    }
                    else {
                        pbmy.saved();
                        pbmy.formPageSaving = false;
                        PB.alert(d);
                    }
                }
            }
        },
        formPageSaveError() {
            let pbmy = window.parent.pbMyModel;
            if (pbmy) {
                pbmy.saved();
            }
        },
        formPageCancel() {
            let pbmy = window.parent.pbMyModel;
            if (pbmy) {
                pbmy.saved();
                pbmy.cancelFormPage();
            }
        },
        formPageClose() {
            let pbmy = window.pbMyModel; // 注意，这个方法是给父窗口调用的
            if (pbmy) {
                pbmy.saved();
                pbmy.cancelFormPage();
            }
        },
        apiRoute(p_uri, p_method, p_body) {
            let obj = {};
            obj.uri = p_uri;
            obj.method = p_method || 'POST';
            obj.body = p_body;
            return obj;
        }
    };

    //function _isObject(o) {
    //    return Object.prototype.toString.call(o) === '[object Object]';
    //}

    Date.prototype.diff = function (date) {
        return (this.getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
    };

    //日期格式化
    Date.prototype.pattern = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        var week = {
            "0": "\u65e5",
            "1": "\u4e00",
            "2": "\u4e8c",
            "3": "\u4e09",
            "4": "\u56db",
            "5": "\u4e94",
            "6": "\u516d"
        };
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        if (/(E+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "\u661f\u671f" : "\u5468") : "") + week[this.getDay() + ""]);
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(fmt)) {
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
        return fmt;
    };


    fn.prototype.init.prototype = fn.prototype;

    window.PB = fn();
})();
//
// (function () {
//     var arrMETA = [
//         {
//             name: 'viewport',
//             content: 'maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0'
//         },
//         {name: 'format-detection', content: 'telephone=no, email=no'}
//     ];
//
//     var html_meta = [];
//
//     for (var i in arrMETA) {
//         var obj = arrMETA[i];
//         if (!document.querySelector('meta[name=' + obj.name + ']')) {
//             var newNode = document.createElement("meta");
//             newNode.name = obj.name;
//             newNode.content = obj.content;
//             document.querySelector('head').appendChild(newNode);
//         }
//
//     }
// })();


