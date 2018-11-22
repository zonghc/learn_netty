package com.pcjz.learn.netty.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * 名称：输入工具<br/>
 * 描述：获取控制台中键盘输入的内容。<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/21 17:29
 * </p>
 */
public class InputUtil {
    /**
     *  名称：键盘输入内容<br/>
     *  描述：读取并缓存键盘输入内容
     */
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

    //屏蔽实例化
    private InputUtil(){}

    /**
     *<p>
     *  名称：获取输入内容<br/>
     *  描述：获取当前控制台输入的一行内容。
     *</p>
     *<p>
     *  @param prompt 提示信息
     *  @return java.lang.String 当前输入的一行内容
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/21 18:42<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    public static String getString(String prompt){
        boolean flag = true;//读取标识
        String str = null;
        while (flag){
            System.out.println(prompt);
            try {
                str = KEYBOARD_INPUT.readLine().trim();
                if ( str == null || "".equals(str)){
                    System.out.println("您输入的信息为空，请重新输入：");
                }else{
                    flag = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                flag = false;
                System.out.println("输入异常："+e.toString());
            }
        }
        return str;
    }

}
