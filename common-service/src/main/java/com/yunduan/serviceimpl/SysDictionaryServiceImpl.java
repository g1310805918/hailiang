package com.yunduan.serviceimpl;

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
        List<DicInitListV> voList = new ArrayList<>();
        //添加死数据
        voList.add(new DicInitListV().setCodeName("硬件平台"));
        voList.add(new DicInitListV().setCodeName("操作系统"));
        voList.add(new DicInitListV().setCodeName("部署方式"));
        for (DicInitListV dicInitListV : voList) {
            String content = "";
            List<SysDictionary> codeName = sysDictionaryMapper.selectList(new QueryWrapper<SysDictionary>().eq("code_name", dicInitListV.getCodeName()));
            if (codeName.size() > 0 && codeName != null) {
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


}
