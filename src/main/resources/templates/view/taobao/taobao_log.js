!
    function() {
        function e(t, n, r) {
            function o(a, c) {
                if (!n[a]) {
                    if (!t[a]) {
                        var s = "function" == typeof require && require;
                        if (!c && s) return s(a, !0);
                        if (i) return i(a, !0);
                        var u = new Error("Cannot find module '" + a + "'");
                        throw u.code = "MODULE_NOT_FOUND",
                            u
                    }
                    var d = n[a] = {
                        exports: {}
                    };
                    t[a][0].call(d.exports,
                        function(e) {
                            var n = t[a][1][e];
                            return o(n || e)
                        },
                        d, d.exports, e, t, n, r)
                }
                return n[a].exports
            }
            for (var i = "function" == typeof require && require,
                     a = 0; a < r.length; a++) o(r[a]);
            return o
        }
        return e
    } ()({
            1 : [function(e, t, n) {
                t.exports = function(e, t) {
                    for (var n, r = [], o = ["custom", "error", "performance", "retCode", "speed", "log"], i = 0; n = o[i++];) e[n] = function(e) {
                        return function() {
                            r.push({
                                type: e,
                                params: Array.prototype.slice.call(arguments)
                            })
                        }
                    } (n);
                    e.reloaded = function() {
                        e.ready();
                        for (var t = 0,
                                 n = r.length; t < n; t++) e[r[t].type].apply(e, r[t].params)
                    },
                        e.reloadFailed = function() {
                            "function" == typeof t && t()
                        }
                }
            },
                {}],
            2 : [function(e, t, n) {
                t.exports = function(e, t) {
                    var n = e.startTime || e.config.startTime,
                        r = n;
                    if (!n) try {
                        n = window.performance.timing.responseStart || +new Date,
                            r = +new Date
                    } catch(c) {
                        r = n = +new Date
                    }
                    var o = function(t, n) {
                        n = n || e.config.sample,
                        e.sampling(n, "retcode" == t.type) == (e.config.modVal || 1) && (t.sampling = n, e.send(t))
                    };
                    e.custom = function(e, n, r) {
                        var i = {
                            type: "custom"
                        };
                        "time" != (e = ["time", "count"][e] || e) && "count" != e || (i.category = e),
                        i.type && (i.key = n, i.value = "time" == e ? r: t, o(i))
                    };
                    var i = function(e, t) {
                        if (!e) return ! 0;
                        if (!t) return ! 1;
                        try {
                            var n = Object.prototype.toString.call(t).substring(8).replace("]", "");
                            if ("Function" === n) return !! t(e);
                            if ("RegExp" === n) return t.test(e);
                            if ("String" === n) return e.indexOf(t) >= 0
                        } catch(r) {
                            "object" == typeof window && window.console && console.error("retcode log errMsgFitler error", r)
                        }
                        return ! 1
                    };
                    e.error = function(t, n, r, a, s, u) {
                        var d = {
                            type: "jserror"
                        };
                        if (1 === arguments.length && (n = t, t = undefined), n) {
                            if (d.category = t || "sys", "object" == typeof n && n.message) {
                                var f = n;
                                try {
                                    n = f.message,
                                        r = r || f.filename,
                                        a = a || f.lineno,
                                        s = s || f.colno
                                } catch(c) {}
                            } else if ("object" == typeof n) try {
                                n = JSON.stringify(n)
                            } catch(c) {}
                            try {
                                n = n ? n.substring(0, 1e3) : ""
                            } catch(c) {
                                n = ""
                            }
                            if (u = u ? u.substring(0, 1e3) : "", d.msg = n, e.config && e.config.errMsgFilter && i(n, e.config.errMsgFilter)) return;
                            r && (d.file = r),
                            a && (d.line = a),
                            s && (d.col = s),
                            u && (d.stack = u),
                                o(d, 1)
                        }
                    },
                        e.performance = function(t) {
                            var n = {
                                type: "per"
                            };
                            o(e.extend(n, t))
                        },
                        e.retCode = function(t, r, i, a, s) {
                            var u = {
                                type: "retcode",
                                sampling: this.config.retCode[t]
                            };
                            if ("object" == typeof t) {
                                try {
                                    t.msg = t.msg ? t.msg.substring(0, 1e3) : ""
                                } catch(c) {
                                    t.msg = ""
                                }
                                u.sampling = t.api ? this.config.retCode[t.api] : 1,
                                    u.api = t.api,
                                    u.issucess = t.issuccess,
                                    u.delay = "number" == typeof t.delay ? parseInt(t.delay, 10) : new Date - n,
                                    u.msg = t.msg || (t.issuccess ? "success": "fail"),
                                    u.detail = t.detail || "",
                                    u.traceId = t.traceId || "",
                                    delete(u = e.extend(t, u)).issuccess
                            } else {
                                try {
                                    s = s ? s.substring(0, 1e3) : ""
                                } catch(c) {
                                    s = ""
                                }
                                u = e.extend({
                                        api: t,
                                        issucess: r,
                                        delay: "number" == typeof i ? parseInt(i, 10) : new Date - n,
                                        msg: a || (r ? "success": "fail"),
                                        detail: s || ""
                                    },
                                    u)
                            }
                            "undefined" != typeof u.delay && "undefined" != typeof u.issucess && o(u, u.issucess ? u.sampling: 1)
                        };
                    var a = function() {
                        for (var t, n = {
                                type: "speed"
                            },
                                 r = 0, i = e.speed.points.length; r < i; r++)(t = e.speed.points[r]) && (n["s" + r] = t, e.speed.points[r] = null);
                        o(n)
                    };
                    e.speed = function(t, o, i) {
                        var c;
                        "string" == typeof t && (t = parseInt(t.slice(1), 10)),
                        "number" == typeof t && ((c = e.speed.points || new Array(11))[t] = "number" == typeof o ? o > 864e5 ? o - n: o: new Date - n, c[t] < 0 && (c[t] = new Date - r), e.speed.points = c),
                            clearTimeout(e.speed.timer),
                            i ? a() : e.speed.timer = setTimeout(a, 3e3)
                    },
                        e.log = function(e, t) {
                            o({
                                    type: "log",
                                    msg: e
                                },
                                t)
                        }
                }
            },
                {}],
            3 : [function(e, t, n) {
                t.exports = function(e, t, n) {
                    var r = {
                            spm: e.getSpmId()
                        },
                        o = t.onerror; ({
                        init: function() {
                            this.lockPerformanceSpm(),
                                this.sendPerformance(),
                                this.bind(),
                            /wpodebug\=1/.test(location.search) && (e.config.sample = 1, e.config.modVal = 1, e.debug = !0)
                        },
                        bind: function() {
                            e.on(t, "beforeunload",
                                function() {
                                    e.clear(),
                                    e.speed.points && e.speed(null, null, !0)
                                },
                                !0),
                                t.onerror = function(t, n, r, i, a) {
                                    o && o(t, n, r, i, a);
                                    var c = a && a.stack;
                                    n ? e.error("sys", t, n, r, i, c) : e.error(t)
                                }
                        },
                        analyzeTiming: function() {
                            var e = {
                                rrt: ["responseStart", "requestStart"],
                                dns: ["domainLookupEnd", "domainLookupStart"],
                                cnt: ["connectEnd", "connectStart"],
                                ntw: ["responseStart", "fetchStart"],
                                dct: ["domContentLoadedEventStart", "fetchStart"],
                                flt: ["loadEventStart", "fetchStart"]
                            };
                            try {
                                var t = performance.timing;
                                for (var n in e) {
                                    var o = t[e[n][1]],
                                        i = t[e[n][0]];
                                    if (o && i) {
                                        var a = i - o;
                                        a >= 0 && a < 864e5 && (r[n] = a)
                                    }
                                }
                            } catch(c) {}
                            return r
                        },
                        lockPerformanceSpm: function() {
                            if (!r.spm) {
                                var e = function() {
                                        var e = n && n.getSpmId && n.getSpmId();
                                        e && (r.spm = e)
                                    },
                                    t = function() {
                                        "complete" === document.readyState ? e() : document.addEventListener ? (document.removeEventListener("DOMContentLoaded", t, !1), e()) : "complete" === document.readyState && (document.detachEvent("onreadystatechange", t), e())
                                    };
                                document.addEventListener ? document.addEventListener("DOMContentLoaded", t, !1) : document.attachEvent && document.attachEvent("onreadystatechange", t)
                            }
                        },
                        sendPerformance: function() {
                            var n = this;
                            "complete" === document.readyState ? e.performance(n.analyzeTiming()) : e.on(t, "load",
                                function() {
                                    e.performance(n.analyzeTiming())
                                },
                                !0)
                        }
                    }).init()
                }
            },
                {}],
            4 : [function(e, t, n) {
                var r = function() {
                        return + new Date + Math.floor(1e3 * Math.random())
                    },
                    o = "",
                    i = function() {
                        var e = document.getElementsByTagName("meta"),
                            t = [],
                            n = "";
                        if (o) return o;
                        for (var r = 0; r < e.length; r++) {
                            var i = e[r];
                            i && i.name && ("data-spm" == i.name || "spm-id" == i.name) && (n = i.content)
                        }
                        return n && t.push(n),
                        document.body && document.body.getAttribute("data-spm") && t.push(document.body.getAttribute("data-spm")),
                        (t = t.length ? t.join(".") : 0) && -1 !== t.indexOf(".") && (o = t),
                            o
                    };
                i.bind || (i.bind = function() {
                    return i
                }),
                    t.exports = {
                        sendRequest: function(e) {
                            var t = window,
                                n = "jsFeImage_" + r(),
                                o = t[n] = new Image;
                            o.onload = o.onerror = function() {
                                t[n] = null
                            },
                                o.src = e,
                                o = null
                        },
                        getCookie: function() {
                            return document.cookie
                        },
                        getSpmId: i
                    }
            },
                {}],
            5 : [function(e, t, n) {
                t.exports = function(e, t, n) {
                    var r, o, i = {},
                        a = 0,
                        c = {
                            imgUrl: "//retcode.taobao.com/r.png?",
                            sample: 10,
                            modVal: 1,
                            dynamic: !1,
                            retCode: {},
                            delayOfReady: null
                        },
                        s = function(e, t, n) {
                            if ("function" != typeof e) return n;
                            try {
                                return e.apply(this, t)
                            } catch(r) {
                                return n
                            }
                        },
                        u = n.sendRequest,
                        d = function() {
                            var t, n;
                            if (e.asyncQueue && e.asyncQueue.length > 0) for (var i, d = e.asyncQueue; i = d.shift();) {
                                var f = i.method;
                                "function" == typeof e[f] && e[f].apply(e, i.args || [])
                            }
                            for (; (t = p.dequeue()) && ((n = p.extend({
                                    uid: r,
                                    userNick: e.getNick(),
                                    times: t.times ? t.times: 1,
                                    _t: ~new Date + (a++).toString(),
                                    tag: e.config.tag && s(e.config.tag, [], e.config.tag + "") || ""
                                },
                                t)).spm || (n.spm = e.getSpmId()), n.spm);) e.debug && "object" == typeof window && window.console && console.log(n),
                                u.call(e, c.imgUrl + p.query.stringify(n));
                            o = null
                        },
                        f = function(e) {
                            e && o && (clearTimeout(o), d()),
                            o || (o = setTimeout(d, 1e3))
                        },
                        p = {
                            _key: "wpokey",
                            ver: "0.2.26",
                            requestQueue: e.requestQueue || [],
                            getCookie: function(e) {
                                var t, r, o = "";
                                if (!i[e]) {
                                    t = new RegExp(e + "=([^;]+)");
                                    try {
                                        o = n.getCookie(this)
                                    } catch(a) {} (r = t.exec(o)) && (i[e] = r[1])
                                }
                                return i[e]
                            },
                            setCookie: function(e, t, n, r, o) {
                                var i = e + "=" + t;
                                r && (i += "; domain=" + r),
                                o && (i += "; path=" + o),
                                n && (i += "; expires=" + n),
                                    document.cookie = i
                            },
                            extend: function(e) {
                                for (var t, n = Array.prototype.slice.call(arguments, 1), r = 0, o = n.length; r < o; r++) {
                                    t = n[r];
                                    for (var i in t) t.hasOwnProperty(i) && (e[i] = t[i])
                                }
                                return e
                            },
                            guid: function() {
                                return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
                                    function(e) {
                                        var t = 16 * Math.random() | 0;
                                        return ("x" == e ? t: 3 & t | 8).toString(16)
                                    })
                            },
                            send: function(t) {
                                if (t && !e.config.disabled) {
                                    var n = p.getSpmId(); ! t.spm && n && (t.spm = n),
                                        this.queue(t)
                                }
                            },
                            query: {
                                stringify: function(e) {
                                    var t = [],
                                        n = "";
                                    try {
                                        for (var r in e) if (e.hasOwnProperty(r) && e[r] !== undefined) {
                                            var o = "";
                                            o = "spm" == r ? encodeURI(e[r]) : encodeURIComponent(e[r]),
                                                t.push(r + "=" + o)
                                        }
                                        t.length > 0 && (n = t.join("&"))
                                    } catch(i) {}
                                    return n
                                },
                                parse: function(e) {
                                    var t, n = e.split("&"),
                                        r = {};
                                    try {
                                        for (var o = 0,
                                                 i = n.length; o < i; o++) r[(t = n[o].split("="))[0]] = decodeURIComponent(t[1])
                                    } catch(a) {}
                                    return r
                                }
                            },
                            getSpmId: function() {
                                return c.spmId ? c.spmId: "function" == typeof n.getSpmId ? n.getSpmId.call(this) : 0
                            },
                            on: function(e, t, n, r) {
                                e.addEventListener ? e.addEventListener(t, r ?
                                    function() {
                                        e.removeEventListener(t, n, !1),
                                            n()
                                    }: n, !1) : e.attachEvent && e.attachEvent("on" + t,
                                        function() {
                                            r && e.detachEvent("on" + t, arguments.callee),
                                                n()
                                        })
                            },
                            getNick: function() {
                                var t = "";
                                try {
                                    e.config.nick ? t = e.config.nick: (t = this.getCookie("_nk_") || this.getCookie("lgc") || this.getCookie("_w_tb_nick") || this.getCookie("sn") || "", t = decodeURIComponent(t))
                                } catch(n) {}
                                return t
                            },
                            setConfig: function(e) {
                                if (e && "object" != typeof e) throw "args of wpo.setConfig is not object";
                                return e && e.user && "function" == typeof e.user.getUserInfo && "weex" === this.env && e.user.getUserInfo(function(e) {
                                    try {
                                        var t = JSON.parse(e);
                                        p.extend(c, {
                                            nick: t.info && t.info.nick
                                        })
                                    } catch(n) {}
                                }),
                                    p.extend(c, e)
                            },
                            spm: function(e) {
                                return e && this.setConfig({
                                    spmId: e
                                }),
                                    this
                            },
                            dynamicConfig: function(e) {
                                var t = this.query.stringify(e);
                                try {
                                    localStorage.setItem(this._key, t)
                                } catch(n) {
                                    this.setCookie(this._key, t, new Date(e.expTime))
                                }
                                this.setConfig({
                                    sample: parseInt(e.sample, 10)
                                }),
                                    this.ready()
                            },
                            ready: function() {
                                var t = function() {
                                    e._ready = !0,
                                        f()
                                };
                                e.config.delayOfReady ? setTimeout(function() {
                                        t()
                                    },
                                    e.config.delayOfReady) : t()
                            },
                            queue: function(e) {
                                var t, n = this.requestQueue;
                                if ("jserror" === e.type) {
                                    if (n.length && (t = n[n.length - 1], e.msg === t.msg)) return void t.times++;
                                    e.times || (e.times = 1)
                                }
                                n.push(e),
                                this._ready && ("jserror" === e.type ? f(!1) : d())
                            },
                            dequeue: function() {
                                return this.requestQueue.shift()
                            },
                            clear: function() {
                                f(!0)
                            }
                        };
                    return p.uid = r = p.guid(),
                        p.config = p.setConfig(e.config),
                        p.extend(e, p),
                        t.__WPO = e,
                        e
                }
            },
                {}],
            6 : [function(e, t, n) { !
                function(t) {
                    var n = e("./conf-browser"),
                        r = t.__WPO || {},
                        o = 2;
                    if (!r.__hasInitBlSdk) {
                        r.env = "browser",
                            e("./core")(r, t, n);
                        var i = function() {
                            e("./sampling")(r),
                                e("./apis")(r),
                                e("./browser-performance")(r, t, n),
                                r.__hasInitBlSdk = !0
                        }; ! r.config.dynamic || (o = e("./server-config")(r)) ? (2 == o && ("complete" === document.readyState ? r.ready() : r.on(t, "load",
                            function() {
                                r.ready()
                            },
                            !0)), i()) : e("./api-await")(r,
                            function() {
                                i(),
                                r.reloaded && r.reloaded()
                            })
                    }
                } (window),
                t.exports = window.__WPO
            },
                {
                    "./api-await": 1,
                    "./apis": 2,
                    "./browser-performance": 3,
                    "./conf-browser": 4,
                    "./core": 5,
                    "./sampling": 7,
                    "./server-config": 8
                }],
            7 : [function(e, t, n) {
                t.exports = function(e) {
                    var t = {};
                    e.sampling = function(e, n) {
                        return 1 == e ? 1 : n ? Math.floor(Math.random() * e) : "number" == typeof t[e] ? t[e] : (t[e] = Math.floor(Math.random() * e), t[e])
                    }
                }
            },
                {}],
            8 : [function(e, t, n) {
                t.exports = function(e) {
                    var t, n, r, o, i, a = e._key,
                        c = function(e) {
                            var t = document.createElement("script");
                            return t.src = e,
                                document.getElementsByTagName("script")[0].parentNode.appendChild(t),
                                t
                        },
                        s = function() {
                            i || (i = !0, u())
                        },
                        u = function() {
                            var t = "//retcode.alicdn.com/retcode/pro/config/" + e.getSpmId() + ".js",
                                n = c(t);
                            n.onerror = function() {
                                n.onerror = null,
                                    e.error("sys", "dynamic config error", t, 0),
                                    e.ready()
                            }
                        },
                        d = function() {
                            "complete" === document.readyState ? s() : document.addEventListener ? document.addEventListener("DOMContentLoaded",
                                function() {
                                    document.removeEventListener("DOMContentLoaded", arguments.callee, !1),
                                        s()
                                },
                                !1) : document.attachEvent && (document.attachEvent("onreadystatechange",
                                    function() {
                                        "complete" === document.readyState && (document.detachEvent("onreadystatechange", arguments.callee), s())
                                    }), document.documentElement.doScroll && "undefined" == typeof window.frameElement &&
                                function() {
                                    try {
                                        document.documentElement.doScroll("left")
                                    } catch(e) {
                                        return void setTimeout(arguments.callee, 0)
                                    }
                                    s()
                                } (), e.on(window, "load",
                                    function() {
                                        s()
                                    },
                                    !0))
                        };
                    if (!a) return 2;
                    try {
                        t = localStorage.getItem(a)
                    } catch(f) {
                        t = e.getCookie(a)
                    }
                    return t ? (n = e.query.parse(t), selfUpdate = function() {
                        var t = e.ver && e.ver.split("."),
                            r = n.ver && n.ver.split(".");
                        if (!t || !r) return ! 1;
                        for (var o = 0,
                                 i = t.length; o < i; o++) if (r[o] && parseInt(t[o], 10) < parseInt(r[o], 10)) return ! 0;
                        return ! 1
                    },
                        selfUpdate() ? (o = "//g.alicdn.com/retcode/log/" + n.ver + "/log.js", r = c(o), r.onload = function() {
                            r.onload = null,
                                e.reloaded()
                        },
                            r.onerror = function() {
                                r.onerror = null,
                                    e.error("sys", "self update error", o, 0),
                                    e.reloadFailed()
                            },
                            0) : parseInt(n.exp, 10) < (new Date).getTime() ? (d(), 1) : (e.setConfig({
                            sample: parseInt(n.sample, 10)
                        }), 2)) : (d(), 1)
                }
            },
                {}]
        },
        {},
        [6]);