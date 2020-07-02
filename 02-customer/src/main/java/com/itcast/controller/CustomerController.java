package com.itcast.controller;

import com.itcast.client.SearchClient;
import com.itcast.entity.Customer;
import com.itcast.service.CustomerService;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private SearchClient searchClient;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer")
    public String customer() {

        System.out.println(Thread.currentThread().getName());

        /*//Eureka直接调用
        // 1 通过eurakaClient获取到SEARCH服务的信息
        InstanceInfo info = eurekaClient.getNextServerFromEureka("SEARCH", false);

        // 2 获取到访问的地址
        String url = info.getHomePageUrl();
        System.out.println(url);

        // 3 通过restTemplate访问
        String result = restTemplate.getForObject(url + "/search", String.class);*/

        /*// Ribbon模式的负载均衡策略是：轮询
        String result = restTemplate.getForObject("http://SEARCH/search", String.class);*/

        String result = searchClient.search();

        // 4 返回
        return result;

    }

    @GetMapping("/customer/{id}")
    @HystrixCommand(fallbackMethod = "findByIdFallBack", commandProperties = {
            @HystrixProperty(name="circuitBreaker.enabled",value="true"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="10"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="70"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="5000")
    })
    public Customer findById(@PathVariable Integer id) throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        //int i = 1/0;
        //Thread.sleep(3000);
        if(id==1) {
            int i= 1/0;
        }

        //
        // return searchClient.findById(id);

        // hystrix请求缓存的实现
        System.out.println(customerService.findById(id));
        System.out.println(customerService.findById(id));
        customerService.clearFindById(id);
        System.out.println(customerService.findById(id));
        System.out.println(customerService.findById(id));
        return customerService.findById(id);


    }

    //findById的降级方法，方法的描述要和方法一致
    public Customer findByIdFallBack(@PathVariable Integer id) {
        return new Customer(-1, "", 0);
    }

    @GetMapping("/getCustomer")
    public Customer findById(@RequestParam Integer id, @RequestParam String name) {
        return searchClient.findById(id,name);
    }

    @GetMapping("/save")             // 会自动转化为Post请求 405
    public Customer save(Customer customer) {
        return searchClient.save(customer);
    }
}
