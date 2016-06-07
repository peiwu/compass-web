package com.iflytek.voicecloud.compass.common;

/**
 * 
 * @author kwliu
 * @date 下午1:59:45, 2015年9月7日
 */
public class Constant
{
    public static final int PARENT_CLASSIFY_TYPE = 0; // 父类
    
    public static final int CHILD_CLASSIFY_TYPE = 1; // 子类
    
    public static final int ACT = 1; // 未删除
    
    public static final int NOT_ACT = 0; // 删除
    
    public static final int defaultPageIndex = 1;
    
    public static final int defaultPageSize = 10000;
    
    public static String httpUrl = ""; //es请求获取数据
    public static String esUrl = "";
    public static String basicDataUrl = ""; //基础数据视图地址
    
    public static String redisUrl = "";
    public static int redisTime;
    public static int threadNum;
    public static int jedisNum;
    public static String emailHost = "mail.iflytek.com"; //用域账户登录
    
    public static String username =""; //用户名
    public static String password = ""; //密码
    public static final int SUPER_ADMIN_USER = 0; //超级管理员
    public static final int ADMIN_USER = 1; //管理员
    public static final int Commercial_USER = 2; //管理员
    public static final int NORMAL_USER = 3; //普通用户
    public static final Long ALL_TAG_ID = -1L;
    
}
