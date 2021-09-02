package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccount;
import com.yunduan.mapper.CollectionAccountMapper;
import com.yunduan.service.CollectionAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CollectionAccountServiceImpl extends ServiceImpl<CollectionAccountMapper, CollectionAccount> implements CollectionAccountService {


    @Autowired
    private CollectionAccountMapper collectionAccountMapper;


    /**
     * 收藏工单
     * @param accountId 用户id
     * @param workOrderId 工单id
     * @return int
     */
    @Override
    public int createCollectionAccount(Long accountId, Long workOrderId) {
        CollectionAccount one = collectionAccountMapper.selectOne(new QueryWrapper<CollectionAccount>().eq("account_id", accountId).eq("work_order_id", workOrderId));
        if (one == null) {
            one = new CollectionAccount();
            one.setAccountId(accountId).setWorkOrderId(workOrderId);
            int row = collectionAccountMapper.insert(one);
            //添加收藏
            return row > 0 ? 1 : -1;
        }else {
            //取消收藏
            int row = collectionAccountMapper.deleteById(one.getId());
            return row > 0 ? 2 : -2;
        }
    }

}
