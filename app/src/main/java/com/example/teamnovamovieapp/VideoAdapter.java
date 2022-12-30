package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private ArrayList<String> poster = new ArrayList<>(); // 검정 고무신, 라바

    private Context context;


    VideoAdapter(){

        // 포스터 이미지 추가하기(일반 애니메이션)
        this.poster.add("blackrubbershoseposter");
        this.poster.add("larvaposter");
        this.poster.add("patmat");
    }

    VideoAdapter(String name){

        // 포스터 이미지 추가하기(진격의 거인)
        this.poster.add("attackontitan01poster");
        this.poster.add("attackontitan02poster");
        this.poster.add("attackontitan03poster");
    }


    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);

        VideoViewHolder holder = new VideoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {

        int resId = getRawResIdByName(poster.get(position)); // 영화 제목으로 image id 가져오기

        Glide.with(context).load(resId).into(holder.video_profile); // 가져온 id를 사용해서 이미지 지정하기

        holder.video_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,VideoDetailActivity.class);

                if(poster.get(holder.getAdapterPosition()).equals("blackrubbershoseposter")){ // 검정고무신 포스터를 클릭한 경우
                    intent.putExtra("title","검정고무신");
                }else if(poster.get(holder.getAdapterPosition()).equals("larvaposter")){ // 라바 포스터를 클릭한 경우
                    intent.putExtra("title","라바");
                }else if(poster.get(holder.getAdapterPosition()).equals("patmat")){ // 패트와 매트 포스터를 클릭한 경우
                    intent.putExtra("title","패트와 매트");
                }else if(poster.get(holder.getAdapterPosition()).equals("attackontitan01poster")){ // 진격의 거인 1기 포스터를 클릭한 경우
                    intent.putExtra("title","진격의 거인 1기");
                }else if(poster.get(holder.getAdapterPosition()).equals("attackontitan02poster")){ // 진경의 거인 2기 포스터를 클릭한 경우
                    intent.putExtra("title","진격의 거인 2기");
                }else if(poster.get(holder.getAdapterPosition()).equals("attackontitan03poster")){ // 진격의 거인 3기 포스터를 클릭한 경우
                    intent.putExtra("title","진격의 거인 3기");
                }

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return poster.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{

        protected ImageView video_profile;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            video_profile = itemView.findViewById(R.id.item_video_profile);
        }
    }

    public int getRawResIdByName(String resName){ // 이미지 파일 이름 으로 R.java 변환
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(resName,"raw",packageName);

        return resId;
    }

    public void clear(){ // poster 데이터 클리어하기(누적 방지)
        int size = poster.size();
        poster.clear();
        notifyItemRangeRemoved(0,size);
    }
}

