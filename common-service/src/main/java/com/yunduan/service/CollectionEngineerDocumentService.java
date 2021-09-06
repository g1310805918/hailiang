package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.request.front.center.FavoritesReq;

import java.util.Map;

public interface CollectionEngineerDocumentService extends IService<CollectionEngineerDocument> {


    /**
     * 工程师收藏知识文档
     * @param engineerId 工程师id
     * @param documentId 知识文档id
     * @param favoritesId 收藏夹id
     * @return int
     */
    int engineerCollectionDocument(String engineerId,String documentId,String favoritesId);


    /**
     * 查询收藏夹下的知识文档列表
     * @param favoritesReq 筛选对象
     * @return map
     */
    Map<String,Object> queryFavoritesInitPage(FavoritesReq favoritesReq);
}
