package com.self.cat.model.owner.service;

import com.self.cat.model.owner.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.domain.vo.UserProfileVo;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service
* @createDate 2026-05-14 16:13:57
*/
public interface UserService extends IService<User> {

    boolean register(UserRegisterDto user);

    User login(String phone, String password);

    String createJwt(User user);

    UserProfileVo getMyProfile(Long id);
}
