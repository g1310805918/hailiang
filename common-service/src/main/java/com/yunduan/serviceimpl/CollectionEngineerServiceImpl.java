package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineer;
import com.yunduan.mapper.CollectionEngineerMapper;
import com.yunduan.service.CollectionEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CollectionEngineerServiceImpl extends ServiceImpl<CollectionEngineerMapper, CollectionEngineer> implements CollectionEngineerService {

    @Autowired
    private CollectionEngineerMapper collectionEngineerMapper;




}
