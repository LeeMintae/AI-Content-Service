package kr.co.soulsoft.aitest200911;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.soulsoft.aitest200911.utils.MakeID;

public class ParticipantActivity extends AppCompatActivity {

    private ConnectivityManager connectivityManager;

    private int participantGender;
    private static final int MAX_AGE = 80;
    private static final int MIN_AGE = 14;
    private static final int MAX_HEIGHT = 250;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WEIGHT = 300;
    private static final int MIN_WEIGHT = 30;
    private final int HEALTH_EVAL_THRESHOLD = 999;
    private int participantHealthEval = HEALTH_EVAL_THRESHOLD;

    public final static String PARTICIPANT_INFO = "PARTICIPANT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);
        setWidget();
    }

    private void setWidget() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("남자");
        arrayList.add("여자");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.spinner_item_template, arrayList);

        ((Spinner)findViewById(R.id.spnGender)).setPrompt("성별 선택");
        ((Spinner)findViewById(R.id.spnGender)).setAdapter(adapter);
        ((Spinner)findViewById(R.id.spnGender)).setOnItemSelectedListener(onGenderSelect);

        ((RadioGroup)findViewById(R.id.rGrpHealthEvaluation)).setOnCheckedChangeListener(onCheckedChangeListener);
        ((RadioGroup)findViewById(R.id.rGrpAgree)).setOnCheckedChangeListener(onCheckedChangeListener);
        findViewById(R.id.btnMainSurvey).setOnClickListener(clickMainSurvey);
    }

    private final Spinner.OnItemSelectedListener onGenderSelect;
    {
        onGenderSelect = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (((TextView)view).getText().toString()) {
                    case "남자":
                        participantGender = 1;
                        break;
                    case "여자":
                        participantGender = 0;
                        break;
                }
                Log.d("참여자 성별", participantGender+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private final RadioGroup.OnCheckedChangeListener onCheckedChangeListener;
    {
        onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getId()) {
                    case R.id.rGrpHealthEvaluation:
                        ArrayList<Integer> healthEval = new ArrayList<>();
                        for (int index=0; index < group.getChildCount(); index++) {
                            healthEval.add(group.getChildAt(index).getId());
                        }
                        participantHealthEval = healthEval.indexOf(checkedId);
                        break;
                    case R.id.rGrpAgree:
                        if (checkedId == R.id.rBtnAgree) {
                            findViewById(R.id.btnMainSurvey).setEnabled(true);
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.notify_agree), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.btnMainSurvey).setEnabled(false);
                        }
                        break;
                    default:
                        break;
                }

            }
        };
    }

    private final View.OnClickListener clickMainSurvey;
    {
        clickMainSurvey = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = ((EditText)findViewById(R.id.eTxtAge)).getText().toString();
                if (age.equals("")) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_age), Toast.LENGTH_SHORT).show();
                    return;
                }
                int ageCount = Integer.parseInt(age);
                if (ageCount > MAX_AGE || ageCount < MIN_AGE) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_age_fault), Toast.LENGTH_SHORT).show();
                    return;
                }
                String height = ((EditText)findViewById(R.id.eTxtHeight)).getText().toString();
                if (height.equals("")) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_height), Toast.LENGTH_SHORT).show();
                    return;
                }
                int heightCount = Integer.parseInt(height);
                if (heightCount > MAX_HEIGHT || heightCount < MIN_HEIGHT) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_height_fault), Toast.LENGTH_SHORT).show();
                    return;
                }
                String weight = ((EditText)findViewById(R.id.eTxtWeight)).getText().toString();
                if (weight.equals("")) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_weight), Toast.LENGTH_SHORT).show();
                    return;
                }
                int weightCount = Integer.parseInt(weight);
                if (weightCount > MAX_WEIGHT || weightCount < MIN_WEIGHT) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_weight_fault), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (participantHealthEval > 4) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_healtheval), Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = ((EditText)findViewById(R.id.eTxtEmail)).getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] emailCount = email.split("@");
                if (emailCount.length < 2) {
                    Toast.makeText(getBaseContext(), getString(R.string.notify_input_email_fault), Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> userInfo = new ArrayList<>();
                userInfo.add("id");
                userInfo.add(participantGender+"");
                userInfo.add(age);
                userInfo.add(height);
                userInfo.add(weight);
                userInfo.add(participantHealthEval+"");
                userInfo.add(email);
                Log.d("(((((((((((( 사용자 정보 )))))))", userInfo.toString());

                Intent i = new Intent(ParticipantActivity.this, SurveyActivity.class);
                i.putExtra(PARTICIPANT_INFO, userInfo);

                if (checkDate()) {
                    startActivity(i);
                };
            }
        };
    }

    private boolean checkDate() {
        Date mDate = new Date(System.currentTimeMillis());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd");
        String today = simpleDateFormat.format(mDate);

        if (Integer.parseInt(today) < 1123) {
            new AlertDialog.Builder(this).setTitle(getString(R.string.msg_survey_popup)).setMessage(getString(R.string.msg_survey_dday_alarm)).setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finishAffinity();
                            System.runFinalization();
                            System.exit(0);
                        }
                    }).show();
            return false;
        } else {
            return true;
        }
    }



    private boolean networkCheck() {
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network index : networks) {
            networkInfo = connectivityManager.getNetworkInfo(index);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                return true;
            }
        }
        Toast.makeText(getBaseContext(), "네트워크가 연결되어있지 않습니다. 데이터 네트워크 혹은 와이파이를 연결해 주세요", Toast.LENGTH_SHORT).show();
        return false;
    }
}