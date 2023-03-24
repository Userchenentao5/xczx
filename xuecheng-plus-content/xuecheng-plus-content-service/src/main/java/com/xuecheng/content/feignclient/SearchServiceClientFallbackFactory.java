package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-22 11:04
 **/

@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
  @Override
  public SearchServiceClient create(Throwable throwable) {
    return courseIndex -> {
      log.debug("调用搜索服务时发生熔断，异常信息：{}", throwable.getMessage());
      return null;
    };
  }
}
