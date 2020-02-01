# 文档对象模型（DOM）


## <font color="#0088dd">**核心概念**</font>

当浏览器加载 Web 页面时，会在内存中创建页面的模型。

创建什么样的对象，这些对象有什么样的属性和方法，对象和对象之间有何种关系，这都是有统一的标准化的规定：DOM 模型。

DOM 对象与页面上的各种元素之间有明确的一一对应关系，访问和修改 DOM 对象等同于修改页面（视觉效果）。

DOM 对象的种类有：

- 文档节点（Document）
- 元素节点（Element）
- 属性节点（Attribute）
- 文本节点（Text）

`注意`，HTML 元素的属性习惯性使用单词“attribute”，对象的属性（也叫特征）使用单词“property”。

**文档节点** 是整棵 DOM 树的顶点，它代表着整个页面。当需要访问任何元素、属性和文本节点时，都需要通过它来进行导航。

**元素节点** 对应着 HTML 页面上的 HTML 元素。对元素节点的访问和操作，就代表着对页面上的 HTML 元素进行访问和操作。HTML 元素的父子关系，也会导致其对应的元素节点间有父子关系。

HTML 元素的开始标签中可以包含若干属性，这些属性在 DOM 树中形成 **属性节点** 。需要注意的是，属性节点 **并非** 元素节点的子节点。

当访问元素节点时可以访问元素内部的文本，这些文本保存在其 **文本节点** 中。文本节点是元素节点的子节点，而文本节点自己并没有子节点。如果一个元素包含文本和其它子元素，这些子元素和文本节点即为“兄弟”关系。

所有的“节点”都有以下关键属性：

<dl>
    <dt>nodeType</dt>
    <dd>表示该节点的类型</dd>
    <dd>9 代表 Document 节点</dd>
    <dd>1 代表 Element 节点</dd>
    <dd>3 代表 Text 节点</dd>
    <dd>8 代表 Comment 节点</dd>
</dl>
<dl>
    <dt>nodeName </dt>
    <dd>元素的标签名，以大写形式表示。</dd>
</dl>
<dl>
    <dt>nodeValue </dt>
    <dd>Text 节点的文本内容</dd>
</dl>


## <font color="#0088dd">**选中页面元素**</font>

访问并更新 DOM 树需要两个步骤：

1. 定位到需要操作的元素所对应的节点。
2. 使用它的文本内容、子元素或属性。

常见的访问/定位元素的方法：

- 选中单个元素
  - getElementById ( )
  - querySelector ( )

- 选中多个元素
  - getElementsByTagName ( )
  - getElementByClassName ( )
  - querySelectorAll ( )

当一个 DOM 方法可以返回多个元素时，它会返回一个 NodeList（即便其中只有一个元素）。

NodeList 看起来像是数组，它有

- length 属性
- item ( ) 方法
- 另外对它可以使用数组的 `[]` 语法（优于 item() 方法）


元素之间的关系：

<dl>
  <dt>父子关系</dt>
  <dd>parentNode 属性</dd>
  <dd>firstChild / lastChild 属性</dd>
</dl>

<dl>
  <dt>兄弟关系</dt>
  <dd>previousSibling / nextSibling 属性</dd>
</dl>

## <font color="#0088dd">**操作所选元素**</font>

- 操作文本内容
  - 文本节点的 nodeValue 属性

- 操作 HTML 内容
  - 元素节点的 innerHTML 属性（Firefox 不支持）

- 操作属性值
  - className / id 属性
  - hasAttribute ( )
  - getAttribute ( )
  - setAttribute ( )
  - removeAttribute ( )

如果要访问一个元素节点中的文本，可以不用通过“取得其文本节点后，再获取文本节点的内容”这种方式，

- FireFox 中可使用 textContent 属性。
- IE 中可使用 innerText 属性。
- 两者互不兼容。

DOM 层次结构操作：

- document.createElement ( )
- document.createTextNode ( )
- document.appendChild ( ) / document.removeChild ( )


## <font color="#0088dd">**操作 DOM 结构**</font>

### **添加节点**

```javascript
function click_handler() {
    var textNode = document.createTextNode('粒粒皆辛苦');
    var liNode = document.createElement('li');
    liNode.appendChild(textNode);

    var olNode = document.getElementsByTagName("ol")[0];
    olNode.appendChild(liNode);
}
```

document.createTextNode('...');  createTextNode() 方法创建一个新的文本节点。

document.createElement("li");  createElement() 方法创建一个新的元素节点。

liNode.appendChild(textNode);

### **删除节点**

```javascript
function click_handler() {
    var all_li = document.getElementsByTagName("li");

    if(all_li.length > 1) {
        var last_li = all_li.item(all_li.length - 1);

        var ol = document.getElementsByTagName("ol")[0];

        console.info(ol);
        console.info(last_li);
        ol.removeChild(last_li);
    }
}
```

### **删除特定节点**

document.getElementById('...');

### **插入节点**

```javascript
function click_handler() {
    var text = document.createTextNode('谁知盘中餐');
    var li = document.createElement('li');
    li.appendChild(text);

    var ol = document.getElementsByTagName('ol')[0];
    var last_li = document.getElementById('last');
    ol.insertBefore(li, last_li);
}
```

### **替换节点**

```javascript
function click_handler() {
    var text = document.createTextNode('谁知盘中餐');
    var li = document.createElement('li');
    li.appendChild(text);

    var ol = document.getElementsByTagName('ol')[0];
    var last_li = document.getElementById('last');
    ol.replaceChild(li, last_li);
}
```

### **DOM 和 CSS**

在 DOM 节点对象里，可以找到与 HTML 元素 `class` 属性对应的 `className` 属性。<small>（注意，这里不是 DOM 对象的 `class` 属性，因为 `class` 是 JavaScript 中的 **保留字**）。</small>

每个 DOM 节点都有一个 `style` 属性，用于操作该节点的样式。`style` 属性引用了一个对象，这个对象中有 HTML 元素可能存在的所有属性。例如：

```javascript
p.style.opacity = "0.0";	// 不透明度
```
