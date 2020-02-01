<span class="title">jquery ajax error 函数和及其参数详细说明</span>

用 jquery 的 *`ajax()`* 方法向服务器发送请求的时候，常常需要使用到 *`error()`* 函数进行错误信息的处理。

一般 *`error()`* 函数返回的参数有三个： *`function(jqXHR jqXHR, String textStatus, String errorThrown)`*。常见调用代码如下：

```js
$.ajax({
    url: '/Home/AjaxGetData',            
    success: function (data) {
        alert(data);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        /* 错误信息处理 */
    }
});
```

这里对这三个参数做详细说明。

# 第一个参数 jqXHR 

这里的 jqXHR 是一个 jqXHR 对象，在 Jquery 1.4 和 1.4 版本之前返回的是 XMLHttpRequest 对象，1.5 版本以后则开始使用 jqXHR 对象，该对象是一个超集，就是该对象不仅包括 XMLHttpRequest 对象，还包含其他更多的详细属性和信息。

这里主要有 4 个属性：

| 属性 | 说明 |
| :- | :- |
| readyState | 当前状态,0：未初始化，1：正在载入，2：已经载入，3：数据进行交互，4：完成。|
| status  | 返回的HTTP状态码，比如常见的404,500等错误代码。|
| statusText | 对应状态码的错误信息，比如404错误信息是not found,500是Internal Server Error。 |
| responseText | 服务器响应返回的文本信息 |


# 第二个参数 String textStatus：

返回的是字符串类型，表示返回的状态，根据服务器不同的错误可能返回下面这些信息："timeout"（超时）, "error"（错误）, "abort"(中止), "parsererror"（解析错误），还有可能返回空值。


# 第三个参数 String errorThrown：

也是字符串类型，表示服务器抛出返回的错误信息，如果产生的是HTTP错误，那么返回的信息就是HTTP状态码对应的错误信息，比如404的Not Found,500错误的Internal Server Error。

```js
$.ajax({
    url: '/AJAX请求的URL',            
    success: function (data) {
        alert(data);
    },
    error: function (jqXHR, textStatus, errorThrown) {
        /* 弹出 jqXHR 对象的信息 */
        alert(jqXHR.responseText);
        alert(jqXHR.status);
        alert(jqXHR.readyState);
        alert(jqXHR.statusText);
        /* 弹出其他两个参数的信息 */
        alert(textStatus);
        alert(errorThrown);
    }
});
```

『完』