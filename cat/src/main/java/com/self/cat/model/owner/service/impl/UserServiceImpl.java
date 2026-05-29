package com.self.cat.model.owner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.common.utils.PasswordUtil;
import com.self.cat.model.owner.controller.UserController;
import com.self.cat.model.owner.domain.User;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.service.UserService;
import com.self.cat.model.owner.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-05-14 16:13:57
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secret;

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

    @Override
    public User login(String phone, String password) {
        User one = this.lambdaQuery().eq(User::getPhone, phone).one();
        if (one == null) {
            return null;
        }
        // 验证密码
        boolean checkPassword = PasswordUtil.checkPassword(password, one.getPassword());
        if (!checkPassword) {
            return null;
        }

        return one;
    }

    @Override
    public String createJwt(User user) {

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        int time30day = 1000 * 60 * 60 * 24 * 30;
        Date now = new Date();
        Date expiration = new Date(now.getTime() + time30day);
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("username", user.getUsername());
        claims.put("phone", user.getPhone());
        String compact = Jwts.builder().setClaims(claims).setIssuedAt(now)              // Add start time
                .setExpiration(expiration)     // Add end time
                .signWith(secretKey)                 // Add signature
                .compact();
        return compact;
    }
}




