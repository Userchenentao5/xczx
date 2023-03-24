package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-20 20:41
 **/

@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

  @Override
  public MediaServiceClient create(Throwable throwable) {
    return (upload, folder, objectName) -> {
      log.debug("调用媒资管理服务上传文件时发生熔断，异常信息：{}", throwable.getMessage());
      return null;
    };
  }
}
