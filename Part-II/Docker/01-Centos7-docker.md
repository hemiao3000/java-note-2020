# Centos 7 上安装 docker

> <small>以下命令默认以 root 进行操作。</small>
> <small>另外，开发环境中，简单起见，记得关闭 Linux 防火墙</small>
> <small>启动： systemctl start firewalld</small>
> <small>关闭： systemctl stop firewalld</small>
> <small>查看状态： systemctl status firewalld</small>
> <small>开机禁用 ： systemctl disable firewalld</small>
> <small>开机启用 ： systemctl enable firewalld</small>

## 一、前期准备工作

- <big>前期准备工作 1：查看内核版本</big>

  docker 官方要求 Linux 内核版本至少 3.8 以上，建议 3.10 以上。通过以下命令可查看内核版本：

  ```sh
  3.10.0-957.el7.x86_64
  ```
  CentOS 7 的内核版本是满足其要求的。

- <big>前期准备 2：卸载旧版本</big>

  较旧的 Docker 版本称为 docker 或 docker-engine 。如果已安装这些程序，请卸载它们以及相关的依赖项。

  ```sh
  yum remove docker \
             docker-client \
             docker-client-latest \
             docker-common \
             docker-latest \
             docker-latest-logrotate \
             docker-logrotate \
             docker-engine
  ```

- <big>前期准备 3：为配置 docker 软件源作准备</big>

  ```sh
  yum install -y \
      yum-utils \
      device-mapper-persistent-data \
      lvm2
  ```

- <big>前期准备 4：为 CentOS 添加 docker 软件源 </big>

  ```sh
  yum-config-manager \
      --add-repo  \
      https://download.docker.com/linux/centos/docker-ce.repo
  ```

- <big>截至目前为止，我们干了什么？</big>

   CentOS 的默认的 yum 软件源中实际上有 docker 的安装包，如果直接进行 `yum install docker` 也是可行的。

   但是 docker 官方考虑到不同的用户对 docker 的不同版本有不同的需求<small>（有人追求最新，有人追求稳定）</small>，他们专门提供了一个仓库/网址用以提供多个版本的 docker 的下载。

   因此，我们必须告知 yum 有这样一个仓库的存在，并且未来要求 yum 从这个仓库中下载我们指定版本的 docker 。<small>而非默认的仓库。</small>


- <big>前期准备 5：查看所有仓库中所有 docker 版本</big>

  ```sh
  yum list docker-ce --showduplicates | sort -r
  ```


## 二、安装 docker 并验证

### 1. 从网络仓库中下载，安装

  ```shell
  yum install docker-ce-xxx
  ```

  上述命令中的 `xxx` 是指定的版本。例如：

  ```shell
  yum install -y docker-ce-18.06.3.ce-3.el7
  ```

  安装过程中会出现类似如下询问：

  ```
  从 https://download.docker.com/linux/centos/gpg 检索密钥
  导入 GPG key 0x621E9F35:
   用户ID     : "Docker Release (CE rpm) <docker@docker.com>"
   指纹       : 060a 61c5 1b55 8a7f 742b 77aa c52f eb6b 621e 9f35
   来自       : https://download.docker.com/linux/centos/gpg
  是否继续？[y/N]：
  ```

  输入 `y` 按回车继续安装。

  最终会出现：

  ```
  完毕！
  ```

### 2. 启动 Docker，并将其加入开机启动

  ```shell
  systemctl start docker
  systemctl enable docker
  ```

  会出现类似如下结果：

  ```sh
  Created symlink from /etc/systemd/system/multi-user.target.wants/docker.service to /usr/lib/systemd/system/docker.service.
  ```

### 3. 验证安装是否成功

  <small>（有 client 和 service 两部分表示docker安装启动都成功了）</small>

  输入 `docker version` 命令，会出现类似如下内容：

  ```sh
  Client:
    Version:    17.12.1-ce
    API version:    1.35
    Go version:    go1.9.4
    Git commit:    7390fc6
    Built:    Tue Feb 27 22:15:20 2018
    OS/Arch:    linux/amd64

  Server:
   Engine:
     Version:    17.12.1-ce
     API version:    1.35 (minimum version 1.12)
     Go version:    go1.9.4
     Git commit:    7390fc6
     Built:    Tue Feb 27 22:17:54 2018
     OS/Arch:    linux/amd64
     Experimental:    false
  ```

### 4. 验证连接 docker hub 中央镜像仓库

输入 `docker search -f is-official=true mysql` 命令，会出现类似如下结果：

```sh
NAME     DESCRIPTION                                     STARS  OFFICIAL ...
mysql    MySQL is a widely used, open-source relation…   8819   [OK]     ...
mariadb  MariaDB is a community-developed fork of MyS…   3102   [OK]     ...
percona  Percona Server is a fork of the MySQL relati…   459    [OK]     ...
```

## 三、配置国内镜像

由于 docker hub 的中央镜像仓库在国外，因此有时我们连接 docker hub 从中下载镜像速度会很感人，因此，我们需要配置国内的镜像网址，从国内现在镜像文件。

    国内的镜像本质上就是 docker hub 中央仓库在国内的一份缓存/备份。

通过 vi 命令编辑相关配置文件：

```sh
vim /etc/docker/daemon.json 
```

如果该文件存在，则将其内容清除；如果文件不存在，<small>（打开该文件后，其内容是空白的），</small>编辑结束后保存退出，即创建。


输入如下内容：

```sh
{
  "registry-mirrors": [
    "https://registry.docker-cn.com",
    "https://docker.mirrors.ustc.edu.cn"
  ]
}
```

<small>该配置文件配置了两个镜像，一个是 docker 官方在中国境内的官方镜像，一个是中科大维护的一个镜像。</small>

重新启动 docker 服务：

```sh
systemctl restart docker
```

查看修改结果：

```sh
docker info
```

会有如下内容：

```
...
Registry Mirrors:
 https://registry.docker-cn.com/
 https://docker.mirrors.ustc.edu.cn/
...
```

## 四、导入已有的镜像文件

考虑到有些场景下的联网的不方便，docker 提供了将已下载的 images 打包导出，再在别处导入的功能。

导出已有 image 使用命令：

```sh
docker save <repository>:<tag> -o <repository>.tar
```

例如：

```sh
docker save mysql:5.7 -o mysql-5.7.tar
```

导入 image 使用命令：

```sh
docker load -i <repository>.tar
```

例如：

```sh
docker load -i mysql-5.7.tar
```