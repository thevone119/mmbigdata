!
    function e(t, n, r) {
        function o(i, u) {
            if (!n[i]) {
                if (!t[i]) {
                    var c = "function" == typeof require && require;
                    if (!u && c) return c(i, !0);
                    if (a) return a(i, !0);
                    throw new Error("Cannot find module '" + i + "'")
                }
                var s = n[i] = {
                    exports: {}
                };
                t[i][0].call(s.exports,
                    function(e) {
                        var n = t[i][1][e];
                        return o(n ? n: e)
                    },
                    s, s.exports, e, t, n, r)
            }
            return n[i].exports
        }
        for (var a = "function" == typeof require && require,
                 i = 0; i < r.length; i++) o(r[i]);
        return o
    } ({
            1 : [function(e, t, n) {
                function r() {
                    var e = c.UA_Opt || l;
                    return e.LogVal ? c[e.LogVal] || "": ""
                }
                var o = e("./src/Promise"),
                    a = e("./src/config"),
                    i = e("./src/util"),
                    u = e("./src/domready"),
                    c = window,
                    s = c.ctl,
                    l = c.UA_Opt = c.UA_Opt || {},
                    f = c.ctl = {
                        noConflict: function() {
                            return c.ctl === f && (c.ctl = s),
                                f
                        },
                        config: function(e, t) {
                            return c.UA_Opt && c.UA_Opt.LogVal ? void 0 : (l = c.UA_Opt = c.UA_Opt || {},
                            t || (t = e, e = "def"), e = (e + "").toLowerCase(), t = i.extend(l, a.map[e] || a.map.def, t || {}), l.LogVal && (c[l.LogVal] = ""), t)
                        },
                        getUA: function() {
                            l = c.UA_Opt = c.UA_Opt || {};
                            var e = l.Token;
                            l.Token = (new Date).getTime() + ":" + Math.random();
                            var t = r();
                            return l.Token = e,
                            l.reload && l.reload(),
                                t
                        },
                        getUmidToken: function() {
                            try {
                                return umx.getToken()
                            } catch(e) {
                                return ""
                            }
                        },
                        getUmidInstance: function() {
                            return y
                        },
                        ready: function(e, t) {
                            return g.promise.then(e, t),
                                g.promise
                        }
                    };
                f.config();
                var d = null,
                    m = o.defer(),
                    h = o.defer(),
                    v = m.promise,
                    p = h.promise; !
                    function(e, t) {
                        function n(t, n) {
                            var r = "AWSC_SPECIFY_" + t.toUpperCase() + "_ADDRESSES";
                            if (e[r]) return e[r];
                            var o = "";
                            for (var a in p) if (p.hasOwnProperty(a) && a === t) {
                                var i = p[a];
                                o = Math.ceil(Math.random() * h) <= i.ratio ? i.grey: i.stable;
                                for (var u = 0; u < o.length; u++) o[u] = (n ? "//" + n + "/": v) + o[u];
                                return o
                            }
                        }
                        function r(e) {
                            for (var t = void 0,
                                     n = 0; n < g.length; n++) {
                                for (var r = g[n], o = 0; o < r.features.length; o++) if (r.features[o] === e) {
                                    t = r;
                                    break
                                }
                                if (t) break
                            }
                            return t
                        }
                        function o(e) {
                            for (var t = 0; t < y.length; t++) {
                                var n = y[t];
                                if (n.name === e) return n
                            }
                        }
                        function a(e) {
                            for (var t = void 0,
                                     n = 0; n < g.length; n++) {
                                var r = g[n];
                                if (r.name === e) {
                                    t = r;
                                    break
                                }
                                if (t) break
                            }
                            return t
                        }
                        function i(e) {
                            return /http/.test(location.protocol) || (e = "https:" + e),
                                e
                        }
                        function u(e, n, r) {
                            if (r) for (var o = 0; o < e.length; o++) {
                                var a = e[o];
                                a = i(a),
                                    t.write("<script src=" + a + "></script>")
                            } else for (var o = 0; o < e.length; o++) {
                                var a = e[o];
                                a = i(a);
                                var u = t.createElement("script");
                                u.async = !1,
                                    u.src = a,
                                    u.id = n;
                                var c = t.getElementsByTagName("script")[0];
                                c && c.parentNode ? c.parentNode.insertBefore(u, c) : (c = t.body || t.head, c && c.appendChild(u))
                            }
                        }
                        function c(t, n, r) {
                            var o = "//acjs.aliyun.com/error?v=" + t + "&e=" + n + "&stack=" + r;
                            o = i(o);
                            var a = new Image,
                                u = "_awsc_img_" + Math.floor(1e6 * Math.random());
                            e[u] = a,
                                a.onload = a.onerror = function() {
                                    try {
                                        delete e[u]
                                    } catch(t) {
                                        e[u] = null
                                    }
                                },
                                a.src = o
                        }
                        function s(e) {
                            Math.random() < .001 && c("awsc_state", "feature=" + e.name + ",state=" + e.state + ",href=" + encodeURIComponent(location.href));
                            for (var t = void 0; t = e.callbacks.shift();) t(e.state, e["export"])
                        }
                        function l(e, t, o) {
                            var a = r(e);
                            if (!a) return void(t && t(C));
                            var i = o && o.cdn,
                                c = o && o.sync,
                                l = o && o.timeout || 5e3;
                            if (0 != a.depends.length) for (var d = 0; d < a.depends.length; d++) {
                                var m = a.depends[d];
                                o && (delete o.sync, delete o.timeout, delete o.cdn),
                                    f(m, void 0, o)
                            }
                            var h = {};
                            if (h.module = a, h.name = e, h.state = _, h.callbacks = [], t && h.callbacks.push(t), h.timeoutTimer = setTimeout(function() {
                                        h.state = b,
                                            s(h)
                                    },
                                    l), y.push(h), !a.moduleLoadStatus) {
                                var v = a.sync;
                                c && (v = c);
                                var p = n(a.name, i);
                                u(p, "AWSC_" + a.name, v)
                            }
                            a.moduleLoadStatus = _
                        }
                        function f(e, t, n) {
                            try {
                                var r = o(e);
                                if (r) return void(r.state === j || r.state === b ? t && t(r.state, r["export"]) : r.state === _ ? t && r.callbacks.push(t) : void 0);
                                l(e, t, n)
                            } catch(a) {
                                c("awsc_error", encodeURIComponent(a.message), a.stack)
                            }
                        }
                        function d(e, t, n) {
                            try {
                                var r = a(e);
                                r.moduleLoadStatus = j;
                                for (var o = void 0,
                                         i = 0; i < y.length; i++) {
                                    var u = y[i];
                                    u.module === r && u.name === t && (o = u, clearTimeout(o.timeoutTimer), delete o.timeoutTimer, o["export"] = n, u.state === _ || u.state === b ? (o.state = j, s(o)) : void 0)
                                }
                                o || (o = {},
                                    o.module = r, o.name = t, o.state = j, o["export"] = n, o.callbacks = [], y.push(o))
                            } catch(l) {
                                c("awsc_error", encodeURIComponent(l.message), l.stack)
                            }
                        }
                        function m() {
                            e.AWSC || (e.AWSC = {},
                                e.AWSC.use = f, e.AWSCInner = {},
                                e.AWSCInner.register = d)
                        }
                        var h = 1e4,
                            v = "//aeu.alicdn.com/",
                            p = {
                                uabModule: {
                                    stable: ["js/cj/106.js"],
                                    grey: ["js/cj/107.js"],
                                    ratio: 1e4
                                },
                                umidPCModule: {
                                    stable: ["security/umscript/3.3.25/um.js"],
                                    grey: ["security/umscript/3.3.28/um.js"],
                                    ratio: 1e4
                                },
                                umidH5Module: {
                                    stable: ["security/umscript/3.3.25/um-m.js"],
                                    grey: ["security/umscript/3.3.28/um-m.js"],
                                    ratio: 1e4
                                },
                                ncPCModule: {
                                    stable: ["js/nc/60.js"],
                                    grey: ["js/nc/60.js"],
                                    ratio: 1e4
                                },
                                ncH5Module: {
                                    stable: ["js/nc/60.js"],
                                    grey: ["js/nc/60.js"],
                                    ratio: 1e4
                                }
                            },
                            g = [{
                                name: "umidPCModule",
                                features: ["umpc"],
                                depends: [],
                                sync: !1
                            },
                                {
                                    name: "umidH5Module",
                                    features: ["umh5"],
                                    depends: [],
                                    sync: !1
                                },
                                {
                                    name: "uabModule",
                                    features: ["uab"],
                                    depends: [],
                                    sync: !1
                                },
                                {
                                    name: "ncPCModule",
                                    features: ["ncPC", "scPC"],
                                    depends: ["uab", "umpc"],
                                    sync: !0
                                },
                                {
                                    name: "ncH5Module",
                                    features: ["ncH5", "scH5"],
                                    depends: ["uab", "umh5"],
                                    sync: !0
                                }],
                            y = [],
                            _ = "loading",
                            j = "loaded",
                            b = "timeout",
                            C = "no such feature";
                        m()
                    } (window, document),
                    c.AWSC.use("uab",
                        function(e, t) {
                            "loaded" === e && (d = t, m.resolve())
                        },
                        {
                            cdn: a.url.uac
                        }),
                    c.AWSC.use("umpc",
                        function(e, t) {
                            "loaded" === e && h.resolve()
                        },
                        {
                            cdn: a.url.umid
                        });
                var g = o.defer(),
                    y = null;
                p.then(function() {
                    var e = document.getElementById("_umfp");
                    y = umx.init({
                        serviceLocation: a.env,
                        containers: {
                            flash: e,
                            dcp: e
                        }
                    })
                }),
                    u(function() {
                        function e() {
                            d && g.resolve()
                        }
                        o.all([v, p]).then(function() {
                                e();
                                var t = setInterval(e, 50);
                                g.promise.always(function() {
                                    clearInterval(t)
                                })
                            },
                            function() {
                                g.reject()
                            }),
                            setTimeout(function() {
                                    c.umx ? d || g.reject() : g.reject()
                                },
                                3e3)
                    })
            },
                {
                    "./src/Promise": 2,
                    "./src/config": 3,
                    "./src/domready": 4,
                    "./src/util": 5
                }],
            2 : [function(e, t, n) {
                function r(e) {
                    return this instanceof r ? (this._state = l, this._onFulfilled = [], this._onRejected = [], this._value = null, this._reason = null, void(m(e) && e(u(this.resolve, this), u(this.reject, this)))) : new r(e)
                }
                function o(e, t) {
                    if (e === t) return void e.reject(new TypeError("A promise cannot be resolved with itself."));
                    if (i(t)) try {
                        t.then(function(t) {
                                o(e, t)
                            },
                            function(t) {
                                e.reject(t)
                            })
                    } catch(n) {
                        e.reject(n)
                    } else e.resolve(t)
                }
                function a(e, t, n) {
                    return function(r) {
                        if (m(t)) try {
                            var a = t(r);
                            o(e, a)
                        } catch(i) {
                            e.reject(i)
                        } else e[n](r)
                    }
                }
                function i(e) {
                    return e && m(e.then)
                }
                function u(e, t) {
                    var n = [].slice,
                        r = n.call(arguments, 2),
                        o = function() {},
                        a = function() {
                            return e.apply(this instanceof o ? this: t, r.concat(n.call(arguments)))
                        };
                    return o.prototype = e.prototype,
                        a.prototype = new o,
                        a
                }
                function c(e) {
                    return function(t) {
                        return {}.toString.call(t) == "[object " + e + "]"
                    }
                }
                function s(e, t) {
                    for (var n = 0,
                             r = e.length; r > n; n++) t(e[n], n)
                }
                var l = 0,
                    f = 1,
                    d = 2;
                r.prototype = {
                    constructor: r,
                    then: function(e, t) {
                        var n = new r;
                        return this._onFulfilled.push(a(n, e, "resolve")),
                            this._onRejected.push(a(n, t, "reject")),
                            this.flush(),
                            n
                    },
                    flush: function() {
                        var e = this._state;
                        if (e !== l) {
                            var t = e === f ? this._onFulfilled.slice() : this._onRejected.slice(),
                                n = e === f ? this._value: this._reason;
                            setTimeout(function() {
                                    s(t,
                                        function(e) {
                                            try {
                                                e(n)
                                            } catch(t) {}
                                        })
                                },
                                0),
                                this._onFulfilled = [],
                                this._onRejected = []
                        }
                    },
                    resolve: function(e) {
                        this._state === l && (this._state = f, this._value = e, this.flush())
                    },
                    reject: function(e) {
                        this._state === l && (this._state = d, this._reason = e, this.flush())
                    },
                    always: function(e) {
                        return this.then(e, e)
                    }
                },
                    r.defer = function() {
                        var e = {};
                        return e.promise = new r(function(t, n) {
                            e.resolve = t,
                                e.reject = n
                        }),
                            e
                    },
                    r.all = function(e) {
                        var t = r.defer(),
                            n = e.length,
                            o = [];
                        return s(e,
                            function(e, r) {
                                e.then(function(e) {
                                        o[r] = e,
                                            n--,
                                        0 === n && t.resolve(o)
                                    },
                                    function(e) {
                                        t.reject(e)
                                    })
                            }),
                            t.promise
                    };
                var m = c("Function");
                t.exports = r
            },
                {}],
            3 : [function(e, t, n) {
                function r() {
                    for (var e, t = document.getElementsByTagName("script"), n = /ctlv?\.js/, r = 0; r < t.length; r++) if (e = o(t[r]), n.test(e)) return t[r];
                    return t[t.length - 1]
                }
                function o(e) {
                    return e.hasAttribute ? e.src: e.getAttribute("src", 4)
                }
                var a = e("./util"),
                    i = {
                        SendMethod: 8,
                        LogVal: "collinaua",
                        Token: (new Date).getTime() + ":" + Math.random(),
                        MaxMCLog: 10,
                        MaxKSLog: 10,
                        MaxMPLog: 10,
                        MaxTCLog: 10,
                        MaxFocusLog: 1,
                        Sync: !0
                    },
                    u = ".alicdn.com",
                    c = "aeis",
                    s = "aeu",
                    l = {
                        cn: {
                            umid: "g" + u,
                            uac: "af" + u
                        },
                        us: {
                            umid: c + u,
                            uac: s + u
                        },
                        aliapp: {
                            umid: c + u,
                            uac: s + u
                        },
                        usaliapp: {
                            umid: c + u,
                            uac: s + u
                        },
                        "in": {
                            umid: c + u,
                            uac: s + u
                        },
                        sg: {
                            umid: c + u,
                            uac: s + u
                        },
                        lazada: {
                            umid: c + u,
                            uac: s + u
                        }
                    },
                    f = r().getAttribute("data-env") || "cn";
                t.exports = {
                    env: f,
                    url: l[f],
                    map: {
                        def: a.extend({
                                Flag: 1670350
                            },
                            i),
                        pc: a.extend({
                                Flag: 97422
                            },
                            i),
                        h5: a.extend({
                                Flag: 1670350
                            },
                            i)
                    }
                }
            },
                {
                    "./util": 5
                }],
            4 : [function(e, t, n) {
                t.exports = function(e) {
                    function t(e) {
                        for (m = 1; e = r.shift();) e()
                    }
                    var n, r = [],
                        o = !1,
                        a = document,
                        i = a.documentElement,
                        u = i.doScroll,
                        c = "DOMContentLoaded",
                        s = "addEventListener",
                        l = "onreadystatechange",
                        f = "readyState",
                        d = u ? /^loaded|^c/: /^loaded|c/,
                        m = d.test(a[f]);
                    return a[s] && a[s](c, n = function() {
                            a.removeEventListener(c, n, o),
                                t()
                        },
                        o),
                    u && a.attachEvent(l, n = function() { / ^c / .test(a[f]) && (a.detachEvent(l, n), t())
                    }),
                        e = u ?
                            function(t) {
                                self != top ? m ? t() : r.push(t) : function() {
                                    try {
                                        i.doScroll("left")
                                    } catch(n) {
                                        return setTimeout(function() {
                                                e(t)
                                            },
                                            50)
                                    }
                                    t()
                                } ()
                            }: function(e) {
                                m ? e() : r.push(e)
                            }
                } ()
            },
                {}],
            5 : [function(e, t, n) {
                t.exports = {
                    extend: function(e) {
                        for (var t, n, r = [].slice.call(arguments), o = r.length, a = 1; o > a; a++) {
                            t = r[a];
                            for (n in t) t.hasOwnProperty(n) && ("Flag" === n && e[n] ? e[n] = e[n] | t[n] : e[n] = t[n])
                        }
                        return e
                    }
                }
            },
                {}]
        },
        {},
        [1]);