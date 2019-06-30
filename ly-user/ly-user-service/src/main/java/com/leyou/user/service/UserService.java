package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    static final String KEY_PREFIX="user:code:phone:";

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
        }
        return this.userMapper.selectCount(user)!=1;
    }

    public Boolean sendVerifyCode(String phone) {
        String code = NumberUtils.generateCode(4);
        stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
        return true;
    }

    public Boolean register(User user, String code) {
        String storeCode=stringRedisTemplate.opsForValue().get(KEY_PREFIX+user.getPhone());
        if(StringUtils.isNotBlank(storeCode)){
            if(storeCode.equals(code)){
                user.setCreated(new Date());
                String salt = CodecUtils.generateSalt();
                user.setSalt(salt);
                user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
                userMapper.insertSelective(user);
                stringRedisTemplate.delete(KEY_PREFIX+user.getPhone());
                return true;
            }
            return null;
        }
        return null;
    }

    public User queryUser(String username, String password) {
        User user=new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);
        if(user1==null){
            return null;
        }
        String newpass=CodecUtils.md5Hex(password,user1.getSalt());
        if(newpass.equals(user1.getPassword())){
        return user1;
        }
        return null;
    }
}
