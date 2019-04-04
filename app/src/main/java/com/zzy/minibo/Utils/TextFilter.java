package com.zzy.minibo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.zzy.minibo.Members.URLHolder;
import com.zzy.minibo.WBListener.StatusTypeListener;
import com.zzy.minibo.Utils.WBClickSpan.StatusDetialClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.TopicClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.UserIdClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.WebUrlClickSpan;

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

    public static SpannableStringBuilder statusTextFliter(Context context, String text, final StatusTypeListener listener){
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
                    Drawable drawable = Drawable.createFromPath(Environment.getExternalStorageDirectory()+"/MiniBo/emotions/["+stringBuilder.toString()+"]");
                    if (drawable != null){
                        drawable.setBounds(8,0,44,36);
                        CenterAlignImageSpan centerAlignImageSpan = new CenterAlignImageSpan(drawable);
                        spannableStringBuilder.append("["+stringBuilder.toString()+"]",centerAlignImageSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else {
                            spannableStringBuilder.append("[").append(stringBuilder.toString()).append("]");
                    }
                    spannableStringBuilder.append("\u00A0");
                    break;
                case '@' ://识别ID
                    int id_start = i;
                    if (id_start < text.length() - 1){
                        id_start++;
                    }
                    StringBuilder username = new StringBuilder();
                    username.append("@");
                    while (text.charAt(id_start) != ':'&&text.charAt(id_start)!=' '&&text.charAt(id_start)!='@'&&text.charAt(id_start)!='：'&&text.charAt(id_start)!=','){
                        username.append(text.charAt(id_start));
                        if (id_start < text.length() - 1){
                            id_start++;
                            if (text.charAt(id_start) == '_'){
                                username.append("_");
                                id_start++;
                            }
                        }else if (id_start == text.length()-1)break;
                    }
                    if (username.length() > 1 && !isSpecialChar(username.toString())){
                        UserIdClickSpan userIdClickSpan_name = new UserIdClickSpan(context,username.toString());
                        SpannableString spannableUsername = new SpannableString(username.toString());
                        spannableUsername.setSpan(userIdClickSpan_name,0,username.toString().length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableUsername);
                        if (id_start != text.length()-1){
                            spannableStringBuilder.append(text.charAt(id_start));
                        }
                        i = id_start;
                    }else {
                        spannableStringBuilder.append("@");
                    }
                    break;
                case '#'://识别话题
                    int topic_start = i;
                    if (topic_start < text.length() - 1){
                        topic_start++;
                    }
                    StringBuilder topic = new StringBuilder();
                    topic.append("#");
                    while (text.charAt(topic_start) != '#'){
                        topic.append(text.charAt(topic_start));
                        if (topic_start < text.length() - 1){
                            topic_start++;
                        }else {
                            break;
                        }
                    }
                    if (text.charAt(topic_start) == '#'){
                        i = topic_start;
                        topic.append("#");
                        TopicClickSpan topicClickSpan = new TopicClickSpan(context,topic.toString());
                        SpannableString spannableTopic = new SpannableString(topic.toString());
                        spannableTopic.setSpan(topicClickSpan,0,topic.toString().length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableTopic);
                    }else {
                        spannableStringBuilder.append("#");
                    }
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
                        WebUrlClickSpan webUrlClickSpan = new WebUrlClickSpan(context,"链接："+matcher_short.group(0));
                        //这里还要判断是否视频链接
//                        WBApiConnector.getShortUrlType(accessToken, matcher_short.group(0), new HttpCallBack() {
//                            @Override
//                            public void onSuccess(String response) {
//                                URLHolder urlHolder = URLHolder.getInstanceFromJSON(response);
//                                if (urlHolder != null){
//                                    listener.videoUrL("onSuccess: "+urlHolder.getUrl_short()+" type:"+urlHolder.getType());
//                                }
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
                        SpannableString spannableStringUrl = new SpannableString("☍网页链接");
                        spannableStringUrl.setSpan(webUrlClickSpan,0,5,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableStringUrl);
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
                            StatusDetialClickSpan statusDetialClickSpan = new StatusDetialClickSpan(context,matcher_long.group(0));
                            SpannableString spannableStringDetial = new SpannableString("全文");
                            spannableStringDetial.setSpan(statusDetialClickSpan,0,2,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(spannableStringDetial);
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


    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!#$%^&*()+=|{}':;',\\[\\].<>/?~！#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
