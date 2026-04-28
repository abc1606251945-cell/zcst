package com.zcst.web.controller.upload;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zcst.common.annotation.Anonymous;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.utils.DateUtils;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.StudentSchedule;
import com.zcst.manage.service.IStudentScheduleService;
import com.zcst.upload.service.FileUploadService;
import com.zcst.upload.service.PythonFileAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class TimetableImportController extends BaseController
{
    private final FileUploadService fileUploadService;

    private final PythonFileAnalysisService pythonFileAnalysisService;

    private final IStudentScheduleService studentScheduleService;

    public TimetableImportController(FileUploadService fileUploadService, PythonFileAnalysisService pythonFileAnalysisService, IStudentScheduleService studentScheduleService)
    {
        this.fileUploadService = fileUploadService;
        this.pythonFileAnalysisService = pythonFileAnalysisService;
        this.studentScheduleService = studentScheduleService;
    }

    @Anonymous
    @PostMapping("/schedule/analyze")
    public AjaxResult uploadScheduleAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam(value = "studentId", required = false) String studentId)
    {
        if (studentId == null || studentId.trim().isEmpty())
        {
            try
            {
                studentId = SecurityUtils.getUsername();
            }
            catch (Exception e)
            {
                return AjaxResult.error("未登录，请传递 studentId 参数或先登录系统");
            }
        }

        if (file.isEmpty())
        {
            return AjaxResult.error("请选择要上传的文件");
        }

        if (file.getSize() > 10 * 1024 * 1024)
        {
            return AjaxResult.error("文件大小不能超过 10MB");
        }

        try
        {
            String url = fileUploadService.uploadScheduleImage(file, studentId);
            Map<String, Object> analysis = pythonFileAnalysisService.submitTimetable(url, studentId, file.getOriginalFilename(), file.getContentType(), file.getSize());

            Object submittedObj = analysis.get("submitted");
            if (!(submittedObj instanceof Boolean) || !((Boolean) submittedObj))
            {
                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("ossUrl", url);
                resp.put("analysis", analysis);
                return AjaxResult.error("调用解析服务失败", resp);
            }

            Object bodyObj = analysis.get("body");
            if (!(bodyObj instanceof JSONObject))
            {
                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("ossUrl", url);
                resp.put("analysis", analysis);
                return AjaxResult.error("解析服务返回非 JSON", resp);
            }

            JSONObject body = (JSONObject) bodyObj;
            Integer code = body.getInteger("code");
            if (code == null || code.intValue() != 0)
            {
                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("ossUrl", url);
                resp.put("analysis", analysis);
                return AjaxResult.error("解析失败：" + body.getString("msg"), resp);
            }

            JSONObject data = body.getJSONObject("data");
            if (data == null)
            {
                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("ossUrl", url);
                resp.put("analysis", analysis);
                return AjaxResult.error("解析服务返回缺少 data", resp);
            }

            JSONArray items = data.getJSONArray("items");
            if (items == null)
            {
                Map<String, Object> resp = new LinkedHashMap<>();
                resp.put("ossUrl", url);
                resp.put("analysis", analysis);
                return AjaxResult.error("解析服务返回缺少 data.items", resp);
            }

            List<StudentSchedule> schedules = new ArrayList<>();
            List<Map<String, Object>> invalidItems = new ArrayList<>();

            for (int i = 0; i < items.size(); i++)
            {
                Object itemObj = items.get(i);
                if (!(itemObj instanceof JSONObject))
                {
                    continue;
                }
                JSONObject item = (JSONObject) itemObj;
                String courseName = item.getString("courseName");
                String location = item.getString("location");
                Date startTime = parseDateFlexible(item.getString("startTime"));
                Date endTime = parseDateFlexible(item.getString("endTime"));

                if (courseName == null || courseName.isBlank() || startTime == null || endTime == null || !endTime.after(startTime))
                {
                    Map<String, Object> invalid = new LinkedHashMap<>();
                    invalid.put("courseName", courseName);
                    invalid.put("location", location);
                    invalid.put("startTime", item.getString("startTime"));
                    invalid.put("endTime", item.getString("endTime"));
                    invalidItems.add(invalid);
                    continue;
                }

                StudentSchedule s = new StudentSchedule();
                s.setStudentId(studentId);
                s.setCourseName(courseName);
                s.setLocation(location);
                s.setStartTime(startTime);
                s.setEndTime(endTime);
                schedules.add(s);
            }

            int inserted = studentScheduleService.replaceStudentSchedule(studentId, schedules);
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("ossUrl", url);
            resp.put("studentId", studentId);
            resp.put("inserted", inserted);
            resp.put("invalidItems", invalidItems);
            return AjaxResult.success("导入成功", resp);
        }
        catch (Exception e)
        {
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    private static Date parseDateFlexible(String value)
    {
        if (value == null || value.isBlank())
        {
            return null;
        }
        Date d = DateUtils.parseDate(value);
        if (d != null)
        {
            return d;
        }
        try
        {
            OffsetDateTime odt = OffsetDateTime.parse(value);
            return Date.from(odt.toInstant());
        }
        catch (Exception ignore)
        {
        }
        try
        {
            LocalDateTime ldt = LocalDateTime.parse(value);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        catch (Exception ignore)
        {
        }
        try
        {
            LocalDateTime ldt = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        catch (Exception ignore)
        {
        }
        return null;
    }
}
