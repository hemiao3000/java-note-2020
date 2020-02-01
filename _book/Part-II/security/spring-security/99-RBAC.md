# Spring Security 实现 RBAC 权限管理

考虑到不是所有的系统的权限管理功能都要用到 **RBAC** 这么『重』的方案，Spring Security 默认的支持的鉴权就只是基于 Role 的判断。如果要进一步细化到 Permission 级别<small>（即完全实现 RBAC 模型）</small>，那么需要自己作一些编码工作。


