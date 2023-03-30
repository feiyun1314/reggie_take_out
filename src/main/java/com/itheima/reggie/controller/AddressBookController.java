package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO
 *
 * @author feiyun
 * @date 2023/3/27 20:08
 * @explain地址铺管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 根据用户Id来查询用户信息
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        log.info("addressBook ：{}",addressBook.toString());

        //获取用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        //查询该用户下所有的地址信息
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        //通过更新时间来排序
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //获取用户Id
        addressBook.setUserId(BaseContext.getCurrentId());
        //将用户信息保存到数据库
        addressBookService.save(addressBook);
        return R.success("新增地址成功！！！");
    }

    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> defaultAddRess(@RequestBody AddressBook addressBook){

        //1获取用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        //2将isDefault字段设为1 1为默认收货地址 其他的都为0
        //使用排他发，先将该用户下的所有地址信息isDefault字段设为0，然后将后台传过来的addressBook对象设为1

        LambdaUpdateWrapper<AddressBook> lambdaQueryWrapper =new LambdaUpdateWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.set(AddressBook::getIsDefault,0);
        // sql update address_book set isdefault=0 where userid=?
        addressBookService.update(lambdaQueryWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);

    }

    /**
     * 根据id查询地址信息
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public R<AddressBook> getById(@PathVariable Long userId){
        log.info("userId:{}",userId);
        AddressBook addressBook = addressBookService.getById(userId);
        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("查询失败！！！！");
    }

    /**
     * 修改信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}",addressBook.toString());
        addressBookService.updateById(addressBook);
        return R.success(addressBook);

    }

    @DeleteMapping
    public R<String> deleteById(@RequestBody AddressBook addressBook){
        addressBookService.removeById(addressBook);
        return R.success("删除成功！！！");
    }

    /**
     * 查询默认收获地址
     * @param
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper =new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(null!=currentId,AddressBook::getUserId,currentId);
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBookServiceOne = addressBookService.getOne(lambdaQueryWrapper);
        if (null!=addressBookServiceOne){
            return R.success(addressBookServiceOne);
        }
        return R.error("该用户没有设置默认地址");
    }

}
