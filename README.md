![add image](https://github.com/lwei20000/springcloud/raw/master/springcloud.assets/*.png)

# SpringCloud入门手册



# 五、服务的隔离与断路器-hystrix

### 5.1 引言

### 5.2 降级机制实现



### 5.1 引言

### 5.3 线程隔离

> 2 信号量的配置信息
>
> 1. 线程隔离策略：name=‘’   
> 2. 指定信号量的最大请求数：name=`hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests`, value=10

### 5.4 断路器

#### 5.4.1断路器的介绍

> 在调用服务时，如果这个服务的失败率达到一定的阀值，这时将断路器从close状态转化到open状态，指定服务暂时无法被访问，如果你访问就直接走fallback方法。在**一定的时间**内，open状态会转化到半开状态half open，允许一个请求发送到指定服务，如果成功就直接open状态，如果失败就直接close状态。

#### 5.4.2配置断路器的监控界面

> 1、导入依赖
>
> ```java
> <dependency>
>     <groupId>org.springframework.cloud</groupId>
>     <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
> </dependency>
> ```

> 2、在启动类中添加注解
>
> ```
> @EnableHystrixDashboard
> @ServletComponentScan("com.itcast.servlet")
> ```

> 3、配置servlet
>
> ```java
> @WebServlet("/hystrix.stream")
> public class HystrixServlet extends HystrixMetricsStreamServlet {
> }
> 
> 在启动类中添加注解
> ```

> 4、测试
>
> 直接访问 http://localhost:8080/hystrix
>
> 在当前位置直接输入隐射好的servlet路径http://localhost:8080/hystrix.stream

#### 5.4.2配置断路器的属性

> 断路器的属性[Circuit Breaker](https://github.com/Netflix/Hystrix/wiki/Configuration#CommandCircuitBreaker)
>
> 1. 断路器的开关：name=`hystrix.command.default.circuitBreaker.enabled` value=true
> 2. 失败阀值的总请求数：name=`hystrix.command.default.circuitBreaker.requestVolumeThreshold` value=20
> 3. 请求总数失败率的百分比name=`hystrix.command.default.circuitBreaker.errorThresholdPercentage` value=50
> 4. 断路器open状态后，多少秒是拒绝请求的 name=`hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds` value=5000
> 5. 强制让服务拒绝请求：name=`hystrix.command.default.circuitBreaker.forceOpen` value=false
> 6. 强制让服务拒绝请求：name=`hystrix.command.default.circuitBreaker.forceClosed` value=false

### 5.5 请求缓存

#### 5.5.1 请求缓存

> 1. 请求缓存的生命周期是一次请求
> 2. 请求缓存是缓存当前线程中的一个方法，将方法参数作为key，方法的返回结果作为value。
> 3. 在一次请求中，目标方法被调用过一次，以后都会被缓存。

#### 5.5.2 请求缓存的实现

> 1. 创建一个service，在service中调用search服务。
>
>    ```java
>    @Service
>    public class CustomerService {
>    
>        @Autowired
>        private SearchClient searchClient;
>    
>        @CacheResult
>        @HystrixCommand(commandKey="findById")
>        public Customer findById(@CacheKey Integer id) throws InterruptedException {
>            return searchClient.findById(id);
>        }
>    
>        @CacheRemove(commandKey = "findById")
>        @HystrixCommand
>        public void clearFindById(@CacheKey Integer id) {
>            System.out.println("缓存被清空");
>        }
>    }
>    ```
>
> 2. 使用请求缓存的注解
>
>    ```
>    @CacheResult
>    @CacheRemove
>    @CacheKey
>    ```
>
> 3. 修改search模块的返回结果
>
>    ```java
>    return new Custom(1,"zhangsan", (int)Math.random()*100000);
>    ```
>
> 4. 编写Filter，去构建HystrixRequestContext对象
>
>    ```java
>    @WebFilter("/*")
>    public class HystrixRequestContextInitFilter implements Filter {
>    
>        @Override
>        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
>            HystrixRequestContext.initializeContext();
>            filterChain.doFilter(servletRequest,servletResponse);
>        }
>    }
>    ```
>
> 5. 修改controller
>
>    ```java
>    // hystrix请求缓存的实现
>    System.out.println(customerService.findById(id));
>    System.out.println(customerService.findById(id));
>    customerService.clearFindById(id);
>    System.out.println(customerService.findById(id));
>    System.out.println(customerService.findById(id));
>    return customerService.findById(id);
>    ```
>
> 6. 测试结果

# 六、服务网关zuul

### 6.1引言

> 它的角色与nginx有点类似。
>
> 1. 客户端维护大量的ip和port信息，直接访问指定服务
> 2. 认证和授权操作，需要再每一个模块中都添加认证和授权的操作。
> 3. 项目迭代，服务要拆分，服务要合并，需要客户端进行大量的变化。
> 4. 统一吧安全性校验都放在zuul上。

### 6.2 zuul的快速入门

> 9. Router and Filter: Zuul



1、建maven项目，修改为SpringBoot

2、导入依赖

> ```xml
><dependency>
>  <groupId>org.springframework.cloud</groupId>
>  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
>    </dependency>
>    
> <dependency>
>  <groupId>org.springframework.cloud</groupId>
>  <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
>    </dependency>
>    ```

3、添加一个注解

> ```
>@EnableEurekaClient
> @EnableZuulProxy
> ```

4、编写yml配置文件

> ```yml
>#制定eureka服务的地址
> eureka:
> client:
>  service-url:
>      defaultZone: http://root:root@localhost:8761/eureka,http://root:root@localhost:8762/eureka
>    
>    # 制定服务名称
> spring:
> application:
>  name: ZUUL
>   server:
>    port: 80
> ```
>   
> 

4、直接测试

> http://localhost/customer/customer/2

### 6.3 zuul常用配置信息

#### 6.3.1、zuul的监控界面

> 1、导入依赖
>
> ```xml
> <dependency>
>     <groupId>org.springframework.cloud</groupId>
>     <artifactId>spring-cloud-starter-actuator</artifactId>
> </dependency>
> ```



> 2、配置文件
>
> ```yml
> #查看zuul的监控界面
> management:
>   endpoints:
>     web:
>       exposure:
>         include: "*"
> ```



> 3、直接访问
>
> http://localhost/actuator/routes

#### 6.3.2、忽略服务配置

```yml
#配置忽略
zuul:
  #基于服务名忽略服务
  ignored-services: eureka
  #监控界面依然可以查看到，但是访问的时候会404
  ignored-patterns: /**/search/**
```

#### 6.3.3、自定义服务配置

#### 6.3.4、灰度发布

> 1、添加一个配置类
>
> 2、准备一个服务，提供2个版本
>
> 3、测试

### 6.4、zuul的过滤器

> 客户端请求发送到zuul服务商，首先通过PreFilter链，如果正常执行，会把请求再次转发给RoutingFilter，请求转发到一个指定的服务，在制定的服务响应一个结果之后，再次总一个PostFilter过滤器，最终将响应信息交给客户端。

![image-20200702101848887](SpringCloud入门手册.assets/image-20200702101848887.png)

### 6.5、zuul的过滤器

> 1、创建POJO类，继承抽象类ZuulFilter
>
> 2、制定当前过滤器的类型

```java
@Override
public String filterType() {
    return FilterConstants.PRE_TYPE;
}
```

> 3、制定过滤器的执行顺序

```java
@Override
public int filterOrder() {
    return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
}
```

> 4、配置是否启用

```java
@Override
public boolean shouldFilter() {
    return true;
}
```

> 5、制定过滤器中的具体业务代码

```java
@Override
public Object run() throws ZuulException {
    System.out.println("prefix过滤器执行");
    return null;
}
```

> 6、测试

### 6.6、PreFilter实现token校验

> 1、准备访问路径，请求参数传递token
>
> 2、创建AuthenticationFilter

```java
@Component
public class AuthenticationFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //1.获取request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //2.获取token参数
        String token = request.getParameter("token");
        //3.对比token
        if(token==null || !"123".equalsIgnoreCase(token)) {
            //4.token校验失败，直接返回
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
        }
        return null;
    }
}
```

> 3、在run方法中编写具体的业务逻辑代码

```java
@Override
public Object run() throws ZuulException {
    //1.获取request对象
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();
    //2.获取token参数
    String token = request.getParameter("token");
    //3.对比token
    if(token==null || !"123".equalsIgnoreCase(token)) {
        //4.token校验失败，直接返回
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
    }
    return null;
}
```

> 4、测试

### 6.6、zuul的降级

> 1、创建POPO类，实现接口FallBackProvider。（不用单独导入hystrix依赖，以为导入zuul的时候hystrix被依赖进来了）

```java
@Component
public class ZuulFallBack implements FallbackProvider {
```

> 2、重写两个方法

```java
@Component
public class ZuulFallBack implements FallbackProvider {

    // 代表制定全部出现问题的服务，都走这个降级方法
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        System.out.println("降级的服务：" + route);
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.INTERNAL_SERVER_ERROR.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                // 给用户响应的信息
                String msg = "当前服务：" + route + "出现问题了！！！";
                return new ByteArrayInputStream(msg.getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                // 制定响应头信息
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };


    }
}
```

> 3、测试
>
> 

### 6.6、zuul动态路由

> 1、创建一个过滤器

//

> 2、在run方法中编写业务逻辑

```java
@Component
public class DynamicRoutingFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER+2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 1、获取request对象
        //1.获取request对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        // 2、获取参数，rediskey
        String redisKey = request.getParameter("redisKey");
        // 3、直接判断
        if(redisKey==null || !redisKey.equalsIgnoreCase("customer")) {
            context.put(FilterConstants.SERVICE_ID_KEY,"customer-v1");
            context.put(FilterConstants.REQUEST_URI_KEY,"/customer");
        } else if (redisKey==null || !redisKey.equalsIgnoreCase("search")) {
            context.put(FilterConstants.SERVICE_ID_KEY,"search");
            context.put(FilterConstants.REQUEST_URI_KEY,"/search/1");
        }
        return null;
    }
}
```

> 3、测试









# 七、服务间的消息传递



# 九、服务的动态配置 config

### 9.1引言

> 1、配置文件分散在不同的项目中，不方便维护。
>
> 2、配置文件的安全问题。
>
> 3、修改完配置文件，无法立即生效，需要重启。

![image-20200702111201817](SpringCloud入门手册.assets/image-20200702111201817.png)

### 9.2搭建config-server

> 1、创建maven工程，修改为SpringBoot
>
> 2、导入依赖
>
> 3、添加注释
>
> 4、编写配置文件（git的操作）
>
> 5、测试

### 9.2修改Customer链接Config

> 1、导入依赖
>
> 2、编写配置文件
>
> 3、修改配置名称
>
> 4、测试

### 9.3

### 9.4实现动态配置

#### 9.4.1实现原理

![image-20200702113543648](SpringCloud入门手册.assets/image-20200702113543648.png)

#### 9.4.2服务连接RabbitMQ

> 1、导入依赖
>
> 2、编写配置文件连接RabbitMQ信息
>
> 3、测试

#### 9.4.3实现手动刷新

> 1、导入依赖
>
> 2、编写配置文件
>
> 3、为customer添加一个controller
>
> 4、测试

#### 9.4.4内网穿透-netapp软件-隧道

#### 9.4.5实现自动刷新

> 1、配置git中的WebHooks
>
> 2、给Config添加一个过滤器
>
> 3、测试





# 十、服务的追踪-Sleuth

### 10.1引言

> 在整个微服务架构中，微服务是很多的，一个请求需要调用很多很多的服务，最终完成一个业务功能，如果整个功能出现了问题，那么在这么多服务中，如何去定位问题的所在点，出现问题的原因是什么。
>
> 1、Sleuth可以获得到整个服务链路信息。
>
> 2、Zipkin通过童兴华假面去看到信息。
>
> 3、Sleuth讲日志信息存储到数据库中。

![image-20200702115238760](SpringCloud入门手册.assets/image-20200702115238760.png)

### 10.2 Sleuth使用

> 1、导入依赖
>
> 2、编写配置文件
>
> 3、测试

### 10.3 Zipkin的使用

> 1、搭建Zipkin的web工程
>
> 2、导入依赖
>
> 3、编写配置文件
>
> 4、测试

#### 10.4 整合RabbitMQ

> 1、导入RabbitMQ依赖
>
> 2、修改配置文件
>
> 3、测试

#### 10.5 Zipkin存储数据到ES

> 1、重新修改zipkin的yml文件
>
> 2、

# 十一、SpringCloud的总体架构图

![image-20200702120615785](SpringCloud入门手册.assets/image-20200702120615785.png)
