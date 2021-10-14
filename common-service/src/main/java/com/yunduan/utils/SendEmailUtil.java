package com.yunduan.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.Setting;
import com.yunduan.entity.WorkOrder;
import com.yunduan.mapper.EngineerMapper;
import com.yunduan.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Component
public class SendEmailUtil {
    public static final transient Logger log = LoggerFactory.getLogger(SendEmailUtil.class);

    @Autowired
    private SettingService settingService;
    @Autowired
    private EngineerMapper engineerMapper;

    //登录账户
    public String account = null;
    //登录密码
    public String password = null;
    //发件邮箱对象id(数据库表id)
    private static final String SEND_EMAIL_ID = "446128498935861248";
    //服务器地址
    private String HOST = "";
    //端口号
    private String PORT = "";
    //协议
    private String PROTOCOL = "";



    /**
     * 发送验证邮件
     * @param email 需要发送的邮件
     * @return 验证码
     * @throws Exception
     */
    public String sendAuthEmail(String email) throws Exception {
        //初始化邮件对象【account、password、host、port、protocol】
        initEmailAccountInfo();
        if (StrUtil.hasEmpty(account) || StrUtil.hasEmpty(password)) {
            log.error("邮件对象不存在、账号密码为空！");
            return null;
        }
        //获取验证码
        String randomCode = getRandomCode();
        //获取发送模板
        String content = getEmailRandomCodeTemplate(randomCode);
        //发送邮件
        commonSend(email,content);
        return randomCode;
    }


    /**
     * 向工程师发送提醒处理用户反馈内容邮件
     * @param workOrder 工单
     */
    public void sendRemindEngineerForWorkOrder(WorkOrder workOrder) {
        Engineer engineer = engineerMapper.selectById(workOrder.getEngineerId());
        if (engineer != null) {
            //发送邮件内容
            String content = "您处理的工单，编号为：" + workOrder.getOutTradeNo() + "，用户已提交新的反馈内容，请您尽快处理。";
            String email = engineer.getEmail();
            try {
                commonSend(email,content);
            } catch (Exception e) {
                log.error("【向工程师发送提醒处理用户反馈内容邮件】发送邮件失败，邮箱：" + email + "\t发送内容：" + content);
            }
        } else {
            log.error("【向工程师发送提醒处理用户反馈内容邮件】工单id = " + workOrder.getId() + "\t工程师id = " + workOrder.getEngineerId() + "\t不存在，发送邮件失败！");
        }
    }



    /**
     * 公共发送邮件方法
     * @param email  需要发送的邮件地址，多个邮箱使用 , 分隔
     * @param content 发送的内容
     */
    public void commonSend(String email,String content) throws Exception {
        Session session = initProperties();
        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(account,"海量数据"));
        //收件人,多人接收
        InternetAddress[] internetAddressesTo = new InternetAddress().parse(email);
        mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressesTo);
        //主题
        mimeMessage.setSubject("VastData");
        //时间
        mimeMessage.setSentDate(new Date());
        //容器类  附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        //可以包装文本，图片，附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        //设置内容
        bodyPart.setContent(content,"text/html;charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
        //添加图片&附件
//            bodyPart = new MimeBodyPart();
//            bodyPart.attachFile("附件地址");
        mimeMessage.setContent(mimeMultipart);
        mimeMessage.saveChanges();
        //发送
        Transport.send(mimeMessage);
    }

    public Session initProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol",PROTOCOL);
        properties.setProperty("mail.smtp.host",HOST);
        properties.setProperty("mail.smtp.port",PORT);
        //使用smtp身份验证
        properties.setProperty("mail.smtp.auth","true");
        //使用SSL，企业邮箱必须开启  、开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory",mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback","false");
        properties.put("mail.smtp.socketFactory.port",PORT);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account,password);
            }
        });
        //设置发送邮件时  debug 调试模式，查看日志信息
        session.setDebug(false);
        return session;
    }


    /**
     * 初始化邮件对象
     * @return setting
     */
    public void initEmailAccountInfo() {
        //获取发送对象
        Setting setting = settingService.getById(SEND_EMAIL_ID);
        Optional.ofNullable(setting).ifPresent(item -> {
            account = item.getEmailAddress();
            password = AESUtil.decrypt(item.getEmailPassword());
            PORT = item.getServicePort();
            HOST = item.getServiceHost();
            PROTOCOL = item.getServiceAgreement();
        });
    }


    /**
     * 获取短信发送邮件验证模板
     * @param code 验证码
     * @return 发送模板
     */
    public String getEmailRandomCodeTemplate(String code) {
        return "<p>您的验证码为："  + code + "</p><br/><p>此验证码3分钟内有效，如非本人操作请忽略。</p>";
    }


    /**
     * 生成验证码
     * @return 验证码
     */
    public String getRandomCode() {
        return RandomUtil.randomNumbers(6);
    }


}
