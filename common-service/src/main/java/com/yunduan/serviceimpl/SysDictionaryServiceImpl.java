package com.yunduan.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.SysDictionary;
import com.yunduan.mapper.SysDictionaryMapper;
import com.yunduan.service.SysDictionaryService;
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



}
