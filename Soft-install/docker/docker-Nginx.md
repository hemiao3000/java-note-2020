# docker 安装 Nginx

docker hub 网址：[https://hub.docker.com/\_/nginx](https://hub.docker.com/_/nginx)

当前<small>（2019-11-20）</small> `latest` 版本是 `1.17.6`；`stable` 版本是 `1.16.1`。


## 一、下载及测试

### step 1：下载镜像

```shell
docker pull nginx:1.16.1
```

### step 2：查看本地镜像

```sh
docker images
```

### step 3：运行容器

```sh
docker run           \
    -d                 \
    --name <指定容器名> \  
    --rm               \
    -p <宿主机端口>:80  \ 
    nginx:1.16.1
```

### step 4：验证安装成功

启动容器后，访问 `http://宿主机ip:8080` 能看到 Nginx 的欢迎界面： `Welcome to nginx!`



## 二、配置与挂载

Nginx 的配置文件位于容器中的 `/etc/nginx/nginx.conf` 文件。我们只需要在虚拟机上创建一个配置文件，将其映射成这个配置文件即可。

- 在宿主机上创建目录及配置文件。

  ```sh
  mkdir -p ~/docker/nginx/conf
  touch ~/docker/nginx/conf/nginx.conf
  ```

  以下内容是 nginx 配置文件的默认内容：

  ```
  user  nginx;
  worker_processes  1;

  error_log  /var/log/nginx/error.log warn;
  pid        /var/run/nginx.pid;

  events {
      worker_connections  1024;
  }


  http {
      include       /etc/nginx/mime.types;
      default_type  application/octet-stream;

      log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                        '$status $body_bytes_sent "$http_referer" '
                        '"$http_user_agent" "$http_x_forwarded_for"';

      access_log  /var/log/nginx/access.log  main;

      sendfile        on;
      #tcp_nopush     on;

      keepalive_timeout  65;

      #gzip  on;

      include /etc/nginx/conf.d/*.conf;
  }
  ```

- 启动容器，并挂载配置文件

  ```
  docker run  \
      -d  \
      --name nginx-test \
      --rm \
      -p 8080:80 \
      -v ~/docker/nginx/conf/nginx.conf:/etc/nginx/nginx.conf:ro \
      nginx:stable
  ```