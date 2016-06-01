package com.sen.haoliyou.tools;

/**
 * Created by Sen on 2016/3/25.
 */
public class StringUItils {
    public  static String delString(String string,int cont){
        if (string==null){
            return " ";
        }
        return string.substring(0,cont);
    }

    //所以我们只需要将半角字符转换为全角字符即可，方法如下
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
}
