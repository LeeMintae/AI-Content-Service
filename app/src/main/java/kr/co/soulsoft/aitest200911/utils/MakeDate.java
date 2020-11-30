package kr.co.soulsoft.aitest200911.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MakeDate {
    public MakeDate() {

    }

    public String makeDateString() {
        String form = "yyMMddHHmmss";
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(form, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        return simpleDateFormat.format(now);
    }
}
