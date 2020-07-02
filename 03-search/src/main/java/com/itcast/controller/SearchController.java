package com.itcast.controller;

import com.itcast.entity.Custom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;

@RestController
public class SearchController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/search")
    public String search() {
        int i= 1/0;
        return "search: " + port;
    }

    @GetMapping("/search/{id}")
    public Custom findById(@PathVariable Integer id) {
        return new Custom(1,"zhangsan", (int)Math.random()*100000);
    }

    @GetMapping("/getCustomer")
    public Custom findById(@RequestParam Integer id, @RequestParam String name) {
        return new Custom(id,name,23);
    }

    @GetMapping("/save")             // 会自动转化为Post请求 405
    public Custom save(Custom custom) {
        return new Custom();
    }
}
