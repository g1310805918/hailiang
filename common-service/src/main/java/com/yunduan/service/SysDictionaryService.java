package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.SysDictionary;
import com.yunduan.vo.SysDictionaryListVo;

import java.util.List;

public interface SysDictionaryService extends IService<SysDictionary> {


    /**
     * 获取 硬件平台、操作系统、部署方式
     * @param codeName 标志名称
     * @return list
     */
    List<SysDictionaryListVo> queryOneLevelSysDictionary(String codeName);
}
