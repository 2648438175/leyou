package com.leyou.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;


import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String secret; // 密钥 ly@Login(Auth}*^31)&%

    private String pubKeyPath;// 公钥路径 D:/rsa/rsa.pub

    private String priKeyPath;// 私钥路径 D:/rsa/rsa.pri

    private int expire;// token过期时间 30

    private PublicKey publicKey; // 公钥

    private PrivateKey privateKey; // 私钥

    private  String cookieName;

    private  Integer cookieMaxAge;

   //<bean init >destory
   // 构造方法之后执行
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);// D:/rsa/rsa.pub
            File priKey = new File(priKeyPath);// D:/rsa/rsa.pri
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {

            throw new RuntimeException();
        }
    }
    

}