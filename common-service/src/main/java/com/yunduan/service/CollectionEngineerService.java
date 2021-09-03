package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.CollectionEngineer;

public interface CollectionEngineerService extends IService<CollectionEngineer> {


    /**
     * 工程师收藏工单id
     * @param engineerId 工程师id
     * @param workOrderId 工单id
     * @return int
     */
    int createCollectionEngineer(Long engineerId,Long workOrderId);
}
