package com.leyou.gateway.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;// 公钥路径 D:/rsa/rsa.pub
    private  String cookieName;//LY_TOKEN

    private PublicKey publicKey; // 公钥

   //<bean init >destory
   // 构造方法之后执行
    @PostConstruct
    public void init(){
        try {

            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

        } catch (Exception e) {

            throw new RuntimeException();
        }
    }
    

}