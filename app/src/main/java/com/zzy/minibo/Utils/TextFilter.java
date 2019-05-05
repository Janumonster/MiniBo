package com.zzy.minibo.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.Time;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.zzy.minibo.WBListener.StatusTextFliterCallback;
import com.zzy.minibo.Utils.WBClickSpan.StatusDetialClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.TopicClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.UserIdClickSpan;
import com.zzy.minibo.Utils.WBClickSpan.WebUrlClickSpan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFilter {

    private static final String TAG = TextFilter.class.getSimpleName();

    public static String IsVideoStatus(String text){
        String base = "http://t.cn/(.{7})";
        Pattern pattern = Pattern.compile(base);
        Matcher m = pattern.matcher(text);
        if (m.find()){
            return m.group();
        }
        return null;
    }

    public static SpannableStringBuilder statusTextFliter(Context context, String text, final StatusTextFliterCallback callback){
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
                        drawable.setBounds(8,0,42,34);
                        CenterAlignImageSpan centerAlignImageSpan = new CenterAlignImageSpan(drawable);
                        spannableStringBuilder.append("["+stringBuilder.toString()+"]",centerAlignImageSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else {
                        spannableStringBuilder.append("[").append(stringBuilder.toString()).append("]");
                    }
//                    spannableStringBuilder.append("\u00A0");
                    break;
                case '@' ://识别ID
                    int id_start = i;
                    if (id_start < text.length() - 1){
                        id_start++;
                    }
                    StringBuilder username = new StringBuilder();
                    while ( id_start<text.length() && !isSpecialChar(String.valueOf(text.charAt(id_start)))){
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
                        SpannableString spannableUsername = new SpannableString("@"+username.toString());
                        spannableUsername.setSpan(userIdClickSpan_name,0,username.toString().length()+1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableStringBuilder.append(spannableUsername);
                        if (id_start < text.length()-1){
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
                    String base_short = "http://t.cn/(.{7})( ?)";
                    Pattern patternShort = Pattern.compile(base_short);
                    Matcher matcher_short = patternShort.matcher(address_short_text);
                    if (matcher_short.find()){
                        WebUrlClickSpan webUrlClickSpan = new WebUrlClickSpan(context,matcher_short.group(0));
                        SpannableString spannableStringUrl = new SpannableString("☍网页链接 ");
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
                            if (callback != null){
                                callback.callback(matcher_long.group(0),true);
                            }
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

    /**
     * 时间过滤器
     * Sun_Apr_07_19:46:42_+0800_2019
     */
    public static String TimeFliter(String statusTime){
        Log.d("TimeFliter",statusTime);
        StringBuilder stringBuilder = new StringBuilder();
        long t = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        int year = calendar.get(Calendar.YEAR);
        int mYear = Integer.valueOf(statusTime.substring(26));
        if (year >= mYear + 1){
            stringBuilder.append(mYear).append("-");
            stringBuilder.append(getMonth(statusTime.substring(4,7))).append("-").append(statusTime.substring(8,10));
            Log.d("TimeFliter ", "一年前："+stringBuilder.toString());
            return stringBuilder.toString();
        }else {
            int month = calendar.get(Calendar.MONTH)+1;
            int mMonth = getMonth(statusTime.substring(4,7));
            Log.d("TimeFliter ", "month = "+mMonth+statusTime.substring(4,7)+month);
            if (month >= mMonth + 1){
                stringBuilder.append(mMonth).append("-").append(statusTime.substring(8,10)).append(" ").append(statusTime.substring(11,16));
                Log.d("TimeFliter ", "一个月前："+stringBuilder.toString());
                return stringBuilder.toString();
            }else {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int mDay = Integer.valueOf(statusTime.substring(8,10));
                if (day > mDay + 1){
                    stringBuilder.append(mMonth).append("-").append(statusTime.substring(8,10)).append(" ").append(statusTime.substring(11,16));
                    Log.d("TimeFliter ", "2天前："+stringBuilder.toString());
                    return stringBuilder.toString();
                } else if(day == mDay + 1){
                    stringBuilder.append("昨天 ").append(statusTime.substring(11,16));
                    Log.d("TimeFliter ", "1天前，不超过2天："+stringBuilder.toString());
                    return stringBuilder.toString();
                } else {
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int mHour = Integer.valueOf(statusTime.substring(11,13));
                    int minute = calendar.get(Calendar.MINUTE);
                    int mMinute = Integer.valueOf(statusTime.substring(14,16));

                    int totalTime = hour * 60 + minute;
                    int mTotalTime = mHour * 60 + mMinute;

                    int timePass = totalTime - mTotalTime;

                    if(timePass >= 60){
                        stringBuilder.append(timePass / 60).append("小时前");
                        Log.d("TimeFliter ", "n小时前："+stringBuilder.toString());
                        return stringBuilder.toString();
                    }else if (timePass >= 1){
                        stringBuilder.append(timePass).append("分钟前");
                        Log.d("TimeFliter ", "1分钟前，不超过一小时："+stringBuilder.toString());
                        return stringBuilder.toString();
                    }else {
                        stringBuilder.append("刚刚");
                        Log.d("TimeFliter ", "不超过1分钟："+stringBuilder.toString());
                        return stringBuilder.toString();
                    }
                }
            }
        }
    }


    /**
     * 数字过滤器
     *
     *
     */
    public static String NumberFliter(String num){
        StringBuilder sb = new StringBuilder();
        int m = 10000;
        int b = 100000000;
        int n = Integer.valueOf(num);
        if (n > m && n < b){
            int k = (n / 1000) % 10;//小数点后的数字
            int a = n / m;//小数点前的数字
            sb.append(a).append(".").append(k).append("万");
            return sb.toString();
        }else if (n >= b){
            int j = (n / 10000000) % 10;
            int c = n / b;
            sb.append(c).append(".").append(j).append("亿");
            return sb.toString();
        }
        return num;
    }

    /**
     *
     * @return Sun_Apr_07_19:46:42_+0800_2019
     */
    public static String createTimeString(){
        StringBuilder sb = new StringBuilder();
        long t = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        int daily = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        sb.append("Sun ");
        switch (month){
            case 0: sb.append("Jan ");break;
            case 1: sb.append("Feb ");break;
            case 2: sb.append("Mar ");break;
            case 3: sb.append("Apr ");break;
            case 4: sb.append("May ");break;
            case 5: sb.append("Jun ");break;
            case 6: sb.append("Jul ");break;
            case 7: sb.append("Aug ");break;
            case 8: sb.append("Sep ");break;
            case 9: sb.append("Oct ");break;
            case 10: sb.append("Nov ");break;
            case 11: sb.append("Dec ");break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd HH:mm:ss ");
        Date date = new Date(System.currentTimeMillis());
        sb.append(simpleDateFormat.format(date)).append("+0800 ").append(year);
        Log.d(TAG, "createTimeString: "+sb.toString());
        return sb.toString();
    }


    private static int getMonth(String substring) {
        switch (substring){
            case "Jan":return 1;
            case "Feb":return 2;
            case "Mar":return 3;
            case "Apr":return 4;
            case "May":return 5;
            case "Jun":return 6;
            case "Jul":return 7;
            case "Aug":return 8;
            case "Sep":return 9;
            case "Oct":return 10;
            case "Nov":return 11;
            case "Dec":return 12;
        }
        return 0;
    }

    /**
     * 是否存在特殊字符
     */
    private static boolean isSpecialChar(String str) {
        String regEx = "[ `~!#$%^&*()+=|{}':;',\\[\\].<>/?~！#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
