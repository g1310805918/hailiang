package com.yunduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunduan.entity.KnowledgeDocument;
import com.yunduan.vo.KnowledgeLazySearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeDocumentMapper extends BaseMapper<KnowledgeDocument> {


    /**
     * 模糊搜所知识文档以及文档分类
     * @param searchContent 搜索内容
     * @return list
     */
    List<KnowledgeLazySearchVo> selectKnowledgeLazySearch(@Param("searchContent") String searchContent);


    /**
     * 某搜索包含xxx的id集合
     * @param searchContent 搜索内容
     * @return list
     */
    List<Long> selectDocumentIdList(@Param("searchContent") String searchContent);
}
