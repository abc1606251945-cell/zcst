package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.utils.DateUtils;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.StudentSchedule;
import com.zcst.manage.service.IStudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/schedule")
public class StudentScheduleController extends BaseController
{
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Autowired
    private IStudentScheduleService studentScheduleService;

    @GetMapping("/my/week")
    @PreAuthorize("@ss.hasRole('student')")
    public AjaxResult getMyWeekSchedule(
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to,
        @RequestParam(value = "days", required = false) Integer days
    )
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty())
        {
            return error("无法获取用户学号");
        }

        Date[] range = resolveRange(from, to, days);
        List<StudentSchedule> list = new ArrayList<>(
            studentScheduleService.selectStudentScheduleByTimeRange(studentId, range[0], range[1])
        );
        list.sort(Comparator.comparing(StudentSchedule::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return success(list);
    }

    private Date parseClientDate(String value)
    {
        String v = String.valueOf(value == null ? "" : value).trim();
        if (v.isEmpty())
        {
            return null;
        }
        try
        {
            long ts = Long.parseLong(v);
            if (ts > 0)
            {
                return new Date(ts);
            }
        }
        catch (Exception ignored)
        {
        }
        try
        {
            Instant instant = Instant.parse(v);
            return Date.from(instant);
        }
        catch (Exception ignored)
        {
        }
        try
        {
            if (v.length() == 10)
            {
                LocalDate ld = LocalDate.parse(v);
                return Date.from(ld.atStartOfDay(ZONE_ID).toInstant());
            }
        }
        catch (Exception ignored)
        {
        }
        return DateUtils.parseDate(v);
    }

    private LocalDate toLocalDate(Date date)
    {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDate();
    }

    private Date startOfDay(LocalDate date)
    {
        return Date.from(date.atStartOfDay(ZONE_ID).toInstant());
    }

    private Date endOfDay(LocalDate date)
    {
        return Date.from(date.atTime(23, 59, 59).atZone(ZONE_ID).toInstant());
    }

    private Date[] resolveRange(String from, String to, Integer days)
    {
        int d = (days == null || days <= 0) ? 7 : days;
        Date fromDate = parseClientDate(from);
        Date toDate = parseClientDate(to);

        LocalDate start;
        LocalDate end;

        if (fromDate == null && toDate == null)
        {
            start = LocalDate.now(ZONE_ID);
            end = start.plusDays(d - 1L);
        }
        else if (fromDate != null && toDate == null)
        {
            start = toLocalDate(fromDate);
            end = start.plusDays(d - 1L);
        }
        else if (fromDate == null)
        {
            end = toLocalDate(toDate);
            start = end.minusDays(d - 1L);
        }
        else
        {
            start = toLocalDate(fromDate);
            end = toLocalDate(toDate);
        }

        if (start.isAfter(end))
        {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        return new Date[] { startOfDay(start), endOfDay(end) };
    }
}
