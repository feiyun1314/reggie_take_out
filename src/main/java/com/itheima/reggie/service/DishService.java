package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 13:38
 * @explain
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据  dish dish_flavor
    @Transactional
    public void saveWithFlavor(DishDto dishDto);

    //根据Id来查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品，同时插入菜品对应的口味数据  dish dish_flavor
    @Transactional
    public void upadteWithFlavor(DishDto dishDto);

}
