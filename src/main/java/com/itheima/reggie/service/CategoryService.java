package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/14 12:39
 * @explain
 */

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
