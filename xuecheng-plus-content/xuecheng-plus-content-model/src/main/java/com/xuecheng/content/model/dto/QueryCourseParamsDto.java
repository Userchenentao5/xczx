package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @description 课程查询参数Dto
 * @author Mr.M
 * @date 2022/9/6 14:36
 * @version 1.0
 */
 @Data
 @ToString
public class QueryCourseParamsDto {

  @ApiModelProperty("审核状态")
  private String auditStatus;

  @ApiModelProperty("课程名称")
  private String courseName;

  @ApiModelProperty("发布状态")
  private String publishStatus;

}
