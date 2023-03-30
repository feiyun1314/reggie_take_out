package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/30 13:55
 * @explain
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrdersDetailService ordersDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    public void submit(Orders orders) {
        //1.先获取用户id
        Long userId = BaseContext.getCurrentId();
        //2.根据当前用户去查询购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingQueryWrapper=new LambdaQueryWrapper<>();
        shoppingQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(shoppingQueryWrapper);
        if(list.size()==0 ||list==null){
            throw new CustomException("购物车信息为空，不能下单");
        }

        AtomicInteger amount=new AtomicInteger(0);
        //获取随机订单号id
        long id = IdWorker.getId();

        List<OrderDetail> orderDetailList = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setImage(item.getImage());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setName(item.getName());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //3.将订单信息插入订单表 一条记录
        //3.1根据当前获取用户的地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("用户地址为空，不能下单");
        }
        //3.2查询用户信息
        User user = userService.getById(userId);
        //3.3组装orders对象
        orders.setUserId(userId);

        orders.setNumber(String.valueOf(id));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(BigDecimal.valueOf(amount.get()));
        orders.setId(id);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getPhone());
        orders.setStatus(2);
        orders.setUserName(user.getName());
        StringBuffer buffer=new StringBuffer();
        orders.setAddress(buffer.append(addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()).append(
                addressBook.getCityName() == null ? "" : addressBook.getCityName()).append(
                        addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()
                ).append(addressBook.getDetail() == null ? "" :addressBook.getDetail()).toString());

        this.save(orders);

        //4.将订单明细插入订单明细表  多条记录
        ordersDetailService.saveBatch(orderDetailList);
        //5.清空购物车信息
        shoppingCartService.remove(shoppingQueryWrapper);
    }
}
