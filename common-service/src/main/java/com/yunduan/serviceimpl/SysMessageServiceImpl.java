package com.yunduan.serviceimpl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.SysMessage;
import com.yunduan.mapper.SysMessageMapper;
import com.yunduan.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    @Autowired
    private SysMessageMapper sysMessageMapper;



    /**
     * 初始化消息页面
     * @param title 消息标题
     * @param pageNo 页号
     * @param pageSize 页面大小
     * @return map
     */
    @Override
    public Map<String,Object> initPageData(String title, Integer pageNo, Integer pageSize) {
        Map<String,Object> map = CollectionUtil.newHashMap();
        //条件构造器
        QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<SysMessage>().like(StrUtil.isNotEmpty(title), "title", title).orderByDesc("create_time");
        //消息列表
        List<SysMessage> messageList = sysMessageMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper).getRecords();
        //总记录条数
        Integer total = sysMessageMapper.selectCount(queryWrapper);
        map.put("voList",messageList);
        map.put("total",total);
        return map;
    }


    /**
     * 发布系统公告
     * @param title 系统标题
     * @param content 内容
     * @return SysMessage
     */
    @Override
    public SysMessage createSysMessage(String title, String content) {
        SysMessage message = new SysMessage().setTitle(title).setContent(content);
        int row = sysMessageMapper.insert(message);
        if (row > 0) {
            return message;
        }
        return null;
    }
}
