package com.xuecheng;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-23 17:41
 **/

@SpringBootTest
public class AuthServiceTest {

  @Resource
  private RestTemplate restTemplate;

  @Test
  void test01() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("code", "1243");
    String queryParams = JSON.toJSONString(map);
    ResponseEntity<String> exchange = restTemplate.exchange("http://localhost:63050/media/send-message?phones=123456789&code=alisms&params={queryParams}", HttpMethod.POST, null, String.class,queryParams);
    System.out.println(exchange);
  }
}
