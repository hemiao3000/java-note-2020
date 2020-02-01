# Docker 安装 Kibana

docker hub 网址：[https://hub.docker.com/_/kibana](https://hub.docker.com/_/kibana)

当前<small>2019-11-21</small> 的最近版本是 `7.4.2` 。与 Elasticsearch 一致。

## 一、安装及测试

### step 1: 下载镜像

```sh
docker pull kibana:7.4.2
```

### step 2: 查看本地镜像

会出现类似以下内容：

```sh
REPOSITORY       TAG      IMAGE ID        CREATED        SIZE
kibana           7.4.2    230d3ded1abc    3 weeks ago    1.1GB
```


### step 3: 运行容器

之前要安装 elasticsearch 时，创建了一个网络，此时，kibana 容器也要运行在此网络中，以便两者通信。

```sh
docker run \
    -d \
    --name kibana-test \
    --net es-net \
    -e ELASTICSEARCH_URL=http://172.17.0.2:9200 \
    -p 5601:5601 \
    kibana:7.4.2
```

-e "ELASTICSEARCH_URL=http://192.168.0.104:9200"