package com.itcast.fallback;

import com.itcast.client.SearchClient;
import com.itcast.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class SearchClientFallback implements SearchClient {
    @Override
    public String search() {
        return "出问题啦。。。";
    }

    @Override
    public Customer findById(Integer id) {
        return null;
    }

    @Override
    public Customer findById(Integer id, String name) {
        return null;
    }

    @Override
    public Customer save(Customer custom) {
        return null;
    }
}
