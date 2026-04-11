package com.zcst.manage.service.impl;

import com.zcst.manage.domain.Major;
import com.zcst.manage.mapper.MajorMapper;
import com.zcst.manage.service.IMajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImpl implements IMajorService
{
    @Autowired
    private MajorMapper majorMapper;

    @Override
    public List<Major> selectMajorList()
    {
        return majorMapper.selectMajorList();
    }
}
