package com.yunduan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Account;
import com.yunduan.entity.CompanyCSI;
import com.yunduan.entity.Engineer;
import com.yunduan.service.AccountService;
import com.yunduan.service.CompanyCSIService;
import com.yunduan.service.EngineerService;
import com.yunduan.utils.AESUtil;
import com.yunduan.utils.SnowFlakeUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class WebApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EngineerService engineerService;
    @Autowired
    private CompanyCSIService companyCSIService;

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
        List<CompanyCSI> list = companyCSIService.list(new QueryWrapper<CompanyCSI>().orderByDesc("create_time"));
        System.out.println("list ====== \n" + JSONObject.toJSONString(list));
    }



    //测试rabbitMQ 发送消息
    @Test
    public void testSendMsg() {
        Account account = new Account().setId(SnowFlakeUtil.getPrimaryKeyId()).setUsername("张三").setMobile("12345678911").setEmail("123123@qq.com");
        rabbitTemplate.convertAndSend("topic_exchange","test.orange",account);
    }


    @Test
    public void testLinkedList() {

        List<String> companyNameList = new ArrayList<>();
        companyNameList.add("海量数据股份有限公司");
        companyNameList.add("云端高科（北京）科技技术有限公司");
        companyNameList.add("百度智能科技技术有限公司");
        companyNameList.add("华为科技技术有限公司");
        companyNameList.add("阿里巴巴集团");

        List<CompanyCSI> companyCSIS = new ArrayList<>();
        CompanyCSI companyCSI = null;
        for (int i = 0; i < 5; i++) {
            companyCSI = new CompanyCSI();
            companyCSI.setId(SnowFlakeUtil.getPrimaryKeyId()).setCompanyName(companyNameList.get(i)).setProductName("VatBase云数据库").setCauMobile("15235656200").setCauEmail("2770807979@qq.com").setCsiNumber("HL" + RandomUtil.randomNumbers(12)).setUpdateTime(DateUtil.now()).setCreateTime(DateUtil.now()).setDelFlag(StatusCodeUtil.NOT_DELETE_FLAG);
            companyCSIS.add(companyCSI);
        }
        companyCSIService.saveBatch(companyCSIS);
    }


    @Test
    public void createEngineerList() {

        List<String> list = new ArrayList<>();

        list.add("海量员工");
        list.add("技术支持工程师");
        list.add("COE工程师");
        list.add("BDE工程师");

        List<Engineer> companyCSIS = new ArrayList<>();
        Engineer engineer = null;
        for (int i = 1; i < 5; i++) {
            engineer = new Engineer();
            engineer.setUsername("测试用户" + i).setHeadPic(StatusCodeUtil.HEAD_PIC).setMobile("1783658828" + i).setEmail("277080797" + i + "@qq.com");
            engineer.setPassword(AESUtil.encrypt("123456")).setIdentity(i).setIdentityName(list.get(i - 1)).setProductCategoryId("").setProductCategoryName("");
            companyCSIS.add(engineer);
        }
        engineerService.saveBatch(companyCSIS);
    }

    @Test
    public void  getOneEngineer() {
        Engineer byEmail = engineerService.findByEmail("2770807972@qq.com");
        System.out.println("byEmail =========================== \n" + JSONObject.toJSONString(byEmail));
    }

    @Test
    public void  testCollectionUtils() {
        List<String> list = new ArrayList<>();

        list.add("海量员工");
        list.add("技术支持工程师");
        list.add("COE工程师");
        list.add("BDE工程师");

        boolean empty = CollectionUtils.isEmpty(list);
        System.out.println("Result = " + empty);
    }

}
