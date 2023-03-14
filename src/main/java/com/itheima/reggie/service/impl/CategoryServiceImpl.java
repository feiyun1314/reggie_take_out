package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 12:40
 * @explain
 */
@Service
public class CategoryServiceImpl  extends ServiceImpl<CategoryMapper, Category> implements CategoryService   {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;
    /**
     * 根据id 删除分类，删除之前进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据categoryId查询表数据
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //查询当前分类是否关联了菜品，如果已经关联，则抛出一个异常
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count >0){
            //如果已经关联，则抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除！！！");
        }

        //查询当前分类是否关联了套餐，如果已经关联，则抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，用categoryId查询表数据
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int mealCount = setMealService.count(setmealLambdaQueryWrapper);
        if(mealCount >0){
            //如果已经关联，则抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除！！！");
        }
        //正常删除
        super.removeById(id);


    }
}
