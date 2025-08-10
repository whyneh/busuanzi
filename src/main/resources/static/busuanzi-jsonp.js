/**
 * Busuanzi 不蒜子统计脚本
 * 用于网站PV/UV统计显示
 */

var bszCaller, bszTag;
// DOM Ready 功能实现
(function() {
    var interval,
        domReadyCallback,
        executeCallbacks,
        isReady = false,
        readyCallbacks = [];

    // 添加ready回调函数
    ready = function(callback) {
        if (isReady || document.readyState === "interactive" || document.readyState === "complete") {
            return callback.call(document);
        } else {
            readyCallbacks.push(function() {
                return callback.call(this);
            });
        }
        return this;
    };

    // 执行所有等待的回调函数
    executeCallbacks = function() {
        for (var i = 0, length = readyCallbacks.length; i < length; i++) {
            readyCallbacks[i].apply(document);
        }
        readyCallbacks = [];
    };

    // DOM准备完成后的处理
    domReadyCallback = function() {
        if (!isReady) {
            isReady = true;
            executeCallbacks.call(window);

            // 移除事件监听器
            if (document.removeEventListener) {
                document.removeEventListener("DOMContentLoaded", domReadyCallback, false);
            } else if (document.attachEvent) {
                document.detachEvent("onreadystatechange", domReadyCallback);
                if (window == window.top) {
                    clearInterval(interval);
                    interval = null;
                }
            }
        }
    };

    // 绑定DOM Ready事件
    if (document.addEventListener) {
        // 现代浏览器
        document.addEventListener("DOMContentLoaded", domReadyCallback, false);
    } else if (document.attachEvent) {
        // IE浏览器兼容
        document.attachEvent("onreadystatechange", function() {
            if (/loaded|complete/.test(document.readyState)) {
                domReadyCallback();
            }
        });

        // IE浏览器特殊处理
        if (window == window.top) {
            interval = setInterval(function() {
                try {
                    if (!isReady) {
                        document.documentElement.doScroll("left");
                    }
                } catch (error) {
                    return;
                }
                domReadyCallback();
            }, 5);
        }
    }
})();

// JSONP 调用器对象
bszCaller = {
    /**
     * 发起JSONP请求获取统计数据
     * @param {string} url - 请求URL
     * @param {function} callback - 回调函数
     */
    fetch: function(url, callback) {
        // 生成随机回调函数名
        var callbackName = "BusuanziCallback_" + Math.floor(Math.random() * 1099511627776);

        // 在全局作用域中注册回调函数
        window[callbackName] = this.evalCall(callback);

        // 替换URL中的回调函数名
        url = url.replace("=BusuanziCallback", "=" + callbackName);

        // 创建script标签进行JSONP请求
        var scriptTag = document.createElement("SCRIPT");
        scriptTag.type = "text/javascript";
        scriptTag.defer = true;
        scriptTag.src = url;
        scriptTag.referrerPolicy = "no-referrer-when-downgrade";

        // 将script标签添加到head中
        document.getElementsByTagName("HEAD")[0].appendChild(scriptTag);
    },

    /**
     * 包装回调函数，添加错误处理
     * @param {function} callback - 原始回调函数
     * @returns {function} 包装后的回调函数
     */
    evalCall: function(callback) {
        return function(data) {
            ready(function() {
                try {
                    // 执行回调并清理script标签
                    callback(data);
                    if (scriptTag && scriptTag.parentElement) {
                        scriptTag.parentElement.removeChild(scriptTag);
                    }
                } catch (error) {
                    // 出错时隐藏统计显示
                    bszTag.hides();
                }
            });
        };
    }
};

// 发起统计数据请求
bszCaller.fetch("//busuanzi.ibruce.info/busuanzi?jsonpCallback=BusuanziCallback", function(data) {
    // 更新页面统计数据并显示
    bszTag.texts(data);
    bszTag.shows();
});

// 页面标签操作对象
bszTag = {
    // 支持的统计类型
    bszs: ["site_pv", "page_pv", "site_uv"],

    /**
     * 更新页面中的统计数字
     * @param {object} data - 包含统计数据的对象
     */
    texts: function(data) {
        this.bszs.map(function(type) {
            var element = document.getElementById("busuanzi_value_" + type);
            if (element) {
                element.innerHTML = data[type];
            }
        });
    },

    /**
     * 隐藏所有统计容器
     */
    hides: function() {
        this.bszs.map(function(type) {
            var container = document.getElementById("busuanzi_container_" + type);
            if (container) {
                container.style.display = "none";
            }
        });
    },

    /**
     * 显示所有统计容器
     */
    shows: function() {
        this.bszs.map(function(type) {
            var container = document.getElementById("busuanzi_container_" + type);
            if (container) {
                container.style.display = "inline";
            }
        });
    }
};