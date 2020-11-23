package kr.co.soulsoft.aitest200911.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import kr.co.soulsoft.aitest200911.R;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {

    private final JSONArray mData;
    private final String YOUTUBE_API_KEY = "AIzaSyDEphcTVCTcqd4bn3fPF6QdPoRa8MZcfCA";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox ckbContentSelect;
//        YouTubePlayerView ytuContent;
        ImageView imgVwYoutubeThumb;
        RatingBar rtbLikePoint;

        ViewHolder(View itemView) {
            super(itemView);

            ckbContentSelect = itemView.findViewById(R.id.ckbContentSelect);
//            ytuContent = itemView.findViewById(R.id.ytuVwContent);
            imgVwYoutubeThumb = itemView.findViewById(R.id.imgVwYoutubeThumb);
            rtbLikePoint = itemView.findViewById(R.id.rtbLikePoint);
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
//        YouTubePlayerView.OnInitializedListener onInitializedListener = new YouTubePlayerView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        };
//
//        try {
//            Log.d("<<<<<<<<<<<<콘텐츠 URL", mData.getJSONObject(position).getString("m_yctnt_url"));
//            String[] urlSource = mData.getJSONObject(position).getString("m_yctnt_url").split("v=");
//            holder.ytuContent.play(urlSource[1], onInitializedListener);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        try {
            String[] urlSource = mData.getJSONObject(position).getString("m_yctnt_url").split("v=");
//            new searchTask().execute(urlSource[1]);
            new YoutubeAsyncTask().execute(urlSource[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private class YoutubeAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpTransport httpTransport = new NetHttpTransport();
                final com.google.api.client.json.JsonFactory jsonFactory = new JacksonFactory();
                final long NUMBER_OF_VIDEOS_RETURNED = 1;

                YouTube youTube = new YouTube.Builder(httpTransport, jsonFactory, new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {

                    }
                }).setApplicationName("나만의 다이어트").build();
                YouTube.Search.List search = youTube.search().list("id,snippet");
                search.setKey(YOUTUBE_API_KEY);
                search.setQ(params[0]);
                search.setType("video");
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                SearchListResponse searchListResponse = search.execute();

                List<SearchResult> searchResultList = searchListResponse.getItems();

                if (searchListResponse != null) {
                    setData(searchResultList.iterator());
                }
            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                System.err.println("There was a service error 2: " + e.getLocalizedMessage() + " , " + e.toString());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }

    private void setData(Iterator<SearchResult> iterator) {
        if (!iterator.hasNext()) {
            Log.i("<<<<<<<<<<<< No Result", "Any result for your query");
        }

        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            SearchResult singleVideo = iterator.next();
            ResourceId resourceId = singleVideo.getId();

            if (resourceId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = (Thumbnail)singleVideo.getSnippet().getThumbnails().get("default");
                sb.append("ID : " + resourceId.getVideoId() + " , 제목 : " + singleVideo.getSnippet().getTitle() + " , 썸네일 주소 : " + thumbnail.getUrl());
                sb.append("\n");

                Log.d("<<<<<<<<<<<<<<결과 데이터 ", sb.toString());
            }
        }
    }

    private class searchTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String response;
            HttpsURLConnection connection;
            try {
                Log.d("<<<<<<<<<<<<<< ID ", params[0]);
                connection = (HttpsURLConnection)(new URL("https://www.googleapis.com/youtube/v3/search?q="+params[0]+"&key="+YOUTUBE_API_KEY+"&part=snippet")).openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
//                PrintWriter printWriter = new PrintWriter(outputStreamWriter);
//                printWriter.flush();


//                response = (new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))).readLine();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("Response Error", "응답 실패");
                    return null;
                } else {
                    Log.d("Response Error", "응답 성공");
                }

                InputStream is = connection.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String result;
                while((result = br.readLine())!=null){
                    sb.append(result+"\n");
                }
                result = sb.toString();

                Log.d("요청 결과 값", result);


                getYoutubeData(new JSONObject(result));

//                printWriter.close();
//                outputStreamWriter.close();
            } catch (Exception e) {
                Log.e("Call at : "+e.getStackTrace()[1].getClassName()+" "+
                        e.getStackTrace()[1].getMethodName(), "Exception Occur");
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    private void getYoutubeData(JSONObject jsonObject) throws JSONException {
        JSONArray contents = jsonObject.getJSONArray("items");

        for (int i=0; i <contents.length(); i++) {
            JSONObject data = contents.getJSONObject(i);
            String imgUrl = data.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");



        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }
}
