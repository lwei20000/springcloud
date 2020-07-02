package com.itcast.service;

import com.itcast.client.SearchClient;
import com.itcast.entity.Customer;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CustomerService {

    @Autowired
    private SearchClient searchClient;

    @CacheResult
    @HystrixCommand(commandKey="findById")
    public Customer findById(@CacheKey Integer id) throws InterruptedException {
        return searchClient.findById(id);
    }

    @CacheRemove(commandKey = "findById")
    @HystrixCommand
    public void clearFindById(@CacheKey Integer id) {
        System.out.println("缓存被清空");
    }
}
