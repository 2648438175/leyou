package com.leyou.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient("user-service")
public interface UserClient extends UserApi {

}
