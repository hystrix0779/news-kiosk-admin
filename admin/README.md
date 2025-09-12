## 运行

### 环境要求

- Java Graalvm 21+
- Maven 3.6+

### 配置

修改数据库配置，配置文件位于`src/resources/application-local.yml`

以 Mysql 为例，其他数据库适配中...

Mysql(`>=5.7版本`)，建议 8.0，首次启动会自动初始化并导入数据

```yaml
# mysql，驱动已经内置，无需安装
spring:
    datasource:
        url: jdbc:mysql://127.0.0.1:3306/cool?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2b8
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
```

### 启动

注：项目使用到了[Mybatis-Flex 的Apt功能](https://mybatis-flex.com/zh/others/apt.html)，如果启动报错，请先执行`mvn compile`编译

1、启动文件：`src/main/java/com/cool/CoolApplication.java`

2、启动完成后，访问：[http://localhost:8001](http://localhost:8001)
