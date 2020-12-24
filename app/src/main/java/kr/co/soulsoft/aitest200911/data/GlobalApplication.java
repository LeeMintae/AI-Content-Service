package kr.co.soulsoft.aitest200911.data;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

import kr.co.soulsoft.aitest200911.R;


public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSdk.init(this, getString(R.string.kakao_app_key));
    }
}
