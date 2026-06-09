package com.self.cat.model.owner.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.exception.UserException;
import com.self.cat.common.http.HttpResult;
import com.self.cat.common.utils.UserContext;
import com.self.cat.common.utils.WeChatTokenService;
import com.self.cat.model.owner.domain.User;
import com.self.cat.model.owner.domain.dto.LoginDto;
import com.self.cat.model.owner.domain.dto.LoginWxDto;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理相关")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_KEY = "token:";

    @Value("${wx.appId}")
    private String APP_ID;

    @Value("${wx.appSecret}")
    private String APP_SECRET;

    @Autowired
    private WeChatTokenService weChatTokenService;

    @PostMapping("/checkLoginStatus")
    @Operation(summary = "检查登录状态")
    public HttpResult<Boolean> checkLoginStatus() {

        // Get user ID
        String userId = UserContext.get("id");

        // Check if user ID is empty
        if (userId == null || userId.isEmpty()) {
            return HttpResult.success(false);
        }

        // Check if it exists in Redis
        String redisKey = TOKEN_KEY + userId;
        if (redisTemplate.opsForValue().get(redisKey) == null) {
            return HttpResult.success(false);
        }

        // The token is valid
        return HttpResult.success(true);
    }

    @PostMapping("/userRegister")
    @Operation(summary = "用户注册")
    public HttpResult<String> userRegister(@RequestBody UserRegisterDto user) {
        String confirmPassword = user.getConfirmPassword();
        String password = user.getPassword();
        if (!confirmPassword.equals(password)) {
            throw new UserException(ResultCode.CONFIRM_ERROR.getCode(), ResultCode.CONFIRM_ERROR.getMessage());
        }

        boolean result = userService.register(user);
        if (result) {
            return HttpResult.success("注册成功");
        }

        return HttpResult.error(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }

    @PostMapping("/loginWx")
    @Operation(summary = "微信登录")
    public HttpResult<String> loginWx(@RequestBody LoginWxDto loginWxDto) {
        log.info("appid: {}, appSecret: {}", APP_ID, APP_SECRET);
        String code = loginWxDto.getCode();
        String phoneCode = loginWxDto.getPhoneCode();

        // 验证 code 是否为空
        if (code == null || code.isEmpty()) {
            log.error("微信登录失败：code 为空");
            return HttpResult.error(40029, "无效的 code，请重新获取");
        }

        // 拿着code 换取 openId 如果OpenId 不存在就获取通过phoneCode真实手机号 进行静默注册
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + APP_ID +
                "&secret=" + APP_SECRET +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        log.info("请求微信接口 URL: {}", url);

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Send the request and get the JSON response
            // 发送请求并获取 JSON 响应
            String response = restTemplate.getForObject(url, String.class);
            log.info("微信登录控制器获取到的数据为：{}", response);

            // 检查微信返回的错误
            if (response != null && response.contains("errcode")) {
                log.error("微信接口返回错误: {}", response);
                // 解析错误码
                if (response.contains("40029")) {
                    return HttpResult.error(40029, "无效的 code，可能已过期或已被使用，请重新登录");
                } else if (response.contains("40013")) {
                    return HttpResult.error(40013, "AppID 无效，请检查配置");
                } else if (response.contains("40002")) {
                    return HttpResult.error(40002, "AppSecret 无效，请检查配置");
                }
                return HttpResult.error(500, "微信登录失败: " + response);
            }
            if (response == null){
                return HttpResult.error(500, "微信登录失败: 无法获取微信接口返回数据");
            }
            // 解析 response，获取 openid 和 session_key
            JSONObject wxResponseJson = JSON.parseObject(response);
            String openid = wxResponseJson.getString("openid");
            String session_key = wxResponseJson.getString("session_key");
            log.info("openid: {}, session_key: {}", openid, session_key);

            // 根据 openid 查询用户，不存在则通过 phoneCode 获取手机号并注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getOpenId, openid);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setOpenId(openid);
                // 解析得到用户手机号
                String accessToken = weChatTokenService.getAccessToken();
                log.info("accessToken:{}", accessToken);
                JSONObject accessTokenJson = JSON.parseObject(accessToken);
                accessToken = accessTokenJson.getString("access_token");
                String phoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
                JSONObject phoneRequest = new JSONObject();
                phoneRequest.put("code", phoneCode);
                String phoneResponse = restTemplate.postForObject(phoneUrl, phoneRequest.toString(), String.class);
                log.info("phoneResponse: {}", phoneResponse);
                if (phoneResponse == null) {
                    return HttpResult.error(500, "微信登录失败: 无法获取微信接口返回数据");
                }
                JSONObject phoneResponseJson = JSON.parseObject(phoneResponse);
                if (phoneResponseJson.containsKey("errcode") && phoneResponseJson.getIntValue("errcode") != 0) {
                    log.error("微信接口返回错误: {}", phoneResponse);
                    return HttpResult.error(500, "微信登录失败: " + phoneResponse);
                }
                String phoneNumber = phoneResponseJson.getJSONObject("phone_info").getString("phoneNumber");
                // 2. 根据手机号查询用户 (Find by phone number)
                LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
                phoneQuery.eq(User::getPhone, phoneNumber);
                User existingUser = userService.getOne(phoneQuery);

                if (existingUser != null) {
                    // 用户已存在，仅仅绑定 openid (User already exists, just bind openid)
                    existingUser.setOpenId(openid);
                    existingUser.setUpdateTime(new Date());
                    userService.updateById(existingUser);

                    // 把找到的用户赋值给主对象，用于后续生成 Token (Assign to main object for later Token generation)
                    user = existingUser;
                    log.info("老用户绑定微信成功，手机号: {}", phoneNumber);
                } else {
                    // 数据库中完全没有这个手机号，执行真正的新注册 (Phone number not in database, execute true new registration)
                    user = new User();
                    user.setUsername("尾号【" + phoneNumber.substring(phoneNumber.length() - 4) +"】小主"); // 设置默认用户名为手机号后4位
                    user.setPhone(phoneNumber);
                    user.setOpenId(openid);
                    user.setCreateTime(new Date());
                    user.setUpdateTime(new Date());
                    userService.save(user);
                    log.info("新用户注册成功，手机号: {}", phoneNumber);
                }
            }

            log.info("phoneCode:{}", phoneCode);
            log.info("code:{}", code);
            // 生成 JWT 返回给前端
            String jwt = userService.createJwt(user);

            // 组装JWT返回给前端
            return HttpResult.success(jwt);
            
        } catch (Exception e) {
            log.error("微信登录异常", e);
            return HttpResult.error(500, "微信登录异常: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public HttpResult<String> login(@RequestBody LoginDto login) {
        log.info("登录控制器被执行");
        String phone = login.getPhone();
        String password = login.getPassword();

        // 验证用户是否存在
        User user = userService.login(phone, password);
        if (user == null) {
            return HttpResult.error(ResultCode.LOGIN_USER.getCode(), ResultCode.LOGIN_USER.getMessage());
        }
        String jwt = userService.createJwt(user);
        // 1. Create a unique key for Redis (e.g., "token:12345")
// 1. 为 Redis 创建一个唯一的键（例如："token:12345"）
        String redisKey = TOKEN_KEY + user.getId();

// 2. Save to Redis with an expiration time (e.g., 30 days)
// 2. 存入 Redis 并设置过期时间（例如：30 天）
        redisTemplate.opsForValue().set(redisKey, jwt, 30, TimeUnit.DAYS);

        return HttpResult.success(jwt);
    }
}
