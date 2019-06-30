package com.leyou.controller;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import com.leyou.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username")String username, @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response){
        String token=authService.accredit(username,password);
        if (StringUtils.isBlank(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge());
        return ResponseEntity.ok().build();
    }


    @GetMapping("verify")
    public  ResponseEntity<UserInfo> verfiy(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            String newToken=JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),newToken,jwtProperties.getCookieMaxAge());
            return  ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}

