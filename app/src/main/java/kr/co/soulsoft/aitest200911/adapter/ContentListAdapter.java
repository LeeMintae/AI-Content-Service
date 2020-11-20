package kr.co.soulsoft.aitest200911.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import org.json.JSONArray;

import kr.co.prnd.YouTubePlayerView;
import kr.co.soulsoft.aitest200911.R;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {

    private JSONArray mData = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox ckbContentSelect;
        YouTubePlayerView ytuContent;
        RatingBar rtbLikePoint;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * Modify Req
     * @param arrayList YouTube Content List data
     */
    public ContentListAdapter(JSONArray arrayList) {
        mData = arrayList;
    }

    @NonNull
    @Override
    public ContentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_item_template, parent, false);
        return new ContentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentListAdapter.ViewHolder holder, int position) {
        YouTubePlayerView.OnInitializedListener onInitializedListener = new YouTubePlayerView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        YouTubePlayer.Provider provider = new YouTubePlayer.Provider() {
            @Override
            public void initialize(String s, YouTubePlayer.OnInitializedListener onInitializedListener) {

            }
        };

        holder.ytuContent.play(null, onInitializedListener);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    Log.d("Content Select", v.getTag().toString());
                }
            }
        };
        holder.ckbContentSelect.setOnClickListener(onClickListener);


        RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        };

        holder.rtbLikePoint.setOnRatingBarChangeListener(onRatingBarChangeListener);
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }
}
