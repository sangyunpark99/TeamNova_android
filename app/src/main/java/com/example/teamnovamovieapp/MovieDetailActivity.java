package com.example.teamnovamovieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetailActivity extends YouTubeBaseActivity {

    private Intent intent;

    private SearchMovieData data;

    private String title; // 검색 + youtube api에도 사용

    private String movieId = "";

    private APIs apis;

    YouTubePlayer.OnInitializedListener onInitalializedListener; // 유튜브 플레이

    Thread getYoutubeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView movieImage = findViewById(R.id.movie_detail_movieImage); // 영화 포스터 이미지
        TextView movieTitle = findViewById(R.id.movie_detail_movieTitle); // 영화 제목
        TextView movieRelease = findViewById(R.id.movie_detail_releaseData); // 영화 개봉 일자
        TextView movieDiscription = findViewById(R.id.movie_detail_movieDescritpion); // 영화 설명
        TextView movieDirector = findViewById(R.id.movie_detail_director); // 영화 감독
        TextView movieActor = findViewById(R.id.movie_detail_actor); // 영화 배우
        TextView userRating = findViewById(R.id.movie_detail_rating); // 영화 평점
        YouTubePlayerView youtubePlayer = findViewById(R.id.movie_detail_youtube_video); // 유튜브 비디오 플레이어

        apis = new APIs(this);

        intent = getIntent();

        if(getExtra()){ // intent로 전달받은 데이터가 존재하는 경우

            title = data.title.replaceAll("</b>",""); // 불필요한 문자 제거

            if(data.imageLink.equals("")){ // 전달받은 이미지가 업는 경우
                movieImage.setImageResource(R.drawable.ic_launcher_foreground); // 안드로이드 기본 이미지로 설정
            }else{ // 전달받은 이미지가 있는 경우
                Glide.with(this).load(Uri.parse(data.imageLink)).into(movieImage); // Glide로 이미지 설정
            }

            movieTitle.setText(title); // 영화 제목 설정
            movieRelease.setText(data.pubDate); // 영화 개봉 일자 설정
            movieDiscription.setText(data.description);// 영화 설명 링크 설정
            movieDirector.setText(data.director);// 영화 감독 이름 설정
            movieActor.setText(data.actor);// 영화 배우이름 설정
            userRating.setText(data.rating);// 영화 평점 설정
        } // 영화 상세 데이터 존재 여부 파악

        movieDiscription.setOnClickListener(new View.OnClickListener() { // 소개 클릭시 해당 영화 소개 링크
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(data.description)); // 웹 뷰로 링크에 대한 웹 페이지 보여줌
                startActivity(intent);
            }
        });

        setOnInitalializedListener(); // Youtube InitializeListener 초기화

        youtubePlayer.setOnClickListener(new View.OnClickListener() { // 유튜브 플레이어 클릭시
            @Override
            public void onClick(View view) {
                try{
                    getYouTube();
                    getYoutubeThread.join(); // Thread 끝날 때까지 기다리기
                }catch (Exception e){
                    e.printStackTrace();
                }

                youtubePlayer.initialize("AIzaSyBGOu-07GT1c6J3-Amv9Qe75HDTWpJuI5U",onInitalializedListener);
            }
        });


    }

    public void setOnInitalializedListener(){
        onInitalializedListener = new YouTubePlayer.OnInitializedListener(){ // 유튜브 영상 재생

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) { // 초기화 성공 한 경우
                youTubePlayer.loadVideo(movieId); // movieId로 영상 로드
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) { // 초기화 실패한 경우
                System.out.println(youTubeInitializationResult);
            }
        };
    }

    public Boolean getExtra(){ // intent extra 받아오기

        Gson gson = new Gson();

        if(intent.hasExtra("movieInfo")){ // 전달받은 데이터가 있는 경우

            String json = intent.getStringExtra("movieInfo"); // Intent로 부터 전달되는 String 값 받기

            data = gson.fromJson(json,SearchMovieData.class); // gson을 사용해서 클래스화 하기

            return true;
        }

        return false; // 전달받은 데이터가 없는 경우
    }

    public void getYouTube() { // youtube 영상 키값 받아오기

        getYoutubeThread = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    movieId = apis.youtubeApi(title);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        getYoutubeThread.start(); // 쓰레드 시작

    }

}