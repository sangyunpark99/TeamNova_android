package com.example.teamnovamovieapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "movie.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // database 생성
        // 데이터 베이스가 생성이 될 때 호출
        // DB -> Table -> Column -> Value 구조
        // AUTOINCREMENT -> 자동 증가 / NOT NULL 절대로 값이 빈값을 넣어주면 안됨
        db.execSQL("CREATE TABLE IF NOT EXISTS Account (accountKey INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT NOT NULL, password TEXT NOT NULL, name TEXT NOT NULL, age INTEGER NOT NULL, subscribe INTEGER NOT NULL)"); // sql 쿼리문을 입력해준다.
        db.execSQL("CREATE TABLE IF NOT EXISTS MovieRecord (recordKey INTEGER PRIMARY KEY AUTOINCREMENT, account_id INTEGER REFERENCES Account (accountKey) ON DELETE CASCADE,title TEXT NOT NULL,imageUri TEXT NOT NULL,contents NOT NULL,writeDate TEXT NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS BoxOffice (boxOfficeKey INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, link TEXT NOT NULL, image TEXT NOT NULL, subtitle TEXT NOT NULL, pubDate TEXT NOT NULL, director TEXT NOT NULL, actor TEXT NOT NULL, userRating TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Subscribe (subScribeKey INTEGER PRIMARY KEY AUTOINCREMENT, account_id INTEGET REFERENCES Account (accountKey),start TEXT NOT NULL, kind TEXT NOT NULL, finish TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int NewVersion) {
        onCreate(db);
    } // db 업데이트하기

    // 계정추가
    public void insertAccount(String id, String password, String name,Integer age ){ // 아이디, 비번, 사용자 이름 추가
        SQLiteDatabase db = getWritableDatabase(); // 데이터 작성 - 세미콜론을 넣어주어야 함
        db.execSQL("INSERT INTO Account (id,password,name,age,subscribe) VALUES('"+id+"','"+password+"','"+name+"','"+age+"',0);");
    }

   public Boolean checkAccount(String id,String pwd){ // 존재하는 계정인지에대한 여부를 알려주는 메소드

        SQLiteDatabase db = getReadableDatabase();

        Cursor idCursor = db.rawQuery("SELECT * FROM Account where id = ? AND password = ? ",new String[]{id,pwd});

        if(idCursor.getCount()>0){
            return true;
        }

        return false;
    }

    public Integer getAccountKey(String id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT accountKey FROM Account Where id = ?",new String[]{id});

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){ // 1개만 존재한다.
                return cursor.getInt(cursor.getColumnIndexOrThrow("accountKey"));
            }
        }

        return null;
    }

    public UserData getUserData(String id){ // 첫 로그인 시에만 사용

        SQLiteDatabase db = getReadableDatabase();

        String name;
        int age;
        int accountKey;

        Cursor cursor = db.rawQuery("SELECT * FROM Account Where id = ?",new String[]{id});

        UserData data = null;

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
                accountKey = cursor.getInt(cursor.getColumnIndexOrThrow("accountKey"));

                data = new UserData(accountKey,name,age);
            }
            return data;
        }
        return null;
    }

    // 게시글 추가
    public void insertMovieRecord(String title, String imageUri, String contents, String writeDate, int referKey){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO MovieRecord (title,imageUri,contents,writeDate,account_id) VALUES('"+title+"','"+imageUri+"','"+contents+"','"+writeDate+"','"+referKey+"');");
    }

    // 게시글 수정
    public void updateMovieRecord(String title, String imageUri, String contents, Integer recordKey){ // recordKey를 사용해서 구분
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE MovieRecord SET title = '"+title+"',imageUri = '"+imageUri+"',contents = '"+contents+"' WHERE recordKey = '"+recordKey+"'");
    }

    //게시글 삭제
    public void deleteMovieRecord(Integer recordKey){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM MovieRecord WHERE recordKey = '" + recordKey + "'");
    }

    //게시글 목록 조회 - 계정마다 다르게
    public ArrayList<MovieRecordData> selectMovieRecord(SQLiteDatabase db, SPHelper sh){

        ArrayList<MovieRecordData> movieRecords = new ArrayList<>();

        ArrayList<String> account = sh.getAccountData();

        int accountKey = getAccountKey(account.get(0));

        // 날짜순으로 정렬
        Cursor cursor = db.rawQuery("SELECT * FROM MovieRecord ORDER BY writeDate DESC",null);

        if(cursor.getCount()!=0){
            //데이터가 존재하면
            while(cursor.moveToNext()){

                if(cursor.getInt(cursor.getColumnIndexOrThrow("account_id")) == accountKey){
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
                    String contents = cursor.getString(cursor.getColumnIndexOrThrow("contents"));
                    String writeDate = cursor.getString(cursor.getColumnIndexOrThrow("writeDate"));
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("recordKey"));

                    MovieRecordData movieRecord = new MovieRecordData(id,imageUri,title,writeDate,contents);

                    movieRecords.add(movieRecord);
                }
            }
        }

        cursor.close(); // cursor 객체 종료

        return movieRecords;
    }

    // 박스오피스 목록 추가 - 일마다 갱신(전날 기준)
    public void insertBoxOffice(String title, String link, String image, String subtitle, String pubDate, String director, String actor, String userRating){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO BoxOffice (title,link,image,subtitle,pubDate,director,actor,userRating) VALUES('"+title+"','"+link+"','"+image+"','"+subtitle+"','"+pubDate+"','"+director+"','"+actor+"','"+userRating+"');");
    }

    public ArrayList<SearchMovieData> getBoxOffice(){
        ArrayList<SearchMovieData> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM BoxOffice",null);

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                String subtitle = cursor.getString(cursor.getColumnIndexOrThrow("subtitle"));
                String pubDate = cursor.getString(cursor.getColumnIndexOrThrow("pubDate"));
                String director = cursor.getString(cursor.getColumnIndexOrThrow("director"));
                String actor = cursor.getString(cursor.getColumnIndexOrThrow("actor"));
                String userRating = cursor.getString(cursor.getColumnIndexOrThrow("userRating"));

                SearchMovieData data = new SearchMovieData(title,link,image,subtitle,pubDate,director,actor,userRating);

                result.add(data);
            }

        }

        return result;
    }

    public void deleteBoxOffice(){
        // 박스오피스 테이블 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM BoxOffice");
    }

    // 구독 정보 가져오기, 구독 정보 업데이트 하기

    public Integer getSubscribe(String id){ // 현재 구독 정보 가져오기

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT subscribe FROM Account Where id = ?",new String[]{id});

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                return cursor.getInt(0);
            }
        }

        return null;
    }

    public void updateSubscribe(String id, int subscribeNum){ // 구독  상태 업데이트 - 확인, 취소
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Account SET subscribe = '"+subscribeNum+"' WHERE id = '"+id+"'");
    }

    public Boolean getSubscribeDetail(Integer key){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Subscribe WHERE account_id = ?",new String[]{String.valueOf(key)});

        if(cursor.getCount()!=0){
            return true;
        }

        return false;
    }

    public void insertSubscribeDetail(String start, String finish, String kind, Integer accountKey){ // 구독 상세 정보 추가
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Subscribe (start,kind,finish,account_id) VALUES('"+start+"','"+kind+"','"+finish+"','"+accountKey+"')");
    }

    public void updateSubscribeDetail(String start, String finish, String kind, Integer accountKey){ // 구독 상세 정보 업데이트
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Subscribe SET start = '"+start+"',kind = '"+kind+"',finish = '"+finish+"', account_id = '"+accountKey+"'");
    }

    public SubscribeData getSubscribeData(Integer key){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Subscribe WHERE account_id = ?",new String[]{String.valueOf(key)});

        if(cursor.getCount()!=0) {
            while (cursor.moveToNext()) {

                String start = cursor.getString(cursor.getColumnIndexOrThrow("start"));
                String finish = cursor.getString(cursor.getColumnIndexOrThrow("finish"));
                String kind = cursor.getString(cursor.getColumnIndexOrThrow("kind"));

                return new SubscribeData(start, finish, kind);

            }
        }

        return null; // 계정이 없는 경우
    }

}
