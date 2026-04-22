package com.zcst.manage.service;

import com.zcst.manage.domain.Vo.DutyApplicationVo;
import com.zcst.manage.domain.dto.SubmitDutyApplicationDTO;

import java.util.List;

public interface IDutyApplicationService
{
    DutyApplicationVo submit(String studentId, SubmitDutyApplicationDTO dto);

    List<DutyApplicationVo> selectMyApplications(String studentId, String status);

    DutyApplicationVo selectMyApplicationDetail(String studentId, String applicationType, Integer id);

    int cancelMyApplication(String studentId, String applicationType, Integer id, String cancelReason);
}
