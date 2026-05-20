package com.self.cat.model.owner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.common.utils.PasswordUtil;
import com.self.cat.model.owner.controller.UserController;
import com.self.cat.model.owner.domain.User;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.service.UserService;
import com.self.cat.model.owner.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-05-14 16:13:57
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(UserRegisterDto user) {
        User u = new User();
        Date now = new Date();
        u.setUsername(user.getUsername());
        String hashPassword = PasswordUtil.hashPassword(user.getConfirmPassword());
        u.setPassword(hashPassword);
        u.setPhone(user.getPhone());
        u.setCreateTime(now);
        u.setUpdateTime(now);
        int insert = userMapper.insert(u);
        return insert > 0;
    }
}




