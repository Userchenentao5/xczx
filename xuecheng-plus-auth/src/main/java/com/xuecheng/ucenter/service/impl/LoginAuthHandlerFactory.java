package com.xuecheng.ucenter.service.impl;

import com.xuecheng.ucenter.service.AuthService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginAuthHandlerFactory implements InitializingBean, ApplicationContextAware {


    private static final
    Map<String, AuthService> LOGIN_AUTH_HANDLER_MAP = new HashMap<>(8);


    private ApplicationContext appContext;


    /**
     * 根据认证类型获取对应的处理器
     *
     * @param authType 认证类型
     * @return 提交类型对应的处理器
     */
    public AuthService getHandler(String authType) {
        return LOGIN_AUTH_HANDLER_MAP.get(authType);
    }


    @Override
    public void afterPropertiesSet() {
        // 将 Spring 容器中所有的 FormSubmitHandler 注册到 FORM_SUBMIT_HANDLER_MAP
        appContext.getBeansOfType(AuthService.class)
                  .values()
                  .forEach(handler -> LOGIN_AUTH_HANDLER_MAP.put(handler.getAuthType(), handler));
    }


    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        appContext = applicationContext;
    }
}
