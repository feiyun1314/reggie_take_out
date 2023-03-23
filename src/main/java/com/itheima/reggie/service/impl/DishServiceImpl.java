package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 13:41
 * @explain
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时插入菜品对应的口味数据  dish dish_flavor
     * @param dishDto
     */

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过lamda表达式的方式给每一个dishID赋值
/*        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());*/

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        //保存菜品口味数据到菜品口味表dish_flavor

        dishFlavorService.saveBatch(flavors);

    }

    //根据Id来查询菜品信息和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //根据Id来查询菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //据Id来查询口味信息 从dish_flavor表中查询
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper =new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }


    @Override
    public void upadteWithFlavor(DishDto dishDto) {
        //1.更新菜品，同时插入菜品对应的口味数据  dish dish_flavor

        //1.1 更新dish表基本信息 dish
        this.updateById(dishDto);
        //1.2 清理当前口味表对应口味数据 dish_flavor delelte
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //1.3 添加当前提交过来的口味信息数据 dish_flavor insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过lamda表达式的方式给每一个dishID赋值
            flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
         }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

}
