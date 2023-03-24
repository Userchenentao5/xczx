package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-23 16:16
 **/

@Component
@Slf4j
public class CheckcodeClientFallbackFactory implements FallbackFactory<CheckCodeClient> {
  @Override
  public CheckCodeClient create(Throwable throwable) {
    return (key, code) -> {
      log.debug("调用验证码服务发生异常：{}", throwable.getMessage());
      return null;
    };
  }
}
