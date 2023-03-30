package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/30 13:52
 * @explain
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
