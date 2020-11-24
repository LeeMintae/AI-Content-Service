package kr.co.soulsoft.aitest200911;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.soulsoft.aitest200911.adapter.SurveyAdapter;
import kr.co.soulsoft.aitest200911.data.DatabaseRequest;
import kr.co.soulsoft.aitest200911.data.Example;
import kr.co.soulsoft.aitest200911.utils.DialogMaker;

public class SurveyActivity extends FragmentActivity {

    // region Value Definition
    private ArrayList<String> participantInfo;
    private String categoryID, categoryName;
//    private CustomTTS customTTS;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
//    private ProgressTimer progressTimer;
    private ViewPager pager;
    private JSONArray selectedData;
    private ArrayList<JSONArray> subData;
    private ArrayList<String> subHasIds;

    private JSONArray totalData;

    private int currentDataIndex, currentPosition;
    private String currentMessage;
    private int timeLimits;

    private ArrayList<String> P_RECORD;

    private ArrayList<Integer> btnAnswerIDs;

    //    private VoiceHandler mVoiceHandler;
    private Bundle bundle;
    private String message;

    private Boolean pageHolding = false;

    private String startTime;
    private ArrayList<String> ANSWER_RECORDS;
    public static final String SURVEY_ANSWER = "SURVEY_ANSWER";
    public static final String CATEGORY_ID = "CATEGORY_ID";


    private boolean[] alreadyChecked = {false, false};
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        setContentView(R.layout.activity_survey);
        setWidget();
        setSurveyInfo();
    }

    private void setWidget() {
        setFullScreen();

        btnAnswerIDs = new ArrayList<>();
//        for (int i=1; i<7; i++)
//            btnAnswerIDs.add(getResources().getIdentifier("rBtnAnswer"+i, "id", "kr.co.soulsoft.aitest200911"));
        for (int i=0; i<6; i++) {
            btnAnswerIDs.add(((RadioGroup)findViewById(R.id.rGrpAnswer)).getChildAt(i).getId());
            findViewById(btnAnswerIDs.get(i)).setOnClickListener(answerClickListener);
        }
//        ((RadioGroup)findViewById(R.id.rGrpAnswer)).setOnCheckedChangeListener(checkedChangeListener);

        findViewById(R.id.btnNextSurvey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageChanger("NEXT");
            }
        });
        findViewById(R.id.btnPrevSurvey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageChanger("PREV");
            }
        });
    }
    private void setFullScreen() {
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private final View.OnClickListener answerClickListener;
    {
        answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String survey_id, sub_id;
                    if (totalData.getJSONObject(currentDataIndex).has("m_sub_id")) {
                        survey_id = totalData.getJSONObject(currentDataIndex).getString("m_survey_id");
                        sub_id = totalData.getJSONObject(currentDataIndex).getString("m_sub_id");
                        addAnswer(btnAnswerIDs.indexOf(v.getId()), startTime, getCurrentTime(), survey_id, sub_id);
                    } else {
                        survey_id = totalData.getJSONObject(currentDataIndex).getString("m_survey_id");
                        addAnswer(btnAnswerIDs.indexOf(v.getId()), startTime, getCurrentTime(), survey_id);
                    }

                    Log.i("응답 정보 : ", ANSWER_RECORDS.toString());
                    pageHolding = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void addAnswer(int answerNumber, String... answerDatas) {
        int index = 0;
        // 응답번호 첫번째 1 처리
        int number = answerNumber + 1;

        // 기 존재하는 응답의 경우 다시 쓰기 작업
        for (String mainRecord : ANSWER_RECORDS) {
            // 설문 항목이 존재하는지 체크
            if (mainRecord.contains(answerDatas[2])) {
                // 설문 하위 항목 입력 여부
                if (answerDatas.length > 3) {
                    // 설문 하위 항목이 존재하는지 체크
                    String temp = answerDatas[3];
                    int subIndex = 0;
                    for (String subRecord : ANSWER_RECORDS) {
                        if (subRecord.contains(answerDatas[3])) {
                            ANSWER_RECORDS.set(subIndex, answerDatas[2] + "/"+answerDatas[3]+"/" + answerDatas[0] + "/" + answerDatas[1] + "/" + number);
                            return;
                        }
                        subIndex++;
                    }
                    // 하위 항목은 존재하지만 새로운 응답일 경우
                    ANSWER_RECORDS.add(answerDatas[2] + "/" + answerDatas[3] + "/" + answerDatas[0] + "/" + answerDatas[1] + "/" + number);
                    return;
                }
                ANSWER_RECORDS.set(index, answerDatas[2] + "//" + answerDatas[0] + "/" + answerDatas[1] + "/" + number);
                return;
            }
            index++;
        }
        // 신규 응답일 경우
        if (answerDatas.length > 3) {
            ANSWER_RECORDS.add(answerDatas[2] + "/"+answerDatas[3] + "/" + answerDatas[0] + "/" + answerDatas[1] + "/" + number);
        } else {
            ANSWER_RECORDS.add(answerDatas[2] + "//" + answerDatas[0] + "/" + answerDatas[1] + "/" + number);
        }
    }

//    private final RadioGroup.OnCheckedChangeListener checkedChangeListener;
//    {
//        checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
//            @SuppressLint("ResourceType")
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId > 0 && !alreadyChecked[0]) {
//                    alreadyChecked[0] = true;
//                    alreadyChecked[1] = false;
//                }
//            }
//        };
//    }

    private void setCategory() {
        categoryID = "cat_20201111141225";
        categoryName = "사용자 유형 분석";
    }

    private void setSurveyInfo() {
        participantInfo = getIntent().getStringArrayListExtra("PARTICIPANT_INFO");
//        categoryID = getIntent().getStringExtra("CATEGORY_ID");
//        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        setCategory();
        progressDialog = ProgressDialog.show(SurveyActivity.this, "설문 정보 설정", "설문을 불러오는 중입니다");

        subData = new ArrayList<>();
        new DatabaseRequest(getBaseContext(), findSurveyResult).execute("GET_SURVEY", categoryID);
//        btnAnswerIDs = new ArrayList<>();
        ANSWER_RECORDS = new ArrayList<>();
    }

    private final DatabaseRequest.ExecuteListener findSurveyResult;
    {
        findSurveyResult = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[0] == null) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), getString(R.string.msg_survey_absent), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    selectedData = new JSONArray(result[0]);
                    subHasIds = new ArrayList<>();
                    for (int i = 0; i < selectedData.length(); i++)
                        if (selectedData.getJSONObject(i).getInt("m_survey_parent") > 0)
                            subHasIds.add(selectedData.getJSONObject(i).getString("m_survey_id"));
                    // 하위 항목 체크
                    if (subHasIds.size() > 0) {
                        for (int index = 0; index < subHasIds.size(); index++)
                            new DatabaseRequest(getBaseContext(), findSurveySubResult).execute("GET_SUB", subHasIds.get(index));
                    } else {
                        setPagerAdapter();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    private final DatabaseRequest.ExecuteListener findSurveySubResult;
    {
        findSurveySubResult = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                if (result[0] == null) {
                    Log.e("하위 항목", "하위 항목 존재하지 않음");
                    return;
                }
                try {
                    subData.add(new JSONArray(result[0]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // ***호출된 횟수 누적으로 하위 설문이 모두 얻어졌을때 페이지 설정 실행
                if (subData.size() == subHasIds.size()) {
                    setPagerAdapter();
                    progressDialog.dismiss();
                }
            }
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPagerAdapter() {
        pager = (ViewPager) findViewById(R.id.vPgrQuestion);
        int dataLength = 0;

        // 하위 항목이 존재할 경우 하위 항목 페이지 수를 다 더하고 겹치는 페이지 수 제거(하위 항목 갯수)
//        if (subData!=null) {
//            for(int index=0; index<subData.size(); index++) {
//                dataLength += subData.get(index).length();
//            }
//            dataLength -= subData.size();
//            Log.d("서브 항목 수", subData.size()+"");
//            Log.d("총 페이지 수", dataLength+"");
//        }
        totalData = new JSONArray();
        try {
            for (int index = 0; index < selectedData.length(); index++) {
                totalData.put(selectedData.getJSONObject(index));
                String tempID = selectedData.getJSONObject(index).getString("m_survey_id");
                if (subHasIds.contains(tempID)) {
                    for (int i = 0; i < subData.size(); i++) {
                        for (int j = 0; j < subData.get(i).length(); j++) {
                            if (subData.get(i).getJSONObject(j).getString("m_survey_id").equals(tempID)) {
                                totalData.put(subData.get(i).getJSONObject(j));
                            }
                        }
                    }
                }
            }
            dataLength = totalData.length();
            Log.i("Total Survey Length : ", dataLength + " 문항");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pager.setAdapter(new SurveyAdapter(getBaseContext(), dataLength, totalData, selectedData));
        // 준비 페이지 포함 되어 1장 추가
        pager.setOffscreenPageLimit(dataLength + 1);
        pager.addOnPageChangeListener(onPageChangeListener);
//        customTTS.speakMessage(getString(R.string.msg_survey_prepare));

        // 응답 후 진행을 위한 터치 리스너 처리
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return pageHolding;
            }
        });
    }

    private final ViewPager.OnPageChangeListener onPageChangeListener;
    {
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == pager.getChildCount()) {
                    // Youtube Content Survey Start
                    Intent i = new Intent(SurveyActivity.this, ContentSelectActivity.class);
                    startActivity(i);
//                    sendReply();
                }
            }

            @Override
            public void onPageSelected(int position) {
                pageHolding = true;
                currentPosition = position;
                // 페이지 표시 설정
                String indexText = currentPosition >= 10 ? (currentPosition)+"":"0"+(currentPosition);
                indexText = indexText+"/"+((pager.getChildCount()-1)+"");
                ((TextView)findViewById(R.id.tvwPageNumber)).setText(indexText);
                findViewById(R.id.rGrpAnswer).setVisibility(View.VISIBLE);
                if (position == 0) {
                    for (int index = 0; index < 6; index++) {
                        ((RadioButton) findViewById(btnAnswerIDs.get(index))).setText("");
                    }
                    for (boolean value : alreadyChecked) {
                        value = false;
                    }
                    ((RadioGroup)findViewById(R.id.rGrpAnswer)).clearCheck();
                    findViewById(R.id.rGrpAnswer).setVisibility(View.INVISIBLE);

//                    customTTS.speakMessage(getString(R.string.msg_survey_prepare));
                    return;
                }
                currentDataIndex = position - 1;
                currentMessage = ((TextView) pager.getChildAt(position).findViewById(R.id.tVwQuestion2)).getText().toString();
//                customTTS.speakMessage(currentMessage);
                List<Example> exampleList = new ArrayList<>();
                ArrayList<String> exTexts = new ArrayList<>();
                try {
                    if (totalData.getJSONObject(currentDataIndex).has("m_sub_id")) {
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex1"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex2"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex3"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex4"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex5"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_sub_ex6"));
                        //초 단위로 환산하기 위해 +0
                        timeLimits = Integer.parseInt(totalData.getJSONObject(currentDataIndex).getString("m_sub_time") + "0");
                    } else {
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex1"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex2"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex3"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex4"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex5"));
                        exTexts.add(totalData.getJSONObject(currentDataIndex).getString("m_survey_ex6"));
                        timeLimits = Integer.parseInt(totalData.getJSONObject(currentDataIndex).getString("m_survey_time") + "0");
                    }
                    // 0이 들어갈 경우 TTS Duration 에러 발생
                    timeLimits = timeLimits == 0 ? 1 : timeLimits;
                    for (int index = 0; index < exTexts.size(); index++) {
                        if (exTexts.get(index).equals("null") || exTexts.get(index).equals("")) {
                            break;
                        }
                        exampleList.add(new Example((index + 1) + "", exTexts.get(index)));
                    }
                    for (int index = 0; index < 6; index++) {
                        if (index >= exampleList.size()) {
                            findViewById(btnAnswerIDs.get(index)).setEnabled(false);
                        } else {
                            findViewById(btnAnswerIDs.get(index)).setEnabled(true);
                        }
                        ((RadioButton) findViewById(btnAnswerIDs.get(index))).setText("");

                    }
                    for (boolean value : alreadyChecked) {
                        value = false;
                    }
                    ((RadioGroup)findViewById(R.id.rGrpAnswer)).clearCheck();

                    markAnswer();

                    for (int index = 0; index < exampleList.size(); index++) {
                        ((RadioButton) findViewById(btnAnswerIDs.get(index))).setText(exampleList.get(index).getExampleNumber() + ". " + exampleList.get(index).getExampleText());
                    }


//                    ExampleAdapter exampleAdapter = new ExampleAdapter(SurveyActivity.this, R.layout.survey_answer_template, exampleList);
//                    ((ListView)findViewById(R.id.lVwAnswerExample)).setAdapter(exampleAdapter);
//                    ((ListView)findViewById(R.id.lVwAnswerExample)).setOnItemClickListener(onItemClickListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("질문 시작 시간", getCurrentTime());
                startTime = getCurrentTime();
                if (((RadioButton)findViewById(btnAnswerIDs.get(0))).getText().toString().equals("")) {
                    pageHolding = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private void markAnswer() {
        int index = 0;
        String survey_id = null, sub_id = null;

        try {
            if (totalData.getJSONObject(currentDataIndex).has("m_sub_id")) {
                survey_id = totalData.getJSONObject(currentDataIndex).getString("m_survey_id");
                sub_id = totalData.getJSONObject(currentDataIndex).getString("m_sub_id");
            } else {
                survey_id = totalData.getJSONObject(currentDataIndex).getString("m_survey_id");
            }
            if (totalData.getJSONObject(currentDataIndex).get("m_survey_parent").equals("1") && sub_id == null ) return;
            Log.i("응답 정보 : ", ANSWER_RECORDS.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (survey_id == null) return;

        for (String record : ANSWER_RECORDS) {
            if (record.contains(survey_id)) {
                if (sub_id != null) {
                    if (record.contains(sub_id)) {
//                        String answer = ANSWER_RECORDS.get(index);
//                        String[] value = answer.split("/");
//                        int indexofValue = Integer.valueOf(value[4])-1;
//                        int chkID = btnAnswerIDs.get(indexofValue);
                        ((RadioButton)findViewById(btnAnswerIDs.get(Integer.valueOf((ANSWER_RECORDS.get(index).split("/"))[4])-1))).setChecked(true);
                        pageHolding = false;
                        return;
                    }
                } else {
//                    String answer = ANSWER_RECORDS.get(index);
//                    String[] value = answer.split("/");
//                    int indexofValue = Integer.valueOf(value[4])-1;
//                    int chkID = btnAnswerIDs.get(indexofValue);
                    ((RadioButton)findViewById(btnAnswerIDs.get(Integer.valueOf((ANSWER_RECORDS.get(index).split("/"))[4])-1))).setChecked(true);
                    pageHolding = false;
                    return;
                }
            }
            index++;
        }
    }

    /**
     *
     * @param direct 페이지 전환 방향
     */
    private void pageChanger(String direct) {
        int temp = pager.getCurrentItem();
        int temp2 = pager.getChildCount();

        Log.d("현재 페이지 상태", temp+"/"+temp2);
        switch (direct) {
            case "NEXT":
                if (currentPosition != 0 && pageHolding ==  true) {
                    Toast.makeText(getBaseContext(), getString(R.string.msg_survey_choiceyet), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pager.getCurrentItem() != (pager.getChildCount()-1)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(pager.getCurrentItem()+1, true);
                        }
                    });
                } else {
                    Intent i = new Intent(SurveyActivity.this, ContentSelectActivity.class);
                    i.putExtra(ParticipantActivity.PARTICIPANT_INFO, participantInfo);
                    int index = 0;
                    StringBuilder recordValue = new StringBuilder();
                    for (String value : ANSWER_RECORDS) {
                        if (index!=0) {
                            recordValue.append(",");
                        }
                        recordValue.append(value);
                        index++;
                    }
                    i.putExtra(SURVEY_ANSWER, recordValue.toString());
                    i.putExtra(CATEGORY_ID, categoryID);

                    startActivity(i);
                }
                break;
            case "PREV":
                if (pager.getCurrentItem() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(pager.getCurrentItem()-1, true);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), getString(R.string.msg_survey_firstpage), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    private void sendReply(){
        int index = 0;
        String recordValue = "";
        for (String value : ANSWER_RECORDS) {
            if (index!=0) {
                recordValue += ",";
            }
            recordValue += value;
            index++;
        }
        new DatabaseRequest(getBaseContext(), replyUploadListener).execute("REPLY", categoryID, participantInfo.get(0), recordValue);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "설문이 종료되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        SurveyActivity.this.finish();
    }


    DatabaseRequest.ExecuteListener replyUploadListener;
    {
        replyUploadListener = new DatabaseRequest.ExecuteListener() {
            @Override
            public void onResult(String... result) {
                progressDialog.dismiss();
                switch (result[0]) {
                    case "INSERT_OK":
                        Toast.makeText(getBaseContext(), "설문 결과 업로드 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case "INSERT_FAIL":
                        Toast.makeText(getBaseContext(), "설문 업로드 실패", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat clickFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return clickFormat.format(date);
    }

    @Override
    public void onBackPressed() {
        DialogMaker dialogMaker = new DialogMaker(SurveyActivity.this, DialogMaker.EXIT_CONFIRM, this);
        dialogMaker.show();
    }
}