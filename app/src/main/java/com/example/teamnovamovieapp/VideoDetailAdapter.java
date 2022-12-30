package com.example.teamnovamovieapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoDetailAdapter extends RecyclerView.Adapter<VideoDetailAdapter.VideoDetailViewHolder> {

    private ArrayList<VideoData> videos = new ArrayList<>();

    private TextView videoSubtitle;
    private VideoView videoView;

    private Utills utills;

    private Context context;

    private SPHelper sh;

    VideoDetailAdapter(ArrayList<VideoData> videos, TextView videoSubtitle, VideoView videoView){
        this.videos = videos;
        this.videoSubtitle = videoSubtitle;
        this.videoView = videoView;
    }


    @NonNull
    @Override
    public VideoDetailAdapter.VideoDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_detail,parent,false);

        VideoDetailViewHolder holder = new VideoDetailViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoDetailAdapter.VideoDetailViewHolder holder, int position) {

        // 예시
        holder.videoNumber.setText(videos.get(position).episode); // 비디오 회수 설정
        holder.videoTitle.setText(videos.get(position).title); // 비디오 제목 설정
        holder.videoDiscription.setText(videos.get(position).discription); // 비디오 설명 설정

        holder.itemView.setOnClickListener(new View.OnClickListener() { // 한 아이템을 클릭하는 경
            @Override
            public void onClick(View view) { // 클릭한 에피소드에 맞게 데이터
                videoSubtitle.setText(videos.get(holder.getAdapterPosition()).episode+" : "+ videos.get(holder.getAdapterPosition()).title);
                setVideoDetail(videos.get(holder.getAdapterPosition()).episode);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoDetailViewHolder extends RecyclerView.ViewHolder {

        protected TextView videoNumber;
        protected TextView videoTitle;
        protected TextView videoDiscription;

        public VideoDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            utills = new Utills();
            videoNumber = itemView.findViewById(R.id.item_video_number); // 비디오 회차시
            videoTitle = itemView.findViewById(R.id.item_video_title); // 비디오 제목
            videoDiscription = itemView.findViewById(R.id.item_video_description); // 비디오 설명

            context = itemView.getContext();
            sh = new SPHelper((AppCompatActivity) itemView.getContext());
        }
    }

    public void setVideoDetail(String currentEpisode){ // 비디오 경로 설정

        for(VideoData item : videos){ // 현재 회차시와 맞는 비디오 경로 설정
            if(item.episode.equals(currentEpisode)){

                // 비디오 경로 설정
                String videoPath = "android.resource://" + context.getPackageName() + "/" + utills.getRawResIdByName(item.videoPath,(AppCompatActivity) context);

                Uri uri = Uri.parse(videoPath); // 비디오 경로 uri로 변경

                videoView.setVideoURI(uri); // video에 uri 첨부

                MediaController mediaController = new MediaController(context);
                videoView.setMediaController(mediaController);

                mediaController.setAnchorView(videoView);

                videoView.start();
            }
        }
    }
}
