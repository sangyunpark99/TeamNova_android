package com.example.teamnovamovieapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MovieSearchFragment extends Fragment {

    ArrayList<SearchMovieData> searchMovieList = new ArrayList<>();

    private Thread getMovieThread;

    private EditText movieSearchEditText;

    private SPHelper sh;

    private RecyclerView recyclerView;
    private RecyclerView saveSearchRecyclerView;

    private ProgressDialog progressDialog;

    private Handler handler;

    private ArrayList<String> movieSearchList = new ArrayList<>();

    private Utills utills = new Utills();

    private APIs api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_movie_search,container,false);

        movieSearchEditText = rootView.findViewById(R.id.movie_search_search_title);

        api = new APIs(this.getContext());

        initProgressDialogue(); // 다이얼로그 초기화

        recyclerView = rootView.findViewById(R.id.movie_search);

        saveSearchRecyclerView = rootView.findViewById(R.id.movie_save_search);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        MovieSearchSaveAdapter adapter = new MovieSearchSaveAdapter(sh, movieSearchEditText);

        saveSearchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        saveSearchRecyclerView.setAdapter(adapter);
        saveSearchRecyclerView.setVisibility(View.GONE);

        handler = new MovieSearchHandler(); // 영화 검

        Button movieSearchButton = rootView.findViewById(R.id.movie_search_search_button);

        movieSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // EditText focus 여부
            @Override
            public void onFocusChange(View view, boolean getFocus) {

                if(getFocus){ // 포커스를 얻었을때
                    saveSearchRecyclerView.setVisibility(View.VISIBLE); // recyclerView 보여주기
                }else{ // 포커스를 잃었을때
                    saveSearchRecyclerView.setVisibility(View.GONE); // recyclerView 안보여주기
                }
            }
        });

        movieSearchButton.setOnClickListener(new View.OnClickListener() { // 영화 검색 버튼을 클릭한 경우
            @Override
            public void onClick(View view) {

                movieSearchEditText.clearFocus();

                if(movieSearchEditText.getText().equals("") || movieSearchEditText.getText().length()==0){ // 영화제목을 검색하지 않은 경우
                    Toast.makeText(getContext(),"엉화제목을 검색해주세요",Toast.LENGTH_SHORT).show();
                }else{ // 영화제목을 검색한 경우

                    String title = movieSearchEditText.getText().toString(); // 검색한 영화 제목
                    title.replaceAll(" ",""); // 띄어쓰기 방지하기
                    sh.addSearchMovieData(title); // 검색한 영화 제목 SharedPreference에 임시저장
                    searchMovie(title); // Naver 영화 API 요청
                    saveSearchRecyclerView.setAdapter(new MovieSearchSaveAdapter(sh,movieSearchEditText));
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() { // 영화 검색 기록 recyclerView 클릭시
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                movieSearchEditText.clearFocus(); // editText 포커싱 해제

                imm.hideSoftInputFromWindow(movieSearchEditText.getWindowToken(),0); // 키보드 숨기
                return false;
            }
        });

        return rootView;
    }

    public void searchMovie(String searchMovieTitle){ // Naver 영화 Api 요청

        progressDialog.show(); // 영화 검색 시작전 로딩창 띄워주

       getMovieThread =  new Thread() {
           @Override
           public void run() {
               super.run();

               ArrayList<String> movieTitle = new ArrayList<>();
               movieTitle.add(searchMovieTitle);

               try {
                   searchMovieList = api.NaverMovieSearchApi(movieTitle);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       };

       getMovieThread.start(); // 쓰레드 시작
        handler.sendEmptyMessageDelayed(0,300);
    }

    public void initProgressDialogue(){ // progressDialogue 초기화
        progressDialog = new ProgressDialog(getActivity()); // 로딩 다이얼로그
        progressDialog.setMessage("영화 정보 갱신중...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sh = new SPHelper((AppCompatActivity) context);
    }

    class MovieSearchHandler extends Handler { // 영화 검색 결과를 업데이트 해주는 핸들러
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            MovieSearchAdapter adapter = new MovieSearchAdapter(searchMovieList); // 어댑터 생성

            progressDialog.dismiss(); // 로딩창 사라지게하기

            recyclerView.setAdapter(adapter); // recyclerView에 어댑터 추가
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // recyclerView layout 지정
        }
    }


    @Override
    public void onStop() { // 페이지 전환시
        super.onStop();

        movieSearchEditText.setText(""); // editText 값 클리어
    }
}
