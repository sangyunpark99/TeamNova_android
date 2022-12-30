package com.example.teamnovamovieapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class SPHelper implements Serializable { // 자동 로그인만 구현, intent로 객체를 전달하기 위해 Serializable 추가

    private SharedPreferences movieBoardSh;
    private SharedPreferences loginSh;
    private SharedPreferences loginAccountSh;
    private SharedPreferences searchSh;

    private SharedPreferences.Editor MovieBoardEditor;
    private SharedPreferences.Editor LoginEditor;
    private SharedPreferences.Editor LoginAccountEditor;
    private SharedPreferences.Editor SearchEditor;

    private Utills utills = new Utills();

    SPHelper(AppCompatActivity activity){
        movieBoardSh = activity.getSharedPreferences("MovieBoard",MODE_PRIVATE); // 영화 감상문(임시저장)
        loginSh = activity.getSharedPreferences("Login",MODE_PRIVATE); // 자동 로그인
        loginAccountSh = activity.getSharedPreferences("LoginAccount",MODE_PRIVATE); // 로그인한 계정
        searchSh = activity.getSharedPreferences("SearchMovie",MODE_PRIVATE); // 영화 검색(검색어 임시저장)

        MovieBoardEditor = movieBoardSh.edit();
        LoginEditor = loginSh.edit();
        LoginAccountEditor = loginAccountSh.edit();
        SearchEditor = searchSh.edit();
    }

    public void addSearchMovieData(String title){ // 영화 검색했던 데이터 임시저장

        String search;

        if(getSearchMovieData().equals("")){ // 아무것도 작성되어 있지 않은 경우
            search = title;
            SearchEditor.putString("search",search);
        }else{ // 작성이 되어있는 경우
            if(!utills.stringToArray(getSearchMovieData()).contains(title)) { // 중복작성
                search = getSearchMovieData() + "," + title;
                SearchEditor.putString("search",search);
            }else{
                SearchEditor.putString("search",getSearchMovieData());
            }
        }


        SearchEditor.commit();
    }

    public void setSearchMovieData(ArrayList<String> data){
        String result = "";

        for(int i = 0 ; i < data.size(); i++){
            if(i == data.size()-1){
                result += data.get(i);
            }else{
                result += data.get(i) + ",";
            }
        }

        SearchEditorClear();

        SearchEditor.putString("search",result);
        SearchEditor.commit();

    }

    public String getSearchMovieData(){
        return searchSh.getString("search","");
    }

    public void SearchEditorClear(){ // 영화 검색했던 데이터 초기화
        SearchEditor.clear();
        SearchEditor.commit();
    }

    public void MovieBoardSaveData(String key, String value){ // 영화 감상문(임시저장) 저장
        MovieBoardEditor.putString(key,value);
        MovieBoardEditor.commit();
    }

    public String MovieBoardGetData(String key){
        return movieBoardSh.getString(key,"");
    } // 영화 감상문(임시저장) 가져오기

    public boolean MovieBoardCheckData(String key){ // 영화 감상문(임시저장) 데이터 존재 유무 파악하기
        return movieBoardSh.getString(key,"").length()!=0; // 0이아니면 true, 0이면 false
    }

    public void MovieBoardEditorClear(){ // 영화 감상문(임시저장) 초기화
        MovieBoardEditor.clear();
        MovieBoardEditor.commit();
    }

    public void LoginSaveData(String key, Boolean value){ // 자동로그인 데이터 저장

        LoginEditor.putBoolean(key,value);
        LoginEditor.commit();
    }

    public void LoginSaveDataClear(){ // 자동로그인 데이터 클리어
        LoginEditor.clear();
        LoginEditor.commit();
    }

    public Boolean LoginGetData(String key){
        return loginSh.getBoolean(key,false);
    } // 현재 자동로그인데 대한 데이터 가져오기

    public void LoginSaveAccount(String id,String name,Integer age){ // 현재 로그인한 계정 데이터 저장

        LoginAccountEditor.putString("id",id);
        LoginAccountEditor.putString("name",name);
        LoginAccountEditor.putString("age",age.toString());
        LoginAccountEditor.commit();
    }

    public void LoginSaveAccountClear(){ // 현재 로그인한 계정 데이터 클리어

        LoginAccountEditor.clear();
        LoginAccountEditor.commit();

    }

    public ArrayList<String> getAccountData(){ // 현재 로그인한 계정 데이터 가져오기

        ArrayList<String> account = new ArrayList<>();

        account.add(loginAccountSh.getString("id",""));
        account.add(loginAccountSh.getString("name",""));
        account.add(loginAccountSh.getString("age",""));

        return account;
    }

}
