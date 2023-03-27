package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/27 18:18
 * @explain
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code= {}"+code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //需要将生产的验证码保存到session中

            session.setAttribute(phone,code);
            return R.error("短信发送成功！");

        }
        return R.error("短信发送失败！");
    }

    /**
     * 移动端用户的登陆
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map user,HttpSession httpSession){
        //获取手机号
        String phone = user.get("phone").toString();
        //获取验证码
        String code = user.get("code").toString();
        //从seesion 中获取验证码
        Object codeinSession = httpSession.getAttribute(phone);
        //进行验证码的比对，（页面提交的code与session中的比对）
        if(codeinSession !=null && code.equals(codeinSession)){
            //如果比对成功说明登录成功
            //判断当前手机号受否为新用户，如果是新用户则直接完成注册
            LambdaQueryWrapper<User> lambdaQueryWrapper =new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User userInfo = userService.getOne(lambdaQueryWrapper);
            if(userInfo == null){
                //如果是新用户则直接完成注册
                userInfo=new User();
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                userService.save(userInfo);

            }
            httpSession.setAttribute("user",userInfo.getId());
            return R.success(userInfo);
        }

        return R.error("登录失败！");
    }

    /**
     * 用户退出
     * @param httpSession
     * @return
     */
    @PostMapping("/loginout")
    public R<String>  loginout(HttpSession httpSession){
        log.info(httpSession.getAttribute("user").toString());
        httpSession.removeAttribute("user");

        return R.success("登出成功！");
    }

}
