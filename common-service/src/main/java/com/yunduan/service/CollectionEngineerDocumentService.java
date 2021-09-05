package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionEngineerDocument;

public interface CollectionEngineerDocumentService extends IService<CollectionEngineerDocument> {


    /**
     * 工程师收藏知识文档
     * @param engineerId 工程师id
     * @param documentId 知识文档id
     * @param favoritesId 收藏夹id
     * @return int
     */
    int engineerCollectionDocument(String engineerId,String documentId,String favoritesId);
}
