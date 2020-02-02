# Docker 安装 ElasticSearch

docker hub 网址：[https://hub.docker.com/_/elasticsearch](https://hub.docker.com/_/elasticsearch)

当前<small>2019-11-21</small>的 7 版本最新的是 `7.4.2` ，6 版本最新的是 `6.8.5`。<small>（没有 `latest` 版本）</small>

## 一、安装及测试

### step 1: 下载镜像

```sh
docker pull elasticsearch:6.8.5
```

### step 2: 查看本地镜像

执行 `docker images` 命令，会出现类似以下内容：

```sh
REPOSITORY       TAG      IMAGE ID        CREATED        SIZE
elasticsearch    6.8.5    b1179d41a7b4    3 weeks ago    855MB
```

### step 3: 运行容器

- 删除曾经已有的同名容器

  ```sh
  docker stop es-test
  docker rm es-test
  ```

- 创建并运行 elasticsearch 容器

  ```sh
  docker run \
    -d \
    --name es-test \
    -p 9200:9200 \
    -p 9300:9300 \
    -e "discovery.type=single-node" \
    elasticsearch:6.8.5 
  ```

### step 4: 验证安装成功

访问网址 `http://主机IP:9200/`，<small>会有一点点延迟，</small>会看到类似如下内容:

```
{
  "name" : "dc1ef18d9e47",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "j99YYT-CRISTsoxgXEB6MA",
  "version" : {
    "number" : "6.8.5",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
    "build_date" : "2019-10-28T20:40:44.881551Z",
    "build_snapshot" : false,
    "lucene_version" : "8.2.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

## 二、配置与挂载


- 删除曾经已有的同名容器

  ```sh
  docker stop es-test
  docker rm es-test
  ```

- 创建本机挂载目录

  ```sh
  mkdir ~/docker/elasticsearch/stanalone/{conf,data}
  ```

elasticsearch 的配置文件是容器内部的 `/usr/share/elasticsearch/config/elasticsearch.yml` 文件。

elasticsearch 的数据的存储目录是容器内部的 `/usr/share/elasticsearch/data` 目录。

- 创建并运行容器

  ```sh
  docker run \
    -d \
    --name es-test \
    -p 9200:9200 \
    -p 9300:9300 \
    -v ~/docker/elasticsearch/conf/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
    -v ~/docker/elasticsearch/data:/usr/share/elasticsearch/data \
    -e "discovery.type=single-node" \
    -e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
    elasticsearch:6.8.5 
  ```

- `-e "discovery.type=single-node"` 表示单节点运行 
- `-e ES_JAVA_OPTS="-Xms256m -Xmx256m"` 设置初始内存和最大内存。


## Kibana

修改你本机<small>（win10）</small>的 Kibana<small>（6.8.5 版本）</small>的配置文件 `config/kibana.yml` 中的 28 行：

```yml
elasticsearch.hosts: ["http://<主机_centos_IP>:9200"]
```

