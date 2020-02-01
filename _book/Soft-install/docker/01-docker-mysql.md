# 通过 docker 安装 Mysql

MySQL 的 Docker 镜像在 dockerhub 上的地址：[https://hub.docker.com/\_/mysql](https://hub.docker.com/_/mysql)

当前<small>（2019-11-19）</small>的 latest 本是 `8.0.18` 。另外，`5.7` 版本是 `5.7.28`；`5.6` 版本是 `5.6.46`。

## 一、安装及测试

### step 1: 下载镜像

```sh
docker pull mysql:5.7.28
```

### step 2: 查看本地镜像

会出现类似以下内容：

```sh
REPOSITORY       TAG      IMAGE ID        CREATED        SIZE
mysql            5.7.28   cd3ed0dfff7e    5 weeks ago    437MB
```


### step 3: 运行容器

- 删除曾经已有的同名容器

  ```sh
  docker stop mysql-test
  docker rm mysql-test
  ```

- 创建并运行 mysql 容器

  ```sh
  docker run             \
      -d                 \
      --name <指定容器名> \
      -p 3306:3306       \
      -e MYSQL_ROOT_PASSWORD=<指定root登录密码> \
      mysql:5.7.28
  ```

### step 4: 验证安装成功

- 进入 mysql-test 容器

  ```sh
  docker exec -it mysql-test /bin/bash
  ```

- 执行 mysql-cli 的连接命令

  ```sh
  mysql -uroot -p123456
  ```

### 可选操作：对 root 的远程连接授权

容器逻辑上等价于另一台电脑。而在 mysql 中，root 用户默认只能从 mysql 服务端所在的电脑上登陆，无法从『另一台』电脑通过远程连接的方式登陆。

当然，这种『默认』行为是可以配置的。

按上述命令，从容器内部连接到 mysql 服务端，执行如下 SQL 语句：

```sql
GRANT ALL ON *.* TO 'root'@'%';
flush privileges;
```

### 可选操作：从容器外部连接

```sh
mysql -h <主机_centos_IP> -P <主机端口号> -u root -p <root登陆密码>
```


## 二、配置与挂载

- 删除曾经已有的同名容器

  ```sh
  docker stop mysql-test
  docker rm mysql-test
  ```

- 创建本机挂载目录

  ```sh
  mkdir ~/docker/mysql/stanalone/{conf,data}
  ``

MySQL 的配置文件在容器内部的 `/etc/mysql/my.cnf` 。其实这个配置文件只是个『引子』，它配置了：让 MySQL 去加载 `/etc/mysql/conf.d` 目录下所有的 `.cnf` 文件。

MySQL 的数据的存储目录是容器内部的 `/var/lib/mysql` 目录。

在主机的 `conf` 目录下新建任意命名的 `.cnf` 文件，其中内容见最后。

- 创建并运行容器

  ```shell
  docker run \
    -d \
    --name mysql-test \
    -v ~/docker/mysql/conf:/etc/mysql/conf.d \
    -v ~/docker/mysql/data:/var/lib/mysql \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=123456 \
    mysql:5.7.28
  ```

- 新建配置文件 xxx.cnf：

  ```properties
  [mysqld]
  # 给 mysql-server 分配一个独一无二的 ID 编号
  server-id = 1 

  # mysql-server 监听/接收连接的端口号
  port = 3306

  # 不把 IP 地址解析为主机名; 以客户端的 IP 地址为依据检查其操作权限。
  skip_name_resolve 

  # mysql-server 存储数据时使用的默认字符集;
  # default-character-set 已过时
  # 另外，utf8 和 utf8mb4 的区别见最后
  character-set-server = utf8mb4 　

  # mysql-server 最大连接数
  max_connections = 300
  ```

『完』