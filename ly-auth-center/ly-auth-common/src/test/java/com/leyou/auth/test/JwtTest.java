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
}
