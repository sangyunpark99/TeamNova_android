package com.example.teamnovamovieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    private int noticePage = 0;
    private ArrayList<Fragment> fragments;

    private Utills utills;
    private DBHelper dh;
    private SPHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 초기화
        sh = new SPHelper(this);
        utills = new Utills();
        dh = new DBHelper(this);

        try {
            checkSubscribe(); // 구독 종료일 비교해서 확인하기
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Fragment boxOfficeFragment = new BoxOfficeFragment();
        Fragment movieSearchFragment = new MovieSearchFragment();
        Fragment movieRecordFragment = new MovieRecordFragment();
        Fragment mapFragment = new MapsFragment();
        Fragment videoFragment = new VideoFragment();


        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView); // bottomNavigation

        fragments = new ArrayList<Fragment>(Arrays.asList(boxOfficeFragment,movieSearchFragment,movieRecordFragment,mapFragment,videoFragment)); // 프래그먼트의 묶음

        replaceFragment(fragments.get(noticePage)); // HomeActivity 시작할때 boxOfficeFragment 페이지로 초기화

        bottomNavigation.setOnItemSelectedListener(item -> { // bottomNavigation에 존재하는 아이템을 클릭한 경우

            switch(item.getItemId()){
                case R.id.menu_box_office: // 홈 아이콘을 클릭한 경우
                    replaceFragment(boxOfficeFragment); // boxOfficeFragment 보여주기
                    noticePage = 0; // 현재 페이지 초기화하기
                    break;
                case R.id.menu_movie_search: // 영화 검색 아이콘을 클릭한 경우
                    replaceFragment(movieSearchFragment); // movieSearchFragment 보여주기
                    noticePage = 1; // 현재 페이지 초기화하기
                    break;
                case R.id.menu_movie_record: // 영화 감상문 아이콘을 클릭한 경우
                    replaceFragment(movieRecordFragment); // movieRecordFragment 보여주기
                    noticePage = 2; // 현재 페이지 초기화하기
                    break;

                case R.id.menu_movie_map: // 지도 마커 아이콘을 클릭한 경우
                    replaceFragment(mapFragment); // mapFragment 보여주기
                    noticePage = 3; // 현재 페이지 값으로 초기화하기
                    break;

                case R.id.menu_movie_video: // 영상 아이콘을 클릭한 경우
                    replaceFragment(videoFragment); // videoFragment 보여주기
                    noticePage = 4; // 현재 페이지 값으로 초기화하기
                    break;
            }
            return true;
        });

    }

    public void replaceFragment(Fragment fragment){ // 프래그먼트 교체하는 메소드
        FragmentManager fragmentManger = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_frame,fragment); // 파라미터로 전달받은 프래그 먼트로 교체하기

        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) { // 화면 전환시 프래그먼트 onCreate가 호출되는 이유로 매칭이 안되어서 사용하는 메소드
        super.onSaveInstanceState(outState);

        outState.putInt("page",noticePage); // page 키로 noticePage(현재 페이지)의 값을 저장
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) { // 화면 전환시 프래그먼트 onCreate가 호출되는 이유로 매칭이 안되어서 사용하는 메소드
        super.onRestoreInstanceState(savedInstanceState);

        noticePage = savedInstanceState.getInt("page"); // page 키값으로 noticePage 값을 가져옴
        replaceFragment(fragments.get(noticePage)); // 가져온 페이지를 통해서 fragment를 교체해줌
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // menu모음 레이아웃을 첨부해줌

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // actionBar에 accountActivity로 연결시켜주는 아이콘을 생성

        int curId = item.getItemId();

        switch (curId){
            case R.id.action_setting:
                Intent intent = new Intent(this,AccountActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkSubscribe() throws ParseException { // 현재 구독 정보를 사용해서 구독기간이 다 되었는지 확인하기 위해서
        // db에서 로그인 유저의 구독 정보 가져오기

        String id = sh.getAccountData().get(0);

        Integer accountKey = dh.getAccountKey(sh.getAccountData().get(0));

        SubscribeData data = dh.getSubscribeData(accountKey); // 구독자 정보 가져오기

        if(data!=null){ // 데이터가 존재하는 경우
            String endDate = data.finish;
            String currentDate = utills.getCurrentTime();

            if(utills.compareDate(currentDate,endDate)){ // 구독 종료일인 경우
                dh.updateSubscribe(id,0);
            }
        }
    }
}