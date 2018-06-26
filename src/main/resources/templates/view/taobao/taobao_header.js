(function() {
    var c = KISSY,
        d = c.DOM,
        b = c.Event;
    function a(K, J) {
        var k = c.get("#" + K),
            y = k && k.q,
            h = k && k.search_type,
            s = k && k.getElementsByTagName("label")[0],
            r = k && k.cat,
            G = c.get("#J_TSearchTabs").getElementsByTagName("li"),
            f = G.length,
            D = {},
            w = false,
            t = false,
            j = "tsearch-tabs-active",
            n = "tsearch-tabs-hover",
            B = function(q) {
                for (var i = 0; i < f; i++) {
                    d[i === q ? "addClass": "removeClass"](G[i], j)
                }
            },
            F = c.get("#J_TSearchCat"),
            I = null,
            g = c.get("#J_TSearchCatHd"),
            m = F && F.getElementsByTagName("div")[0],
            p = m && m.getElementsByTagName("a") || [],
            v = p.length,
            E,
            u = function(i) {
                for (E = 0; E < v; E++) {
                    if (p[E].getAttribute("data-value") === i) {
                        return p[E]
                    }
                }
                return null
            },
            A = function() {
                d.removeClass(F, "tsearch-cat-active")
            },
            H = function() {
                d.addClass(F, "tsearch-cat-active")
            },
            z = function(i) {
                for (E = 0; E < v; E++) {
                    d[p[E] === i ? "addClass": "removeClass"](p[E], "tsearch-cat-selected")
                }
                A();
                g.innerHTML = i.innerHTML;
                r.value = i.getAttribute("data-value")
            },
            x = function() {
                y.focus();
                if (c.UA.ie) {
                    y.value = y.value
                }
            };
        if (!k) {
            return
        }
        if (c.get("#J_TSearchTabs")) {
            var C = 0,
                o = {
                    "\u5b9d\u8d1d": ["item", "\u8f93\u5165\u60a8\u60f3\u8981\u7684\u5b9d\u8d1d"],
                    "\u6dd8\u5b9d\u5546\u57ce": ["mall", "\u8f93\u5165\u60a8\u60f3\u8981\u7684\u5546\u54c1"],
                    "\u5e97\u94fa": ["shop", "\u8f93\u5165\u60a8\u60f3\u8981\u7684\u5e97\u94fa\u540d\u6216\u638c\u67dc\u540d"],
                    "\u62cd\u5356": ["auction", "\u8f93\u5165\u60a8\u60f3\u8981\u7684\u5b9d\u8d1d"],
                    "\u6dd8\u5427": ["taoba", "\u8f93\u5165\u60a8\u60f3\u8981\u641c\u7d22\u7684\u5185\u5bb9"],
                    "\u5b9d\u8d1d\u5206\u4eab": ["share", "\u8f93\u5165\u60a8\u60f3\u8981\u7684\u5b9d\u8d1d"]
                },
                l = {
                    "\u5bf6\u8c9d": ["item", "\u8f38\u5165\u60a8\u60f3\u8981\u7684\u5bf6\u8c9d"],
                    "\u6dd8\u5bf6\u5546\u57ce": ["mall", "\u8f38\u5165\u60a8\u60f3\u8981\u7684\u5546\u54c1"],
                    "\u5e97\u92ea": ["shop", "\u8f38\u5165\u60a8\u60f3\u8981\u7684\u5e97\u8216\u540d\u6216\u638c\u6ac3\u540d"],
                    "\u62cd\u8ce3": ["auction", "\u8f38\u5165\u60a8\u60f3\u8981\u7684\u5bf6\u8c9d"],
                    "\u6dd8\u5427": ["taoba", "\u8f38\u5165\u60a8\u60f3\u8981\u641c\u7d22\u7684\u5167\u5bb9"],
                    "\u5bf6\u8c9d\u5206\u4eab": ["share", "\u8f38\u5165\u60a8\u60f3\u8981\u7684\u5bf6\u8c9d"]
                };
            for (; C < f; C++) { (function() {
                var L = C,
                    i = c.trim(G[L].getElementsByTagName("a")[0].innerHTML),
                    q = o[i] || l[i];
                if (!q) {
                    return
                }
                D[q[0]] = {
                    index: L,
                    hint: q[1]
                };
                b.on(G[L], "click",
                    function(M) {
                        M.halt();
                        B(L);
                        d.removeClass(G[L], n);
                        h.value = q[0];
                        s.innerHTML = q[1];
                        x()
                    });
                b.on(G[L], "mouseenter",
                    function(M) {
                        M.halt();
                        if (!d.hasClass(G[L], j)) {
                            d.addClass(G[L], n)
                        }
                    });
                b.on(G[L], "mouseleave",
                    function(M) {
                        M.halt();
                        if (!d.hasClass(G[L], j)) {
                            d.removeClass(G[L], n)
                        }
                    })
            })()
            }
        }
        b.on(y, "focus",
            function() {
                s.innerHTML = "";
                d.addClass(s, "hidden")
            });
        b.on(y, "blur",
            function() {
                if (c.trim(y.value) === "" && !w) {
                    s.innerHTML = D[h.value]["hint"];
                    d.removeClass(s, "hidden")
                }
            });
        b.on("#J_TSearchTabs", "mousedown",
            function() {
                w = true;
                t = true;
                setTimeout(function() {
                    w = false
                })
            });
        b.on("#J_TSearchCat", "click",
            function(i) {
                i.halt();
                var q = i.target;
                switch (true) {
                    case d.hasClass(q.parentNode, "tsearch-cat-hd") : case d.hasClass(q, "tsearch-cat-hd") : H();
                    break;
                    case q.parentNode.nodeName.toLowerCase() === "div": z(q);
                        x();
                        break
                }
            });
        b.on(document, "click", A);
        b.on(k, "submit",
            function() {
                switch (k.search_type.value) {
                    case "item":
                        if (y.value === "") {
                            k.action = "//list.taobao.com/browse/cat-0.htm"
                        }
                        break;
                    case "mall":
                        k.action = "//list.tmall.com/search_product.htm";
                        break;
                    case "shop":
                        k.action = "//shopsearch.taobao.com/browse/shop_search.htm";
                        break;
                    case "auction":
                        k.atype.value = "a";
                        break;
                    case "taoba":
                        k.action = "//ba.taobao.com/index.htm";
                        break;
                    case "share":
                        k.tracelog.value = "msearch2fx";
                        if (y.value === "") {
                            k.action = "//jianghu.taobao.com/square.htm"
                        } else {
                            k.keyword.value = y.value;
                            k.action = "//fx.taobao.com/view/share_search.htm"
                        }
                        break
                }
            });
        s.innerHTML = "";
        setTimeout(function() {
            if (!t) {
                h.value = (J && J.searchType) ? J.searchType: "item";
                if (document.domain.indexOf("shopsearch.taobao.com") > -1) {
                    h.value = "shop"
                }
                var i = D[h.value];
                s.innerHTML = i.hint;
                B(i.index)
            }
            if (F && (I = u(r.value))) {
                z(I)
            }
            if (c.trim(y.value) !== "") {
                s.innerHTML = ""
            }
            if (J && J.autoFocus) {
                x()
            }
            k.atype.value = "";
            if (k.keyword) {
                k.keyword.value = ""
            }
            if (k.tracelog) {
                k.tracelog.value = ""
            }
        })
    }
    function e(o) {
        var h = c.get("#" + o);
        if (!h) {
            return
        }
        var f = h.q;
        if (!f) {
            return
        }
        if (! (window.TB && TB.Suggest || c.Suggest)) {
            return
        }
        var i = c.Suggest ? c: TB,
            n = new i.Suggest(f, "//suggest.taobao.com/sug", {
                resultFormat: "\u7ea6%result%\u4e2a\u5b9d\u8d1d"
            });
        var g = h.ssid;
        if (g) {
            setTimeout(function() {
                    g.value = "s5-e"
                },
                0);
            g.setAttribute("autocomplete", "off");
            var j = function() {
                if (g.value.indexOf("-p1") == -1) {
                    g.value += "-p1"
                }
            };
            try {
                if (n.subscribe) {
                    n.subscribe("onItemSelect", j)
                }
                if (n.on) {
                    n.on("onItemSelect", j)
                }
            } catch(l) {}
        }
        var m = h.elements.search_type;
        var p = function() {
            return m.value
        };
        var k = n._needUpdate;
        n._needUpdate = function() {
            var q = p();
            return (q === "item" || q === "mall") && k.call(n)
        }
    }
    TB.Header = {
        init: function(f) {
            if (c.get("#J_TSearch")) {
                a("J_TSearchForm", f);
                setTimeout(function g() {
                        if (typeof g.count == "undefined") {
                            g.count = 0
                        }
                        g.count++;
                        if (! (window.TB && TB.Suggest || c.Suggest)) {
                            setTimeout(arguments.callee, 200)
                        } else {
                            e("J_TSearchForm")
                        }
                    },
                    200)
            }
        }
    }
})();