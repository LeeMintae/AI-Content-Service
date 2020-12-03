package kr.co.soulsoft.aitest200911.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import kr.co.soulsoft.aitest200911.utils.MakeID;
import kr.co.soulsoft.aitest200911.utils.PreferenceSetting;

public class DatabaseRequest extends AsyncTask<String, String, String> {
    //region Value&Interface
    private static String SERVER_IP;
//    private final static String PHP_URL = "/soul/res/php/request_handler.php";
    private final static String PHP_URL = "/sscr/res/php/request_handler.php";
    private final String ID = "PARTICIPANT_ID=";
    private final String USE = "USE=";
    private String dbUse;

    public final static String INSERT = "INSERT";
    public final static String REPLY = "REPLY";

    public interface ExecuteListener {
        void onResult(String... result);
    }

    private final ExecuteListener executeListener;
    private static String currentUserID;
    //endregion

    public DatabaseRequest(Context context, ExecuteListener executeListener) {
        SERVER_IP = new PreferenceSetting(context).loadPreference(0);
        Log.d("서버 IP", SERVER_IP);
        this.executeListener = executeListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String response;
        String parameters;
        dbUse = params[0];
        String CATEGORY_ID = "CATEGORY_ID=";
        String SURVEY_ID = "SURVEY_ID=";
        String REPLIES = "REPLIES=";
        String CONTENTS = "CONTENTS=";
        switch (dbUse) {
//            case "INSERT":
//                Log.d("설문 참여자 정보", params[1] + params[2] + params[3]);
//                parameters = makeParameter(params[0], params[1], params[2], params[3]);
//                break;
            case "INSERT":
                parameters = makeParameter(params);
                Log.d("[[[[[[파라메터 확인", parameters);
                break;
            case "GET_ALL_CATEGORY":
            case "GET_CONTENT": // Get YouTube Content List
                parameters = USE+params[0];
                break;
            case "GET_SURVEY":
                parameters = USE+params[0]+"&"+ CATEGORY_ID +params[1];
                Log.d("[[[[[[파라메터 확인", parameters);
                break;
            case "GET_SUB":
                parameters = USE+params[0]+"&"+ SURVEY_ID +params[1];
                break;
            case "REPLY":
//                String[] temp = params[2].split(",");
//                ArrayList<String> replyData = new ArrayList<>();
//                for (String value : temp) {
//                    replyData.add(value);
//                }
//                parameters = USE+params[0]+"&"+ID+params[1]+"&"+REPLIES+replyData;
                parameters = USE+params[0]+"&"+
                        CATEGORY_ID +params[1]+"&"+
                        ID+params[2]+"&"+
                        REPLIES +params[3]+"&"+
                        CONTENTS +params[4];
                Log.i("응답 확인", parameters);
                break;
            default:
                Log.e("USE 파라메터 에러", params[0]);
                return null;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection)(new URL("http://"+SERVER_IP+PHP_URL)).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.write(parameters);

            printWriter.flush();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("Response Error", "응답 실패");
                return null;
            }
            response = (new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))).readLine();
            Log.d("요청 결과 값", response);

            printWriter.close();
            outputStreamWriter.close();

        } catch (Exception e) {
            Log.e("Call at : "+e.getStackTrace()[1].getClassName()+" "+
                    e.getStackTrace()[1].getMethodName(), "Exception Occur");
            return null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        switch (dbUse) {
            case INSERT:
                Log.d("<<<<<<<<<<<<<생성 ID", currentUserID);
                executeListener.onResult(currentUserID, response);
                break;
            case "GET_ALL_CATEGORY":
            case "GET_SURVEY":
            case "GET_SUB":
            case "REPLY":
            case "GET_CONTENT":
                executeListener.onResult(response);
                break;
            default:
                break;
        }
        super.onPostExecute(response);
    }

    private String makeParameter(String... params) {
        currentUserID = new MakeID().getID();
        String AGE = "PARTICIPANT_AGE=";
        String GENDER = "PARTICIPANT_GENDER=";
        String HEIGHT = "PARTICIPANT_HEIGHT=";
        String WEIGHT = "PARTICIPANT_WEIGHT=";
        String HLEVEL = "PARTICIPANT_HLEVEL=";
        String EMAIL = "PARTICIPANT_EMAIL=";
        String CELLPHONE = "PARTICIPANT_CELLPHONE=";
        return USE+params[0]+"&"+
                ID+currentUserID+"&"+
                GENDER +params[1]+"&"+
                AGE +params[2]+"&"+
                HEIGHT +params[3]+"&"+
                WEIGHT +params[4]+"&"+
                HLEVEL +params[5]+"&"+
                EMAIL +params[6]+"&"+
                CELLPHONE + params[7];
    }
}
