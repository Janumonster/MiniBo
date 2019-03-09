package com.zzy.minibo.WeiBoTools;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFilter {

    public static String IsVideoStatus(String text){
        String base = "http://t.cn/(.{7})";
        Pattern pattern = Pattern.compile(base);
        Matcher m = pattern.matcher(text);
        if (m.find()){
            return m.group();
        }
        return null;
    }


}
