package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/27 18:15
 * @explain
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
