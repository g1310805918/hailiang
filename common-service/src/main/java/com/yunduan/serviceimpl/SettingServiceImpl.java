package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.Setting;
import com.yunduan.mapper.SettingMapper;
import com.yunduan.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {


    @Autowired
    private SettingMapper settingMapper;

}
