package com.zcst.upload.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.zcst.upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传服务实现类
 * 通过查询 OSS 上当天已有文件数量来实现序号持久化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileStorageService fileStorageService;

    @Value("${dromara.x-file-storage.aliyun-oss[0].access-key:}")
    private String accessKey;

    @Value("${dromara.x-file-storage.aliyun-oss[0].secret-key:}")
    private String secretKey;

    @Value("${dromara.x-file-storage.aliyun-oss[0].end-point:}")
    private String endpoint;

    @Value("${dromara.x-file-storage.aliyun-oss[0].bucket-name:}")
    private String bucketName;

    @Value("${dromara.x-file-storage.aliyun-oss[0].base-path:test/}")
    private String basePath;

    @Value("${dromara.x-file-storage.aliyun-oss[0].domain:}")
    private String domain;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 缓存当天的文件数量和已使用的序号
    private String cachedDate = null;
    private Set<Integer> usedSequences = new HashSet<>();

    @Override
    public String uploadFile(MultipartFile file, String basePath) {
        try {
            // 添加日志，查看文件信息
            log.info("开始上传文件 - 文件名：{}, 类型：{}, 大小：{}", 
                     file.getOriginalFilename(), 
                     file.getContentType(),
                     file.getSize());
            
            // 直接上传
            FileInfo fileInfo = fileStorageService.of(file)
                    .setPath(basePath)
                    .upload();

            if (fileInfo == null) {
                throw new RuntimeException("文件上传失败");
            }

            log.info("文件上传成功：{}", fileInfo.getUrl());
            return fileInfo.getUrl();

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }

    @Override
    public String uploadScheduleImage(MultipartFile file, String studentId) {
        try {
            // 添加日志，查看文件信息
            log.info("开始上传课表照片 - 文件名：{}, 类型：{}, 大小：{}, 学号：{}", 
                     file.getOriginalFilename(), 
                     file.getContentType(),
                     file.getSize(),
                     studentId);
            
            String datePath = LocalDate.now().format(DATE_FORMATTER);

            String filename = generateSequentialFilename(datePath, file.getOriginalFilename());

            // basePath 已经在 application.yml 中配置为 timetable/
            // 所以这里只需要拼接相对路径（只包含日期，不包含学号）
            String fullPath = "schedule/" + datePath + "/";

            log.info("上传路径：{}", fullPath);
            log.info("保存文件名：{}", filename);

            // 直接上传，不需要指定 platform（会自动使用 default-platform）
            // setPath 设置目录路径（以/结尾），setSaveFilename 设置文件名
            FileInfo fileInfo = fileStorageService.of(file)
                    .setPath(fullPath)
                    .setSaveFilename(filename)  // 指定保存的文件名
                    .upload();

            if (fileInfo == null) {
                throw new RuntimeException("文件上传失败");
            }

            log.info("课表照片上传成功：{}", fileInfo.getUrl());
            return fileInfo.getUrl();

        } catch (Exception e) {
            log.error("课表照片上传失败", e);
            throw new RuntimeException("课表照片上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 生成顺序文件名（带序号）
     * 格式：{日期}{4 位序号}.{扩展名}
     * 例如：202603290001.jpg
     * 
     * 实现方式：查询 OSS 上当天已有文件数量，然后找到下一个可用的序号
     */
    private String generateSequentialFilename(String datePath, String originalFilename) {
        // 查询 OSS 上当天已有文件，获取已使用的序号
        Set<Integer> existingSequences = queryTodayFileSequences(datePath);

        // 找到下一个可用的序号（从 1 开始）
        int sequence = 1;
        while (existingSequences.contains(sequence)) {
            sequence++;
        }

        String extension = getExtension(originalFilename);
        String dateStr = LocalDate.now().format(FILE_DATE_FORMATTER);
        String timestamp = dateStr + String.format("%04d", sequence);

        log.info("生成文件名：日期路径={}, 序号={}, 文件名={}", datePath, sequence, timestamp + "." + extension);

        // 更新缓存
        updateCache(dateStr, sequence);

        return timestamp + "." + extension;
    }

    /**
     * 查询 OSS 上当天已上传文件的序号
     * 
     * @param datePath 日期路径，格式：yyyy/MM/dd
     * @return 已使用的序号集合
     */
    private Set<Integer> queryTodayFileSequences(String datePath) {
        String today = LocalDate.now().format(FILE_DATE_FORMATTER);
        
        // 检查缓存是否有效
        if (today.equals(cachedDate) && !usedSequences.isEmpty()) {
            log.debug("从缓存获取当天文件序号，数量：{}", usedSequences.size());
            return new HashSet<>(usedSequences);
        }
        
        Set<Integer> sequences = new HashSet<>();
        
        try {
            // 创建 OSS 客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
            
            // 构建前缀路径：{base-path}schedule/{yyyy/MM/dd}/
            String prefix = basePath + "schedule/" + datePath + "/";
            
            log.info("查询 OSS 文件列表，prefix: {}", prefix);
            
            // 列出指定前缀的所有对象
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(prefix)
                    .withMaxKeys(1000); // 最多获取 1000 个文件
            
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            
            // 遍历所有文件，提取序号
            objectListing.getObjectSummaries().forEach(objectSummary -> {
                String key = objectSummary.getKey();
                log.debug("发现文件：{}", key);
                
                // 从文件路径中提取日期和序号
                // 格式：{base-path}schedule/{studentId}/{yyyy/MM/dd}/{yyyyMMdd}{序号}.{ext}
                if (key.contains(datePath)) {
                    String fileName = key.substring(key.lastIndexOf('/') + 1);
                    Integer seq = extractSequenceFromFileName(fileName, today);
                    if (seq != null) {
                        sequences.add(seq);
                        log.debug("从文件 {} 提取序号：{}", fileName, seq);
                    }
                }
            });
            
            // 处理分页（如果文件超过 1000 个）
            while (objectListing.isTruncated()) {
                String nextMarker = objectListing.getNextMarker();
                listObjectsRequest.setMarker(nextMarker);
                objectListing = ossClient.listObjects(listObjectsRequest);
                
                objectListing.getObjectSummaries().forEach(objectSummary -> {
                    String key = objectSummary.getKey();
                    if (key.contains(datePath)) {
                        String fileName = key.substring(key.lastIndexOf('/') + 1);
                        Integer seq = extractSequenceFromFileName(fileName, today);
                        if (seq != null) {
                            sequences.add(seq);
                        }
                    }
                });
            }
            
            ossClient.shutdown();
            
            log.info("查询到当天已有 {} 个文件，最大序号：{}", sequences.size(), 
                     sequences.isEmpty() ? 0 : sequences.stream().max(Integer::compareTo).get());
            
            // 更新缓存
            this.cachedDate = today;
            this.usedSequences = sequences;
            
        } catch (Exception e) {
            log.error("查询 OSS 文件列表失败，使用默认值", e);
            // 查询失败时，使用缓存或从 1 开始
            if (today.equals(cachedDate)) {
                return new HashSet<>(usedSequences);
            }
        }
        
        return sequences;
    }

    /**
     * 从文件名中提取序号
     * 文件名格式：{yyyyMMdd}{4 位序号}.{扩展名}
     * 例如：202603290001.jpg -> 1
     * 
     * @param fileName 文件名
     * @param today 今天的日期（yyyyMMdd）
     * @return 序号，如果格式不匹配返回 null
     */
    private Integer extractSequenceFromFileName(String fileName, String today) {
        try {
            // 去掉扩展名
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                fileName = fileName.substring(0, lastDot);
            }
            
            // 检查是否以今天的日期开头
            if (fileName.startsWith(today)) {
                // 提取序号部分（日期后面的 4 位数字）
                String seqStr = fileName.substring(today.length());
                if (seqStr.length() == 4 && seqStr.matches("\\d{4}")) {
                    return Integer.parseInt(seqStr);
                }
            }
        } catch (Exception e) {
            log.debug("从文件名提取序号失败：{}", fileName, e);
        }
        return null;
    }

    /**
     * 更新缓存
     */
    private void updateCache(String dateStr, int sequence) {
        if (dateStr.equals(cachedDate)) {
            usedSequences.add(sequence);
        } else {
            cachedDate = dateStr;
            usedSequences = new HashSet<>();
            usedSequences.add(sequence);
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }

    @Override
    public String getFileUrl(String filePath) {
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }

        if (domain != null && !domain.isEmpty()) {
            return domain + filePath;
        }

        return filePath;
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            // 直接使用文件路径删除
            fileStorageService.delete(filePath);
            log.info("文件删除成功：{}", filePath);
        } catch (Exception e) {
            log.error("文件删除失败：{}", filePath, e);
            throw new RuntimeException("文件删除失败：" + e.getMessage(), e);
        }
    }
}
