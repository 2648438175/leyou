package com.leyou.auth.test;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath="D:/tmp/rsa/rsa.pub";
    private static final String priKeyPath="D:/tmp/rsa/rsa.pri";

    private PublicKey publicKey;
    private PrivateKey privateKey;

//    @Test
//    public void testRsa() throws Exception {
//        RsaUtils.generateKey(pubKeyPath,priKeyPath,"1234");
//    }

    @Before
    public void testGetRsa() throws Exception{
        publicKey=RsaUtils.getPublicKey(pubKeyPath);
        privateKey=RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        String token=JwtUtils.generateToken(new UserInfo(666L,"yumi"),privateKey,5);
        System.out.println(token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token="eyJhbGciOiJSUzI1NiJ9.eyJpZCI6NjY2LCJ1c2VybmFtZSI6Inl1bWkiLCJleHAiOjE1NjE4NjE5NzV9.DRMiifmx3kMkyhSzqgV-hBYXgw0HN03kB0d7BofZTYzEKRYBG-hGMBwFnwGQMMuHrNCyCfYWhNImOUGXHH9tsbwFvhwVo4xu2gf-d8y1rb5hR3GSgAQe5Th24enDflL-fB7hCABnIc6ud51dVwh8z0yeiTgm8_OsjkvhTH3xFZA";
        UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println(infoFromToken);
    }
}
