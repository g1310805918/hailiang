package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionFavorites;

import java.util.List;

public interface CollectionFavoritesService extends IService<CollectionFavorites> {


    /**
     * 用户收藏夹列表
     * @param accountId 用户id
     * @param documentId 文档id
     * @return list
     */
    List<CollectionFavorites> queryFavoritesCollectionCount(String accountId,String documentId);

}
