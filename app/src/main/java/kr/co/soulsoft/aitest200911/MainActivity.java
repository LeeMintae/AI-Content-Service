package kr.co.soulsoft.aitest200911;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.data.DatabaseRequest;
import kr.co.soulsoft.aitest200911.utils.DialogMaker;

public class MainActivity extends FragmentActivity {

    //region Value Definition
    private ArrayList<String> participantInfo;
    private JSONArray dataSource;
    private String categoryID, categoryName;
    private int click = 0;
    public static final String CATEGORY_ID = "cat_20201111141225";
//    public static final String CATEGORY_ID = "cat_20201130222122";

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        setContentView(R.layout.activity_main);

        SetWidget();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        findViewById(R.id.cnstLayoutMain).setBackground(getDrawable(R.drawable.survey_bg1));
        ((Button)findViewById(R.id.btnSurveyStart)).setText(getString(R.string.btn_next_description));
    }

    private void SetWidget() {

        setFullScreen();
        checkPermission();

        findViewById(R.id.btnSurveyStart).setOnClickListener(startSurvey);
    }

    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void setFullScreen() {
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private DatabaseRequest.ExecuteListener executeListener;
    {
        executeListener = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result == null) {
                    return;
                }
                ArrayList<String> listSource = new ArrayList<>();

                try {
                    dataSource = new JSONArray(result[0]);
                    for (int i=0; i<dataSource.length(); i++) {
                        listSource.add(dataSource.getJSONObject(i).getString("m_category_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private final View.OnClickListener startSurvey;
    {
        startSurvey = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click == 0) {
                    findViewById(R.id.cnstLayoutMain).setBackground(getDrawable(R.drawable.survey_bg2));
                    ((Button)v).setText(getString(R.string.btn_start));
                    click++;
                } else {
                    Intent i = new Intent(MainActivity.this, ParticipantActivity.class);
//                i.putExtra(LoginActivity.PARTICIPANT_INFO, participantInfo);
                    startActivity(i);



                    click = 0;
                }
            }
        };
    }


}