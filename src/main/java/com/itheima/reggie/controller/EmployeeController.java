package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/8 23:36
 * @explain
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 需要将id存入session中
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        /**
         * 1.将用户传过来的密码进行加密
         * 2.根据用户传过来的username去查询数据库
         * 3.如果没有查询到则返回失败结果
         * 4.密码比对，不一致返回密码错误
         * 5.查看员工状态，如果是已禁用的话返回员工已禁用
         * 6.登录成功，将员工id存入session并返回成功结果
         */
        R r =employeeService.login(request,employee);
        return  r;
    }

    /**
     * 员工退出
     * @param request 需要将id存入session中
     * @param
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){


        R r =employeeService.logout(request);
        return  r;
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始密码 123456 md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //使用公共字段自动填充   MyMetaObjectHandler
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        log.info("传入的员工信息：{}",employee.toString());
        return R.success("新增员工成功！");
    }

    /**
     *员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> findAll(int page, int pageSize, String name){
        log.info("page = {},pageSize = {} +name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     *根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info("员工信息："+employee.toString());
        Long empById = (Long)request.getSession().getAttribute("employee");

        //记录当前线程id
        long id = Thread.currentThread().getId();
        log.info("当前线程Id为：{}",id);

        //使用公共字段自动填充   MyMetaObjectHandler
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empById);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public  R<Employee> getById(@PathVariable Long id ){
        log.info("根据ID查询用户信息");
        Employee employee = employeeService.getById(id);
        if(employee !=null){
            return R.success(employee);
        }
        return R.error("没有查询到该员工！");
    }
}
