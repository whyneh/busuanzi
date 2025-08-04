(function () {
  // 统计项名称列明
  const STATISTICS = ["site_pv", "page_pv", "site_uv", "page_uv"];
  // 定义API地址，请自行修改
  const API_URL = "http://127.0.0.1:10010/api";
  // localStorage中存储身份标识的key
  const LOCALSTORAGE_KEY = "bsz-id";
  // 请求超时时间（毫秒）
  const TIMEOUT = 5000;
  // 防抖延迟时间（毫秒）
  const DEBOUNCE_DELAY = 300;
  // 是否使用PJAX
  const PJAX = document.currentScript.hasAttribute("pjax");

  /**
   * 防抖函数
   */
  function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }

  /**
   * 更新页面元素
   */
  const modify = function (result) {
    STATISTICS.forEach(function (item) {
      // 查找对应统计项的页面元素
      let element = document.getElementById("busuanzi_value_" + item);
      if (element != null) {
        element.innerHTML = result.data[item];
      }
      // 查找统计容器元素，使其显形，去除隐藏
      let container = document.getElementById("busuanzi_container_" + item);
      if (container != null) {
        container.style.display = "inline";
      }
    });
  }

  /**
   * 获取统计数据的函数
   */
  const fetchData = function () {
    let xhr = new XMLHttpRequest();
    xhr.open("GET", API_URL, true);
    xhr.timeout = TIMEOUT;

    // 获取localStorage中的身份标识
    let bszId = localStorage.getItem(LOCALSTORAGE_KEY);
    // 如果存在身份标识，放入到请求头中
    if (bszId != null) {
      xhr.setRequestHeader("Authorization", bszId);
    }
    // 设置来源页面的请求头
    xhr.setRequestHeader("Referer", window.location.href);

    // 设置请求状态改变的回调函数
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          try {
            const response = JSON.parse(xhr.responseText);
            // 如果请求成功
            if (response.success === true) {
              modify(response);
              // 获取响应头中的身份标识
              let bszId = xhr.getResponseHeader("Authorization");
              // 如果响应头中有身份标识，则保存到localStorage
              if (bszId != null && bszId !== "") {
                localStorage.setItem(LOCALSTORAGE_KEY, bszId);
              }
            } else {
              console.warn('API返回错误:', response);
            }
          } catch (error) {
            console.error('JSON解析失败:', error);
          }
        } else {
          console.error('请求失败:', xhr.status, xhr.statusText);
        }
      }
    };

    // 超时处理
    xhr.ontimeout = function() {
      console.error('请求超时');
    };

    // 网络错误处理
    xhr.onerror = function() {
      console.error('网络错误');
    };

    // 发送请求
    xhr.send();
  };

  // 防抖处理后的请求函数
  const debouncedFetch = debounce(fetchData, DEBOUNCE_DELAY);

  // 执行获取数据请求
  fetchData();

  // 如果启用了pjax支持
  if (PJAX) {
    // 重写pushState方法，在页面切换时重新获取统计数据
    let originalPushState = window.history.pushState;
    window.history.pushState = function () {
      originalPushState.apply(this, arguments);
      debouncedFetch();
    };
    // 监听popstate事件，在浏览器前进后退时重新获取统计数据
    window.addEventListener("popstate", function (event) {
      debouncedFetch();
    }, false);
  }

})();