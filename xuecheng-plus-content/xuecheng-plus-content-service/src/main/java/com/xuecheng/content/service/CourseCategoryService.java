package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-07 19:48
 **/
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 获取课程分类树
     * @param id 根节点标识
     * @return 返回结果
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
