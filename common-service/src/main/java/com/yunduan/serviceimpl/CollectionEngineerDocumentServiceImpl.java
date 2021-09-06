package com.yunduan.serviceimpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.CollectionEngineerDocument;
import com.yunduan.entity.KnowledgeDocumentNoPass;
import com.yunduan.mapper.CollectionEngineerDocumentMapper;
import com.yunduan.mapper.KnowledgeDocumentNoPassMapper;
import com.yunduan.request.front.center.FavoritesReq;
import com.yunduan.service.CollectionEngineerDocumentService;
import com.yunduan.service.KnowledgeDocumentThreeCategoryService;
import com.yunduan.utils.ContextUtil;
import com.yunduan.utils.ExtractRichTextUtil;
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
public class CollectionEngineerDocumentServiceImpl extends ServiceImpl<CollectionEngineerDocumentMapper, CollectionEngineerDocument> implements CollectionEngineerDocumentService {

    @Autowired
    private CollectionEngineerDocumentMapper collectionEngineerDocumentMapper;
    @Autowired
    private KnowledgeDocumentNoPassMapper knowledgeDocumentNoPassMapper;
    @Autowired
    private KnowledgeDocumentThreeCategoryService threeCategoryService;


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


    /**
     * 查询收藏夹下的知识文档列表
     * @param favoritesReq 筛选对象
     * @return map
     */
    @Override
    public Map<String, Object> queryFavoritesInitPage(FavoritesReq favoritesReq) {
        Map<String, Object> map = new HashMap<>();
        //条件构造器
        QueryWrapper<CollectionEngineerDocument> queryWrapper = new QueryWrapper<CollectionEngineerDocument>()
                .eq("engineer_id", ContextUtil.getUserId())
                .eq(StrUtil.hasEmpty(favoritesReq.getFavoritesId()), "favorites_id", null)
                .eq(StrUtil.isNotEmpty(favoritesReq.getFavoritesId()), "favorites_id", favoritesReq.getFavoritesId());

        //收藏夹下的知识文档内容
        List<KnowledgeListVo> voList = new ArrayList<>();
        //收藏夹下的知识文档列表
        List<CollectionEngineerDocument> records = collectionEngineerDocumentMapper.selectPage(new Page<>(favoritesReq.getPageNo(), favoritesReq.getPageSize()), queryWrapper).getRecords();
        if (records.size() > 0 && records != null) {
            KnowledgeListVo vo = null;
            for (CollectionEngineerDocument record : records) {
                KnowledgeDocumentNoPass document = knowledgeDocumentNoPassMapper.selectById(record.getId());
                if (document == null) {
                    continue;
                }
                vo = new KnowledgeListVo().setId(record.getDocumentId().toString()).setDocTitle(document.getDocTitle());
                vo.setDocProfile(ExtractRichTextUtil.dealContent(document.getDocContent())).setCategoryName(threeCategoryService.getKnowledgeCategoryName(document.getThreeCategoryId().toString()));
                voList.add(vo);
            }
        }
        map.put("voList",voList);
        map.put("total", collectionEngineerDocumentMapper.selectCount(queryWrapper));
        return map;
    }


}
