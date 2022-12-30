package com.example.teamnovamovieapp;

public class MovieRecordData {

    private String imageUri;
    private String recordTitle;
    private String recordDate;
    private String recordContent;
    private int id;

    MovieRecordData(int id,String image, String recordTitle, String recordDate, String recordContent){

        this.id = id;
        this.imageUri = image;
        this.recordTitle = recordTitle;
        this.recordDate = recordDate;
        this.recordContent = recordContent;
    }


    public String getImage(){
        return imageUri;
    }

    public String getRecordTitle(){
        return recordTitle;
    }

    public String getRecordDate(){
        return recordDate;
    }

    public String getRecordContent(){
        return recordContent;
    }

    public Integer getId(){
        return id;
    }

}
