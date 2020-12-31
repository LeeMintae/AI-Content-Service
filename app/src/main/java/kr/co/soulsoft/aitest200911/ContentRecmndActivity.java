package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class ContentRecmndActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_content_recmnd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setWidget();
    }


    private void setWidget() {
        findViewById(R.id.btnPContent01).setOnClickListener(onClickListener);
        findViewById(R.id.btnPContent02).setOnClickListener(onClickListener);
        findViewById(R.id.btnPContent03).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener;
    {
        onClickListener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ContentViewerActivity.class);
                switch (v.getId()) {
                    case R.id.btnPContent01:
                        intent.putExtra("CONTENT_ID", "SMXKWVyI96Y");
                        break;
                    case R.id.btnPContent02:
                        intent.putExtra("CONTENT_ID", "s14NQ6Cz4QE");
                        break;
                    case R.id.btnPContent03:
                        intent.putExtra("CONTENT_ID", "3VouSaW_LPw");
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        };
    }
}