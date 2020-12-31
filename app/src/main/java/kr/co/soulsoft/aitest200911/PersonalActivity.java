package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class PersonalActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_personal);
        setWidget();
    }

    private void setWidget() {

        findViewById(R.id.btnPersonalContent).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener;
    {
        onClickListener=  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<<<<<<<<<<<<test", "test");

                progressDialog = ProgressDialog.show(PersonalActivity.this, "맞춤형 콘텐츠 로드", "콘텐츠 데이터를 가져오는 중입니다");
                Timer timer = new Timer();
                timer.schedule(timerTask, 2000);
            }
        };
    }

    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            progressDialog.dismiss();
            Intent intent = new Intent(PersonalActivity.this, ContentRecmndActivity.class);
            startActivity(intent);
        }
    };
}