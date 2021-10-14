package com.yunduan.serviceimpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionAccountDocument;
import com.yunduan.entity.CollectionFavorites;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.mapper.CollectionAccountDocumentMapper;
import com.yunduan.mapper.CollectionFavoritesMapper;
import com.yunduan.mapper.KnowledgeDocumentMapper;
import com.yunduan.request.front.center.AddFavoritesReq;
import com.yunduan.request.front.center.FavoritesReq;
import com.yunduan.service.CollectionFavoritesService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.vo.KnowledgeListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CollectionFavoritesServiceImpl extends ServiceImpl<CollectionFavoritesMapper, CollectionFavorites> implements CollectionFavoritesService {

    @Autowired
    private CollectionFavoritesMapper collectionFavoritesMapper;
    @Autowired
    private CollectionAccountDocumentMapper collectionAccountDocumentMapper;
    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService knowledgeDocumentThreeCategoryService;


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


    /**
     * 查询用户收藏夹信息
     * @param favoritesReq 查询对象
     * @return list
     */
    @Override
    public Map<String,Object> queryAccountFavoritesVos(FavoritesReq favoritesReq) {
        Map<String,Object> map = new HashMap<>();
        //收藏夹id
        String favoritesId = StrUtil.hasEmpty(favoritesReq.getFavoritesId()) ? "0" : favoritesReq.getFavoritesId();

        //收藏夹下的文档分页记录
        List<CollectionAccountDocument> records = collectionAccountDocumentMapper.selectPage(
                new Page<>(favoritesReq.getPageNo(), favoritesReq.getPageSize()),
                new QueryWrapper<CollectionAccountDocument>()
                        .eq("favorites_id", favoritesId)
                        .eq("account_id", ContextUtil.getUserId())
        ).getRecords();

        //收藏夹下的知识文档内容
        List<KnowledgeListVo> voList = new ArrayList<>();
        if (records.size() > 0 && records != null) {
            KnowledgeListVo vo = null;
            for (CollectionAccountDocument record : records) {
                //查询文档
                KnowledgeDocument document = knowledgeDocumentMapper.selectOne(new QueryWrapper<KnowledgeDocument>().eq("id", record.getDocumentId()));
                if (document == null) {
                    continue;
                }
                vo = new KnowledgeListVo();
                String categoryName = knowledgeDocumentThreeCategoryService.getKnowledgeCategoryName(document.getThreeCategoryId().toString());
                vo.setId(record.getId().toString()).setDocId(record.getDocumentId().toString()).setDocTitle(document.getDocTitle()).setCategoryName(categoryName).setDocProfile(ExtractRichTextUtil.dealContent(document.getDocContent()));
                voList.add(vo);
            }
        }
        Integer total = collectionAccountDocumentMapper.selectCount(new QueryWrapper<CollectionAccountDocument>().eq("favorites_id", favoritesId).eq("account_id", ContextUtil.getUserId()));
        map.put("voList",voList);
        map.put("total",total);
        return map;
    }


    /**
     * 修改收藏夹名称
     * @param id 收藏夹id
     * @param favoritesName 收藏夹名称
     * @return int
     */
    @Override
    public int changeFavoritesName(String id, String favoritesName) {
        //收藏夹
        CollectionFavorites favorites = collectionFavoritesMapper.selectOne(new QueryWrapper<CollectionFavorites>().eq("id", id));
        if (favorites != null) {
            favorites.setFavoritesName(favoritesName);
            return collectionFavoritesMapper.updateById(favorites);
        }
        log.error("收藏夹不存在，传递的收藏夹id = " + id);
        return 0;
    }


    /**
     * 添加收藏夹
     * @param engineerId 工程师id
     * @param addFavoritesReq 收藏夹对象
     * @return int
     */
    @Override
    public int createFavorite(Long engineerId, AddFavoritesReq addFavoritesReq) {
        CollectionFavorites favorites = new CollectionFavorites();
        favorites.setId(SnowFlakeUtil.getPrimaryKeyId()).setAccountId(engineerId).setFavoritesName(addFavoritesReq.getFavoritesName()).setCreateTime(DateUtil.now());
        return collectionFavoritesMapper.insert(favorites);
    }


}
