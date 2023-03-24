package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程预览数据模型
 * @date 2022/9/16 15:03
 */
@Data
@ToString
public class CoursePreviewDto {

  //课程基本信息,课程营销信息
  CourseBaseInfoDto courseBase;

  //课程计划信息
  List<TeachplanDto> teachplans;

  //师资信息暂时不加...

}
