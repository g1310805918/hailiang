package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.SysDictionary;
import com.yunduan.mapper.SysDictionaryMapper;
import com.yunduan.service.SysDictionaryService;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.DicInitListV;
import com.yunduan.vo.SysDictionaryListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryMapper, SysDictionary> implements SysDictionaryService {

    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;


    /**
     * 获取 硬件平台、操作系统、部署方式
     * @param codeName 标志名称
     * @return list
     */
    @Override
    public List<SysDictionaryListVo> queryOneLevelSysDictionary(String codeName) {
        List<SysDictionaryListVo> voList = new ArrayList<>();
        //一级数据字典列表
        List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("code_name", codeName).orderByDesc("id"));
        if (sysDictionaries.size() > 0 && sysDictionaries != null) {
            SysDictionaryListVo vo = null;
            for (SysDictionary sysDictionary : sysDictionaries) {
                vo = new SysDictionaryListVo().setId(sysDictionary.getId().toString()).setContent(sysDictionary.getContent());
                voList.add(vo);
            }
        }
        return voList;
    }


    /**
     * 获取标签列表
     * @return list
     */
    @Override
    public List<DicInitListV> queryDictionaryInit() {
        //添加死数据
        List<DicInitListV> voList = CollectionUtil.newArrayList(
                new DicInitListV().setCodeName("硬件平台"),
                new DicInitListV().setCodeName("操作系统"),
                new DicInitListV().setCodeName("部署方式"));
        for (DicInitListV dicInitListV : voList) {
            String content = "";
            List<SysDictionary> codeName = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("code_name", dicInitListV.getCodeName()));
            if (CollectionUtil.isNotEmpty(codeName)) {
                int index = codeName.size();
                for (int i = 0; i < index; i++) {
                    content += codeName.get(i).getContent();
                    if (i != index - 1) {
                        content += "、";
                    }
                }
            }
            dicInitListV.setContent(content);
        }
        return voList;
    }


    /**
     * 添加属性值
     * @param codeName 标签名
     * @param content 内容
     * @return int
     */
    @Override
    public int createSysDictionary(String codeName, String content) {
        SysDictionary sysDictionary = sysDictionaryMapper.selectOne(new QueryWrapper<SysDictionary>().eq("code_name", codeName).eq("content", content));
        if (sysDictionary != null) {
            return StatusCodeUtil.HAS_EXIST;
        }
        sysDictionary = new SysDictionary().setCodeName(codeName).setContent(content);
        return sysDictionaryMapper.insert(sysDictionary);
    }


    /**
     * 查询操作系统下的一级子集
     * @param codeName 标签名称
     * @return list
     */
    @Override
    public List<DicInitListV> querySysOperationOneLevelList(String codeName) {
        //操作系统下的子集
        List<SysDictionary> sysDictionaryList = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("code_name", codeName).orderByDesc("create_time"));
        //封装结果集合
        return getResultList(sysDictionaryList);
    }


    /**
     * 获取封装结果
     * @param sysDictionaryList 数据集合
     * @return list
     */
    private List<DicInitListV> getResultList(List<SysDictionary> sysDictionaryList) {
        List<DicInitListV> voList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sysDictionaryList)) {
            DicInitListV vo = null;
            for (SysDictionary sysDictionary : sysDictionaryList) {
                vo = new DicInitListV().setId(sysDictionary.getId().toString()).setCodeName(sysDictionary.getContent());
                List<SysDictionary> twoLevelList = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("parent_id", sysDictionary.getId()).orderByDesc("create_time"));
                if (CollectionUtil.isNotEmpty(twoLevelList)) {
                    int index = twoLevelList.size();
                    String content = "";
                    for (int i = 0; i < index; i++) {
                        content += twoLevelList.get(i).getContent();
                        if (i != index - 1) {
                            content += "、";
                        }
                    }
                    vo.setContent(content);
                }
                voList.add(vo);
            }
        }
        return voList;
    }



    /**
     * 添加版本
     * @param parentId 父id
     * @param codeName 标签名称
     * @param content 内容
     * @return int
     */
    @Override
    public int createBanBen(String parentId, String codeName, String content) {
        SysDictionary sysDictionary = sysDictionaryMapper.selectOne(new QueryWrapper<SysDictionary>().eq("parent_id", parentId).eq("code_name", codeName).eq("content", content));
        if (sysDictionary != null) {
            return StatusCodeUtil.HAS_EXIST;
        }
        sysDictionary = new SysDictionary().setCodeName(codeName).setContent(content).setParentId(Convert.toLong(parentId));
        return sysDictionaryMapper.insert(sysDictionary);
    }


    /**
     * 获取版本信息
     * @param parentId 父id
     * @return list
     */
    @Override
    public List<DicInitListV> querySysBanBenList(String parentId) {
        //版本集合
        List<SysDictionary> banBenList = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("parent_id", parentId).orderByDesc("create_time"));
        //封装结果
        return getResultList(banBenList);
    }


}
