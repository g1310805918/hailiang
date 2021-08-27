package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.mapper.CollectionAccountDocumentMapper;
import com.yunduan.service.CollectionAccountDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollectionAccountDocumentServiceImpl extends ServiceImpl<CollectionAccountDocumentMapper, CollectionAccountDocument> implements CollectionAccountDocumentService {

    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;





}
