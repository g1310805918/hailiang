package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineer;
import com.yunduan.mapper.CollectionEngineerMapper;
import com.yunduan.service.CollectionEngineerService;
import com.yunduan.utils.StatusCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CollectionEngineerServiceImpl extends ServiceImpl<CollectionEngineerMapper, CollectionEngineer> implements CollectionEngineerService {

    @Autowired
    private CollectionEngineerMapper collectionEngineerMapper;

    /**
     * 工程师收藏工单id
     * @param engineerId 工程师id
     * @param workOrderId 工单id
     * @return int
     */
    @Override
    public int createCollectionEngineer(Long engineerId, Long workOrderId) {
        CollectionEngineer one = collectionEngineerMapper.selectOne(new QueryWrapper<CollectionEngineer>().eq("engineer_id", engineerId).eq("work_order_id", workOrderId));
        if (one == null) {
            one = new CollectionEngineer().setEngineerId(engineerId).setWorkOrderId(workOrderId);
            int row = collectionEngineerMapper.insert(one);
            return row > 0 ? 1 : -1;
        }else {
            //不为空表示取消收藏
            int row = collectionEngineerMapper.deleteById(one.getId());
            return row > 0 ? 2 : -2;
        }
    }



}
