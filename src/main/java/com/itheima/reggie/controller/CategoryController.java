package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 12:43
 * @explain
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:"+category);
        categoryService.save(category);
        return R.success("新增分类成功！！！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        log.info("page:{}  pageSize:{}",page,pageSize);
        Page<Category> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();
        //添加条件过滤条件
        queryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }

    /**
     * 根据Id来删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        log.info("ids为："+ids);
        //categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("分类删除成功！");

    }

    /**
     * 根据id 来修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> edit(@RequestBody Category category){
        log.info("category:{}"+category.toString());

        categoryService.updateById(category);

        return R.success("分类修改成功！！！");

    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加条件
        if(category.getType()!=null){
            lambdaQueryWrapper.eq(Category::getType,category.getType());
        }
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }

}
