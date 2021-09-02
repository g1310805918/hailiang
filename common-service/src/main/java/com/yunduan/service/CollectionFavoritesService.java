package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.request.front.center.FavoritesReq;

import java.util.List;
import java.util.Map;

public interface CollectionFavoritesService extends IService<CollectionFavorites> {


    /**
     * 用户收藏夹列表
     * @param accountId 用户id
     * @param documentId 文档id
     * @return list
     */
    List<CollectionFavorites> queryFavoritesCollectionCount(String accountId,String documentId);


    /**
     * 查询用户收藏夹信息
     * @param favoritesReq 查询对象
     * @return list
     */
    Map<String,Object> queryAccountFavoritesVos(FavoritesReq favoritesReq);


    /**
     * 修改收藏夹名称
     * @param id 收藏夹id
     * @param favoritesName 收藏夹名称
     * @return int
     */
    int changeFavoritesName(String id,String favoritesName);
}
