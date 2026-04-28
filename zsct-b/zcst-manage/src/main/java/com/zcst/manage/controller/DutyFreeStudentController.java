package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.domain.Vo.AvailableStudentVo;
import com.zcst.manage.domain.Vo.FreeStudentVo;
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IVenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/manage/duty/free/student")
public class DutyFreeStudentController extends BaseController
{
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Autowired
    private IDutyScheduleService dutyScheduleService;

    @Autowired
    private IVenueService venueService;

    @GetMapping("/list")
    public AjaxResult list(
        @RequestParam("dutyDate") String dutyDate,
        @RequestParam("period") String period,
        @RequestParam(value = "venueId", required = false) Integer venueId
    )
    {
        if (dutyDate == null || dutyDate.isEmpty()) {
            return error("参数错误：dutyDate 不能为空");
        }
        if (period == null || period.isEmpty()) {
            return error("参数错误：period 不能为空");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dutyDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return error("参数错误：dutyDate 格式应为 yyyy-MM-dd");
        }

        String normalizedPeriod = normalizeDutyPeriod(period);
        String[] parts = normalizedPeriod.split("-");
        if (parts.length != 2) {
            return error("参数错误：period 格式应为 HH:mm-HH:mm 或 1-2节 等");
        }

        int[] startHm = parseHm(parts[0]);
        int[] endHm = parseHm(parts[1]);
        if (startHm == null || endHm == null) {
            return error("参数错误：period 格式应为 HH:mm-HH:mm");
        }

        Date startTime = Date.from(date.atTime(startHm[0], startHm[1]).atZone(ZONE_ID).toInstant());
        Date endTime = Date.from(date.atTime(endHm[0], endHm[1]).atZone(ZONE_ID).toInstant());
        if (!endTime.after(startTime)) {
            return error("参数错误：period 结束时间必须晚于开始时间");
        }

        List<FreeStudentVo> result = new ArrayList<>();

        List<Venue> venues = new ArrayList<>();
        if (venueId != null && venueId > 0) {
            Venue v = venueService.selectVenueByVenueId(Long.valueOf(venueId));
            if (v != null) {
                venues.add(v);
            }
        } else {
            venues.addAll(venueService.selectVenueList(new Venue()));
        }

        for (Venue v : venues) {
            if (v == null || v.getVenueId() == null) {
                continue;
            }
            List<AvailableStudentVo> list = dutyScheduleService.getAvailableStudents(v.getVenueId().intValue(), startTime, endTime);
            for (AvailableStudentVo s : list) {
                FreeStudentVo vo = new FreeStudentVo();
                vo.setStudentId(s.getStudentId());
                vo.setStudentName(s.getName());
                vo.setVenueId(v.getVenueId().intValue());
                vo.setVenueName(v.getVenueName());
                vo.setPeriod(period);
                vo.setDutyDate(dutyDate);
                vo.setStartTimeTs(startTime.getTime());
                vo.setEndTimeTs(endTime.getTime());
                result.add(vo);
            }
        }

        return success(result);
    }

    private static String normalizeDutyPeriod(String period)
    {
        if ("1-2节".equals(period)) return "08:30-10:00";
        if ("3-4节".equals(period)) return "10:00-12:30";
        if ("5-6节".equals(period)) return "12:30-15:10";
        if ("7-8节".equals(period)) return "15:10-18:00";
        if ("9-10节".equals(period)) return "18:00-20:00";
        if ("11-12节".equals(period)) return "20:00-22:00";
        return period;
    }

    private static int[] parseHm(String hm)
    {
        try {
            String[] p = hm.split(":");
            if (p.length != 2) return null;
            int h = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            if (h < 0 || h > 23) return null;
            if (m < 0 || m > 59) return null;
            return new int[] { h, m };
        } catch (Exception e) {
            return null;
        }
    }
}
