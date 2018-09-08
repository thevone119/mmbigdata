package com.bingo.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;

/**
 * Created by Administrator on 2018-07-14.
 * 实现邮件发送,接收
 */
@Service
public class MailService {
    @Autowired
    JavaMailSender jms;

    @Value("${spring.mail.username}")
    private String mailfrom;

    @Value("${spring.mail.password}")
    private String mailpwd;

    @Value("${spring.mail.host}")
    private String mailhost;




    public void sendMail(String mailto,String title,String textmsg){
        //建立邮件消息
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        //发送者
        mainMessage.setFrom(mailfrom);
        //接收者
        mainMessage.setTo(mailto);
        //发送的标题
        mainMessage.setSubject(title);
        //发送的内容
        mainMessage.setText(textmsg);
        jms.send(mainMessage);
    }

    public void sendMailSSL(String mailto,String title,String textmsg) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        // Get a Properties object
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", mailhost);
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailfrom, mailpwd);
            }});

        //建立邮件消息
        MimeMessage  mainMessage = new MimeMessage(session);

        /**
        //发送者
        mainMessage.setFrom(new InternetAddress(mailfrom));
        //接收者
        Address to[] = new InternetAddress[1];
        mainMessage.setReplyTo(to);
        //mainMessage.setRecipients(Message.RecipientType.TO, to);
        //发送的标题
        mainMessage.setSubject(title);
        //发送的内容
        mainMessage.setText(textmsg);
        **/
        MimeMessageHelper helper = new MimeMessageHelper(mainMessage, true,"UTF-8");
        helper.setFrom(mailfrom);
        helper.setTo(mailto);
        helper.setSubject(title);
        helper.setText(textmsg, true);


        Transport.send(mainMessage);
        //jms.send(mainMessage);
    }



    public void sendMailHtml(String mailto,String title,String html){
        MimeMessage message = null;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailfrom);
            helper.setTo(mailto);
            helper.setSubject(title);
            helper.setText(html, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jms.send(message);
    }
}
