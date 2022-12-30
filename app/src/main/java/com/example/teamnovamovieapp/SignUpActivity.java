package com.example.teamnovamovieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText id;
    private EditText password;
    private EditText passwordCheck;
    private EditText name;
    private EditText age;

    private DBHelper dh;

    private Utills utills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 초기화
        dh = new DBHelper(this);

        utills = new Utills();

        signUpButton = findViewById(R.id.signup_button);
        id = findViewById(R.id.signup_id);
        password = findViewById(R.id.signup_password);
        passwordCheck = findViewById(R.id.signup_password_check);
        name = findViewById(R.id.signup_name);
        age = findViewById(R.id.signup_age);

        // 모든 값 존재 여부 확인, 비밀번호 일치 확인

        signUpButton.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼을 클릭했을때
            @Override
            public void onClick(View view) {

                checkSignUp(id.getText().toString(), password.getText().toString(), passwordCheck.getText().toString(), name.getText().toString(), age.getText().toString());
            }
        });

    }

    public void checkSignUp(String id, String password, String passwordCheck, String name, String age){ // 회원가입 메소드

        if(id.length()!=0 && password.length()!=0 && passwordCheck.length()!=0 && name.length()!=0 && age.length()!=0){
            // 전부다 입력을 해준 경우

            if(dh.getUserData(id)!=null){ // 이미 존재하는 계정인 경우
                Toast.makeText(this,"이미 가입한 계정입니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(passwordCheck)) { // 비밀번호와 비밀번호 확인란이 다른 경우
                Toast.makeText(this, "비밀번호와 비밀번호 확인란이 다릅니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashPassword = utills.getHash(password);

            dh.insertAccount(id,hashPassword,name,Integer.parseInt(age)); // sqlite에 계정 등록
            dh.close();

            Toast.makeText(this,"회원가입이 완료되었습니다!",Toast.LENGTH_SHORT);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }else{ // 필요한 정보를 전부다 입력해주지 않은 경우
            if(id.length() == 0 || password.length() ==0 ){ // id, password 입력 안한경우
                Toast.makeText(this,"아이디와 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                return;
            }

            if(passwordCheck.length() == 0){ // 패스워드 확인을 안한 경우
                Toast.makeText(this,"비밀번호를 확인란을 입력해주세요",Toast.LENGTH_SHORT).show();
                return;
            }

            if(name.length() ==0 || age.length() == 0 ){ // 이름과 나이를 입력하지 않은 경우
                Toast.makeText(this,"이름과 나이를 입력해주세요",Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    @Override
    protected void onStop() { // 회원가입 화면 제거
        super.onStop();

        finish();
    }
}