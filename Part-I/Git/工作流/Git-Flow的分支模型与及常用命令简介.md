#  Git Flow 的分支模型与及常用命令简介



Git Flow 的源码可以通过以下链接下载：

    https://github.com/nvie/gitflow

或者，直接输入以下命令安装 git flow：

    apt-get install git-flow
    
我们可以通过以下命令来初始化一个现有的 git 本地仓库。

    git flow init
    
接着回答几个关于分支的问题。不用担心，使用默认值即可，直接按回车键。

```
No branches exist yet. Base branches must be created now 
Branch name for production releases: [master] 
Branch name for “next release” development: [develop] 
How to name your supporting branch prefixes? 
Feature branches? [feature/] 
Release branches? [release/] 
Hotfix branches? [hotfix/] 
Support branches? [support/] 
Version tag prefix? []
```

这样，便完成了 git flow 的初始化工作。

在 git flow 的分支模型中，有两个主分支 master 和 develop，还有几个额外的分支来支持代码的版本管理。下面先简要介绍一下这些分支的特点和 git flow 常用命令的使用。    

可以使用下图来说明 git flow 这几分支的常用命令：

    git flow commands

![gitflow_3](img/gitflow_3.png)

在开发的整个阶段，只有两个主分支贯穿于整个开发阶段：master 分支和 develop 分支。功能特性的开发以及bug的修复都通过创建新的分支来实现，且这些分支的生命周期都比较短暂。开发成员之间的开发可以做到尽量不干扰对方，这保证了代码的稳定性。 
git flow 的分支模型简单清晰，易于使用。通过本文对 git flow 常用命令的介绍，你可以尽情享受它为我们管理代码带来的方便。

