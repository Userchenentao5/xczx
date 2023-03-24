package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Slf4j
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

  @Autowired
  MediaFileService mediaFileService;

  @ApiOperation("媒资列表查询接口")
  @PostMapping("/files")
  public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
    Long companyId = 1232141425L;
    return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

  }

  @ApiOperation("上传文件")
  @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile upload,
                                    @RequestParam(value = "folder", required = false) String folder,
                                    @RequestParam(value = "objectName", required = false) String objectName) {

    String contentType = upload.getContentType();
    Long companyId = 1232141425L;
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    uploadFileParamsDto.setFileSize(upload.getSize());
    if (contentType.contains("image")) {
      //图片
      uploadFileParamsDto.setFileType("001001");
    } else {
      //其它
      uploadFileParamsDto.setFileType("001003");
    }

    uploadFileParamsDto.setRemark("");
    uploadFileParamsDto.setFilename(upload.getOriginalFilename());
    uploadFileParamsDto.setContentType(contentType);

    UploadFileResultDto uploadFileResultDto = null;
    try {
      uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, upload.getBytes(), folder, objectName);
    } catch (IOException e) {
      XueChengPlusException.cast("上传文件发生异常！");
    }
    return uploadFileResultDto;
  }

  @ApiOperation("预览文件")
  @GetMapping("/preview/{mediaId}")
  public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
    return RestResponse.success(mediaFileService.getFileUrlById(mediaId));
  }

  @PostMapping("/send-message")
  public String testMethod(String code, String params, String phones) {
    log.info("测试参数接收");
    return null;
  }

}
