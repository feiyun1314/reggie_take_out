package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.OrdersDetailService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/30 13:58
 * @explain
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersDetailService ordersDetailService;
    @Autowired
    private UserService userService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单明细：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 查询订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(Integer page,Integer pageSize){
        log.info("订单详情：{}"+page+pageSize);
        //获取当前用户id下所有的订单信息
        Long currentId = BaseContext.getCurrentId();
        Page<Orders> pageInfo =new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage =new Page<>();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,currentId);
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage);
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item,ordersDto);
            //根据 oders表中的id来查询oder_detail 表，通过orderid关联  可以查询出多条记录
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId,id);
            List<OrderDetail> list = ordersDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);

    }

    /**
     * 查询订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageByBackend(Integer page, Integer pageSize, Long number, String beginTime,String endTime){
        log.info("订单详情：{}"+page+pageSize+number+beginTime+endTime);
        //获取当前用户id下所有的订单信息
        Page<Orders> pageInfo =new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage =new Page<>();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(number!=null,Orders::getNumber,number);
        lambdaQueryWrapper.ge(beginTime!=null,Orders::getOrderTime,beginTime);
        lambdaQueryWrapper.le(endTime!=null,Orders::getOrderTime,endTime);
        ordersService.page(pageInfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage);

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            User user = userService.getById(item.getUserId());
            ordersDto.setUserName(user.getName());
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);

    }
}
