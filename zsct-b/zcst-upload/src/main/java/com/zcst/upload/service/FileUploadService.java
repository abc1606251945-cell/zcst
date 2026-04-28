package com.zcst.upload.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传单个文件
     *
     * @param file     上传的文件
     * @param basePath 基础路径
     * @return 文件访问 URL
     */
    String uploadFile(MultipartFile file, String basePath);

    /**
     * 上传课表照片（带日期和序号）
     *
     * @param file      上传的文件
     * @param studentId 学生 ID（用于分类）
     * @return 文件访问 URL
     */
    String uploadScheduleImage(MultipartFile file, String studentId);

    /**
     * 获取文件 URL
     *
     * @param filePath 文件路径
     * @return 访问 URL
     */
    String getFileUrl(String filePath);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);
}
