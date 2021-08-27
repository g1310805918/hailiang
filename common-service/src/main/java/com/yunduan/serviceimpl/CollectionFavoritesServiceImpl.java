package com.yunduan.serviceimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.mapper.CollectionAccountDocumentMapper;
import com.yunduan.mapper.CollectionFavoritesMapper;
import com.yunduan.service.CollectionFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CollectionFavoritesServiceImpl extends ServiceImpl<CollectionFavoritesMapper, CollectionFavorites> implements CollectionFavoritesService {

    @Autowired
    private CollectionFavoritesMapper collectionFavoritesMapper;
    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;


    /**
     * 用户收藏夹列表
     * @param accountId 用户id
     * @param documentId 文档id
     * @return list
     */
    @Override
    public List<CollectionFavorites> queryFavoritesCollectionCount(String accountId,String documentId) {
        //用户收藏夹列表
        List<CollectionFavorites> favorites = collectionFavoritesMapper.selectList(new QueryWrapper<CollectionFavorites>().eq("account_id", accountId));
        if (favorites.size() > 0 && favorites != null) {
            for (CollectionFavorites favorite : favorites) {
                //当前收藏夹总数
                Integer count = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", accountId).eq(favorite.getId() != null,"favorites_id", favorite.getId()));
                favorite.setCollectionCount(count);
                //当前文件夹是否收藏
                Integer isCollect = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", accountId).eq(favorite.getId() != null,"favorites_id", favorite.getId()).eq("document_id", documentId));
                favorite.setIsCollect(isCollect > 0 ? 1 : 0);
            }
        }

        //在集合中添加用户默认收藏夹数据
        CollectionFavorites defaultFavorites = new CollectionFavorites();
        defaultFavorites.setFavoritesName("默认收藏夹");

        //默认收藏夹下的收藏数量
        Integer defaultCount = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", accountId).eq("favorites_id", null));
        defaultFavorites.setCollectionCount(defaultCount);

        //默认收藏夹下是否收藏当前知识文档
        Integer defaultIsCollectionCount = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("account_id", accountId).eq("favorites_id", null).eq("document_id",documentId));
        defaultFavorites.setIsCollect(defaultIsCollectionCount > 0 ? 1 : 0);

        //将默认收藏夹加入集合
        favorites.add(defaultFavorites);
        return favorites;
    }



}
