package com.zzy.minibo.WeiBoTools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFilter {

    public static String IsVideoStatus(String text){
        String base = "http://t.cn/(.{7})";
        Pattern pattern = Pattern.compile(base);
        Matcher m = pattern.matcher(text);
        if (m.find()){
            return m.group();
        }
        return null;
    }



    public static SpannableStringBuilder statusTextFliter(Context context,String text){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int i = 0;
        while (i < text.length()){
            switch (text.charAt(i)){
                case '[' ://识别表情
                    StringBuilder stringBuilder = new StringBuilder();
                    if (i < text.length() - 1){
                        i++;
                    }
                    while (text.charAt(i) != ']'){
                        stringBuilder.append(text.charAt(i));
                        if (i < text.length() - 1){
                            i++;
                        }
                    }
//                    spannableStringBuilder.append("\u00A0");
                    int resourID = EmotionsMatcher.getEomtions("["+stringBuilder.toString()+"]");
                    if (resourID !=0){
                        Drawable drawable = context.getResources().getDrawable(resourID);
                        if (drawable != null){
                            drawable.setBounds(8,0,44,36);
                            CenterAlignImageSpan centerAlignImageSpan = new CenterAlignImageSpan(drawable);
                            spannableStringBuilder.append("["+stringBuilder.toString()+"]",centerAlignImageSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }else {
                            spannableStringBuilder.append("[").append(stringBuilder.toString()).append("]");
                        }
                    }else {
                        spannableStringBuilder.append("[").append(stringBuilder.toString()).append("]");
                    }

                    break;
                case '@' ://识别ID
                    if (i < text.length() - 1){
                        i++;
                    }
                    StringBuilder username = new StringBuilder();
                    username.append("@");
                    while (text.charAt(i) != ':'&&text.charAt(i)!=' '&&text.charAt(i)!='@'&&text.charAt(i)!='：'){
                        username.append(text.charAt(i));
                        if (i < text.length() - 1){
                            i++;
                        }else if (i == text.length()-1)break;
                    }
                    SingleClickSpan singleClickSpan_name = new SingleClickSpan(context,username.toString());
                    SpannableString spannableUsername = new SpannableString(username.toString());
                    spannableUsername.setSpan(singleClickSpan_name,0,username.toString().length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(spannableUsername);
                    if (i!=text.length()-1){
                        spannableStringBuilder.append(text.charAt(i));
                    }
                    break;
                case '#'://识别话题
                    if (i < text.length() - 1){
                        i++;
                    }
                    StringBuilder topic = new StringBuilder();
                    topic.append("#");
                    while (text.charAt(i) != '#'){
                        topic.append(text.charAt(i));
                        if (i < text.length() - 1){
                            i++;
                        }else if (i == text.length()-1)break;
                    }
                    topic.append("#");
                    SingleClickSpan singleClickSpan_topic = new SingleClickSpan(context,topic.toString());
                    SpannableString spannableTopic = new SpannableString(topic.toString());
                    spannableTopic.setSpan(singleClickSpan_topic,0,topic.toString().length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(spannableTopic);
                    break;
                case 'h' ://识别链接
                    String address_short_text;
                    if (i+19 <= text.length()){
                        address_short_text = text.substring(i,i+19);
                    }else {
                        break;
                    }
                    String base_short = "http://t.cn/(.{7})";
                    Pattern patternShort = Pattern.compile(base_short);
                    Matcher matcher_short = patternShort.matcher(address_short_text);
                    if (matcher_short.find()){
                        SingleClickSpan weiBoClickSpan_quanwen = new SingleClickSpan(context,"链接："+matcher_short.group(0));
                        SpannableString spannableStringAdd = new SpannableString("☍网页链接");
                        spannableStringAdd.setSpan(weiBoClickSpan_quanwen,0,5,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableStringAdd);
                        i = i + 19;
                    }
                    break;
                case '全'://识别全文
                    if (i+1<text.length()&&text.charAt(i+1)=='文'){
                        String address_long_text = text.substring(i,text.length()-1);
                        String base_long = "http://m.weibo.cn/(.*)";
                        Pattern patternLong = Pattern.compile(base_long);
                        Matcher matcher_long = patternLong.matcher(address_long_text);
                        if (matcher_long.find()){
                            SingleClickSpan weiBoClickSpan_quanwen = new SingleClickSpan(context,matcher_long.group(0));
                            SpannableString spannableStringAdd = new SpannableString("全文");
                            spannableStringAdd.setSpan(weiBoClickSpan_quanwen,0,2,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(spannableStringAdd);
                            i = text.length();
                        }else {
                            spannableStringBuilder.append(text.charAt(i));
                        }
                    }else {
                        spannableStringBuilder.append(text.charAt(i));
                    }
                    break;
                default:
                    spannableStringBuilder.append(text.charAt(i));
            }
            i++;
        }

        return spannableStringBuilder;
    }


}
