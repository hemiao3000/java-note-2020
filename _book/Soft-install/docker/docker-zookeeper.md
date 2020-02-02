# 通过 docker 安装 zookeeper

zookeeper 的 Docker 镜像在 dockerhub 上的地址：[https://hub.docker.com/\_/zookeeper](https://hub.docker.com/_/zookeeper)

当前<small>（2019-11-28）</small>的 latest 本是 `3.5.6` 。

## 一、安装及测试

### step 1: 下载镜像

```sh
docker pull zookeeper:3.5.6
```

### step 2: 查看本地镜像

会出现类似以下内容：

```sh
REPOSITORY       TAG      IMAGE ID        CREATED        SIZE
zookeeper        latest   c91a7d13d4d9    5 days ago     224MB
```

- zookeeper 的配置文件 `zoo.cfg` 在容器内是 `/conf/zoo.cfg` 文件；
- zookeeper 所需存储的数据在容器内的 `/data` 目录下；
- zookeeper 的运行日志存储信息存储在容器内的 `/datalog` 目录下。

## 单机模式

单机模式是一种特殊集群模式：只有一台机器的集群。这台机器同时扮演者 zookeeper 集群中的多种角色。<small>注意，机器只有一个，角色还是多个的。</small>

### step 1

在宿主机上创建要映射的文件和目录：

```sh
mkdir -p ~/docker/zookeeper/standalone/{conf,data,datalog}
```

在 `~/dockerstanalone/conf/` 下创建 `zoo.cfg` 文件，并在其中写下如此内容：

```properties
tickTime=2000
dataDir=/data
dataLogDir=/datalog
clientPort=2181
initLimit=10
syncLimit=5
server.1=<宿主机IP>:2888:3888
```

### step 2

创建并运行容器：

```sh
docker run \
    -d \
    --name zookeeper-standalone \
    -v ~/docker/zookeeper/standalone/conf:/conf \
    -v ~/docker/zookeeper/standalone/data:/data \
    -v ~/docker/zookeeper/standalone/datalog:/datalog \
    -p 2181:2181 \
    -p 2888:2888 \
    -p 3888:3888 \
    zookeeper:3.5.6
```

### step 3

验证安装启动是否成功

1. 进入容器，并进入 zookeeper 的 `bin` 目录。

```sh
docker exec -it zookeeper-standalone /bin/bash
cd /apache-zookeeper-3.5.6-bin/bin
```

执行客户端连接操作：

```sh
./zkCli.sh -server <宿主机ip>:2181
```

## 集群模式

为创建运行多个 zookeeper 容器创建多个映射目录：

```sh
mkdir -p ~/docker/zookeeper/node1/{conf,data,datalog}
mkdir -p ~/docker/zookeeper/node2/{conf,data,datalog}
mkdir -p ~/docker/zookeeper/node3/{conf,data,datalog}
```


```
tickTime=2000
dataDir=/data
dataLogDir=/datalog
clientPort=2181
initLimit=10
syncLimit=5
server.1=<宿主机IP>:2888:3888
server.2=<宿主机IP>:2888:3888
server.3=<宿主机IP>:2888:3888
```

在各个节点的 `data` 目录下创建 `myid` 文件，其中对应分别写入一个数字：1、2、3。<small>（该文件有且仅有一行）</small>。

```sh
touch ~/docker/zookeeper/node1/data/myid
echo 1 > ~/docker/zookeeper/node1/data/myid

touch ~/docker/zookeeper/node2/data/myid
echo 2 > ~/docker/zookeeper/node2/data/myid

touch ~/docker/zookeeper/node3/data/myid
echo 3 > ~/docker/zookeeper/node3/data/myid
```

创建并启动 3 个容器：

!FILENAME 第 1 个 zookeeper 容器
```sh
docker run \
    -d \
    --name zookeeper-node1 \
    -v ~/docker/zookeeper/node1/conf:/conf \
    -v ~/docker/zookeeper/node1/data:/data \
    -v ~/docker/zookeeper/node1/datalog:/datalog \
    -p 2180:2181 \
    -p 2887:2888 \
    -p 3887:3888 \
    zookeeper:3.5.6
```

!FILENAME 第 2 个 zookeeper 容器
```sh
docker run \
    -d \
    --name zookeeper-node2 \
    -v ~/docker/zookeeper/node2/conf:/conf \
    -v ~/docker/zookeeper/node2/data:/data \
    -v ~/docker/zookeeper/node2/datalog:/datalog \
    -p 2181:2181 \
    -p 2888:2888 \
    -p 3888:3888 \
    zookeeper:3.5.6
```

!FILENAME 第 3 个 zookeeper 容器
```sh
docker run \
    -d \
    --name zookeeper-node3 \
    -v ~/docker/zookeeper/node3/conf:/conf \
    -v ~/docker/zookeeper/node3/data:/data \
    -v ~/docker/zookeeper/node3/datalog:/datalog \
    -p 2182:2181 \
    -p 2889:2888 \
    -p 3889:3888 \
    zookeeper:3.5.6
```

以上配置关键：

- 站在容器自身的角度看，它们自己都认为自己使用的端口始终是：2181、2888、3888
- 站在宿主机映射的角度看，它们分别使用的宿主机的端口是：
    - 2180、2181、2182
    - 2887、2888、2889
    - 3887、3888、3889

```sh
docker exec -it zookeeper-node1 /apache-zookeeper-3.5.6-bin/bin/zkServer.sh status

ZooKeeper JMX enabled by default
Using config: /conf/zoo.cfg
Client port found: 2181. Client address: localhost.
Mode: follower
```