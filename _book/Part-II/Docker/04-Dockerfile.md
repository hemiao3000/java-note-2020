# Dockerfile

Docker 镜像是⼀个特殊的⽂件系统，除了提供容器运⾏时所需的程序、库、资源、配置等⽂件外，还包含了为运⾏时准备的⼀些配置参数<small>（如匿名卷、环境变量、⽤户等）</small>。镜像不包含任何动态数据，其内容在构建之后也不会被改变。

镜像的定制实际上就是定制每⼀层所添加的配置、⽂件。如果可以把每⼀层修改、安装、构建、操作的命令都写⼊⼀个脚本，⽤这个脚本来构建、定制镜像，那么之前提及的⽆法重复的问题、镜像构建透明性的问题、体积的问题就都会解决，这个脚本就是 Dockerfile。

Dockerfile 是⼀个⽂本⽂件，其内包含了⼀条条的指令（Instruction），每⼀条指令构建⼀层，因此每⼀条指令的内容，就是描述该层应当如何构建。有了 Dockerfile，当需要定制⾃⼰额外的需求时，只需在 Dockerfile 上添加或者修改指令，重新⽣成 image 即可，省去了敲命令的麻烦。

Dockerfile ⽂件格式如下：

```shell
##Dockerfile ⽂件格式
# This dockerfile uses the ubuntu image
# VERSION 2 - EDITION 1
# Author: docker_user
# Command format: Instruction [arguments / command] ..

#（1）第⼀⾏必须指定基础镜像信息
FROM ubuntu
k
# （2）维护者信息
MAINTAINER docker_user docker_user@email.com

# （3）镜像操作指令
RUN echo "deb http://archive.ubuntu.com/ubuntu/ raring main universe" >> /etc/apt/
sources.list
RUN apt-get update && apt-get install -y nginx
RUN echo "\ndaemon off;" >> /etc/nginx/nginx.conf

# （4）容器启动执⾏指令
CMD /usr/sbin/nginx
```

Dockerfile 分为四部分：**基础镜像信息**、**维护者信息**、**镜像操作指令**、**容器启动执⾏指令** 。

- ⼀开始必须要指明所基于的镜像名称，

- 接下来⼀般会说明维护者信息；

- 后⾯则是镜像操作指令，如 RUN 指令。每执⾏⼀条 RUN 指令，镜像添加新的⼀层，并提交；

- 最后是 CMD 指令，来指明运⾏容器时的操作命令。