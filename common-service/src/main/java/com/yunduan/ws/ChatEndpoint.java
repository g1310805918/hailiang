package com.yunduan.ws;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * websocket
 * 暂时未使用到
 */
//@Component
//@ServerEndpoint("/chat-work-order/{accountId}")
public class ChatEndpoint {

    private static final transient Logger log = LoggerFactory.getLogger(ChatEndpoint.class);

    /**
     * 全部在线会话
     */
    protected static Map<String, Session> onlineSession = new ConcurrentHashMap<>();


    /**
     * 新的客户端连接调用的方法
     * @param session
     */
//    @OnOpen
    public void onOpen(Session session, @PathParam("accountId") String accountId) {
        if (StrUtil.hasEmpty(accountId)) {
            log.error("连接用户【accountId】为空");
            return;
        }
        //向在线会话中添加上线记录
        onlineSession.put(accountId, session);
        log.info(accountId + "建立新的链接");
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message
     */
//    @OnMessage
    public void onMessage(String message) throws IOException {
        //解析用户发送的消息内容、取出需要发送给某个用户的用户id
        JSONObject jsonObject = JSONObject.parseObject(message);

        //判断用户是否存在
        Session isExist = onlineSession.get(jsonObject.getString("toAccountId"));
        //如果存在，直接发送消息
        if (isExist != null) {
            onlineSession.get("1").getBasicRemote().sendText(message);
        }
        //将消息保存到数据库中

    }


//    @OnClose
    public void onClose(@PathParam("accountId") String accountId){
        onlineSession.remove(accountId);
    }


//    @OnError
    public void onError(Session session, Throwable error) {
        log.error("回话异常: " + session.getId(), error);
    }



}
