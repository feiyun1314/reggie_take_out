package com.itheima.reggie.controller;

import com.itheima.reggie.service.OrdersDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/30 13:56
 * @explain
 */
@RestController
@Slf4j
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrdersDetailService ordersDetailService;
}
