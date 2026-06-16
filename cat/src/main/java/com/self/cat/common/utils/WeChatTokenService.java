package com.self.cat.common.utils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeChatTokenService {

    // 1. Remove 'static' so Spring can inject the values.
    // 1. 移除 'static'，这样 Spring 才能注入这些值。
    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String appSecret;

    // 2. Remove 'static' from the method.
    // 2. 移除方法上的 'static'。
    public String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/stable_token";

        // 3. Create the JSON body.
        // 3. 创建 JSON 请求体。
        JSONObject body = new JSONObject();
        body.put("grant_type", "client_credential");
        body.put("appid", appId);
        body.put("secret", appSecret);
        body.put("force_refresh", false);

        // 4. Set headers for JSON.
        // 4. 设置 JSON 请求头。
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        // 5. Send a POST request, not GET.
        // 5. 发送 POST 请求，而不是 GET。

        return restTemplate.postForObject(url, request, String.class);
    }
}
