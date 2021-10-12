package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionAccountDocument;

public interface CollectionAccountDocumentService extends IService<CollectionAccountDocument> {


    /**
     * 知识文档
     * 收藏/取消收藏
     * @param userId 当前登录用户id
     * @param docId 知识文档id
     * @param favoritesId 收藏夹id
     * @return Int
     */
    int accountCollect(Long userId, Long docId, Long favoritesId);
}
