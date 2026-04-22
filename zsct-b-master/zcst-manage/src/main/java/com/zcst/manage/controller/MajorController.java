package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.manage.domain.Major;
import com.zcst.manage.service.IMajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/major")
public class MajorController extends BaseController
{
    @Autowired
    private IMajorService majorService;

    @GetMapping("/list")
    public AjaxResult list()
    {
        List<Major> list = majorService.selectMajorList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Major m : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("majorId", m.getMajorId());
            item.put("majorName", m.getMajorName());
            item.put("collegeId", m.getCollegeId());
            result.add(item);
        }
        return success(result);
    }
}
