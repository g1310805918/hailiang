package com.yunduan;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.yunduan.entity.Account;
import com.yunduan.service.AccountService;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class WebApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AccountService accountService;

    @Test
    void contextLoads() {
        System.out.println("hello test");
    }


    @Test
    public void saveAccount(){
        Account account = new Account();
        account.setId(SnowFlakeUtil.getPrimaryKeyId()).setUsername("李四").setMobile("11112341230").setEmail("456@qq.cm").setPassword("123").setHeadPic(StatusCodeUtil.HEAD_PIC).setCreateTime(new Date());
//        boolean save = accountService.save(account);
//        Account save = accountService.createAccount(account);
//        System.out.println("save = " + save);
    }

    @Test
    public void  getOne() {
        List<Account> list = accountService.list();
        for (Account account : list) {
            //格式化日期时间  yyyy-MM-dd HH:mm:ss
            String format = DateUtil.formatDateTime(account.getCreateTime());
            account.setCreateDateTime(format);
            System.out.println("account = " + account);
        }
    }



    //测试rabbitMQ 发送消息
    @Test
    public void testSendMsg() {
        rabbitTemplate.convertAndSend("topic_exchange","a.orange","【RabbitMQ】我这是一条测试消息！！！！！");
    }




}
