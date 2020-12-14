package kr.co.soulsoft.aitest200911;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.adapter.ContentListAdapter;
import kr.co.soulsoft.aitest200911.data.DatabaseRequest;
import kr.co.soulsoft.aitest200911.utils.DialogMaker;
import kr.co.soulsoft.aitest200911.utils.MakeDate;

public class ContentSelectActivity extends AppCompatActivity {

    // region Value Definition
    private JSONArray resultData;
    private ArrayList<JSONArray> contentDataArray;
    private ProgressDialog progressDialog;
    private ContentListAdapter contentListAdapter;
    private ArrayList<String> SELECTED_CONTENTS;

    private String ParticipantID;
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
                    Intent intent = new Intent(getBaseContext(), ContentViewerActivity.class);
                    resultData = new JSONArray(result[0]);
                    contentListAdapter = new ContentListAdapter(resultData, contentViewListener, contentSelectListener, ratingChangeListener);
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

    private final ContentListAdapter.ContentViewListener contentViewListener;
    {
        contentViewListener = new ContentListAdapter.ContentViewListener() {
            @Override
            public void onResult(String contentID) {
                Log.d("((((((((((((((((( Content ID 확인", contentID);
                Intent intent = new Intent(getBaseContext(), ContentViewerActivity.class);
                intent.putExtra("CONTENT_ID", contentID);
                startActivity(intent);
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
            public void onResult(JSONObject targetContent, float ratingValue) {
                ratingChangeHandler(targetContent, ratingValue);
            }
        };
    }

    private void selectedContentHandler(boolean isChecked, JSONObject selectedContent, float ratingValue) {
        int index = 0;
        try {
            String id = selectedContent.getString("m_yctnt_idx");
            String[] contentID = selectedContent.getString("m_yctnt_url").split("v=");
            if (isChecked) {
                for (String content : SELECTED_CONTENTS) {
                    if (content.contains(id)) {
                        SELECTED_CONTENTS.set(index, "id:"+id+",content_id:"+contentID[1]+",rating:"+ratingValue);
                        return;
                    }
                    index++;
                }
                SELECTED_CONTENTS.add(index, "id:"+id+",content_id:"+contentID[1]+",rating:"+ratingValue);
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
    private void ratingChangeHandler(JSONObject targetContent, float ratingValue) {
        int index = 0;
        String id = null;
        try {
            id = targetContent.getString("m_yctnt_idx");
            String[] contentID = targetContent.getString("m_yctnt_url").split("v=");

            for (String content : SELECTED_CONTENTS) {
                if (content.contains(id)) {
                    SELECTED_CONTENTS.set(index, "id:"+id+",content_id:"+contentID[1]+",rating:"+ratingValue);
                    return;
                }
                index++;
            }
            Log.d("<<<<<<<<<<<<<<변화 확인", SELECTED_CONTENTS.toString());
        } catch (JSONException e) {
            e.printStackTrace();
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
                        participantInfo.get(6),
                        participantInfo.get(7));
            }
        };
    }

    private DatabaseRequest.ExecuteListener executeListener;
    {
        executeListener = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[1].equals("INSERT_OK")) {
                    ParticipantID = result[0];
                    Log.d("<<<<<<<<<<<<<<<<<< 설문자 정보 생성", result[0]);
                    StringBuilder recordValue = new StringBuilder();
                    int index=0;
                    for (String value : SELECTED_CONTENTS) {
                        if (index==0) {
                            recordValue.append("[");
                        } else {
                            recordValue.append(",");
                        }
                        recordValue.append("{"+value+"}");
                        if (index+1 == SELECTED_CONTENTS.size()) {
                            recordValue.append("]");
                        }
                        index++;
                    }
                    Log.d("<<<<<<<<<<<< 선택 정보 ", recordValue.toString());
                    new DatabaseRequest(getBaseContext(), surveyResultListener).execute(DatabaseRequest.REPLY,
                            getIntent().getStringExtra(SurveyActivity.CATEGORY_ID),
                            ParticipantID,
                            getIntent().getStringExtra(SurveyActivity.SURVEY_ANSWER),
                            recordValue.toString());
                } else {
                    Log.d("<<<<<<<<<<<<<<<<<< 설문자 생성 실패", result[1]);
                }
            }
        };
    }

    private DatabaseRequest.ExecuteListener surveyResultListener;
    {
        surveyResultListener = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[0].equals("INSERT_OK")) {
                    Log.d("<<<<<<<<<<<<<<<<<< 설문 완료", result[0]);
                    saveSurveyFinish(ParticipantID);
                    DialogMaker dialogMaker = new DialogMaker(ContentSelectActivity.this, DialogMaker.SURVEY_FINISH, ContentSelectActivity.this);
                    dialogMaker.show();
                }else {
                    Log.d("<<<<<<<<<<<<<<<<<< 결과 업로드 실패", result[0]);
                }
            }
        };
    }

    private void saveSurveyFinish(String id) {
        File saveFile;
        if(Build.VERSION.SDK_INT < 29)
            saveFile = new File(Environment.getExternalStorageDirectory()+"/SSCR_SurveyCheck");
        else
            saveFile = this.getExternalFilesDir("/SSCR_SurveyCheck");

        if (!saveFile.exists()) {
            saveFile.mkdir();
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(saveFile+"/survey_check.txt", false));
            String value = "[{id:"+id+
                    ",category_id:"+getIntent().getStringExtra(SurveyActivity.CATEGORY_ID)+
                    ",date:"+new MakeDate().makeDateString() +
                    ",status:done}]";
            bufferedWriter.append(value);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}