package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 视频处理任务
 *
 * @author 30952
 */
@Slf4j
@Component
public class VideoTask {

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private MediaFileProcessService mediaFileProcessService;

    //ffmpeg绝对路径
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 视频处理
     */
    @XxlJob("videoJobs")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);

        //一次取出2条记录，可以调整此数据，一次处理的最大个数不要超过cpu核心数
        List<MediaProcess> mediaProcesses = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, Runtime.getRuntime().availableProcessors());

        //数据个数
        int size = mediaProcesses.size();

        log.debug("取出待处理视频记录"+size+"条");
        if (size == 0) {
            return;
        }

        //启动size上线程数量的线程池

        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        mediaProcesses.forEach(mediaProcess -> {
            threadPoolTaskExecutor.execute(()->{
                //桶
                String bucket = mediaProcess.getBucket();
                //存储路径
                String filePath = mediaProcess.getFilePath();
                //原始视频的md5值
                String fileId = mediaProcess.getFileId();
                //原始文件名称

                //下载文件
                //先创建临时文件，为原始的视频文件
                File originalVideo = null;
                //处理结束的mp4文件
                File mp4Video = null;
                try {
                    originalVideo = File.createTempFile("original", null);
                    mp4Video = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    log.error("下载待处理的原始文件前创建临时文件失败:{}，文件信息：{}", e.getMessage(), mediaProcess);
                }
                try {
                    originalVideo = mediaFileService.downloadFileFromMinIO(originalVideo, bucket, filePath);

                    //开始处理视频
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegPath, originalVideo.getAbsolutePath(), mp4Video.getName(), mp4Video.getAbsolutePath());
                    String result = mp4VideoUtil.generateMp4();
                    if(!"success".equals(result)){
                        //记录错误信息
                        log.error("generateMp4 error ,video_path is {},error msg is {}",bucket+filePath,result);
                        mediaFileProcessService.saveProcessFinishStatus(3,fileId,null,result);
                        return ;
                    }
                    //将mp4上传至minio
                    //文件路径
                    String objectName = getFilePath(fileId, ".mp4");
                    mediaFileService.addMediaFilesToMinIO(mp4Video.getAbsolutePath(),bucket,objectName);

                    //访问url
                    String url = "/"+bucket+"/"+objectName;
                    //将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
                    mediaFileProcessService.saveProcessFinishStatus(2, fileId, url, null);
                } catch (Exception e) {
                    log.debug("上传文件出错：{}", e.getMessage());
                }finally {
                    //清理文件
                    if(originalVideo!=null){
                        try {
                            originalVideo.deleteOnExit();
                        } catch (Exception ignored) {

                        }
                    }
                    if (mp4Video != null) {
                        try {
                            mp4Video.deleteOnExit();
                        } catch (Exception ignored) {

                        }
                    }
                }
                countDownLatch.countDown();

            });
        });

        //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);


    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

}
