在使用 Docker 时，经常会操作镜像与容器，这就会涉及各种操作指令的使用。

| 指令 | 说明 |
| :- | :- |
| docker images | 列出镜像 |
| docker search | 搜索镜像 |
| docker pull | 拉取镜像 |
| docker rmi | 删除镜像 |
| docler run | 创建并启动容器 |
| docker ps | 列出容器 |
| docker exec | 执行容器 |
| docker stop | 停止容器 |
| docker start | 启动容器 |
| docker rm | 删除容器 |
| docker build | 构建镜像 |


# 列出镜像

通过 *`docker images`* 指令可以查看本地镜像列表中已有的镜像：

```shell
docker images
```

它所返回的镜像列表信息包含了一下 5 个字段：

| 字段名 | 说明 |
| -: | :- |
| REPOSITORY | 镜像的名称。会一并显示它的 Namespace |
| TAG | 镜像的标签 |
| IMAGE ID | 镜像的 ID。一个长 64 位的十六进制字符串（SHA256 算法的运算结果）|
| CREATE | 镜像的创建时间 |
| SIZE | 镜像所占用的硬盘空间（包括被共享的镜像层的大小） |

当本地镜像较多时，还可以使用通配符过滤出符合条件的镜像。

```sh
sudo docker images ph*
```

# 搜索镜像

想知道在 Docker Hub 中包含了哪些镜像，除了可以登录 Docker Hub，在官网搜索外，还可以直接通过 Docker 命令搜索。

```sh
docker search mysql
```

显示出来的搜索结果中包含了镜像的如下信息：

| 字段名 | 说明 |
| -: | :- |
| NAME | 镜像的名称 |
| DESCRIPTION | 镜像的简单描述 |
| STARTS | 镜像在的点赞数 |
| OFFICIAL | 镜像是否为 Docker 官方提供<small>（建议使用官方提供的镜像）</small> |
| AUTOMATED | 镜像是否使用了自动构建 |



# 拉取镜像

通过 *`docker pull`* 指令可以拉取仓库镜像到本地<small>（默认都是拉取 Docker Hub 仓库镜像）。</small>

```sh
docker pull ubuntu
```

Docker 会利用镜像的分层机制，将镜像分为多个包进行下载，我们可以在终端输出中看到每层的下载状态。

- 注意:

  - 按照镜像的命名规则（`Namespace/Repository:Tag` ），上例中的 ubuntu 就是一个镜像的 Repository 名。

  - 没有指明 Namespace，就表示 Docker 官方管理的镜像。

  - 没有指明 Tag，Docker 默认会使用 `latest` 标签，表示最新版本。

  - 通常情况下，还是倾向于提供更确定的镜像信息，以减少镜像版本带来的不确定性。


# 删除镜像

当本地存放过多不需要的镜像时，可以通过 *`docker rmi`* 指令将其删除。在删除镜像时，需要指定镜像名称或镜像 ID 。

```sh
docker rmi -f <image_name or image_id>
```

默认情况下，如果至少还存在一个容器使用该镜像，那么该镜像无法删除。*`-f`* 选项表示停止容器，并强制删除该镜像。不过并不推荐强制删除。

如果一个镜像中含有某些与其它镜像共享的镜像层，这些被共享的镜像层仍会被保留下来，

只有未被其它镜像使用的层会被删除。

我们同样可以指定镜像 ID 删除镜像：

```sh
docker rmi 4e38e38c8ce0
```



# 创建并启动容器

Docker 镜像主要用于创建容器，可以使用 **`docker run`** 指令创建并启动容器。

- 示例：

  ```sh
  docker run -d -p 5000:80 --name test hello-world
  ```

- 命令说明：

  - *`docker run`* : 表示创建并启动一个容器。最后的 hello-word 表示制作该容器的。
  - *`-d`* : 表示容器启动后在后台运行
  - *`-p 5000:80`* : 表示将主机的 5000 端口和容器的 80 端口『对接』。即，宿主机从 5000 端口收到的数据，由系统送到 docker 的 80 端口。
  - *`--name test`* : 表示所创建的容器的名字为 test 。
  - *`hello-world`* : 表示以 `hello-world` 镜像为基础创建容器。

- 需要注意的是：

  - *`docker run`* 命令包含了两个动作：**创建容器** 和 **启动容器** 。
  - 镜像和容器是一对多的关系：同一个镜像可以用来制作多个容器。

- Docker 容器有以下两种运行态：

  - 前台交互式

    - 容器运行在前台，容器运行时直接连接到容器中运行的程序上

    - 这种场景下，我们通常会通过附加的参数打开容器的伪终端和输入流，从而实现与容器中程序的交互

    - 当通过命令退出和关闭链接时，容器即停止运行

    - `sudo docker run -i -t ubuntu /bin/bash`

    - `-i` 表示开启了 input（输入）功能 

    - `-t` 表示开启了一个连接容器里边的 terminal（终端）

    - `-it` 常一起使用

  - 后台守护式

    - 容器运行在后台，运行的过程中不会占用当前输入指定的终端，也不会连接到容器内的程序上。

    - 这种运行后台容器必须通过指令来关闭<small>（`docker stop`）</small>。

    - `sudo docker run -d nginx`


# 列出容器

生成容器后，可以通过 **`docker ps`** 指令查看当前运行的所有容器：

```sh
docker ps
```

| 字段名 | 说明 |
| -: | :- |
| CONTAINER ID | 容器的唯一性标识 |
| IMAGE | 容器所使用的镜像 |
| COMMAND | 容器启动时运行的命令<small>（即，容器中的主程序）</small> |
| CREATED | 容器的创建时间 |
| STATUS | 容器的运行状态。<small>Up 表示运行中，Exited 标识已停止</small> |
| PORTS | 容器内部包括的端口映射到的主机端口 |
| NAMES | 容器的名称 |


使用 *`docker ps`* 命令只会列出正在运行中的程序，如果要列出所有容器，需要携带参数 *`-a`* 。

```sh
sudo docker ps -a
```

当我们创建了较多容器时<small>（使用 *`docker ps`* 命令返回结果过多）</small>，可以使用 *`-l`* 参数列出最后创建的那个容器：


```sh
sudo docker ps -l
```

也可以使用 *`-n`* 参数可以列出数个最近创建的容器：

```sh
sudo docker ps -n 2
```

# 执行命令

当生成容器后，客户端可以通过 *`docker exec`* 指令与运行中的容器进行通信，在通信时需要指定容器的 ID 或 Name 。


- 例如，让容器去执行 *`ls -l`* 。

  ```sh
  docker exec f0c9a8b6e8c5 ls -l
  ```


# 停止容器

当不需要容器运行时，可以使用 *`docker stop`* 指令停止指定的容器。

- 语法：

  ```sh
  docker stop <container_name or container_id>
  ```

使用上述指令停止容器时会略有延迟，成功后返回该容器的 ID 。

另外，*`docker ps`* 命令的执行结果中不会显示已停止的容器，如需查看这些容器要使用 *`-a`* 选项。

我们还可以通过 *`docker kill`* 指令立即杀死运行容器进程。

- 语法：

  ```sh
  docker kill <container_name or container_id>
  ```


# 启动容器

容器停止后，如果需要重新访问该容器中的程序，需要重新启动该容器：

```sh
docker start <container_id or container_name>
```

除了 *`docker start`* 指令可以启动已停止的容器外，还可以使用 *`docker restart`* 指令重启容器。

```shell
docker restart <container_id or container_name> 
```

# 删除容器

当不需要使用容器时，则可以使用 *`docker rm`* 指令删除已停止的容器：

```shell
docker rm <container_id or container_name> 
```

默认情况下，上述指令只能删除已停止的容器。如需要删除正在运行的容器，则需要添加 *`-f`* 参数强制删除。

*`docker rm`* 指令可以传入多个容器的 ID 或名字进行批量删除，甚至可以套用 *`docker ps`* 命令全部删除：

```shell
docker rm -f $(docker ps -aq)
```

*`docker ps -aq`* 指令会获得所有容器的 ID 。

因为 Docker 特有的镜像机制，在创建容器过程中并没有大量的 IO 操作，所以创建过程是秒级的。因此，我们更倾向于**随用随建，随停随删**。在停止程序所在容器时，将容器一并删除。


# 构建镜像

除了可以通过 *`docker pull`* 指令从仓库拉取镜像之外，还可以通过 *`docker build`* 指令构建 Docker 镜像。

- 有两种方式进行镜像构建：

  1. 进入 Dockerfile 文件所在目录后，可以使用 *`docker build`* 指令进行镜像构建。

      ```
      cd ~/workspace/dockerspace/
      docker build -t hellodocker .
      ```

     上述的 **`.`** 表示通过当前目录下的 Dockerfile 文件进行镜像构建。


  2. 在其他任意目录进行镜像构建

     ```sh
     docker build -t hellodocker ~/workspace/dockerspace/
     ```

     通过同一个 Dockerfile 构建出来的镜像 ID 会相同。


# 文件拷贝

如果我们需要将文件拷贝到容器内可以使用 cp 命令

```sh
docker cp <主机中的文件路径名> <容器名>:<容器中的目录>
```

例如：

```sh
docker cp /tmp/1.txt microboom:/tmp/1.txt
```

也可以反向将文件从容器内拷贝出来：

```sh
docker cp <容器名>:<容器中的文件路径名> <主机中的文件路径名>
```

# 目录挂载

我们可以在创建容器的时候，将宿主机的目录与容器内的目录进行映射，这样容器中的某个目录和真机中的某个目录是同一个目录，容器和真机就可以以此为桥梁相互影响。

例如，我们可以创建一个运行 mysql 的容器，将容器中存放数据文件的目录映射到真机中的某个目录。这样，哪怕是该容器被删除了，曾经存储的数据仍然还在。

- 语法：

  ```sh
  docker run ... -v 宿主机目录:容器目录
  ```

例如：

  ```sh
  docker run                    \
    -d                          \
    --rm                        \
    -v /home/ben/mysql/conf:/etc/mysql/conf.d   \
    -v /home/ben/mysql/data:/var/lib/mysql      \
    -p 3306:3306                \
    -e MYSQL_ROOT_PASSWORD=123  \
    --name mysql                \
    mysql:5.7.22
  ```

# 查看容器 IP 地址

我们可以通过以下命令查看容器运行的各种数据：

```sh
docker inspect <container_name_or_id>
```

当然，从中查找容器的 IP 地址很不方便，因此，可以对此再细化一下：

```sh
docker inspect --format='\{\{.NetworkSettings.IPAddress}}' <container_name_or_id>
```

补，去掉上述命令中的两个 `\` 。


『完』
