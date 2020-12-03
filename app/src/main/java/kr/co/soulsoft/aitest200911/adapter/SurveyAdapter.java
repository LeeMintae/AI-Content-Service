package kr.co.soulsoft.aitest200911.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import kr.co.soulsoft.aitest200911.R;

public class SurveyAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private JSONArray dataSource, parentData;
    private int pageCount = 0;

    public SurveyAdapter(Context context, int dataLength, JSONArray dataSource, JSONArray parentData) {
        this.context = context;
        // 준비 페이지 포함
        this.pageCount = dataLength+1;
        this.dataSource = dataSource;
        this.parentData = parentData;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view;
        layoutInflater = LayoutInflater.from(context);

        if (position==0) {
            view = layoutInflater.inflate(R.layout.survey_prepare, null);
            container.addView(view, null);
            return view;
        }
        int dataIndex = position - 1;
        view = layoutInflater.inflate(R.layout.survey_question_template, null);
        try {
            if (dataSource.getJSONObject(dataIndex).has("m_sub_id")) {
                String parentID = dataSource.getJSONObject(dataIndex).getString("m_survey_id");
                for(int index=0; index<parentData.length(); index++) {
                    if (parentData.getJSONObject(index).getString("m_survey_id").equals(parentID)) {
                        ((TextView)view.findViewById(R.id.tVwQuestion1)).setText(parentData.getJSONObject(index).getString("m_survey_text"));
                    }
                }
                ((TextView)view.findViewById(R.id.tVwQuestion2)).setText(dataSource.getJSONObject(dataIndex).getString("m_sub_text"));
            } else {
                view.findViewById(R.id.tVwQuestion1).setVisibility(View.GONE);
                view.findViewById(R.id.spaceQuestionGap).setVisibility(View.GONE);
                Log.d("<<<<<<<<<<<<<<<<<텍스트 확인",dataSource.getJSONObject(dataIndex).getString("m_survey_text") );
                ((TextView)view.findViewById(R.id.tVwQuestion2)).setText(Html.fromHtml(dataSource.getJSONObject(dataIndex).getString("m_survey_text"), Html.FROM_HTML_MODE_LEGACY));
            }
//            if (subSource.size() > 0) {
//                if (dataSource.getJSONObject(position).getInt("m_survey_parent") > 0) {
//
//                }
//            } else {
//                ((TextView)view.findViewById(R.id.tVwQuestion2)).setText(dataSource.getJSONObject(position).getString("m_survey_text"));
//                view.findViewById(R.id.tVwQuestion1).setVisibility(View.GONE);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        container.addView(view, null);
        return view;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
