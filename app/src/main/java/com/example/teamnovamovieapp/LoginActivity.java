package com.example.teamnovamovieapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AgeRange;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dh;
    private SPHelper sh;

    private CheckBox autoLoginCheckBox;

    private Thread getBoxOfficeMovieThread;
    ArrayList<SearchMovieData> boxOfficeMovie = new ArrayList<>();

    private Handler handler = new Handler();

    private Utills utills = new Utills();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       View loginButton = findViewById(R.id.login_button); // 로그인할때 사용하는 버튼
       View signButton = findViewById(R.id.login_to_signUp_button); // 회원가입 할때 사용하는 버튼
       View kakaoLoginButton = findViewById(R.id.login_kakao_login); // 카카오 로그인 API로 로그인하는 버튼

       EditText id = findViewById(R.id.login_id); // id를 입력하는 EditText View
       EditText password = findViewById(R.id.login_password); // password를 입력하는 password View
       autoLoginCheckBox = findViewById(R.id.login_auto_login_box); // 자동로그인 여부에 대해 체크하는 체크박스

       dh = new DBHelper(this);
       sh = new SPHelper(this);

        if(sh.LoginGetData("autoLogin")) { // 자동 로그인 체크박스를 체크한 경우
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }

       loginButton.setOnClickListener(new View.OnClickListener() { // 로그인버튼을 클릭한 경우

           @Override
           public void onClick(View view) {

               String idValue = id.getText().toString(); // editText id값 가져오기
               String pwValue = password.getText().toString(); // editText password값 가져오기

               if(idValue.length() == 0 || pwValue.length() == 0){ // id, password가 입력이 되지 않은 경우
                   Toast.makeText(LoginActivity.this,"아이디, 비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                   return;
               } // 아이디 비밀번호를 입력하지 않은 경우

               if(dh.checkAccount(idValue,utills.getHash(pwValue))){ // 로그인한 계정이 존재하는 경우

                   UserData data = dh.getUserData(idValue); // id로 로그인 정보 가져오기
                   sh.LoginSaveAccount(idValue,data.name,data.age); //로그인 정보 저장
                   sh.MovieBoardEditorClear();

                   Intent intent = new Intent(getApplicationContext(),HomeActivity.class); // 명시적 Intent를 사용해서 HomActivity 호출
                   startActivity(intent);

               }else{ // 계정이 존재하지 않는 경우
                   Toast.makeText(LoginActivity.this,"없는 계정입니다",Toast.LENGTH_SHORT).show(); // 없는 계정이라고 Toast 메시지 띄우주기
               }


           }
       });

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() { // 카카오톡 로그인 가능 여부 확인
            @Override
            public void onClick(View view) {
               if( UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)){ // 카카오톡어플 설치 되어 있는 경우
                   UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this,callback); // 카카오톡으로 로그인

               }else { // 카카오 어플이 설치되어 있지 않은 경우
                   UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,callback); // 웹으로 카카오 홈페이지 호출해서 로그인
               }
            }
        });

       signButton.setOnClickListener(new View.OnClickListener() { // 회원가입
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(),SignUpActivity.class); // 명시적 인텐트로 SignUpActivity 클래스 호출
               startActivity(intent);
           }
       });
    }

    public Function2<OAuthToken,Throwable,Unit> callback = new Function2<OAuthToken, Throwable, Unit>() { // 카카오 로그인에 사용되는 callback 함수
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

            if(oAuthToken != null){
                //로그인이 되었을때
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if (throwable != null) { // 사용자 정보 요청 실패한 경우
                            Log.e("kakao", "사용자 정보 요청 실패", throwable);
                        } else { // DB에 존재하지 않는 계정이면 계정 추가하기

                            String id = user.getKakaoAccount().getEmail();
                            String pwd = utills.getHash(user.getId().toString());
                            String ageRange = (user.getKakaoAccount().getAgeRange()).toString();
                            // AGE_20_29 이러한 형태로 나옴
                            String[] ages = ageRange.split("_"); // "_"기준으로 나눠주기
                            Integer age = (Integer.parseInt(ages[1]) + Integer.parseInt(ages[2]))/2;
                            String name = user.getKakaoAccount().getProfile().getNickname();

                            if(dh.getUserData(id)==null){ // 현재 존재하지 않는 계정인경우
                                dh.insertAccount(id,pwd,name,age); // sqlite에 계정 추가하기
                            }

                            sh.LoginSaveAccount(id,name,age); // sharedPreference 로그인 한 사용자 데이터 추가
                        }

                        return null;
                    }
                });

                finish();
                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                startActivity(intent);
            }

            if(throwable != null){
                //결과에 오류가 존재할때
            }

            return null;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        dh.deleteBoxOffice(); // boxOffice Table 삭제
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(autoLoginCheckBox.isChecked()){ // 자동로그인 체크박스가 체크 되어 있는 경우
            sh.LoginSaveData("autoLogin",true); // sharedPreference에 autoLogin key의 value로 true 저장
        }else{ // 자동로그인 체크박스가 체크 되어 있지 않은 경우
            sh.LoginSaveData("autoLogin",false); // sharedPreference에 autoLogin key의 value로 false 저장
        }
    }

}