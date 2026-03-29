package com.zcst.upload.controller;

import com.zcst.common.annotation.Anonymous;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController extends BaseController {

    private final FileUploadService fileUploadService;

    /**
     * 上传课表照片
     * 支持两种模式：
     * 1. 已登录：自动从 token 中获取学号
     * 2. 未登录：需要传递 studentId 参数
     */
    @Anonymous
    @PostMapping("/schedule")
    public AjaxResult uploadSchedule(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "studentId", required = false) String studentId) {
        
        // 如果没有传递 studentId，尝试从登录信息中获取
        if (studentId == null || studentId.trim().isEmpty()) {
            try {
                studentId = SecurityUtils.getUsername();
            } catch (Exception e) {
                return AjaxResult.error("未登录，请传递 studentId 参数或先登录系统");
            }
        }
        
        if (file.isEmpty()) {
            return AjaxResult.error("请选择要上传的文件");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return AjaxResult.error("只能上传图片文件");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return AjaxResult.error("文件大小不能超过 10MB");
        }

        if (studentId == null || studentId.trim().isEmpty()) {
            return AjaxResult.error("学生 ID 不能为空");
        }

        try {
            String url = fileUploadService.uploadScheduleImage(file, studentId);
            return AjaxResult.success("上传成功", url);
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     */
    @Anonymous
    @PostMapping("/file")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("请选择要上传的文件");
        }

        try {
            String url = fileUploadService.uploadFile(file, "uploads/");
            return AjaxResult.success("上传成功", url);
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }
}
