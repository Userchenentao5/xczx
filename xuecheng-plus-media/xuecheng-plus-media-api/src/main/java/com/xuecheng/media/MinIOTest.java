package com.xuecheng.media;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.compress.utils.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试MinIO
 * @date 2022/9/11 21:24
 */
public class MinIOTest {

  static MinioClient minioClient =
      MinioClient.builder()
          .endpoint("http://192.168.101.65:9000")
          .credentials("minioadmin", "minioadmin")
          .build();

  //上传文件
  public static void upload() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    try {
      boolean found =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket("testbucket").build());
      //检查testbucket桶是否创建，没有创建自动创建
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
      } else {
        System.out.println("Bucket 'testbucket' already exists.");
      }
      //上传1.mp4
      minioClient.uploadObject(
          UploadObjectArgs.builder()
              .bucket("testbucket")
              .object("1.mp4")
              .filename("D:\\utils\\ffmpeg\\玫瑰戒指.mp4")
              .build());
      System.out.println("上传成功");
    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
      System.out.println("HTTP trace: " + e.httpTrace());
    }

  }

  public static void getFile(String bucket, String filepath, String outFile) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    try {

      try (InputStream stream = minioClient.getObject(
          GetObjectArgs.builder()
              .bucket(bucket)
              .object(filepath)
              .build());
           FileOutputStream fileOutputStream = new FileOutputStream(outFile);
      ) {

        // Read data from stream
        IOUtils.copy(stream, fileOutputStream);
        System.out.println("下载成功");
      }

    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
      System.out.println("HTTP trace: " + e.httpTrace());
    }

  }

  public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//    upload();
   getFile("video","e/4/e41c422e370a5eb066beba5765ffd1f3/e41c422e370a5eb066beba5765ffd1f3.avi","D:\\utils\\ffmpeg\\科技.mp4");
  }

}
