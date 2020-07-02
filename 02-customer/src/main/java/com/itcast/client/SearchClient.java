package com.itcast.client;

import com.itcast.entity.Customer;
import com.itcast.factory.SearchClientFallBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(value="SEARCH",fallback = SearchClientFallback.class)
@FeignClient(value="SEARCH",fallbackFactory = SearchClientFallBackFactory.class)
public interface SearchClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    String search();

    @RequestMapping(value="/search/{id}",method = RequestMethod.GET)
    Customer findById(@PathVariable(value="id") Integer id);

    @RequestMapping(value="/getCustomer",method = RequestMethod.GET)
    Customer findById(@RequestParam(value="id") Integer id, @RequestParam(value="name") String name);

    @RequestMapping(value="/save",method = RequestMethod.GET)            // 会自动转化为Post请求 405
    Customer save(Customer custom);

}
