package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionAccount;

public interface CollectionAccountService extends IService<CollectionAccount> {


    /**
     * 收藏工单
     * @param accountId 用户id
     * @param workOrderId 工单id
     * @return int
     */
    int createCollectionAccount(Long accountId,Long workOrderId);
}
