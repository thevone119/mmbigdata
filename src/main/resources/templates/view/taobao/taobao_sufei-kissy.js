/* 2017-05-15 10:58:48 */
!
    function(e) {
        if ("object" == typeof exports) module.exports = e();
        else if ("function" == typeof define && define.amd) define(e);
        else {
            var t;
            "undefined" != typeof window ? t = window: "undefined" != typeof global ? t = global: "undefined" != typeof self && (t = self),
                t.sufei = e()
        }
    } (function() {
        return function e(t, n, i) {
            function r(o, a) {
                if (!n[o]) {
                    if (!t[o]) {
                        var u = "function" == typeof require && require;
                        if (!a && u) return u(o, !0);
                        if (s) return s(o, !0);
                        throw new Error("Cannot find module '" + o + "'")
                    }
                    var f = n[o] = {
                        exports: {}
                    };
                    t[o][0].call(f.exports,
                        function(e) {
                            var n = t[o][1][e];
                            return r(n ? n: e)
                        },
                        f, f.exports, e, t, n, i)
                }
                return n[o].exports
            }
            for (var s = "function" == typeof require && require,
                     o = 0; o < i.length; o++) r(i[o]);
            return r
        } ({
                1 : [function(e, t, n) {
                    function i(e) {
                        return this instanceof i || !l(e) ? void 0 : s(e)
                    }
                    function r(e) {
                        var t, n;
                        for (t in e) n = e[t],
                            i.Mutators.hasOwnProperty(t) ? i.Mutators[t].call(this, n) : this.prototype[t] = n
                    }
                    function s(e) {
                        return e.extend = i.extend,
                            e.implement = r,
                            e
                    }
                    function o() {}
                    function a(e, t, n) {
                        for (var i in t) if (t.hasOwnProperty(i)) {
                            if (n && -1 === d(n, i)) continue;
                            "prototype" !== i && (e[i] = t[i])
                        }
                    }
                    t.exports = i,
                        i.create = function(e, t) {
                            function n() {
                                e.apply(this, arguments),
                                this.constructor === n && this.initialize && this.initialize.apply(this, arguments)
                            }
                            return l(e) || (t = e, e = null),
                            t || (t = {}),
                            e || (e = t.Extends || i),
                                t.Extends = e,
                            e !== i && a(n, e, e.StaticsWhiteList),
                                r.call(n, t),
                                s(n)
                        },
                        i.extend = function(e) {
                            return e || (e = {}),
                                e.Extends = this,
                                i.create(e)
                        },
                        i.Mutators = {
                            Extends: function(e) {
                                var t = this.prototype,
                                    n = u(e.prototype);
                                a(n, t),
                                    n.constructor = this,
                                    this.prototype = n,
                                    this.superclass = e.prototype
                            },
                            Implements: function(e) {
                                c(e) || (e = [e]);
                                for (var t, n = this.prototype; t = e.shift();) a(n, t.prototype || t)
                            },
                            Statics: function(e) {
                                a(this, e)
                            }
                        };
                    var u = Object.__proto__ ?
                            function(e) {
                                return {
                                    __proto__: e
                                }
                            }: function(e) {
                                return o.prototype = e,
                                    new o
                            },
                        f = Object.prototype.toString,
                        c = Array.isArray ||
                            function(e) {
                                return "[object Array]" === f.call(e)
                            },
                        l = function(e) {
                            return "[object Function]" === f.call(e)
                        },
                        d = Array.prototype.indexOf ?
                            function(e, t) {
                                return e.indexOf(t)
                            }: function(e, t) {
                                for (var n = 0,
                                         i = e.length; i > n; n++) if (e[n] === t) return n;
                                return - 1
                            }
                },
                    {}],
                2 : [function(e, t, n) {
                    t.exports = {
                        app: "kissy",
                        style: "app/common/sufei-tb.css"
                    }
                },
                    {}],
                3 : [function(e, t, n) {
                    var i = {},
                        r = {};
                    i.on = function(e, t) {
                        var n = r[e] || (r[e] = []);
                        return n.push(t),
                            i
                    },
                        i.off = function(e, t) {
                            if (!e && !t) return r = {},
                                i;
                            var n = r[e];
                            if (n) if (t) for (var s = n.length - 1; s >= 0; s--) n[s] === t && n.splice(s, 1);
                            else delete r[e];
                            return i
                        },
                        i.fire = function(e, t) {
                            var n = r[e];
                            if (n) {
                                n = n.slice();
                                for (var s = 0,
                                         o = n.length; o > s; s++) n[s](t)
                            }
                            return i
                        },
                        t.exports = i
                },
                    {}],
                4 : [function(e, t, n) {
                    var i = e("./mod/sufei-kissy"),
                        r = e("./config/sufei-kissy");
                    screen.width < 800 && (r.style = "app/common/sufei-h5.css");
                    var s = "0.1.0";
                    r.version = s,
                        r.style = "//g.alicdn.com/sd/sufei/" + s + "/" + r.style,
                        t.exports = new i(r),
                        window.__sufei_injected__ = 1
                },
                    {
                        "./config/sufei-kissy": 2,
                        "./mod/sufei-kissy": 7
                    }],
                5 : [function(e, t, n) {
                    t.exports = function() {
                        "use strict";
                        var e, t, n, i, r = {
                                '"': '"',
                                "\\": "\\",
                                "/": "/",
                                b: "\b",
                                f: "\f",
                                n: "\n",
                                r: "\r",
                                t: "	"
                            },
                            s = function(t) {
                                throw {
                                    name: "SyntaxError",
                                    message: t,
                                    at: e,
                                    text: n
                                }
                            },
                            o = function(i) {
                                return i && i !== t && s("Expected '" + i + "' instead of '" + t + "'"),
                                    t = n.charAt(e),
                                    e += 1,
                                    t
                            },
                            a = function() {
                                var e, n = "";
                                for ("-" === t && (n = "-", o("-")); t >= "0" && "9" >= t;) n += t,
                                    o();
                                if ("." === t) for (n += "."; o() && t >= "0" && "9" >= t;) n += t;
                                if ("e" === t || "E" === t) for (n += t, o(), ("-" === t || "+" === t) && (n += t, o()); t >= "0" && "9" >= t;) n += t,
                                    o();
                                return e = +n,
                                    isFinite(e) ? e: void s("Bad number")
                            },
                            u = function() {
                                var e, n, i, a = "";
                                if ('"' === t) for (; o();) {
                                    if ('"' === t) return o(),
                                        a;
                                    if ("\\" === t) if (o(), "u" === t) {
                                        for (i = 0, n = 0; 4 > n && (e = parseInt(o(), 16), isFinite(e)); n += 1) i = 16 * i + e;
                                        a += String.fromCharCode(i)
                                    } else {
                                        if ("string" != typeof r[t]) break;
                                        a += r[t]
                                    } else a += t
                                }
                                s("Bad string")
                            },
                            f = function() {
                                for (; t && " " >= t;) o()
                            },
                            c = function() {
                                switch (t) {
                                    case "t":
                                        return o("t"),
                                            o("r"),
                                            o("u"),
                                            o("e"),
                                            !0;
                                    case "f":
                                        return o("f"),
                                            o("a"),
                                            o("l"),
                                            o("s"),
                                            o("e"),
                                            !1;
                                    case "n":
                                        return o("n"),
                                            o("u"),
                                            o("l"),
                                            o("l"),
                                            null
                                }
                                s("Unexpected '" + t + "'")
                            },
                            l = function() {
                                var e = [];
                                if ("[" === t) {
                                    if (o("["), f(), "]" === t) return o("]"),
                                        e;
                                    for (; t;) {
                                        if (e.push(i()), f(), "]" === t) return o("]"),
                                            e;
                                        o(","),
                                            f()
                                    }
                                }
                                s("Bad array")
                            },
                            d = function() {
                                var e, n = {};
                                if ("{" === t) {
                                    if (o("{"), f(), "}" === t) return o("}"),
                                        n;
                                    for (; t;) {
                                        if (e = u(), f(), o(":"), Object.hasOwnProperty.call(n, e) && s('Duplicate key "' + e + '"'), n[e] = i(), f(), "}" === t) return o("}"),
                                            n;
                                        o(","),
                                            f()
                                    }
                                }
                                s("Bad object")
                            };
                        return i = function() {
                            switch (f(), t) {
                                case "{":
                                    return d();
                                case "[":
                                    return l();
                                case '"':
                                    return u();
                                case "-":
                                    return a();
                                default:
                                    return t >= "0" && "9" >= t ? a() : c()
                            }
                        },
                            function(r, o) {
                                var a;
                                return n = r,
                                    e = 0,
                                    t = " ",
                                    a = i(),
                                    f(),
                                t && s("Syntax error"),
                                    "function" == typeof o ?
                                        function u(e, t) {
                                            var n, i, r = e[t];
                                            if (r && "object" == typeof r) for (n in r) Object.prototype.hasOwnProperty.call(r, n) && (i = u(r, n), void 0 !== i ? r[n] = i: delete r[n]);
                                            return o.call(e, t, r)
                                        } ({
                                                "": a
                                            },
                                            "") : a
                            }
                    } ()
                },
                    {}],
                6 : [function(e, t, n) {
                    function i(e) {
                        try {
                            var t, n, i = e.data.split(a);
                            i.length > 1 ? (t = i[0], n = i[1]) : (i = r(i[0]), t = i.type, n = i.content);
                            for (var s = 0,
                                     o = l.length; o > s; s++) l[s].event === t && l[s].callback(n)
                        } catch(u) {}
                    }
                    function r(e) {
                        return o ? o(e) : s(e)
                    }
                    var s = e("./json_parse"),
                        o = window.JSON && JSON.parse,
                        a = "@=_=@",
                        u = null,
                        f = null,
                        c = null,
                        l = [],
                        d = {
                            uid: 0,
                            hid: -1,
                            q: [],
                            tm: 0,
                            postMessage: function(e, t) {
                                var n = ++d.uid,
                                    i = d.q,
                                    r = {
                                        name: +new Date + "" + n + "^" + document.domain + "&" + t,
                                        uid: n,
                                        target: e
                                    };
                                i.push(r),
                                d.tm || (d.tm = setInterval(function() {
                                        var e = d.q;
                                        if (! (0 === e.length || e[0].uid <= d.hid)) {
                                            var t = e[0];
                                            d.hid = t.uid,
                                                t.target.name = t.name
                                        }
                                    },
                                    10))
                            },
                            ListenerMessage: function(e, t) {
                                function n() {
                                    if (!e) return ! 1;
                                    var n = e.name;
                                    if (n !== i) {
                                        d.q.shift(),
                                            i = n;
                                        var s = r.exec(n);
                                        if (!s) return;
                                        t && t({
                                            origin: s[2],
                                            data: s[3]
                                        })
                                    }
                                }
                                var i = "",
                                    r = /^(\d+?)\^(.+?)&(.*?)$/;
                                setInterval(n, 10)
                            }
                        };
                    window.SufeiMessenger = t.exports = {
                        initListener: function(e) {
                            u = e.currentWin,
                                f = e.originWin,
                                c = e.msgTransfer,
                            u && f && c && (u.postMessage ? u.addEventListener ? u.addEventListener("message", i, !1) : u.attachEvent && u.attachEvent("onmessage", i) : "fromFrame" == c ? d.ListenerMessage(f, i) : d.ListenerMessage(u, i))
                        },
                        register: function(e, t) {
                            l.push({
                                event: e,
                                callback: t
                            })
                        },
                        send: function(e) {
                            var t = e.type + a + e.content;
                            u && f && c && (f.postMessage ? f.postMessage(t, "*") : "fromFrame" == c ? d.postMessage(f, t) : d.postMessage(u, t, "*"))
                        }
                    }
                },
                    {
                        "./json_parse": 5
                    }],
                7 : [function(e, t, n) {
                    function i(e) {
                        return function(t) {
                            return {}.toString.call(t) == "[object " + e + "]"
                        }
                    }
                    function r(e) {
                        var t = e.dataType;
                        g(t) && (/script/.test(t.join("")) ? e.dataType = "jsonp": e.dataType = "json")
                    }
                    var s, o, a = e("../emit"),
                        u = e("../sufei-base"),
                        f = window,
                        c = [].slice,
                        l = f.$ && f.$.__io__ ? f.$: null,
                        d = f.KISSY || l,
                        p = null,
                        v = null,
                        g = (i("String"), Array.isArray || i("Array")),
                        h = u.extend({
                            initialize: function(e) {
                                var t = this;
                                a.on("event:cleanHijackQueue@sufei",
                                    function() {
                                        for (var e, n, i, r = [t.currentRequest].concat(t.queue), s = 0; s < r.length; s++) r[s] && (n = r[s].sufeiData, e = n && n.defer, i = n && n.response, e && o.apply(e, [i]))
                                    }),
                                    h.superclass.initialize.call(t, e),
                                d && (d.use ? d.use(d.version >= "1.4" ? "io,promise": "ajax",
                                    function(e, n, i) {
                                        p = n,
                                            v = d.Defer || i.Defer,
                                            s = v.prototype.resolve,
                                            o = v.prototype.reject,
                                            t.setup()
                                    }) : (p = d.__io__, v = d.__promise__.Defer, s = v.prototype.resolve, o = v.prototype.reject, t.setup()))
                            },
                            setup: function() {
                                var e = this;
                                p.on("start",
                                    function(t) {
                                        var n = t.ajaxConfig || t.io && (t.io.cfg || t.io.config) || {},
                                            i = t.io,
                                            r = i && (i.defer || i._defer),
                                            o = n.dataType;
                                        if (g(o) && (o = o.join("")), /json|\*/.test(o)) {
                                            var a = n.success;
                                            n.success = function(t, i, o) {
                                                var u = e.validate(t);
                                                u.pass ? (a && a.apply(n.context || n, arguments), n.sufeiData && n.sufeiData.defer && s.apply(n.sufeiData.defer, [c.call(arguments)])) : ("string" == typeof t && (t = u.result), e.run({
                                                    url: t.url,
                                                    config: d.merge({
                                                            sufeiData: {
                                                                defer: r,
                                                                response: c.call(arguments)
                                                            }
                                                        },
                                                        n),
                                                    im: u.immediate
                                                }))
                                            },
                                            r && d.version >= "1.4" && (r.resolve = function(t) {
                                                var n = e.validate(t && t[0]);
                                                n.pass && s.apply(r, [t])
                                            })
                                        }
                                    })
                            },
                            getValidateURI: function(e) {
                                var t = this;
                                new p({
                                    url: e,
                                    dataType: "jsonp",
                                    sufei: !0,
                                    success: function(e) {
                                        t.status = 200,
                                            a.fire("event:showDialog@sufei", e.url)
                                    }
                                })
                            },
                            resendRequest: function(e) {
                                var t = this;
                                if (p) {
                                    var n = [t.currentRequest];
                                    d.each(n.concat(t.queue),
                                        function(n, i) {
                                            r(n),
                                                n.url = t.addQueryToken(n.url, e),
                                                new p(n)
                                        }),
                                        t.reset()
                                }
                            }
                        });
                    t.exports = h
                },
                    {
                        "../emit": 3,
                        "../sufei-base": 8
                    }],
                8 : [function(e, t, n) {
                    function i() {
                        var e = u.createElement("b");
                        return e.innerHTML = "<!--[if lte IE 7]><i></i><![endif]-->",
                        1 === e.getElementsByTagName("i").length
                    }
                    function r(e) {
                        return g ? g(e) : v(e)
                    }
                    function s(e, t) {
                        function n() {
                            i.onload = i.onreadystatechange = null,
                                i = null,
                                t()
                        }
                        e || t && t();
                        var i = u.createElement("link");
                        i.charset = "utf-8",
                            i.rel = "stylesheet",
                            i.href = e;
                        var r = "onload" in i;
                        m && !r && setTimeout(function() {
                                o(i, t)
                            },
                            1),
                            r ? i.onload = n: i.onreadystatechange = function() { / loaded | complete / .test(i.readyState) && n()
                            },
                            f.appendChild(i),
                        u.createStyleSheet && u.createStyleSheet(e)
                    }
                    function o(e, t) {
                        var n, i = e.sheet;
                        if (m) i && (n = !0);
                        else if (i) try {
                            i.cssRules && (n = !0)
                        } catch(r) {
                            "NS_ERROR_DOM_SECURITY_ERR" === r.name && (n = !0)
                        }
                        setTimeout(function() {
                                n ? t() : o(e, t)
                            },
                            20)
                    }
                    var a = window,
                        u = document,
                        f = u.head || u.getElementsByTagName("head")[0] || u.documentElement,
                        c = a.navigator.userAgent,
                        l = e("./emit"),
                        d = e("./class"),
                        p = e("./messenger"),
                        v = e("./json_parse"),
                        g = a.JSON && JSON.parse,
                        h = function() {},
                        m = +c.replace(/.*(?:AppleWebKit|AndroidWebKit)\/?(\d+).*/i, "$1") < 536,
                        y = /iphone|ipad|ipod/.test(c.toLowerCase()),
                        w = "rgv587_flag",
                        x = "sm";
                    t.exports = d.create({
                        initialize: function(e) {
                            var t = this;
                            e = e || {},
                                t.app = e.app || "",
                                t.version = e.version || "",
                                t.style = e.style || "",
                                t.dialog = null,
                                t.reset(),
                                t.listen()
                        },
                        reset: function() {
                            var e = this;
                            e.currentRequest = null,
                                e.queue = [],
                                e.status = 0
                        },
                        listen: function() {
                            var e = this;
                            l.on("event:cleanHijackQueue@sufei",
                                function() {
                                    e.reset()
                                }),
                                l.on("event:showDialog@sufei",
                                    function(t) {
                                        e.dialog ? e.show(t) : s(e.style,
                                            function() {
                                                e.render(t),
                                                    e.show()
                                            })
                                    }),
                                l.on("msg:validateSuccess@sufei",
                                    function(t) {
                                        e.resendRequest(t),
                                            e.hide(),
                                            p.send({
                                                type: "msg:resetCheckCode@sufei",
                                                content: ""
                                            })
                                    })
                        },
                        initMessenger: function(e) {
                            var t = this;
                            p.initListener({
                                currentWin: a,
                                originWin: e,
                                msgTransfer: "fromFrame"
                            }),
                                p.register("msg:validateSuccess@sufei",
                                    function(e) {
                                        l.fire("msg:validateSuccess@sufei", e)
                                    }),
                                p.register("child",
                                    function(e) {
                                        e = r(decodeURIComponent(e));
                                        var n = t.validate(e);
                                        n.pass ? e.queryToken ? l.fire("msg:validateSuccess@sufei", e.queryToken) : t.hide() : l.fire("event:showDialog@sufei", e.url)
                                    })
                        },
                        show: function(e) {
                            var t = this;
                            e && t.frame.src != e && (t.frame.src = e),
                            t.dialog && (t.dialog.style.display = "block"),
                                p.send({
                                    type: "msg:refreshCheckCode@sufei",
                                    content: ""
                                }),
                                l.fire("event:dialogShow@sufei")
                        },
                        hide: function() {
                            l.fire("event:cleanHijackQueue@sufei"),
                            this.dialog && (this.dialog.style.display = "none"),
                                l.fire("event:dialogHide@sufei")
                        },
                        render: function(e) {
                            var t = this,
                                n = u.createElement("div");
                            n.style.display = "none",
                                t.app ? n.className = "sufei-dialog sufei-dialog-" + t.app: n.className = "sufei-dialog",
                                n.innerHTML = ['<div class="sufei-dialog-mask">', i() ? '<iframe frameborder="none" src="javascript:\'\'"></iframe>': "", "</div>", '<div class="sufei-dialog-content">', '<iframe id="sufei-dialog-content" frameborder="none" src="' + e + '"></iframe>', '<div class="sufei-dialog-close" id="sufei-dialog-close">\u5173\u95ed</div>', "</div>"].join(""),
                                u.body.appendChild(n);
                            var r = u.getElementById("sufei-dialog-close");
                            r.addEventListener ? r.addEventListener("click",
                                function() {
                                    t.hide()
                                },
                                !1) : r.attachEvent("onclick",
                                function() {
                                    return t.hide(),
                                        !1
                                }),
                                t.dialog = n,
                                t.frame = u.getElementById("sufei-dialog-content"),
                                t.initMessenger(t.frame.contentWindow),
                            y && t.iosFix(),
                                t.render = function() {}
                        },
                        iosFix: function() {
                            var e = u.body,
                                t = "sufei-ios-fix-fixed",
                                n = 0,
                                i = this.dialog;
                            e.classList.add(t),
                                l.on("event:dialogShow@sufei",
                                    function() {
                                        n = e.scrollTop,
                                            e.scrollTop = 0,
                                            i.style.height = Math.max(window.innerHeight, e.scrollHeight) + "px",
                                            setTimeout(function() {
                                                    e.scrollTop = 0
                                                },
                                                200)
                                    }),
                                l.on("event:dialogHide@sufei",
                                    function() {
                                        e.scrollTop = n
                                    })
                        },
                        validate: function(e) {
                            if ("string" == typeof e) try {
                                e = r(e)
                            } catch(t) {
                                return {
                                    pass: !0
                                }
                            }
                            return ! e || e[w] !== x && e[w + "0"] !== x ? {
                                pass: !0
                            }: {
                                result: e,
                                pass: !1,
                                immediate: e[w] === x
                            }
                        },
                        run: function(e) {
                            var t = this;
                            return t.status > 0 && !e.config.sufei ? void t.queue.push(e.config) : void(e.im ? (t.currentRequest = t.currentRequest || e.config, t.status = 200, l.fire("event:showDialog@sufei", e.url)) : (t.currentRequest = e.config, t.status = 100, t.getValidateURI(e.url)))
                        },
                        setup: h,
                        getValidateURI: h,
                        resendRequest: h,
                        addQueryToken: function(e, t) {
                            return e += /\?/.test(e) ? "&" + t: "?" + t
                        },
                        parseJSON: r
                    })
                },
                    {
                        "./class": 1,
                        "./emit": 3,
                        "./json_parse": 5,
                        "./messenger": 6
                    }]
            },
            {},
            [4])(4)
    });