package com.example.teamnovamovieapp;

public class SearchMovieData {

    String title;
    String description;
    String imageLink;
    String subTitle;
    String pubDate;
    String director;
    String actor;
    String rating;

    SearchMovieData(String title, String description, String imageLink, String subTitle, String pubDate, String director, String actor, String rating){
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
        this.subTitle = subTitle;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.rating = rating;
    }
}
