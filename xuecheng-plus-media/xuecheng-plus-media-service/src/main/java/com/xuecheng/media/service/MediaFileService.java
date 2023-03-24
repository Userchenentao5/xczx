package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.File;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

  /**
   * @description 媒资文件查询方法
   * @param pageParams 分页参数
   * @param queryMediaParamsDto 查询条件
   * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
   * @author Mr.M
   * @date 2022/9/10 8:57
  */
   PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

  /**
   * 上传文件
   *
   * @param companyId           组织机构id
   * @param uploadFileParamsDto 文件信息
   * @param bytes               字节数组
   * @param folder              文件目录，如果不传则默认年、月、日
   * @param objectName          文件名
   * @return com.xuecheng.media.model.dto.UploadFileResultDto 上传文件结果
   */
  UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

  /**
   * 检查文件是否存在
   * @param fileMd5 文件的md5
   * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
   * @author Mr.M
   */
  RestResponse<Boolean> checkFile(String fileMd5);

  /**
   * 检查分块是否存在
   * @param fileMd5  文件的md5
   * @param chunkIndex  分块序号
   * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
   * @author Mr.M
   */
  RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);


  /**
   * 上传分块
   * @param fileMd5  文件md5
   * @param chunk  分块序号
   * @param bytes  文件字节
   * @return com.xuecheng.base.model.RestResponse
   * @author Mr.M
   */
  RestResponse<Boolean> uploadChunk(String fileMd5,int chunk,byte[] bytes);

  /**
   * 合并分块
   *
   * @param companyId           机构id
   * @param fileMd5             文件md5
   * @param chunkTotal          分块总和
   * @param uploadFileParamsDto 文件信息
   * @return com.xuecheng.base.model.RestResponse
   * @author Mr.M
   */
  RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

  /**
   * @description 根据id查询文件url
   * @param id  文件id
   * @return com.xuecheng.media.model.po.MediaFiles 文件信息
   * @author Mr.M
   * @date 2022/9/13 17:47
   */
  String getFileUrlById(String id);

  File downloadFileFromMinIO(File originalVideo, String bucket, String filePath);

  void addMediaFilesToMinIO(String absolutePath, String bucket, String objectName);
}
