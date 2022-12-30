package com.example.teamnovamovieapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.http.Url;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.MovieSearchViewHolder> {

    ArrayList<SearchMovieData> results = new ArrayList<>();

    private Context context;

    MovieSearchAdapter(ArrayList<SearchMovieData> searchMovieData){
        this.results = searchMovieData;
    }

    @NonNull
    @Override
    public MovieSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_search,parent,false);

        MovieSearchViewHolder holder = new MovieSearchViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSearchViewHolder holder, int position) {

        if(results.get(position).imageLink.equals("")){ // 전달받은 이미지가 없는 경우
            holder.movie_profile.setImageResource(R.drawable.ic_launcher_foreground); // 기본 이미지로 보여주기
        }else{ // 전달받은 이미지가 있는 경우
            Glide.with(context).load(Uri.parse(results.get(position).imageLink)).into(holder.movie_profile); // 전달받은 이미지로 보여주기
        }

        String title = results.get(position).title;

        title.replace("</b>",""); // 제목에 </b>태그 없애기
        title.replace("&apos;",""); // 제목에 &apos; 없애기

        holder.movie_title.setText(title); // 영화 제목 설정하기
        holder.movie_releaseDate.setText(results.get(position).pubDate); // 영화 개봉 날짜 설정하기
        holder.movie_rating.setText(results.get(position).rating); // 영화 평점 설정하기
        holder.movie_director.setText(results.get(position).director); // 영화 감독 설정하기

        holder.itemView.setOnClickListener(new View.OnClickListener(){ // 각각의 아이템을 클릭한 경우
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MovieDetailActivity.class); // MovieDetailAcitivty로 이동하기
                Gson gson = new Gson();
                String json = gson.toJson(results.get(holder.getAdapterPosition())); // gson을 사용해서 JSON

                System.out.println(json);

                intent.putExtra("movieInfo",json); // json화한 데이터 보내기

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MovieSearchViewHolder extends RecyclerView.ViewHolder {

        protected ImageView movie_profile;
        protected TextView movie_title;
        protected TextView movie_releaseDate;
        protected TextView movie_rating;
        protected TextView movie_director;

        public MovieSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            movie_profile = itemView.findViewById(R.id.movieSearch_posterImage);
            movie_title = itemView.findViewById(R.id.title);
            movie_releaseDate = itemView.findViewById(R.id.releaseDate);
            movie_rating = itemView.findViewById(R.id.userRating);
            movie_director = itemView.findViewById(R.id.director);
        }
    }
}
