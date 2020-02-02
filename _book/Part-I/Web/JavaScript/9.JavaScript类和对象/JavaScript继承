# JavaScript 中的继承

由于 JavaScript 语言本身的缺陷，导致 JavaScript 本身并没有提供 class 关键字，而语法层面继承更是无从谈起。
  
因此，如何实现继承有各种不同的方案。其中，机制目前为止（不考虑 ES6、ES7 语法的改进），最好的继承实现方案是：寄生组合式继承。

实现继承需要完成两部分工作：

- 构造函数属性的继承
- 建立子类和父类原型的链接

要实现第一部分工作可以在子类中调用超类的构造函数：

```javascript
SuperType.apply(this, arguments);
```

要实现第二部分工作则可以使用业内通用的 inherit 方法。

inherit 方法逻辑上干了类似如下工作：

```javascript
function object(o){
    function F(){}
    F.prototype = o;
    return new F();
}
```


```javascript
/**
 * 子类的原型对象是父类原型对象的一个实例。
 * 否则，多个子类公用父类的同一个原型对象，会有潜在隐患：一个子类改变了其父类原型对象中的内容，对其它子类都有影响。
 */
function inherit(subType, superType) {
    var prototype = Object.create(superType.prototype);
    prototype.constructor = subType; // 增强对象
    subType.prototype = prototype; // 指定对象
}

function SuperType(name) {
    this.name = name;
}

SuperType.prototype.sayName = function () {
    console.log(this.name)
};

function SubType(name, age) {
    SuperType.call(this, name);
    this.age = age;
}

inherit(SubType, SuperType);
```