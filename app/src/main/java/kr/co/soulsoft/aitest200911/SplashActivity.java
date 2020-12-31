package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        setContentView(R.layout.activity_splash);



        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (checkRecord()) {

                    progressDialog = ProgressDialog.show(SplashActivity.this, "사용자 데이터 확인", "사용자 데이터를 가져오는 중입니다");
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 3000);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        },2000);
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            progressDialog.dismiss();
            Intent i = new Intent(SplashActivity.this, PersonalActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    };

    private boolean checkRecord() {
        File saveFile;

        if(Build.VERSION.SDK_INT < 29)
            saveFile = new File(Environment.getExternalStorageDirectory()+"/SSCR_SurveyCheck");
        else
            saveFile = this.getExternalFilesDir("/SSCR_SurveyCheck");

        if(saveFile == null)
            saveFile.mkdir();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile+"/survey_check.txt"));
            String temp = bufferedReader.readLine();
            Log.d("<<<<<<<<<<<<<<<<< 참여 정보 확인", temp);
            JSONArray jsonArray = new JSONArray(temp);
            JSONObject record = jsonArray.getJSONObject(0);
            if (record.getString("category_id").equals(MainActivity.CATEGORY_ID)) {

//                DialogMaker dialogMaker = new DialogMaker(ParticipantActivity.this, DialogMaker.SURVEY_FINISH, this);
//                dialogMaker.show();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException j) {
            j.printStackTrace();
            return false;
        }
        return false;
    }
}