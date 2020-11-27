package kr.co.soulsoft.aitest200911;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.adapter.ContentListAdapter;
import kr.co.soulsoft.aitest200911.data.DatabaseRequest;

public class ContentSelectActivity extends AppCompatActivity {

    // region Value Definition
    private JSONArray resultData;
    private ArrayList<JSONArray> contentDataArray;
    private ProgressDialog progressDialog;
    private ContentListAdapter contentListAdapter;
    private ArrayList<String> SELECTED_CONTENTS;
    // endregion

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
        SELECTED_CONTENTS = new ArrayList<>();
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
            public void onResult(boolean isChecked, JSONObject selectedContent, float ratingValue) {
                Log.d("((((((((((((((( 확인", selectedContent.toString());
                selectedContentHandler(isChecked, selectedContent, ratingValue);
            }
        };
    }

    private final ContentListAdapter.RatingChangeListener ratingChangeListener;
    {
        ratingChangeListener = new ContentListAdapter.RatingChangeListener() {
            @Override
            public void onResult(String id, float ratingValue) {
                ratingChangeHandler(id, ratingValue);
            }
        };
    }

    private void selectedContentHandler(boolean isChecked, JSONObject selectedContent, float ratingValue) {
        int index = 0;
        try {
            String id = selectedContent.getString("m_yctnt_idx");
            if (isChecked) {
                for (String content : SELECTED_CONTENTS) {
                    if (content.contains(id)) {
                        SELECTED_CONTENTS.set(index, id+"/"+ratingValue);
                        return;
                    }
                    index++;
                }
                SELECTED_CONTENTS.add(id+"/"+ratingValue);
            } else {
                for (String content : SELECTED_CONTENTS) {
                    if (content.contains(id)){
                        SELECTED_CONTENTS.remove(index);
                        return;
                    }
                    index++;
                }
            }
            Log.d("<<<<<<<<<<<<< Selected Content Count ", SELECTED_CONTENTS.size()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ratingChangeHandler(String id, float ratingValue) {
        int index = 0;
        for (String content : SELECTED_CONTENTS) {
            if (content.contains(id)) {
                SELECTED_CONTENTS.set(index, id+"/"+ratingValue);
                return;
            }
            index++;
        }
    }

    private final View.OnClickListener surveyFinishClick;
    {
        surveyFinishClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SELECTED_CONTENTS.size() < 5) {
                    Toast.makeText(getBaseContext(), getString(R.string.msg_content_limit), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("<<<<<<<<<<<<< 선택 결과", SELECTED_CONTENTS.toString());
                ArrayList<String> participantInfo = getIntent().getStringArrayListExtra(ParticipantActivity.PARTICIPANT_INFO);
                new DatabaseRequest(getBaseContext(), executeListener).execute(DatabaseRequest.INSERT,
                        participantInfo.get(1),
                        participantInfo.get(2),
                        participantInfo.get(3),
                        participantInfo.get(4),
                        participantInfo.get(5),
                        "testmail@soulsoft.co.kr");
            }
        };
    }

    private DatabaseRequest.ExecuteListener executeListener;
    {
        executeListener = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[1] == "INSERT_OK") {
                    Log.d("<<<<<<<<<<<<<<<<<< 설문자 정보 생성", result[0] + " : "+result[1]);
                } else {
                    Log.d("<<<<<<<<<<<<<<<<<< 설문자 정보 생성", result[1]);
                }
            }
        };
    }
}