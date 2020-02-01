# Ubuntu 上安装 Docker

## 安装 docker

```shell
sudo apt install docker-io
```


## 将当前用户添加至 docker 用户组

  ```shell
  sudo groupadd docker #确保docker用户组的存在
  sudo gpasswd -a $USER docker #将当前用户添加至docker用户组
  newgrp docker #更新docker用户组
  ```

## Docker 配置 Daocloud 加速器

国内下载 Docker HUB 官方的相关镜像比较慢，可以使用国内（docker.io）的一些镜像加速器，镜像保持和官方一致，关键是速度块，推荐使用。

- 编辑 `daemon.json`

  ```sh
  vim /etc/docker/daemon.json
  ```
- 配置国内镜像地址

  ```sh
  {
    "registry-mirrors" : [
      "http://registry.docker-cn.com",
      "http://docker.mirrors.ustc.edu.cn",
      "http://hub-mirror.c.163.com"
    ],
    "insecure-registries" : [
      "registry.docker-cn.com",
      "docker.mirrors.ustc.edu.cn"
    ],
    "debug" : true,
    "experimental" : true
  }
  ```

  `:wq` 保存退出。

4. 重启 Docker：`systemctl daemon-reload` 和 `systemctl restart docker`

5. 执行 `docker pull hello-world` 会看到类似如下内容：

  ```
  Using default tag: latest
  latest: Pulling from library/hello-world
  1b930d010525: Pull complete 
  Digest: sha256:c3b4ada4687bbaa170745b3e4dd8ac3f194ca95b2d0518b417fb47e5879d9b5f
  Status: Downloaded newer image for hello-world:latest
  ```

6. 执行 `docker run hello-world` 会看到类似如下内容：

  ```
  Hello from Docker!
  This message shows that your installation appears to be working correctly.

  To generate this message, Docker took the following steps:
  1. The Docker client contacted the Docker daemon.
  2. The Docker daemon pulled the "hello-world" image from the Docker Hub. (amd64)
  3. The Docker daemon created a new container from that image which runs the executable that produces the output you are currently reading.
  4. The Docker daemon streamed that output to the Docker client, which sent it to your terminal.

  To try something more ambitious, you can run an Ubuntu container with:
    $ docker run -it ubuntu bash

  Share images, automate workflows, and more with a free Docker ID:
    https://hub.docker.com/

  For more examples and ideas, visit:
    https://docs.docker.com/get-started/
   ```


## 最后的绝招

如果经过配置，仍出现网络问题，那么可以在使用 *`docker pull`* 命令从网络仓库下载镜像时，直接/强行指定中央仓库网址<small>（而非使用默认的网址）</small>。

- 可以从网易的中央仓库查询镜像信息：

  ```
  https://c.163.com/hub#/home
  ```

- *`docker pull`* 手动指定网易的仓库网址 

  ```shell
  docker pull hub.c.163.com/library/redis:alpine
  ```

