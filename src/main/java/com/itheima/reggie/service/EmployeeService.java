package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/8 23:32
 * @explain
 */
public interface EmployeeService extends IService<Employee> {
    R login(HttpServletRequest request, Employee employee);

    R logout(HttpServletRequest request);
}
