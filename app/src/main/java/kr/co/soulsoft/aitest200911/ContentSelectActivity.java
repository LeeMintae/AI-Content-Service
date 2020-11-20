package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import kr.co.soulsoft.aitest200911.adapter.ContentListAdapter;
import kr.co.soulsoft.aitest200911.data.DatabaseRequest;

public class ContentSelectActivity extends AppCompatActivity {

    private JSONArray resultData;
    private ArrayList<JSONArray> contentDataArray;
    private ProgressDialog progressDialog;
    private ContentListAdapter contentListAdapter;

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
        getContentList();
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
                    Log.d("<<<<<<<<< 콘텐츠 리스트", resultData.length()+"");
                    RecyclerView recyclerView = findViewById(R.id.rcyclrContentList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    recyclerView.setAdapter(contentListAdapter);
                    contentListAdapter = new ContentListAdapter(resultData);
                    for (int i=0; i < resultData.length(); i++) {

                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                try {
//                    selectedData = new JSONArray(result[0]);
//                    subHasIds = new ArrayList<>();
//                    for (int i = 0; i < selectedData.length(); i++)
//                        if (selectedData.getJSONObject(i).getInt("m_survey_parent") > 0)
//                            subHasIds.add(selectedData.getJSONObject(i).getString("m_survey_id"));
//                    // 하위 항목 체크
//                    if (subHasIds.size() > 0) {
//                        for (int index = 0; index < subHasIds.size(); index++)
//                            new DatabaseRequest(getBaseContext(), findSurveySubResult).execute("GET_SUB", subHasIds.get(index));
//                    } else {
//                        setPagerAdapter();
//                        progressDialog.dismiss();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        };
    }
}