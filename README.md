# jjsoup
**jjsoup** 是基于[jsoup](https://github.com/jhy/jsoup)使用[javassist](https://github.com/jboss-javassist/javassist)提供类似Python [requests.Session()](http://docs.python-requests.org/zh_CN/latest/user/advanced.html)的API从而简化cookie保持
```java
//创建新的Session对象,等价于requests.Session()
Session session = JJsoup.newSession();
//针对Session的统一设置
session.proxy("127.0.0.1", 8888)
        .ignoreContentType(true)
        .timeout(10 * 600);
//与Jsoup.connect()完全相同
session.connect("https://github.com/KingFalse/jjsoup").execute();
//cookie会自动存入session对象中
System.err.println(session.cookies());
```

## Getting started
```xml
<dependency>
    <groupId>me.kagura</groupId>
    <artifactId>jjsoup</artifactId>
    <version>0.2.0</version>
</dependency>
```
## What's New
* 自动识别JSON，如果.requestBody()传入的是JSON则会设置Content-Type为application/json;charset=
* 提供Map<String, Object> ext = session.ext;用于存放临时变量，更方便的构建交互式爬虫
* 完全自动的Cookie保持
