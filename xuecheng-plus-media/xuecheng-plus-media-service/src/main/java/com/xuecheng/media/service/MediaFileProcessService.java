package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件处理业务方法
 * @date 2022/9/10 8:55
 */
public interface MediaFileProcessService {

    /**
     * 将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
     *
     * @param status   处理结果，2:成功3失败
     * @param fileId   文件id
     * @param url      文件访问url
     * @param errorMsg 失败原因
     * @author Mr.M
     */
    public void saveProcessFinishStatus(int status, String fileId, String url, String errorMsg);

    /**
     * 获取待处理任务
     *
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
     * @author Mr.M
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);


}
