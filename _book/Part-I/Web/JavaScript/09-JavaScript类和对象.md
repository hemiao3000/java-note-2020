# JavaScript 类和对象

实例化对象有几种方式：

- 使用内置类
- new Object
- 对象字面量
- 构造函数
- 原型对象模式

## 使用内置类

JavaScript 有几个内置类，例如：Number、Array、String、Date 和 Math。这些内置类都有自己的属性和方法。此外，JavaScript 还提供了一种非常优秀的面向对象编程结构，所以我们也可以创建自己的自定义类。

使用对象语法：

```javascript
var x = new Number("5");
```

## 对象字面量

还可以使用 {} 语法创建对象（及对象字面量语法），此时可以直接为对象属性和方法赋值：

```javascript
var obje = {
  name : "tom",
  age: 20,
  sayHello : function () {
    console.info('hello');
  }
}
```

除了“点语法”，还可以通过 “[]语法” 来访问对象的成员。

## 定义类

如果想定义一个具有可重用性的类，可以结合构造函数和原型对象两种方式共同实现<small>（其实，单独一种也可以实现，不过不建议如此）</small>。

通常，是在构造方法中定义类的属性，并对其初始化：

```javascript
function Circle(r) {  
      this.r = r;  
}  
Circle.PI = 3.14159;  
```

通过原型对象定义其所具有的的方法：

```javascript
Circle.prototype.area = function() {  
  return Circle.PI * this.r * this.r;  
}  
```

