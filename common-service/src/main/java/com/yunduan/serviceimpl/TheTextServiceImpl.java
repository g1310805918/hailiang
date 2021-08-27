package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.TheText;
import com.yunduan.mapper.TheTextMapper;
import com.yunduan.service.TheTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TheTextServiceImpl extends ServiceImpl<TheTextMapper, TheText> implements TheTextService {

    @Autowired
    private TheTextMapper theTextMapper;


}
