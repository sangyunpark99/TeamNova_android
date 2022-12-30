package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.sql.Array;
import java.util.ArrayList;

public class BoxOfficeAdapter extends RecyclerView.Adapter<BoxOfficeAdapter.BoxOfficeViewHolder>{

    ArrayList<SearchMovieData> results = new ArrayList<>();
    private Context context;

    BoxOfficeAdapter(ArrayList<SearchMovieData> results){
        this.results = results;
    }

    @NonNull
    @Override
    public BoxOfficeAdapter.BoxOfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boxoffice,parent,false);

        BoxOfficeViewHolder holder = new BoxOfficeViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoxOfficeAdapter.BoxOfficeViewHolder holder, int position) {

        Glide.with(context).load(Uri.parse(results.get(position).imageLink)).into(holder.movie_profile); // Glide를 사용해서 이미지 첨부

        holder.movie_profile.setOnClickListener(new View.OnClickListener() { // 영화 포스터 이미지를 클릭한 경우
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,MovieDetailActivity.class); // 명시적 인텐트를 사용해서 MovieDetailActivity 소환

                Gson gson = new Gson();
                String result = gson.toJson(results.get(holder.getAdapterPosition())); // arraylist를 json화

                intent.putExtra("movieInfo",result);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class BoxOfficeViewHolder extends RecyclerView.ViewHolder {

        protected ImageView movie_profile; // 영화 포스터 이미지

        public BoxOfficeViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            movie_profile = itemView.findViewById(R.id.posterImage);

        }
    }

}
