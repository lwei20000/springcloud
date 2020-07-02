package com.itcast.servlet;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/hystrix.stream")
public class HystrixServlet extends HystrixMetricsStreamServlet {

}
