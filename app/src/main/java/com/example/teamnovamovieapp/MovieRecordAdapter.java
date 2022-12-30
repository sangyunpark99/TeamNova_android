package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MovieRecordAdapter extends RecyclerView.Adapter<MovieRecordAdapter.MovieRecordViewHolder> {

    ArrayList<MovieRecordData> records = new ArrayList<>();
    private Context context;
    private DBHelper dh;
    private Utills utills;

    MovieRecordAdapter(Context context, SPHelper sh){

        this.context = context;
        dh = new DBHelper(context); // context 초기화가 안되어서 해줌
        records = dh.selectMovieRecord(dh.getReadableDatabase(),sh);
        utills = new Utills();
    }

    @NonNull
    @Override
    public MovieRecordAdapter.MovieRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_record,parent,false);

        MovieRecordViewHolder holder = new MovieRecordViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecordAdapter.MovieRecordViewHolder holder, int position) {

        ArrayList<String> uris = utills.stringToArray(records.get(position).getImage()); // String 형태의 이미지 값을 배열로 변경

        ArrayList<Uri> setUris = new ArrayList<>();

        if(records.size()>0){ // 기록한 게시물이 존재할때

            for(int i = 0; i < uris.size(); i++){
                setUris.add(Uri.parse(uris.get(i))); // String형태의 이미지 값을 Uri 형태로 변경
            }

            // 이미지들을 나타내는 recyclerView
            holder.record_images.setAdapter(new MovieRecordShowImagesAdapter(context,setUris)); // Uris들을 어댑터로 보냄
            holder.record_images.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)); // 레이아웃 초기화
            new PagerSnapHelper().attachToRecyclerView(holder.record_images); // recyclerView에서 이미지 슬라이드

            holder.record_title.setText(records.get(position).getRecordTitle()); // 영화 감상문 제목 설정
            holder.record_content.setText(records.get(position).getRecordContent()); // 영화 감상문 내용 설정
            holder.record_date.setText(records.get(position).getRecordDate()); // 영화 감상문 날짜 설정

            holder.record_board.setOnClickListener(new View.OnClickListener() { // 영화 감상문 레이아웃 클릭한 경우 - 수정
                @Override
                public void onClick(View view) { // 데이터 수정
                    Intent intent = new Intent(context,MovieRecordCreateActivity.class); // 명시적 인텐트로 MovieRecordCreateActivity클래스 소환

                    //putExtra로 데이터 전달
                    intent.putExtra("title",records.get(holder.getAdapterPosition()).getRecordTitle()); // 영화 감상문 제목 전달
                    intent.putExtra("content",records.get(holder.getAdapterPosition()).getRecordContent()); // 영화 감상문 내용 전달
                    intent.putExtra("date",records.get(holder.getAdapterPosition()).getRecordDate()); // 영화 감상문 작성 날짜 전달
                    intent.putExtra("image",records.get(holder.getAdapterPosition()).getImage()); // 영화 감상문 이미지 전달
                    intent.putExtra("recordKey",records.get(holder.getAdapterPosition()).getId()); // 영화 감상문 고유키 전달

                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    // 내부 클래스
    public class MovieRecordViewHolder extends RecyclerView.ViewHolder {

        protected RecyclerView record_images;
        protected TextView record_title;
        protected TextView record_content;
        protected TextView record_date;
        protected ConstraintLayout record_board;

        public MovieRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            record_images = itemView.findViewById(R.id.movie_record_recyclerView); // 영화감상문 이미지들을 나타내는 recyclerView
            record_title = itemView.findViewById(R.id.movie_record_title); // 영화감상문의 제목
            record_content = itemView.findViewById(R.id.movie_record_content); // 영화 감상문의 내용
            record_date = itemView.findViewById(R.id.movie_record_writeDate); // 영화 감상문의 작성날짜
            record_board = itemView.findViewById(R.id.movie_board); // 영화 감상문 하나를 나타내는 레이아웃

        }
    }

}
