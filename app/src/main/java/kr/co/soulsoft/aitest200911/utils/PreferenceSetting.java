package kr.co.soulsoft.aitest200911.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class PreferenceSetting {
    private Context context;
//    private static final String DEFAULT_SERVER_IP = "127.0.0.1";
    private static final String DEFAULT_SERVER_IP = "210.105.232.237";

    public PreferenceSetting(Context context) {this.context = context;}

    public String loadPreference(int category) {
        String returnValue;
        SharedPreferences preferences = context.getSharedPreferences("prefInfo", Activity.MODE_PRIVATE);
        switch (category) {
            default:
                returnValue = preferences.getString("ServerAddress", DEFAULT_SERVER_IP);
                break;
        }
        return returnValue;
    }

    public void savePreference(int category, String value) {
        SharedPreferences preferences = context.getSharedPreferences("prefInfo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (category) {
            default:
                editor.putString("ServerAddress", value);
                break;
        }
        editor.apply();
        Toast.makeText(context, "서버 IP 정보 변경됨", Toast.LENGTH_SHORT).show();
    }
}
