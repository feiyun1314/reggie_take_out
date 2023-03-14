package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/8 23:32
 * @explain
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;
    @Override
    public R login(HttpServletRequest request, Employee employee) {
        /**
         * 1.将用户传过来的密码进行加密
         * 2.根据用户传过来的username去查询数据库
         * 3.如果没有查询到则返回失败结果
         * 4.密码比对，不一致返回密码错误
         * 5.查看员工状态，如果是已禁用的话返回员工已禁用
         * 6.登录成功，将员工id存入session并返回成功结果
         */

        // 1.将用户传过来的密码进行加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes(StandardCharsets.UTF_8));
        log.info("password:"+password);
        //2.根据用户传过来的username去查询数据库
        //mybatis plus LambdaQueryWrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        //通过username去查询数据量
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employeebyDb = employeeMapper.selectOne(queryWrapper);

        //3.如果没有查询到则返回失败结果
        if(employeebyDb==null){
            return R.error("登录失败！！！");
        }

        //4.密码比对，不一致返回密码错误
        if(!password.equals(employeebyDb.getPassword())){
            return R.error("密码错误！！！");
        }

        //5.查看员工状态，如果是已禁用的话返回员工已禁用
        if(employeebyDb.getStatus()==0){
            return R.error("该账号已被禁用！！！");
        }

        //6.登录成功，将员工id存入session并返回成功结果
        request.getSession().setAttribute("employee",employeebyDb.getId());

        return R.success(employeebyDb);
    }

    @Override
    public R logout(HttpServletRequest request) {
        /**
         * 1.清理用户ID
         * 2.返回成功信息
         */
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！！！");
    }
}
