package com.xuecheng.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 搜索服务远程接口
 * @author Mr.M
 * @date 2022/9/20 20:29
 * @version 1.0
 */
 @FeignClient(value = "checkcode", fallbackFactory = CheckcodeClientFallbackFactory.class)
public interface CheckCodeClient {

 @PostMapping(value = "/checkcode/verify")
 public Boolean verify(@RequestParam("key") String key, @RequestParam("code")String code);
}
