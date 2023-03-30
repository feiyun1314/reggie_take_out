package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.mapper.OrdersDetailMapper;
import com.itheima.reggie.service.OrdersDetailService;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/30 13:54
 * @explain
 */
@Service
public class OrderDetailServiceImpl  extends ServiceImpl<OrdersDetailMapper, OrderDetail>  implements OrdersDetailService {
}
