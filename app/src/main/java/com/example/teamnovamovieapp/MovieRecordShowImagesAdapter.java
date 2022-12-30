package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;

public class MovieRecordShowImagesAdapter extends RecyclerView.Adapter<MovieRecordShowImagesAdapter.MovieRecordShowImagesViewHolder> {

    ArrayList<Uri> recordImages = new ArrayList<>();

    private Context context;
    private SnapHelper sh;

    MovieRecordShowImagesAdapter(Context context,ArrayList<Uri> uris){
        this.context = context;
        this.recordImages = uris;
    }

    @NonNull
    @Override
    public MovieRecordShowImagesAdapter.MovieRecordShowImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_record_show_images,parent,false);

        MovieRecordShowImagesViewHolder holder = new MovieRecordShowImagesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecordShowImagesAdapter.MovieRecordShowImagesViewHolder holder, int position) {

        for(Uri uri : recordImages){ // 이미지 uri를 하나씩 for문으로 이미지 권한 부여해주기

            if(uri!=null && !uri.equals("")){ // uri가 null이 아닌경우 권한 부여하기
                context.getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        holder.record_image.setImageURI(recordImages.get(position)); // 이미지 적용하기
    }

    @Override
    public int getItemCount() {
        return recordImages.size();
    }

    public class MovieRecordShowImagesViewHolder extends RecyclerView.ViewHolder {

        protected ImageView record_image;

        public MovieRecordShowImagesViewHolder(@NonNull View itemView) {
            super(itemView);

            record_image = itemView.findViewById(R.id.movie_record_image);
        }
    }
}
