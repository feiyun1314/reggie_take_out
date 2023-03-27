package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealService;
import com.itheima.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *套餐管理
 * @author feiyun
 * @date 2023/3/23 13:26
 * @explain
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setMealService.saveWithDish(setmealDto);
        log.info("套餐信息：{}",setmealDto.toString());
        return R.success("新增套餐成功！");
    }

    /**
     * 套餐信息管理
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();


        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询setmeal表记录
        setMealService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage);
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collectList = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.getById(item.getCategoryId());
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(collectList);

        return R.success(setmealDtoPage);
    }

    /**
     * 批量修改停售,起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> BatchStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        //修改表 setmeal中stauts状态即可
        //update set status=? from seameal;


        //根据ids来修改status状态，1为起售，0为停售
        for (Long id : ids) {
            Setmeal setmeal=new Setmeal();
            setmeal.setStatus(status);
            setmeal.setId(id);
            setMealService.updateById(setmeal);
        }
        return R.success("修改成功！");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteAll(@RequestParam List<Long> ids){
        setMealService.removeWithDish(ids);
        return R.success("套餐数据删除成功！！！");
    }
}
