package com.yunduan.serviceimpl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunduan.entity.AccountMsg;
import com.yunduan.mapper.AccountMsgMapper;
import com.yunduan.request.front.message.InitListReq;
import com.yunduan.service.AccountMsgService;
import com.yunduan.utils.StatusCodeUtil;
import com.yunduan.vo.AccountMessageListVo;
import com.yunduan.vo.MsgDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AccountMsgServiceImpl extends ServiceImpl<AccountMsgMapper, AccountMsg> implements AccountMsgService {

    @Autowired
    private AccountMsgMapper accountMsgMapper;


    /**
     * 用户消息列表
     * @param userId 用户id
     * @param initListReq 筛选对象
     * @return map
     */
    @Override
    public Map<String,Object> accountMsgListVo(Long userId, InitListReq initListReq) {
        Map<String,Object> map = new HashMap<>();
        List<AccountMessageListVo> voList = new ArrayList<>();
        //用户消息列表
        List<AccountMsg> accountMsgList = accountMsgMapper.selectPage(
                new Page<>(initListReq.getPageNo(), initListReq.getPageSize()),
                new QueryWrapper<AccountMsg>()
                        .eq("account_id", userId)
                        .eq(initListReq.getMessageType() != null,"msg_type",initListReq.getMessageType())
        ).getRecords();

        if (accountMsgList.size() > 0 && accountMsgList != null) {
            AccountMessageListVo vo = null;
            for (AccountMsg msg : accountMsgList) {
                vo = new AccountMessageListVo();
                vo.setId(msg.getId().toString()).setCreateTime(msg.getCreateTime()).setMessageTitle(msg.getMsgTitle()).setMessageType(msg.getMsgType());
                voList.add(vo);
            }
        }
        //总消息数
        Integer total = accountMsgMapper.selectCount(new QueryWrapper<AccountMsg>().eq("account_id", userId).eq(initListReq.getMessageType() != null, "msg_type", initListReq.getMessageType()));
        //未读数量
        Integer noReadTotal = accountMsgMapper.selectCount(new QueryWrapper<AccountMsg>().eq("account_id", userId).eq(initListReq.getMessageType() != null, "msg_type", initListReq.getMessageType()).eq("is_read", 0));
        map.put("voList",voList);
        map.put("total",total);
        map.put("noReadTotal",noReadTotal);
        return map;
    }


    /**
     * 消息详情
     * @param msgId 消息id
     * @return MsgDetailVo
     */
    @Override
    public MsgDetailVo queryMsgById(String msgId) {
        //消息记录
        AccountMsg msg = accountMsgMapper.selectOne(new QueryWrapper<AccountMsg>().eq("id", msgId));
        if (msg != null) {
            MsgDetailVo vo = new MsgDetailVo();
            vo.setId(msg.getId().toString());
            if (msg.getMsgType() == 1) {
                //系统消息
                vo.setMessageTitle(msg.getMsgTitle()).setContent(msg.getMsgContent()).setCreateTime(msg.getCreateTime().toString());
            }else {
                //验证消息
                JSONObject jsonObject = JSONObject.parseObject(msg.getMsgContent());
                if (jsonObject != null) {
                    vo.setUsername(jsonObject.getString(StatusCodeUtil.MSG_USERNAME)).setMobile(jsonObject.getString(StatusCodeUtil.MSG_USER_MOBILE));
                    vo.setAccountId(jsonObject.getString(StatusCodeUtil.MSG_ACCOUNT_ID)).setApplyTime(jsonObject.getString(StatusCodeUtil.MSG_APPLY_TIME)).setApplyStatus(StatusCodeUtil.MSG_APPLY_STATUS);
                }
            }
            return vo;
        }
        return null;
    }
}
