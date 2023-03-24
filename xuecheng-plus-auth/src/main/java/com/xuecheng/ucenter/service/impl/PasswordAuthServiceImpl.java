package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 账号密码认证
 * @date 2022/9/29 12:12
 */
@Service
public class PasswordAuthServiceImpl implements AuthService {

  @Resource
  XcUserMapper xcUserMapper;

  @Resource
  PasswordEncoder passwordEncoder;

  @Resource
  private CheckCodeClient checkCodeClient;

  @Override
  public String getAuthType() {
    return "password";
  }

  @Override
  public XcUserExt execute(AuthParamsDto authParamsDto) {
    //校验验证码
    String checkcode = authParamsDto.getCheckcode();
    String checkcodekey = authParamsDto.getCheckcodekey();

    if(StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
      throw new RuntimeException("验证码为空");

    }
    Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
    if(!verify){
      throw new RuntimeException("验证码输入错误");
    }

    //账号
    String username = authParamsDto.getUsername();
    XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
    if (user == null) {
      //返回空表示用户不存在
      throw new RuntimeException("账号不存在");
    }
    XcUserExt xcUserExt = new XcUserExt();
    BeanUtils.copyProperties(user, xcUserExt);
    //校验密码
    //取出数据库存储的正确密码
    String passwordDb = user.getPassword();
    String passwordForm = authParamsDto.getPassword();
    boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
    if (!matches) {
      throw new RuntimeException("账号或密码错误");
    }
    return xcUserExt;
  }
}
