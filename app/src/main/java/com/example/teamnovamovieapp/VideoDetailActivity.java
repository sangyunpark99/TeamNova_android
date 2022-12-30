package com.example.teamnovamovieapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity { // 시리즈 동영상 보는 액티비티

    private ArrayList<VideoData> videos = new ArrayList<>();

    private TextView videoTitle;
    private TextView videoSubTitle;
    private VideoView videoView;
    private RecyclerView recyclerView;

    private Utills utills;

    public int currentEpisode = 1; // 현재 보여줘야할 에피소드 - 기본값 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);


        // intent로 포스터에 맞는 애니메이션 제목 받아오기
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        utills = new Utills();

        videoTitle = findViewById(R.id.video_detail_title);
        videoSubTitle = findViewById(R.id.video_detail_subtitle);
        videoView = findViewById(R.id.video_detail_movieView);
        recyclerView = findViewById(R.id.video_detail_recyclerView);

        try {

            JSONObject json = getLocalJson(); // video.json 파일 가져오기
            setVideoResults(json,title); // 제목에 맞는 비디오 결과값 가져오기

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setVideoDetail(title); // 현재 회차에 맞는 비디오 설정하기

        // recyclerView 회차시 목록
        recyclerView.setAdapter(new VideoDetailAdapter(videos,videoSubTitle,videoView));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }

    public JSONObject getLocalJson() throws JSONException { // local json 데이터 가져오기
        String json= null;

        try{
            InputStream is = getAssets().open("video.json");

            int size = is.available();
            byte[] buffer = new byte[size]; // buffer를 사용해서 읽어오기

            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new JSONObject(json);
    }

    public void setVideoResults(JSONObject obj, String title) throws JSONException { // video에 대한 내용 설정하기

        JSONArray jsonArray = obj.getJSONObject(title).getJSONArray("results");

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String videoTitle = jsonObject.getString("title");
            String discription = jsonObject.getString("discription");
            String episode = jsonObject.getString("episode");
            String videoPath = jsonObject.getString("videoPath");

            VideoData data = new VideoData(videoTitle,discription,episode,videoPath);
            videos.add(data);
        }
    }

    public void setVideoDetail(String title){ // 전달받은 비디오 제목으로 비디오 영상 설정하기

        for(VideoData item : videos){ // 현재 회차시와 맞는 비디오 경로 설정
            if(item.episode.equals(currentEpisode + "화")){

                videoSubTitle.setText(currentEpisode+"화 : " + item.title); // 부제목 설정
                videoTitle.setText(title); // 제목 설정


                // 비디오 경로 설정
                String videoPath = "android.resource://" + getPackageName() + "/" + utills.getRawResIdByName(item.videoPath,this);

                Uri uri = Uri.parse(videoPath); // 비디오 경로 uri로 변경

                videoView.setVideoURI(uri); // video에 uri 첨부

                MediaController mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);

                mediaController.setAnchorView(videoView);

                videoView.start();
            }
        }
    }
}