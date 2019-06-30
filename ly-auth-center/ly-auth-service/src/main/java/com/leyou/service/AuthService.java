package com.leyou.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import com.leyou.client.UserClient;
import com.leyou.config.JwtProperties;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private  JwtProperties jwtProperties;

    public String accredit(String username, String password){
        try {
            User user = userClient.queryUser(username, password);
            if (user != null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(user.getId());
                userInfo.setUsername(user.getUsername());
                String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
                return token;
            }
        }catch (Exception e) {
                e.printStackTrace();
        }
        return null;
    }
}
