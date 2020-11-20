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
                    contentListAdapter = new ContentListAdapter(resultData);
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
}