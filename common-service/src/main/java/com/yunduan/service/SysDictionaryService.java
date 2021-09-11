package com.yunduan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunduan.entity.SysDictionary;
import com.yunduan.vo.DicInitListV;
import com.yunduan.vo.SysDictionaryListVo;

import java.util.List;

public interface SysDictionaryService extends IService<SysDictionary> {


    /**
     * 获取 硬件平台、操作系统、部署方式
     * @param codeName 标志名称
     * @return list
     */
    List<SysDictionaryListVo> queryOneLevelSysDictionary(String codeName);


    /**
     * 获取标签列表
     * @return list
     */
    List<DicInitListV> queryDictionaryInit();


    /**
     * 添加属性值
     * @param codeName 标签名
     * @param content 内容
     * @return int
     */
    int createSysDictionary(String codeName,String content);


    /**
     * 查询操作系统下的一级子集
     * @param codeName 标签名称
     * @return list
     */
    List<DicInitListV> querySysOperationOneLevelList(String codeName);


    /**
     * 添加版本
     * @param parentId 父id
     * @param codeName 标签名称
     * @param content 内容
     * @return int
     */
    int createBanBen(String parentId,String codeName,String content);


    /**
     * 获取版本信息
     * @param parentId 父id
     * @return list
     */
    List<DicInitListV> querySysBanBenList(String parentId);
}
