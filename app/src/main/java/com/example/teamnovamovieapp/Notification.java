package com.example.teamnovamovieapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class Notification extends BroadcastReceiver {

    Integer notificationID = 1;
    String channelID = "channel1";

    private DBHelper dh;

    private NotificationManager notificationManager;
    private String id = "";

    @Override
    public void onReceive(Context context, Intent intent) { // broadcastReceiver가 호출이 되면 무엇을 할 것인지를 정의

            id = intent.getStringExtra("id");

            // 초기화
            dh = new DBHelper(context);

            // notification을 띄워준다.
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Boolean autoLoginState = intent.getBooleanExtra("autoLogin",false);

            createNotificationChannel();
            deliverNotification(context,autoLoginState);
    }

    private void cancelSubscription(String id){ // 구독 취소 함수

        dh.updateSubscribe(id,0);
    }
    private void deliverNotification(Context context, Boolean autoLoginState) { // notification 알림 등록함

        cancelSubscription(id); // 전달받은 아이디를 통해 구독 취소요청

        Intent contentIntent = new Intent(context,LoginActivity.class);

        if(autoLoginState){ // 자동로그인이 되어 있는 경우
            contentIntent = new Intent(context,HomeActivity.class);
        }else{ // 자동 로그인이 되어 있지 않은 경우
            contentIntent = new Intent(context,LoginActivity.class);
        }

        PendingIntent contentPendingIntent = PendingIntent.getActivity(context,notificationID,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(R.mipmap.ic_launcher_moveapplogo)
                .setContentTitle("구독")
                .setContentText("구독이 만료되었습니다.")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(notificationID,builder.build());


    }

    public void createNotificationChannel(){ // notification channel생성 함수
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelID,
                    "notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("notification Test");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
