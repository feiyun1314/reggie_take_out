package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetMealService;
import com.itheima.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 13:40
 * @explain
 */
@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的管理关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作setmeal insert
        this.save(setmealDto);
        //保存套餐和菜品的关联信息， 操作setmeal_dish insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //需要将SetmealDish中的SetmealId赋值
        //赋值方式将setmealDto的id赋值给SetmealDish中的SetmealId
        List<SetmealDish> collectList = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collectList);



    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //1.首先查询套餐状态，看受否能删除
        //如果状态为1的话不能删除
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            //2.如果不能删除则抛出一个业务异常

            throw new CustomException("套餐商品正售中，不能删除！！！");
        }
        //3.可以删除，先删除setmeal 表记录
        this.removeByIds(ids);
        //4.可以删除，再删除setmeal_dish表信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
