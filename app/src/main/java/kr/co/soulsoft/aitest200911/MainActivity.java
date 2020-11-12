package kr.co.soulsoft.aitest200911;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.data.DatabaseRequest;

public class MainActivity extends FragmentActivity {

    private ArrayList<String> participantInfo;
    private JSONArray dataSource;
    private String categoryID, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        setContentView(R.layout.activity_main);

        SetWidget();
    }

    private void SetWidget() {
        setFullScreen();
//        new DatabaseRequest(getBaseContext(), executeListener).execute("GET_ALL_CATEGORY");
        findViewById(R.id.btnSurveyStart).setOnClickListener(startSurvey);
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


    private View.OnClickListener startSurvey;
    {
        startSurvey = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ParticipantActivity.class);
//                i.putExtra(LoginActivity.PARTICIPANT_INFO, participantInfo);
                startActivity(i);
            }
        };
    }
}