package com.bingo.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

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
