package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.mapper.CompanyCSIMapper;
import com.yunduan.service.CompanyCSIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CompanyCSIServiceImpl extends ServiceImpl<CompanyCSIMapper, CompanyCSI> implements CompanyCSIService {

    @Autowired
    private CompanyCSIMapper companyCSIMapper;



}
