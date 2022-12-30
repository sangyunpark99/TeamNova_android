package com.example.teamnovamovieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoFragment extends Fragment {

    private RecyclerView animationRecyclerView;
    private RecyclerView animationRecyclerView2;

    private DBHelper dh;
    private SPHelper sh;
    private Context context;
    private ViewGroup rootView;

    private VideoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_video,container,false);

        dh = new DBHelper(context);
        sh = new SPHelper((AppCompatActivity) context);
        adapter = new VideoAdapter();

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onStart() { // 구독이 될때, 구독이 취소될때를 구분하기 위해 lifeCycle 사용
        super.onStart();

        animationRecyclerView = rootView.findViewById(R.id.fragment_video_animation_recyclerView);
        animationRecyclerView2 = rootView.findViewById(R.id.fragment_video_animation2_recyclerView);

        TextView animationCategory = rootView.findViewById(R.id.fragment_video_animation_category);
        TextView animationCategory2 = rootView.findViewById(R.id.fragment_video_animation_category2);

        String id = sh.getAccountData().get(0); // 로그인한 계정 id 가져오기

        SubscribeData data = dh.getSubscribeData(dh.getAccountKey(sh.getAccountData().get(0)));

        String dataType = "";

        if(data!=null){ // 데이터가 존재하지 않는 경우
            dataType = data.type;
        }

        if(dh.getSubscribe(id) == 0){ // 구독을 하지 않은 경우

            new AlertDialog.Builder(context).setTitle("알림").setMessage("구독 하지 않아서 시청하실 수 없습니다.").create().show(); // alert 메시지 띄워주기

            animationCategory.setVisibility(View.GONE); // 카테고리 비활성화
            animationCategory2.setVisibility(View.GONE); // 카테고리 비활성화

            adapter.clear(); // adapter 클리어
            animationRecyclerView.setAdapter(adapter);
            animationRecyclerView2.setAdapter(adapter);

        }else{ // 구독을 한경우

            animationCategory.setVisibility(View.VISIBLE); // 카테고리 활성화
            animationCategory2.setVisibility(View.VISIBLE); // 카테고리 활성화

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
            layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);

            if(dataType.equals("Base")){ // 구독 종류가 기본인 경우
                // 애니메이션 - 검정고무신, 라바, 패트와 매트

                animationCategory2.setVisibility(View.GONE);

                animationRecyclerView.setLayoutManager(layoutManager);
                animationRecyclerView.setAdapter(new VideoAdapter());
            }else{ // 구독 종류가 프리미엄인 경우
                // 애니메이션
                animationRecyclerView.setLayoutManager(layoutManager);
                animationRecyclerView.setAdapter(new VideoAdapter());

                // 진격의 거인
                animationRecyclerView2.setLayoutManager(layoutManager1);
                animationRecyclerView2.setAdapter(new VideoAdapter("진격의 거인"));
            }


        }
    }
}
