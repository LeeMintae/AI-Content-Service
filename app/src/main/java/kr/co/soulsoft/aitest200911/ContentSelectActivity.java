package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.adapter.ContentListAdapter;
import kr.co.soulsoft.aitest200911.data.DatabaseRequest;

public class ContentSelectActivity extends AppCompatActivity {

    private JSONArray resultData;
    private ArrayList<JSONArray> contentDataArray;
    private ProgressDialog progressDialog;
    private ContentListAdapter contentListAdapter;
    private ArrayList<String> SELECTED_CONTENTS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        setContentView(R.layout.activity_content_select);

        setWidget();
    }

    private void setWidget() {
        Log.d("<<<<<<<<<<<<<< 사용자 정보", getIntent().getStringArrayListExtra(ParticipantActivity.PARTICIPANT_INFO).toString());
        Log.d("<<<<<<<<<<<<<< 카테고리 정보", getIntent().getStringExtra(SurveyActivity.CATEGORY_ID));
        Log.d("<<<<<<<<<<<<<< 설문 정보", getIntent().getStringExtra(SurveyActivity.SURVEY_ANSWER));
        setFullScreen();
        getContentList();
        findViewById(R.id.btnFinishSurvey).setOnClickListener(surveyFinishClick);
    }
    private void setFullScreen() {
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getContentList(){
        progressDialog = ProgressDialog.show(ContentSelectActivity.this, getString(R.string.msg_dialog_content_title), getString(R.string.msg_dialog_content_loading));
        new DatabaseRequest(getBaseContext(), findContentResult).execute("GET_CONTENT");
    }

    private final DatabaseRequest.ExecuteListener findContentResult;
    {
        findContentResult = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[0] == null) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), getString(R.string.msg_survey_absent), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    resultData = new JSONArray(result[0]);
                    contentListAdapter = new ContentListAdapter(resultData, contentSelectListener, ratingChangeListener);
                    Log.d("<<<<<<<<< 콘텐츠 리스트", resultData.length()+"");

                    RecyclerView recyclerView = findViewById(R.id.rcyclrContentList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    recyclerView.setAdapter(contentListAdapter);


                    for (int i=0; i < resultData.length(); i++) {

                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private final ContentListAdapter.ContentSelectListener contentSelectListener;
    {
        contentSelectListener = new ContentListAdapter.ContentSelectListener() {
            @Override
            public void onResult(JSONObject selectedContent, float ratingValue) {
                Log.d("((((((((((((((( 확인", selectedContent.toString());
            }
        };
    }

    private final ContentListAdapter.RatingChangeListener ratingChangeListener;
    {
        ratingChangeListener = new ContentListAdapter.RatingChangeListener() {
            @Override
            public void onResult(String id, float ratingValue) {

            }
        };
    }

    private final View.OnClickListener surveyFinishClick;
    {
        surveyFinishClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }
}