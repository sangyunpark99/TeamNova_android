package com.example.teamnovamovieapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BoxOfficeFragment extends Fragment {

    ArrayList<SearchMovieData> boxOfficeMovie = new ArrayList<>();
    DBHelper dh;
    RecyclerView recyclerView;

    private Thread getBoxOfficeMovieThread;
    private Handler handler;
    private ProgressBar progressBar;
    private APIs api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_box_office,container,false);

        dh = new DBHelper(rootView.getContext());

        api = new APIs(this.getContext());

        progressBar = rootView.findViewById(R.id.box_office_progressBar);

        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.box_office); // recylcerView

        handler = new BoxOfficeHandler();

        if(dh.getBoxOffice().size()!=0){ // sqlite에 데이터가 저장되어 있는 경우
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2)); // recyclerView LayoutManager 달아주기
            recyclerView.setAdapter(new BoxOfficeAdapter(dh.getBoxOffice())); // recyclerView adapter 달아주기
        }else{ // sqlite에 데이터가 저장되어 있지 않은 경우
            getBoxOfficeMovie(); // api 호출해서 데이터 가져오기
        }

        return rootView;
    }

    public void getBoxOfficeMovie(){ // Box Office api 사용하여 OpenAPI 데이터 가져온 후, db에 데이터 저장

        progressBar.setVisibility(View.VISIBLE);

        getBoxOfficeMovieThread = new Thread(){

            @Override
            public void run() { // BoxOffice Api(박스오피스 순위) + Naver Search Api(영화 포스터) 데이터를 가져오는 Thread

                // BoxOffice API에는 영화에 대한 포스터가 존재하지 않아 BoxOffice API로 데이터를 받아온 후, 그 데이터를 이용하여 다시 Naver Search API를 사용해서 영화 포스터 이미지를 가져옴
                super.run();

                boxOfficeMovie.clear(); // 이전 boxOfficeMovie 누적 방지
                boxOfficeMovie = api.boxOfficeApi();

                handler.sendEmptyMessage(0);

            }
        };
        getBoxOfficeMovieThread.start();
    }

    public void setDatabase(){
        for(SearchMovieData movie : boxOfficeMovie){
            dh.insertBoxOffice(movie.title,movie.description, movie.imageLink, movie.subTitle, movie.pubDate, movie.director,movie.actor, movie.rating);
        }
    }

    class BoxOfficeHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            progressBar.setVisibility(View.GONE);
            setDatabase();
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2)); // recyclerView LayoutManager 달아주기
            recyclerView.setAdapter(new BoxOfficeAdapter(dh.getBoxOffice())); // recyclerView adapter 달아주기
        }
    }
}
