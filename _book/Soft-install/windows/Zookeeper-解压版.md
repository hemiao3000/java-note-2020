<span class="title">Zookeeper 解压版的安装（Window）</span>

# 下载、解压

<small>Zookeeper 只有解压版本，没有安装版。</small>

- [官网下载](https://www.apache.org/dyn/closer.cgi/zookeeper/)

- [国内镜像](https://mirrors.tuna.tsinghua.edu.cn/apache/zookeeper/)

将下载的 `apache-zookeeper-3.5.6-bin.tar.gz` 解压开，解压到例如 `D:\ProgramFiles\` 目录下。

# 配置

zookeeper 在运行是需要有两个目录来记录数据和日志信息。在 zookeeper 解压目录下创建 *`data`* 和 *`log`* 目录。 <small>（原则上可以在你认为合适的任何地方创建这两个目录）</small>

- `D:\ProgramFiles\apache-zookeeper-3.5.6-bin\data`

- `D:\ProgramFiles\apache-zookeeper-3.5.6-bin\log`

zookeeper 的解压目录下的 *`conf`* 目录下放的是 zookeeper 的配置文件。其中名为 *`zoo_sample.cfg`* 的配置文件是模板文件。

复制 *`zoo_sample.cfg`* 文件，命名为 *`zoo.cfg`*。修改 *`zoo.cfg`* 文件的第 12 行<small>（*`dataDir=/tmp/zookeeper`*）</small>，并添加一行新内容：

```
dataDir=D:\\ProgramFiles\\apache-zookeeper-3.5.6-bin\\data
dataLogDir=D:\\ProgramFiles\\apache-zookeeper-3.5.6-bin\\log
```

这两个目录就是之前你所创建的那两个目录。

# 运行、验证

进入 zookeeper 解压目录的 *`bin`* 下，运行 *`zkServer.cmd`* 。

```java
public static void main(String[] args) throws Exception {

    // 创建一个Zookeeper实例，第一个参数为目标服务器地址和端口，第二个参数为Session超时时间，第三个为节点变化时的回调方法
    ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 30000, new WatcherTest());

    String node = "/node2";
    Stat stat = zk.exists(node, false);
    if (null == stat) {
        // 创建一个节点，数据为test,不进行ACL权限控制，节点为永久性的
        String createResult = zk.create(node, "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(createResult);
    }

    // 取得/node2/test节点下的数据,返回byte[]
    byte[] b = zk.getData(node, false, stat);
    System.out.println(new String(b));
    zk.close();
}
```

```java
public class WatcherTest implements Watcher {

    public void process(WatchedEvent arg0) {
        System.out.println("========================");
        System.out.println("path:" + arg0.getPath());
        System.out.println("type:" + arg0.getType());
        System.out.println("state:" + arg0.getState());
        System.out.println("========================");
    }

}
```

