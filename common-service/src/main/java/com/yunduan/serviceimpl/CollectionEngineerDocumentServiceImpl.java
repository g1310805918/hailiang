package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.service.CollectionEngineerDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CollectionEngineerDocumentServiceImpl extends ServiceImpl<CollectionEngineerDocumentMapper, CollectionEngineerDocument> implements CollectionEngineerDocumentService {

    @Autowired
    private CollectionEngineerDocumentMapper collectionEngineerDocumentMapper;


    /**
     * 工程师收藏知识文档
     * @param engineerId 工程师id
     * @param documentId 知识文档id
     * @param favoritesId 收藏夹id
     * @return int
     */
    @Override
    public int engineerCollectionDocument(String engineerId, String documentId,String favoritesId) {
        //收藏记录
        CollectionEngineerDocument one = collectionEngineerDocumentMapper.selectOne(new QueryWrapper<CollectionEngineerDocument>().eq("engineer_id", engineerId).eq("document_id", documentId));
        if (one != null) {
            //取消收藏
            int row = collectionEngineerDocumentMapper.deleteById(one.getId());
            return row > 0 ? 2 : -2;
        }else {
            //收藏
            one = new CollectionEngineerDocument();
            //工程师id、文档id、收藏夹id为空表示默认收藏夹
            one.setEngineerId(Convert.toLong(engineerId)).setDocumentId(Convert.toLong(documentId)).setFavoritesId(StrUtil.hasEmpty(favoritesId) ? null : Convert.toLong(favoritesId));
            int row = collectionEngineerDocumentMapper.insert(one);
            return row > 0 ? 1 : -1;
        }
    }


}
