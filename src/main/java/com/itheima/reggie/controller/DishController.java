package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/22 16:06
 * @explain菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        log.info(dishDto.toString());
        return R.success("新增菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //添加分页构造器对象
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage =new Page<>();

        //条件过滤器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);

        //属性拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //对象拷贝，给dishDto赋值
            BeanUtils.copyProperties(item, dishDto);
            //获取CategoryID来查询category表获取name属性
            Long categoryId = item.getCategoryId();
            //查询category表信息
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);

    }

    /**
     *根据Id来查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("根据ID查询用户信息");
        //根据id查询用户信息
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto!=null){
            return R.success(dishDto);
        }
        return null;
    }

    /**
     * 修改菜品信息和对应的口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("dishDto"+dishDto.toString());
        dishService.upadteWithFlavor(dishDto);
        return R.success("保存成功");
    }

   /* *//**
     * 批量停售
     * @param ids
     * @return
     *//*
    @PostMapping("/status/0")
    public  R<String> updateByIds1(Long[] ids){

        log.info("ids:"+ids.length);
        //根据ids来修改status状态，1为起售，0为停售
        for (Long id : ids) {
            Dish dish=new Dish();
            dish.setStatus(0);
            dish.setId(id);
            dishService.updateById(dish);
        }
        return R.success("修改成功！");
    }
*/

    /**
     * 批量起售停售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public  R<String> updateByIds2(@PathVariable Integer status, Long[] ids){

        log.info("ids:"+ids.length);
        //根据ids来修改status状态，1为起售，0为停售
        for (Long id : ids) {
            Dish dish=new Dish();
            dish.setStatus(status);
            dish.setId(id);
            dishService.updateById(dish);
        }
        return R.success("修改成功！");
    }

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long[] ids){
        //根据id 删除菜品信息和对应的口味信息
        for (Long id : ids) {
            //根据id 删除菜品信息
            dishService.removeById(id);
            //根据id口味信息
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(lambdaQueryWrapper);
        }
        return R.success("删除成功！！！！");
    }

}