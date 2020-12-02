package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class ContentViewerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private YouTubePlayerView youTubePlayerView;
    private final static String KEY = "AIzaSyDEphcTVCTcqd4bn3fPF6QdPoRa8MZcfCA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_viewer);
        setWidget();
    }

    private void setWidget() {
        youTubePlayerView = findViewById(R.id.ytVwContent);
        youTubePlayerView.initialize(KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(getIntent().getStringExtra("CONTENT_ID"));
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("<<<<<<<<<<<<< Initiallization Failed", youTubeInitializationResult.toString());
        Toast.makeText(getBaseContext(), "유튜브 플레이어 초기화 실패", Toast.LENGTH_SHORT).show();
    }
}