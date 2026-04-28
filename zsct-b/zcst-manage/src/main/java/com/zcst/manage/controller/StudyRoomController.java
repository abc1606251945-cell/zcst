package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.exception.ServiceException;
import com.zcst.manage.service.IStudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自习场地占用情况查询
 */
@RestController
@RequestMapping("/manage/studyRoom")
public class StudyRoomController extends BaseController
{
    @Autowired
    private IStudyRoomService studyRoomService;

    /**
     * 查询自习场地占用情况。
     *
     * 支持参数：
     * cjid/CJID：场地号，可不传；
     * startDate：查询开始日期，格式 yyyy-MM-dd；
     * endDate：查询结束日期，格式 yyyy-MM-dd。
     */
    @GetMapping("/occupancy")
    public AjaxResult occupancy(@RequestParam Map<String, String> params)
    {
        return success(queryOccupancy(params));
    }

    /**
     * POST 版本，方便后续表单或小程序端复用同一查询逻辑。
     */
    @PostMapping("/occupancy")
    public AjaxResult occupancyByPost(@RequestBody(required = false) Map<String, String> params)
    {
        return success(queryOccupancy(params == null ? Collections.emptyMap() : params));
    }

    private List<Map<String, String>> queryOccupancy(Map<String, String> params)
    {
        String cjid = firstNotBlank(params, "cjid", "CJID");
        LocalDate startDate = parseDate(firstNotBlank(params, "startDate", "START_DATE"), "startDate");
        LocalDate endDate = parseDate(firstNotBlank(params, "endDate", "END_DATE"), "endDate");
        if (endDate.isBefore(startDate)) {
            throw new ServiceException("endDate 不能早于 startDate");
        }
        return studyRoomService.selectStudyRoomOccupancy(cjid, startDate, endDate);
    }

    private LocalDate parseDate(String value, String fieldName)
    {
        if (value == null || value.trim().isEmpty()) {
            throw new ServiceException(fieldName + " 不能为空，格式为 yyyy-MM-dd");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new ServiceException(fieldName + " 格式错误，应为 yyyy-MM-dd");
        }
    }

    private String firstNotBlank(Map<String, String> params, String... keys)
    {
        for (String key : keys) {
            String value = params.get(key);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }
}
