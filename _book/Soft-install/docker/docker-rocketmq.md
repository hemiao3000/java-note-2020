# Docker 安装 RocketMQ

docker hub 上没有 RocketMQ 的官方镜像。不过 RocketMQ 官方在 github 上提供了 dockerfile<small>（及相关使用教程）</small>，用以生成 RocketMQ 的 Docker image 。


## 安装及测试

### 下载

从 github 上下载 RocketMQ 的 *dockerfile*<small>（及其他）</small>[rocketmq-docker](https://github.com/apache/rocketmq-docker)

```
git clone https://github.com/apache/rocketmq-docker.git
```

### 生成镜像

```shell
cd image-build
sh build-image.sh 4.6.0 centos
```

*`4.6.0`* 和 *`centos`* 分别是 *`RMQ-VERSION`* 和 *`BASE-IMAGE`* 。

*`RMQ-VERSION`* 和 *`BASE-IMAGE`* 的可选值如下：

| RMQ-VERSION | BASE-IMAGE |
| :-: | :-: |
| 4.6.0 | centos |
| 4.5.2 | alpine |
| 4.5.1 | |
| 4.5.0 | |
| 4.4.0 | |
| ...   | |

经过短暂（或漫长，取决于网速）的等待，执行 *`docker images`* 命令会发现已生成 rocketmq 的 docker image 。

```
REPOSITORY                TAG                 IMAGE ID            CREATED             SIZE
apacherocketmq/rocketmq   4.6.0               5c0ee30862a2        22 hours ago        498MB
```

### 生成启动脚本

在 rocketmq-docker 的根目录下执行下述命令：

```shell
sh stage.sh 4.6.0
```

和上述命令一样，*`4.6.0`* 是 *`RMQ-VERSION`*，如有需要，可使用其他版本号。

该命令会在 rocketmq-docker 目录下的 *`stages`* 内生成一个 *`4.6.0`* 目录。<small>有些 linux 系统在 *`4.6.0`* 目录下还有一层叫做 *`template`* 的目录。</small>


### 启动 rocketmq 

```shell
cd stages/4.6.0 
./play-docker.sh centos
```

ubuntu 下执行

```shell
cd stages/4.6.0/template
./play-docker.sh centos
```

会看到类似如下信息：

```
Play RocketMQ docker image of tag 4.6.0-centos
Starting RocketMQ nodes...
8ebe584818034bc948c7498ddb680d2f531a1e1b2f597bc224974c4458d4af2c
35f37ed4415f7f58b28772d7678a3a1b4096c7a821b52a4a4814f53f400e32e4
```

执行 *`docker ps`* 命令，会看到类似如下信息：

```
CONTAINER ID        IMAGE                           COMMAND CREATED STATUS PORTS NAMES
35f37ed4415f        apacherocketmq/rocketmq:4.6.0   ...     ...     ...    ...   rmqbroker
8ebe58481803        apacherocketmq/rocketmq:4.6.0   ...     ...     ...    ...   rmqnamesrv
```

