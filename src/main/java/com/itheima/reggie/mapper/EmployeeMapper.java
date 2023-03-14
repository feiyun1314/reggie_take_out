package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/8 23:31
 * @explain
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
