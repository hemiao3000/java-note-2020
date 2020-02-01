# Docker 安装 FastDFS

FastDFS 在 docker hub 上没有官方（Official）镜像。最高下载量的镜像是 [https://hub.docker.com/r/season/fastdfs](https://hub.docker.com/r/season/fastdfs)，有 100k+，这个量也算是很官方了。

当前<small>2019-11-27</small>的版本最新的是 `1.2`，但是 `latest` 版本是 ？

## 一、安装及测试

### step 1: 下载镜像

```sh
docker pull season/fastdfs:1.2
```

因为是非官方（official）的镜像，因此需要出现 `season` 这样的用户/组织名。

### step 2: 查看本地镜像

执行 `docker images 命令，会出现类似以下内容：

```sh
REPOSITORY       TAG      IMAGE ID        CREATED        SIZE
season/fastdfs  1.2       510c4e6ec9f3    4 years ago    228MB
```

### step 3: 运行容器

由于 FastDFS 分为 tracker 和 storage，因此，创建启动 fastDFS 最少也是两个容器。

- 删除曾经已有的同名容器

  ```sh
  docker stop fastdfs-tarcker-test
  docker stop fastdfs-storage-test

  docker rm fastdfs-tracker-test
  docker rm fastdfs-storage-test
  ```

- 创建并运行 fastdfs tracker 容器

  ```sh
  docker run \
    -d \
    --name fastdfs-trakcer-test \
    season/fastdfs:1.2 \
    tracker
  ```

- 创建并运行 fastdfs storage 容器

  ```sh
  docker run \
    -d \
    --name storage \
    -e TRACKER_SERVER:<宿主机ip>:22122 \
    season/fastdfs:1.2 \
    storage
  ```


### step 4: 验证安装成功

```sh
docker ps
```

## 二、配置与挂载


- 删除曾经已有的同名容器

  ```sh
  docker stop fastdfs-tracker-test
  docker stop fastdfs-storage-test

  docker rm fastdfs-tarcker-test
  docker rm fastdfs-storage-test
  ```

- 创建本机挂载目录

  ```sh
  mkdir ~/docker/fastdfs/tracker/{conf,data}
  mkdir ~/docker/fastdfs/storage/{conf,data,store_path}
  ```

elasticsearch 的配置文件是容器内部的 `/usr/share/elasticsearch/config/elasticsearch.yml` 文件。

elasticsearch 的数据的存储目录是容器内部的 `/usr/share/elasticsearch/data` 目录。

- 创建并运行 Tracker 容器

  ```sh
  docker run \
    -d \
    --name fastdfs-trakcer-test \
    -v ~/docker/fastdfs/tracker/data:/fastdfs/tracker/data \
    season/fastdfs:1.2 \
    tracker
  ```

- 创建并运行 Storage 容器

  ```sh
  docker run \
    -d \
    --name fastdfs-storage-test \
    -v ~/docker/fastdfs/storage/data:/fastdfs/storage/data \
    -v ~/docker/fastdfs/store_path:/fastdfs/store_path \
    -e TRACKER_SERVER:<宿主机ip>:22122 \
    season/fastdfs:1.2 \
    storage
  ```



