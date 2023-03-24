package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-03 19:24
 **/

@Slf4j
@Service
public class CourseBaseInfoServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseInfoService {

  @Resource
  private CourseBaseMapper courseBaseMapper;

  @Resource
  private CourseMarketMapper courseMarketMapper;

  @Resource
  private CourseCategoryMapper courseCategoryMapper;

  @Resource
  private CourseMarketService courseMarketService;

  @Override
  public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
    //构建查询条件对象
    LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
    //构建查询条件，根据课程名称查询
    queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
    //构建查询条件，根据课程审核状态查询
    queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
    //构建查询条件，根据课程发布状态查询
    queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

    //分页对象
    Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
    // 查询数据内容获得结果
    Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
    // 获取数据列表
    List<CourseBase> list = pageResult.getRecords();
    // 获取数据总数
    long total = pageResult.getTotal();
    // 构建结果集
    return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

    //合法性校验，采用jsr303框架进行校验
//        if (StringUtils.isBlank(dto.getName())) {
//            XueChengPlusException.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            XueChengPlusException.cast("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            XueChengPlusException.cast("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            XueChengPlusException.cast("适应人群");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            XueChengPlusException.cast("收费规则为空");
//        }
    //新增对象
    CourseBase courseBaseNew = new CourseBase();
    //将填写的课程信息赋值给新增对象
    BeanUtils.copyProperties(dto, courseBaseNew);
    //设置审核状态
    courseBaseNew.setAuditStatus("202002");
    //设置发布状态
    courseBaseNew.setStatus("203001");
    //机构id
    courseBaseNew.setCompanyId(companyId);
    //添加时间
    courseBaseNew.setCreateDate(LocalDateTime.now());
    //插入课程基本信息表
    int insert = courseBaseMapper.insert(courseBaseNew);
    Long courseId = courseBaseNew.getId();

    //课程营销信息
    CourseMarket courseMarketNew = new CourseMarket();
    BeanUtils.copyProperties(dto, courseMarketNew);
    courseMarketNew.setId(courseId);
    //收费规则
//        String charge = dto.getCharge();
//
//        //收费课程必须写价格
//        if ("201001".equals(charge)) {
//            BigDecimal price = dto.getPrice();
//            if (ObjectUtils.isEmpty(price) || price.compareTo(new BigDecimal(0)) <= 0) {
//                XueChengPlusException.cast("收费课程价格不能为空且价格必须大于0");
//            }
//            courseMarketNew.setPrice(dto.getPrice());
//            courseMarketNew.setOriginalPrice(dto.getOriginalPrice());
//        }
//
//        //插入课程营销信息
//        int insert1 = courseMarketMapper.insert(courseMarketNew);
    int insert1 = saveCourseMarket(courseMarketNew);

    if (insert <= 0 || insert1 <= 0) {
      throw new RuntimeException("新增课程基本信息失败");
    }

    //添加成功
    //返回添加的课程信息
    return getCourseBaseInfo(courseId);

  }

  //根据课程id查询课程基本信息，包括基本信息和营销信息
  @Override
  public CourseBaseInfoDto getCourseBaseInfo(long courseId) {

    CourseBase courseBase = courseBaseMapper.selectById(courseId);
    CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

    if (courseBase == null) {
      XueChengPlusException.cast("课程标识异常，不存在对应的课程");
    }
    CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
    BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
    if (courseMarket != null) {
      BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
    }

    //查询分类名称
    CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
    courseBaseInfoDto.setStName(courseCategoryBySt.getName());
    CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
    courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

    return courseBaseInfoDto;
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
  @Override
  public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {

    // 校验
    //课程id
    Long courseId = dto.getId();
    CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
    if (courseBaseUpdate == null) {
      XueChengPlusException.cast("课程不存在");
    }
    // 本机构只能修改本机构发布的课程
    if (!companyId.equals(courseBaseUpdate.getCompanyId())) {
      XueChengPlusException.cast("只允许修改本机构的课程");
    }
    BeanUtils.copyProperties(dto, courseBaseUpdate);

    //更新
    courseBaseUpdate.setChangeDate(LocalDateTime.now());
    courseBaseMapper.updateById(courseBaseUpdate);

    //封装营销信息
    CourseMarket courseMarket = new CourseMarket();
    BeanUtils.copyProperties(dto, courseMarket);

    //收费规则
//        String charge = dto.getCharge();
//
//        //收费课程必须写价格
//        if ("201001".equals(charge)) {
//            BigDecimal price = dto.getPrice();
//            if (ObjectUtils.isEmpty(price)) {
//                throw new XueChengPlusException("收费课程价格不能为空");
//            }
//            courseMarket.setPrice(dto.getPrice());
//        }
//
//        //保存课程营销信息，没有则添加，有则更新
//        courseMarketService.saveOrUpdate(courseMarket);
    saveCourseMarket(courseMarket);

    //返回添加的课程信息
    return getCourseBaseInfo(courseId);
  }

  // 抽取对营销信息的保存
  private int saveCourseMarket(CourseMarket courseMarket) {

    //收费规则
    String charge = courseMarket.getCharge();

    if (StringUtils.isBlank(charge)) {
      XueChengPlusException.cast("收费规则没有选择");
    }

    //收费课程必须写价格
    if ("201001".equals(charge)) {
      BigDecimal price = courseMarket.getPrice();
      if (ObjectUtils.isEmpty(price)) {
        throw new XueChengPlusException("收费课程价格不能为空");
      }
    }

    // 保存
    return courseMarketService.saveOrUpdate(courseMarket) ? 1 : 0;
  }

}
