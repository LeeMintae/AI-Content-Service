package kr.co.soulsoft.aitest200911.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeID {

    public MakeID() {

    }

    public String getID() {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String currentTime = simpleDateFormat.format(now);

        return "p"+currentTime;
    }
}
