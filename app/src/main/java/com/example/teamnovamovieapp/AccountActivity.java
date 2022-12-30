package com.example.teamnovamovieapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AccountActivity extends AppCompatActivity {

    private Button logOutButton;
    private Button subscribeButton;
    private Button subscribeCancelButton;
    private TextView accountName;
    private TextView accountAge;
    private TextView subscribeCheck;
    private Dialog dialog;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private SPHelper sh;
    private DBHelper dh;

    private Boolean isMonthChecked = false;
    private Boolean isBasicChecked = false;

    private Utills utills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // R.id 선언
        logOutButton = findViewById(R.id.account_activity_logout_button);
        subscribeButton = findViewById(R.id.account_activity_subscribe_button);
        subscribeCancelButton = findViewById(R.id.account_activity_subscribe_cancel);
        accountName = findViewById(R.id.account_activity_accountName);
        accountAge = findViewById(R.id.account_activity_accountAge);
        subscribeCheck = findViewById(R.id.account_activity_subscribe);

        sh = new SPHelper(this);
        dh = new DBHelper(this);
        utills = new Utills();

        initDialogue();

        // 계정 이름
        accountName.setText(sh.getAccountData().get(1)); // 로그인한 계정의 이름 가져오기

        // 계정 나이
        accountAge.setText(sh.getAccountData().get(2)); // 로그인한 계정의 나이 가져오기

        // 구독 여부 확인하기
        String id = sh.getAccountData().get(0); // sharedPreference에서 id값 가져오기

        Boolean isSubscribe = dh.getSubscribe(id).equals(1); // 구독여부에 대한 정보

        if(isSubscribe){ // 구독여부에 따라 text 변경
            subscribeCheck.setText("구독중");
        }else{
            subscribeCheck.setText("구독 안하는 중");
        }

        subscribeButton.setOnClickListener(new View.OnClickListener() { // 구독하기 버튼 누른 경우
            @Override
            public void onClick(View view) {

                if(isSubscribe){

                    subscribeCheck.setText("구독중"); // 구독여부에 대해 구독중이라고 text 변경하기
                    Toast.makeText(getBaseContext(),"이미 구독을 완료하였습니다.",Toast.LENGTH_SHORT).show(); // 구독 완료 Toast 메시지 출력

                }else{

                    subscribeCheck.setText("구독 안하는 중"); // 구독여부에 대해 구독하지 않고 있다고 text 변경
                    dialog.show();

                }
            }
        });

        subscribeCancelButton.setOnClickListener(new View.OnClickListener() { // 구독 취소하기 버튼을 누른 경우
            @Override
            public void onClick(View view) {

                if(isSubscribe){
                    dh.updateSubscribe(id,0); // 구독 취소하기
                    Toast.makeText(getBaseContext(),"구독을 취소하였습니다.",Toast.LENGTH_SHORT).show(); // 구독 취소 Toast 메시지 출력
                    cancelAlarm();
                    finish();
                }else{
                    Toast.makeText(getBaseContext(),"구독을 취소 하실 수 없습니다.",Toast.LENGTH_SHORT).show(); // 구독 취수 불가 Toast 메시지 출력
                }
            }
        });

        // 로그아웃을 한 경우
        logOutButton.setOnClickListener(new View.OnClickListener() { // 로그 아웃 버튼을 클릭한 경우
            @Override
            public void onClick(View view) {
                sh.LoginSaveAccountClear(); // 자동 로그인에 필요했던 계정 클리어
                sh.LoginSaveDataClear(); // SharedPreference 로그인 데이터 클리어
                sh.SearchEditorClear(); // 영화 검색 데이터 초기화

                Intent intent = new Intent(getBaseContext(),LoginActivity.class); // 명시적 인텐트로 LoginActivity 소환
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 Acitvity를 제외한 다른 Activity 제거
                startActivity(intent);

                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() { // 카카오 계정 로그 아웃
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                });

                finish();
            }
        });
    }

    public void initDialogue(){
        dialog = new Dialog(this);
        dialog.setTitle("구독 선택 사항");
        dialog.setContentView(R.layout.subscribedialog);

        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        CheckBox monthSubscribe = dialog.findViewById(R.id.month_subscribe);
        CheckBox yearSubscribe = dialog.findViewById(R.id.year_subscribe);
        CheckBox baseSubscribe = dialog.findViewById(R.id.base_subscribe);
        CheckBox premiumSubscribe = dialog.findViewById(R.id.premium_subscribe);



        cancelButton.setOnClickListener(new View.OnClickListener() { // 다이얼로그 취소
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() { // 다이얼로그 확인
         @Override
            public void onClick(View view) {

             String id = sh.getAccountData().get(0);
             Integer key = dh.getAccountKey(id);

             dh.updateSubscribe(id,1); // 구독 하기

             // 현재 날짜 불러오기(시작일) - utills
             String currentDay = utills.getCurrentTime(); // 구독 시작일
             String finishDay = ""; // 구독 마감일

             if(isMonthChecked){// 한달 기준이 체크되어 있는 경우

                 try {
                     finishDay = utills.getFinishTime("Month",currentDay);

                 } catch (ParseException e) {
                     e.printStackTrace();
                 }

                 if(isBasicChecked){ // 기본 타입 체크되어 있는 경우
                     if(dh.getSubscribeDetail(key)){ // 이미 구독을 한번이라도 했었던 경우
                         dh.updateSubscribeDetail(currentDay,finishDay,"Base",key);
                     }else{ // 구독을 아직 한번도 해보지 않은 경우
                         dh.insertSubscribeDetail(currentDay,finishDay,"Base",key);
                     }
                 }else{ // 프리미엄 타입 체크되어 있는 경우
                     if(dh.getSubscribeDetail(key)){ // 이미 구독을 한번이라도 했었던 경우
                         dh.updateSubscribeDetail(currentDay,finishDay,"Premium",key);
                     }else{ // 구독을 아직 한번도 해보지 않은 경우
                         dh.insertSubscribeDetail(currentDay,finishDay,"Premium",key);
                     }
                 }

             }else{// 1년 기준이 체크되어 있는 경우 - 자동 구독
                 try {
                     finishDay = utills.getFinishTime("Year",currentDay); // 1년 더하기

                 } catch (ParseException e) {
                     e.printStackTrace();
                 }

                 if(isBasicChecked){ // 기본 타입 체크되어 있는 경우
                     if(dh.getSubscribeDetail(key)){ // 이미 구독을 한번이라도 했었던 경우
                         dh.updateSubscribeDetail(currentDay,finishDay,"Base",key);
                     }else{ // 구독을 아직 한번도 해보지 않은 경우
                         dh.insertSubscribeDetail(currentDay,finishDay,"Base",key);
                     }
                 }else{ // 프리미엄 타입 체크되어 있는 경우
                     if(dh.getSubscribeDetail(key)){ // 이미 구독을 한번이라도 했었던 경우
                         dh.updateSubscribeDetail(currentDay,finishDay,"Premium",key);
                     }else{ // 구독을 아직 한번도 해보지 않은 경우
                         dh.insertSubscribeDetail(currentDay,finishDay,"Premium",key);
                     }
                 }
             }
                 try {
                     setAlarm();
                 } catch (ParseException e) {
                     e.printStackTrace();
                 }

                // 전부 초기화
                monthSubscribe.setChecked(false);
                yearSubscribe.setChecked(false);
                baseSubscribe.setChecked(false);
                premiumSubscribe.setChecked(false);

             dialog.dismiss(); // 다이얼로그 제거

             finish(); // 액티비티 종료
            }
        });

        // 체크 박스 선택

        monthSubscribe.setOnClickListener(new View.OnClickListener() { // 월간 구독을 누른 경우
            @Override
            public void onClick(View view) {
                yearSubscribe.setChecked(false); // 연간 구독 체크 해재
                isMonthChecked = true;
            }
        });

        yearSubscribe.setOnClickListener(new View.OnClickListener() { // 연간 구독을 누른 경우
            @Override
            public void onClick(View view) {
                monthSubscribe.setChecked(false);
                isMonthChecked = false;
            }
        });

        baseSubscribe.setOnClickListener(new View.OnClickListener() { // 기본 구독을 누른 경우
            @Override
            public void onClick(View view) {
                premiumSubscribe.setChecked(false);
                isBasicChecked = true;
            }
        });

        premiumSubscribe.setOnClickListener(new View.OnClickListener() { // 프리미엄 구독을 누른 경우
            @Override
            public void onClick(View view) {
                baseSubscribe.setChecked(false);
                isBasicChecked = false;
            }
        });

    }

    public void setAlarm() throws ParseException {
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,Notification.class); // 알람 조건 충족시, 리시버로 전달될 인텐트 설정
        intent.putExtra("id",sh.getAccountData().get(0));
        System.out.println(sh.getAccountData().get(0));
        intent.putExtra("autoLogin",sh.LoginGetData(dh.getAccountKey(sh.getAccountData().get(0)).toString()));

        pendingIntent = PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_IMMUTABLE);
        // 일정한 시간이 흐른 뒤에 intent를 전달하기 때문에 pendingIntent로 만들어야 한다.
        // pendingIntent를 여러개 사용하는 경우 notificationid값으로 구분해준다.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Long triggerTime = getFinishTime();// 실제 월간, 년간 구독인 경우
            Integer test = 5; // 시현 용
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,test,pendingIntent); // Doze 모드에서 실
        }
    }

    public void cancelAlarm(){ // 알람 취소 함수
        if(alarmManager!=null){ // 알람 취소
            alarmManager.cancel(pendingIntent);
        }
    }

    public Long getFinishTime() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SubscribeData data = dh.getSubscribeData(dh.getAccountKey(sh.getAccountData().get(0)));
        String finishDate = data.finish; // 구독 만료 날짜
        String startDate = data.start; // 구독 시작 날짜

        Date finishResult = sdf.parse(finishDate);
        Date startResult = sdf.parse(startDate);

        Calendar finishCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();

        finishCal.setTime(finishResult);
        startCal.setTime(startResult);

        return finishCal.getTimeInMillis() - startCal.getTimeInMillis(); // 종료되는 날짜 - 시작되는 날짜
    }
}