Base_Module
===============

**base**：是一个基于MVP+RxJava的的基础框架，它承载了MVP的设计风格，我们的上层APP只需要继承它的BaseActivity、BaseFragment、BaseModel 、IBaseView 、BasePresenter 就可以了。上层要做的仅仅只是往对应的层填充独有的业务。  

**common**：是一个通用的工具集，里面集成了一些常用的第三方库，包括： 
 
* 网络库：网络采用[Retrofit2](https://github.com/square/retrofit) + [OkHttp](https://github.com/square/okhttp) + [RxJava](https://github.com/ReactiveX/RxJava) + [RxLifecycle](https://github.com/trello/RxLifecycle)，其中RxLifecycle是为了解决RxJava使用时内存泄露问题。  参考博文：
[Retrofit + RxJava ＋ OkHttp 让网络请求变的简单](https://www.jianshu.com/p/5bc866b9cbb9)
[Retrofit用法详解](http://duanyytop.github.io/2016/08/06/Retrofit%E7%94%A8%E6%B3%95%E8%AF%A6%E8%A7%A3/)
[给 Android 开发者的 RxJava 详解](https://gank.io/post/560e15be2dca930e00da1083) 
* 图库：图片加载使用[Glide](https://github.com/bumptech/glide)，对外提供已封装好的ETNetImageView，这样即使以后更换图片加载库也不影响上层APP的调用。配合transform支持圆角和圆形图片展示。参考文章：
[Glide官方使用说明](https://muyangmin.github.io/glide-docs-cn/)  
* 数据库：[greenDAO](https://github.com/greenrobot/greenDAO)，是一个高效的数据库访问ORM框架（对象关系映射），节省了自己编写SQL的时间，快速的增删查改等操作。但是其有个问题，数据库升级时会默认删除旧表，重新生成新表，造成数据丢失，所以需要在升级时备份数据并迁移。具体可参看[GreenDaoUpgradeHelper](https://github.com/yuweiguocn/GreenDaoUpgradeHelper)。  
* 事件总线：[EventBus](https://github.com/greenrobot/EventBus)  
* 依赖注入：[butterknife](https://github.com/JakeWharton/butterknife)  
* 权限管理：[RxPermissions](https://github.com/tbruyelle/RxPermissions)  
* 其他工具类：如埋点统计、日志管理、沉浸式、侧滑退出等。