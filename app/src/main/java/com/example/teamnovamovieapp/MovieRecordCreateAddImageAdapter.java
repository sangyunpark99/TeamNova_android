package com.example.teamnovamovieapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MovieRecordCreateAddImageAdapter extends RecyclerView.Adapter<MovieRecordCreateAddImageAdapter.MovieRecordCreateAddImageViewHolder>{

    private Context context;
    ArrayList<Uri> Images = new ArrayList<>();

    MovieRecordCreateAddImageAdapter(ArrayList<Uri>imaegUris){ // 생성자로 필요한 데이터 넘김받기
        Images = imaegUris;
    }


    @NonNull
    @Override
    public MovieRecordCreateAddImageAdapter.MovieRecordCreateAddImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_record_create_add_image,parent,false);

        MovieRecordCreateAddImageViewHolder holder = new MovieRecordCreateAddImageViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecordCreateAddImageViewHolder holder, int position) {
        holder.add_image.setImageURI(Uri.parse(String.valueOf(Images.get(position))));
    }

    @Override
    public int getItemCount() {
        return Images.size();
    }

    public class MovieRecordCreateAddImageViewHolder extends RecyclerView.ViewHolder{

        protected ImageView add_image;

        public MovieRecordCreateAddImageViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            add_image = itemView.findViewById(R.id.movie_create_add_image);
        }
    }
}
