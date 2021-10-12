package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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


    /**
     * 知识文档
     * 收藏/取消收藏
     * @param userId 当前登录用户id
     * @param docId  知识文档id
     * @param favoritesId 收藏夹id
     * @return Int
     */
    @Override
    public int accountCollect(Long userId, Long docId, Long favoritesId) {
        CollectionAccountDocument record = collectionAccountDocumentMapper.selectOne(new QueryWrapper<CollectionAccountDocument>().eq("account_id", userId).eq("document_id", docId));
        if (record != null) {
            //取消收藏
            int row = collectionAccountDocumentMapper.delete(new QueryWrapper<CollectionAccountDocument>().eq("id", record.getId()));
            return row > 0 ? 1 : -1;
        } else {
            //添加收藏
            record = new CollectionAccountDocument().setAccountId(userId).setDocumentId(docId).setCreateTime(DateUtil.now()).setFavoritesId(favoritesId == null ? 0L : favoritesId);
            int row = collectionAccountDocumentMapper.insert(record);
            return row > 0 ? 2 : -2;
        }
    }



}
