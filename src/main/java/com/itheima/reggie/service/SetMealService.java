package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 13:39
 * @explain
 */
public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的管理关系
     * @param setmealDto
     */
    @Transactional
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 批量删除
     * @param ids
     */
    @Transactional
    void removeWithDish(List<Long> ids);
}
