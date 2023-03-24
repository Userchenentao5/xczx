package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程预览、发布接口
 * @date 2022/9/16 14:59
 */
public interface CoursePublishService {

    /**
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * 获取课程预览信息
     * @author Mr.M
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     *
     * @param courseId 课程id
     * @author Mr.M
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * @param companyId 机构id
     * @param courseId  课程id
     * @author Mr.M
     */
    void publish(Long companyId, Long courseId);

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @param file 静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public void uploadCourseHtml(Long courseId, File file);

    Boolean saveCourseIndex(long courseId);

    /**获取课程发布信息
     * @param courseId 课程标识
     * @return 课程发布信息
     */
    CoursePublish getCoursePublish(Long courseId);
}
