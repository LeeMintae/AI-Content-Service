package kr.co.soulsoft.aitest200911.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class MakeID {

    public MakeID() {

    }

    public String getID() {
        String form = "yyMMddHHmmss";
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(form, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String currentTime = simpleDateFormat.format(now);

        int temp = new Random().nextInt(900)+100;

        return "p"+currentTime+temp;
    }
}
