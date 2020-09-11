package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Test temp = new Test();
    }

    public class Test{
        public Test(){}
        public Test(String value){

        }
    }
}