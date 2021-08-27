package com.yunduan.utils;

import cn.hutool.core.util.RandomUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class SendEmailUtils {

    //登录账户
    private static final String account = "lieyin@intenginetech.com";

    //登录密码
    private static final String password = "Ly8888";

    //服务器地址
    private static final String host = "hwsmtp.exmail.qq.com";

    //端口号
    private static final String port = "465";

    //协议
    private static final String protocol = "smtp";

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 发送邮件
     * @param email  需要发送的邮件地址，多个邮箱使用 , 分隔
     * @param content 发送的内容
     */
    public void send(String email) throws Exception {
        Session session = initProperties();
        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(account,"Intenginetech"));
        //收件人,多人接收
        InternetAddress[] internetAddressesTo = new InternetAddress().parse(email);
        mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressesTo);
        //主题
        mimeMessage.setSubject("Intenginetech Login Email");
        //时间
        mimeMessage.setSentDate(new Date());
        //容器类  附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        //可以包装文本，图片，附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        //发送的内容
        String content = "";
        //生成6位数随机验证码
        String code = RandomUtil.randomNumbers(6);

        //设置内容
        bodyPart.setContent(content,"text/html; charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
        //添加图片&附件
//            bodyPart = new MimeBodyPart();
//            bodyPart.attachFile("附件地址");
        mimeMessage.setContent(mimeMultipart);
        mimeMessage.saveChanges();
        //发送
        Transport.send(mimeMessage);
        //没有异常表示发送成功 ---> redis保存验证码3分钟
        redisUtil.setStringKeyValue(email,code,3, TimeUnit.MINUTES);
    }

    public Session initProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol",protocol);
        properties.setProperty("mail.smtp.host",host);
        properties.setProperty("mail.smtp.port",port);
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
        properties.put("mail.smtp.socketFactory.port",port);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account,password);
            }
        });
        session.setDebug(false);
        return session;
    }

}
