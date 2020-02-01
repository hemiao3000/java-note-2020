<span class="title">ES6 新特性</span>

# ECMAScript 6

我们平时所使用的 JavaScript 语言，实际上是有一个语法标准的，被称作 `ECMAScpirt` 。该标准由各大浏览器厂商讨论决定，再进而在各家的浏览器上进行支持。

2015 年 6 月，ECMAScript 的第 6 个版本发布，俗称 `ECMAScript 6` 。另外，自此 ECMASCript 开始使用年号作版本，因此，ECMAScript 6 也叫作 `ECMAScript 2015` 。

现在 ES6 变为一个历史名词，泛指从 ESMAScript 5.1 版本之后的一切版本，涵盖了 ES2015、ES2016、ES2017 等。

截至目前位置，并非所有的浏览器都支持 ES6，你可以使用 ES6 进行编码，但最终它需要被转换成 ES5 的代码才能保证通用性<small>（在你和浏览器之间达成妥协）</small>。这里最简单<small>（当然也是一个不规范）</small>的解决办法是在页面上引入：*`babel.js`* 。

```js
<script src="https://cdn.staticfile.org/babel-standalone/6.26.0/babel.min.js"></script>
```

# let 命令

之前，我们写 js 定义变量的时候，只有一个关键字 `var`，`var` 有一个问题，就是定义的变量有时会莫名奇妙的成为全局变量。

例如这样的一段代码：

```js
<script type="application/javascript">
  for (var i = 0; i < 5; i++) {
    console.log(i);
  }

  console.log("循环外：" + i)
</script>
```

在循环外部也可以获取到变量 i 的值，显然变量 i 的作用域范围太大了。

- 语法:

```js
let <变量名>; 
```

`let` 用以替代 var 声明变量。`let` 声明的变量只在 `let` 命令所在的代码块内有效。<small>是真正意义上的局部变量。</small>


# const 命令

- 语法:

```js
const <变量名>; 
```

`const` 也可以替代 var 来声明变量。`const` 声明的变量是常量，不能被修改，类似于 java 中 `final` 关键字。

# 字符串扩展

在 ES6 中，为字符串扩展了几个新的 API ：

| API | 说明 |
| :- | :- |
| `includes()` | 返回布尔值，表示是否找到了参数字符串。|
| `startsWith()` | 返回布尔值，表示参数字符串是否在原字符串的头部。|
| `endsWith()` | 返回布尔值，表示参数字符串是否在原字符串的尾部。|

```js
<script type="application/javascript">
  let str = "hello microboom";
  console.log(str, " 中是否包含了microboom => ", str.includes("microboom"));
  console.log(str, " 中是否包含了microbom => ", str.includes("microbom"));
  console.log(str, " 中是否以h开头 => ", str.startsWith("h"));
  console.log(str, " 中是否以a开头 => ", str.startsWith("a"));
  console.log(str, " 中是否以m结束 => ", str.endsWith("m"));
  console.log(str, " 中是否以h结束 => ", str.endsWith("h"));
</script>
```

ES6 中提供 <code>\`</code><small>（反单引号，键盘上 ESC 下方的 `~` 键）</small>做为字符串模板标记，我们可以这样使用。

```js
<script>
  let str = `
  hello
  microboom
  微潮
  `;
  console.log(str);
</script>
```

在两个 <code>\`</code> 之间的部分都会被作为字符串的值，可以任意换行。

# 解构表达式

ES6 中允许按照一定模式从数组和对象中提取值，然后对变量进行赋值，这被称为 <font color="#0088dd">**解构**</font>（Destructuring）。

## 数组解构

  比如有一个数组：

  ```js
  let arr = [1,2,3]
  ```

  ES6 之前，只能通过下标访问其中每一个元素的值。但到了 ES6 可以这样：

  ```js
  const [x,y,z] = arr;  // x=arr[0], y=arr[1], z=arr[2]
  const [x,y] = arr;    // x=arr[0], y=arr[1]
  const [x] = arr;      // x=arr[0]
  ```

## 对象解构
  
  ```js
  const person = {
      name: "jack",
      age: 21,
      language: ['java','js','css']
   };
   
  // 解构表达式获取值
  const {name, age, language} = person;
  ```

  上面的 `name`、`age` 和 `language` 和 person 对象的属性名是一致的。

  如过想要用其它变量接收，需要额外指定别名：

  ```js
  const {name:n, age:a, language:l} = person;
  console.info(n, a, l);
  ```

  name 是 person 中的属性名，冒号后面的 n 是解构后要赋值给的变量。

# 函数优化

## 函数参数默认值

在 ES6 以前，我们无法给一个函数参数设置默认值，只能自己实现该逻辑。现在可以这么写：

```js
function add(a, b = 1) {
    return a + b;
}

add(10);
```

## 箭头函数

箭头函数是 ES6 中定义函数的简写。

- 一个参数时：

  ```js
  var print = function (obj) {
    console.info(obj);
  }

  // 简写为
  var print = obj => console.info(obj);
  ```

<small>本质上这就是 lambda 表达式概念在 JS 中的应用。不同的是在 Java 中使用的是 `->`，而在 ES6 里使用的是 `=>` 。</small>

- 两个参数时：

  ```js
  var sum = function (a, b) {
    return a + b;
  }

  // 简写为
  var sum = (a, b) => return a + b;
  ```

  没有参数时，需要通过 `()` 进行占位，代表参数部分

  ```js
  let sayHello = () => console.info('hello');
  ```

- 代码不止一行时：

  ```js
  let sayHello = () => {
    console.info('hello');
    console.info('world');
  };
  ```

# 对象的函数属性简写

```js
let person = {
    name: ...,
    age: ...,
    // 老版
    eat1: function (food) {
        console.info(this.name + 'is eating ' + food);
    },
    // 箭头函数简写版
    eat2: (food) => console.info(this.name + 'is eating ' + food);
    // 更简的版本
    eat3(food) {
      console.info(this.name + 'is eating ' + food);
    }
};
```

# 箭头函数结合结构表达式

```js
const person = { ... };

// 原代码
function hello(person) {
  console.info(person.name);
} 
hello(person);

// 现代码
var hello = ({name}) => console.info(name);
hello(person);
```

注意，上述的例子中，*`name`* 是被 `{ }` 包裹着的。

# 新增的数组的 map 方法

`map()` 方法接收一个函数，将原数组中的所有元素用这个函数处理后放入 <font color="#0088dd">**新数组**</font> 返回。

```js
// 字符串数组
let arr = ['1', '2', '3', '4'];
console.info(arr);

let newArr = arr.map(s => parseInt(s));
console.info(newArr);
```

# 新增的数组的 reduce 方法

`reduce()` 方法接收两个参数：

- 第一个参数必须，传入的是上一次 reduce 处理的结果。
- 第二个参数可选，是数组中要处理的下一个元素。

`reduce()` 会从左到右依次把数组中的元素用 `reduce()` 处理，并把处理的结果作为下次 `reduce()` 的第一个参数。如果是第一次，会把前两个元素作为计算参数，或者把用户指定的初始值作为起始参数。

```js
const arr = [1, 20, -5, 3];
let result = arr.reduce((a, b)=>{
  return a + b;
});
console.log(result);
```

说明：

- 第一轮循环中，a 的值是 0/nil<small>（因为没有上一轮）</small>， b 的值是当前的 arr[0]，即 1 。
- 第二轮循环中，a 的值是 `0 + 1`，即 1，b 的值是当前的 arr[1]，即 20 。
- 第三轮循环中，a 的值是 `1 + 21`，即 22，b 的值是当前的 arr[2]，即 -5 。
- 第四轮循环中，a 的值是 `22 + (-5)`，即 17，b 的值是当前的 arr[3]，即 3 。

# 扩展运算符

扩展运算符（spread）是三个点（`...`）， 用以将一个数组转为用逗号分隔的参数序列 。

```js
// 基本使用
console.log(...[1, 2, 3]); // 等同于
console.log(1, 2, 3);

console.log(1, ...[2, 3, 4], 5); // 等同于
console.log(1, 2, 3, 3, 4, 5);


// 为函数传值
function add(x, y) {
  return x + y;
}

var numbers = [1, 2];
add(...numbers); // 等同于
add(1, 2);

// 数组合并
let arr = [...[1,2,3],...[4,5,6]];// 等同于
let arr = [1, 2, 3, 4, 5, 6];

// 与解构表达式结合
const [first, ...rest] = [1, 2, 3, 4, 5]; // 等同于
first = 1;
rest = [2, 3, 4, 5];

// 将字符串转成数组
console.log([...'hello']) // 等同于
console.log(['h', 'e', 'l', 'l', 'o']);
```
