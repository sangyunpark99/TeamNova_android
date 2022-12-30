package com.example.teamnovamovieapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieSearchSaveAdapter extends RecyclerView.Adapter<MovieSearchSaveAdapter.MovieSearchSaveViewHolder>{

    private ArrayList<String> searchList;

    private SPHelper sh;

    private Context context;

    private Utills utills;

    private EditText movieTitle;

    MovieSearchSaveAdapter(SPHelper sh, EditText editText){

        utills = new Utills();
        this.sh = sh;
        searchList = utills.stringToArray(sh.getSearchMovieData());
        this.movieTitle = editText;
    }

    @NonNull
    @Override
    public MovieSearchSaveAdapter.MovieSearchSaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_save_search,parent,false);

        MovieSearchSaveViewHolder holder = new MovieSearchSaveViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchSaveAdapter.MovieSearchSaveViewHolder holder, int position) {
        holder.title.setText(searchList.get(position)); // 제목 저장

        holder.cancelIcon.setOnClickListener(new View.OnClickListener() { // 삭제 버튼 클릭시
            @Override
            public void onClick(View view) {
                    searchList.remove(holder.getAdapterPosition()); // 선택한 항목 제거
                    sh.setSearchMovieData(searchList); // sharedPreference에 데이터 다시 갱신
                    notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() { // 검색했던 내용 검색시
            @Override
            public void onClick(View view) {
                String title = searchList.get(holder.getAdapterPosition()); // 검색한 내용의 값 받아오기
                movieTitle.setText(title); // EditText에 초기화
                movieTitle.setSelection(title.length());
            }
        });
    }

    @Override
    public int getItemCount() {

        if(searchList.size() == 1 && searchList.get(0).equals("")){ // 아무것도 없는 경우
            return 0; // 아무것도 그려주지 마!
        }

        return searchList.size();
    }

    public class MovieSearchSaveViewHolder extends RecyclerView.ViewHolder{

        protected ImageView cancelIcon;
        protected TextView title;

        public MovieSearchSaveViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            title = itemView.findViewById(R.id.item_save_movie_search); // 검색 기록 나타내는 아이콘
            cancelIcon = itemView.findViewById(R.id.item_save_cancel); // 검색 기록 지우는 아이콘
        }
    }
}
