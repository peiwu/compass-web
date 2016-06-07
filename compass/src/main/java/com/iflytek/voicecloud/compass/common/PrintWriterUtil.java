package com.iflytek.voicecloud.compass.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author kwliu
 * @date 下午6:29:24, 2015年10月9日
 */
public class PrintWriterUtil
{
    public static void returnPrintWriterJson(HttpServletResponse response, String json)
    {
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter;
        try
        {
            printWriter = response.getWriter();
            printWriter.write(json);
            printWriter.flush();
            printWriter.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void returnPrintWriterBoolean(PrintWriter printWriter, boolean condition)
    {
        if (condition)
        {
            printWriter.write("true");
        }
        else
        {
            printWriter.write("false");
        }
        printWriter.flush();
        printWriter.close();
        
    }
}
