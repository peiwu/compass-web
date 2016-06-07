package com.iflytek.voicecloud.compass.common;
import java.util.Properties;//类似于hashmap，提供键值对的集合

import javax.mail.*;
import javax.mail.internet.*;

public class LoginUtil
{
    //邮件服务器的ip、发件人、收件人、邮件标题、内容
    public boolean isLegal() throws AddressException, MessagingException{
        //创建session
        Properties pro = new Properties();
        pro.put("mail.smtp.host", Constant.emailHost);//指定邮件服务器的ip地址
        //下面这个第二个参数必须用字符串来设置为true，否则会报错
        pro.put("mail.smtp.auth", "true");//验证发件人是否合法用户
        //第二个参数的作用是：当邮件服务器需要认证的时候，会自动调用Authenticator里的getPasswordAuthentication()方法来取得用户名、密码信息
        Session se = Session.getDefaultInstance(pro, new auth());
        //连接到邮件服务器，并发送邮件
        Transport tran = se.getTransport("smtp");
        try{
            tran.connect();//连接
        }
        catch(Exception e){
            System.out.println("Error: cannot connect to mail.iflytek.com");
            return false;
        }
        
        //tran.sendMessage(msg, msg.getAllRecipients());
        tran.close();//关闭连接
        return true;
    }
    //邮箱服务器需要认证的时候，需要写个类继承Authenticator这个抽象类
    public class auth extends Authenticator{
        //Authenticator中的唯一一个方法，作用是返回用来认证的用户名和密码
        @Override
		public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(Constant.username, Constant.password);
            }
    }
}