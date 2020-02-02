<span class>Apache Commons Chain</span>

# 基本概念

apache commons chain 提供了对 CoR 模式的基础支持。<small>CoR 模式，是 Chain of Responsebility 的缩写</small>。Commons Chain 实现了 Chain of Responsebility 和 Command 模式，其中的 `Catalog + 配置文件` 的方式使得调用方和 Command 的实现方的耦合度大大的降低，提高了灵活性。

使用 Apachecommons chain，需要将 *`commons-chain.jar`* 放入你的 classpath。另外，如果要使用 `Catalog + 配置文件` 的方式还需要加入 *`commons-digester.jar`* 包。

从使用的角度来看，Commons Chain 和工作流<small>（workflow）</small>非常相似。

# 极简案例

```java
public class Command1 implements Command {

    @Override
    public boolean execute(Context arg0)throws Exception {
        System.out.println("Command1 is done!");
        return false;
    }

}

public class Command2 implements Command { ... }

public class Command3 implements Command { ... }

public static void main(String[] args) {
    Chain chain = new ChainBase();
    chain.addCommand(new Command1());
    chain.addCommand(new Command2());
    chain.addCommand(new Command3());

    Context ctx = new ContextBase();
    chain.execute(ctx);
}
```

# 基本对象

## Command 接口

它是 Commons Chain 中最重要的接口，表示在 Chain 中的具体某一步要执行的命令。它只有一个方法：`boolean execute(Context context)`。如果返回 true，那么表示 Chain 的处理结束，Chain 中的其他命令不会被调用；返回 false，则 Chain 会继续调用下一个 Command，直到：

> - Command 返回 true；
> - Command 抛出异常；
> - Chain 的末尾；

## Context 接口

它表示命令执行的上下文，在命令间实现共享信息的传递。***`Context`*** 接口的父接口是 Map，***`ContextBase`*** 实现了 ***`Context`***。对于 web 环境，可以使用 ***`WebContext`*** 类及其子类<small>（*`FacesWebContext`*、*`PortletWebContext`* 和 *`ServletWebContext`*）</small>。

## Chain 接口

它表示“命令链”，要在其中执行的命令，需要先添加到 ***`Chain`*** 中。***`Chain`*** 的父接口是 ***`Command`***，***`ChainBase`*** 实现了它。


## Filter 接口

它的父接口是 Command，它是一种特殊的 Command。除了 Command 的 *`.execute()`*，它还包括一个方法：*`boolean postprocess(Context context, Exception exception)`* 。Commons Chain 会在执行了 Filter 的 *`.execute()`* 方法之后，执行 *`.postprocess()`*<small>（不论 Chain 以何种方式结束）</small>。Filter 的执行 *`.execute()`* 的顺序与 Filter 出现在 Chain 中出现的位置一致，但是执行 postprocess 顺序与之相反。

如：如果连续定义了 filter1 和 filter2，那么 execute 的执行顺序是：filter1 -> filter2；而 postprocess 的执行顺序是：filter2 -> filter1。

## Catalog 接口

它是逻辑命名的 Chain 和 Command 集合。通过使用它，Command 的调用者不需要了解具体实现 Command 的类名，只需要通过名字就可以获取所需要的 Command 实例。

# 基本使用

除了上述极简案例中那样直接使用 ChainBase 外，还可以自己创建 ChainBase 的子类再使用：

```java

public class CommandChain extends ChainBase {

    // 增加命令的顺序也决定了执行命令的顺序
    public CommandChain() {
        addCommand( new Command1());
        addCommand( new Command2());
        addCommand( new Command3());
    }
}

public static void main(String[] args) throws Exceptio {
    Command process = new CommandChain();
    Context ctx = new ContextBase();
    process.execute( ctx);
}
```

除了在程序中注册命令之外，还可以使用配置文件来完成。

```xml
<?xml version="1.0" encoding="gb2312"?>
<catalog>
    <chain name="CommandChain">
        <!-- 定义的顺序决定执行的顺序 -->
        <command id="command1" className= "chain.Command1"/>
        <command id="command2" className= "chain.Command2"/>
        <command id="command3" className= "chain.Command3"/>
    </chain>
    <command name="command4" className="chain.Command1"/>
</catalog>
```

装入配置文件的代码如下：

```java
public static void main(String[] args) {
    final String cfgFile = "/chain-cfg.xml";    // from classpath
    ConfigParser parser = new ConfigParser();

    parser.parse(AppTest.class.getResource(cfgFile));
    Catalog catalog = CatalogFactoryBase.getInstance().getCatalog();

    // 加载 Chain
    Command cmd = catalog.getCommand("CommandChain");
    Context ctx = new ContextBase();
    cmd.execute(ctx);

    // 加载 Command
    cmd = catalog.getCommand("command4");
    cmd.execute(ctx);
}
```

**注意**：使用配置文件的话，需要使用 Commons Digester。而 Digester 则依赖：Commons  Collections、Commons Logging 和 Commons BeanUtils。

---

加载 Catalog 到 web 应用。为了在 web 应用中加载 Catalog，需要在对应的 web.xml 中添加：

```xml
<context-param>
    <param-name>org.apache.commons.chain.CONFIG_CLASS_RESOURCE</param-name>
    <param-value>resources/catalog.xml</param-value>
</context-param>

<listener>
    <listener-class>org.apache.commons.chain.web.ChainListener</listener-class>
</listener>
```

默认情况下，Catalog 会被加载到 Servlet Context 中，对应的属性名字是 *`catalog`* 。因此获取 Catalog ：

```java
Catalog catalog = (Catalog) request.getSession() .getServletContext().getAttribute("catalog");
```

Over
