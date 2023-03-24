package com.xuecheng;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-03 10:40
 **/

@SpringBootTest
public class ContentServiceApplicationTests {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseBaseInfoService courseBaseInfoService;

    @Resource
    CourseCategoryService courseCategoryService;

    @Test
    void contextLoads() {
        System.out.println(courseBaseMapper.selectCount(null));
    }

    @Test
    void testCourseBaseService() {
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("测试");
        System.out.println(courseBaseInfoService.queryCourseBaseList(new PageParams(), queryCourseParamsDto));
    }

    @Test
    void testCourseCategoryService() {
        System.out.println(courseCategoryService.queryTreeNodes("1"));
    }
}
