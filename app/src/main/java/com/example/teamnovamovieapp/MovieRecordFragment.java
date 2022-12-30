package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;

public class MovieRecordFragment extends Fragment {

    private DBHelper dh;
    private RecyclerView recyclerView;
    private PagerSnapHelper sh;
    private SPHelper sph;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movie_record,container,false);

        dh = new DBHelper(rootView.getContext());
        sh = new PagerSnapHelper();

        recyclerView = rootView.findViewById(R.id.movie_record);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sh.attachToRecyclerView(rootView.findViewById(R.id.movie_record_recyclerView));

        loadRecentDB(); // Sqlite 데이터 갱신

        View addRecord = rootView.findViewById(R.id.movie_record_add);

        addRecord.setOnClickListener(new View.OnClickListener() { // 영화 감상문 추가 버튼 클릭시
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(),MovieRecordCreateActivity.class); // 영화 감상문 생성 Activity 소환
                startActivity(intent);
            }
        });

        return rootView;
    }


    public void loadRecentDB(){ // Sqlite DB로부터 데이터 가져와서 데이터 최신화하기
        recyclerView.setAdapter(new MovieRecordAdapter(this.getContext(),sph));
    }

    @Override
    public void onResume() { // 데이터 갱신을 위한 코드
        super.onResume();
        loadRecentDB();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        AppCompatActivity activity = (AppCompatActivity) context;

        sph = new SPHelper(activity);
    }
}
