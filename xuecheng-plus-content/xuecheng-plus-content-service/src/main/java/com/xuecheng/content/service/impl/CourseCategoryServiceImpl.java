package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @program: xuecheng-plus-project148
 * @description:
 * @author: 陈恩涛
 * @create: 2023-02-07 19:48
 **/

@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService{

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        // 获取所有的课程分类节点
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        // 返回结果集
        List<CourseCategoryTreeDto> result = new ArrayList<>();

        // 树节点map，节点标识-节点，方便寻找树节点的父节点
        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();

        // 构建树形结构
        courseCategoryTreeDtos.forEach(node -> {
            nodeMap.put(node.getId(), node);

            // 添加一级节点
            if (id.equals(node.getParentid())) {
                result.add(node);
            }

            // 添加二级节点
            CourseCategoryTreeDto parentNode = nodeMap.get(node.getParentid());
            if (parentNode != null) {
                List<CourseCategoryTreeDto> childrenTreeNodes = parentNode.getChildrenTreeNodes();
                if (childrenTreeNodes == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<>());
                }
                parentNode.getChildrenTreeNodes().add(node);
            }
        });

        return result;
    }
}
