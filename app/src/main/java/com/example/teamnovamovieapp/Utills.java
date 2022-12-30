package com.example.teamnovamovieapp;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Utills {

    public ArrayList<String> stringToArray(String value){ // 이미지 string을 ,를 사용해서 ArrayList<String>으로 변환
        ArrayList<String> result = new ArrayList<>();
        String[] uris = value.split(",");

        result.addAll(Arrays.asList(uris));

        return result;
    }

    public String arrayToString(ArrayList<Uri> array,String strSeparator){ // 배열을 Separator를 사용해서 하나의 String으로 변환
        String str = "";

        for(int i = 0; i < array.size(); i++){
            str += array.get(i);

            if(i < array.size()-1){
                str += strSeparator;
            }
        }

        return str;
    }

    public int getRawResIdByName(String resName, AppCompatActivity activity){ // mp4 파일 이름으로 R.java 변환
        String packageName = activity.getPackageName();
        int resId = activity.getResources().getIdentifier(resName,"raw",packageName);

        return resId;
    }

    public String getHash(String pwd){

        String digest = "";

        try{

            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(pwd.getBytes());
            byte byteData[] = sh.digest();

            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < byteData.length; i++){
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            digest = sb.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return digest;
    }

    public String getCurrentTime(){ // 현재 시간 가져오는 함수
        String current = "";

        Long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        current = sdf.format(date);

        return current;
    }

    public String getFinishTime(String type, String nowDate) throws ParseException { // 구독 종류에 따라 구독이 종료되는 시간
        String result = "";

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(nowDate);
        cal.setTime(date);

        if(type.equals("Month")){ // 월별 구독인 경우
            cal.add(Calendar.MONTH,1);
            result = sdf.format(cal.getTime());
        }else{ // 연간 구독인 경우
            cal.add(Calendar.YEAR,1);
            result = sdf.format(cal.getTime());
        }

        return result;
    }

    public Boolean compareDate(String first, String second) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar firstCal = Calendar.getInstance();
        Calendar secondCal = Calendar.getInstance();

        Date firstDate = sdf.parse(first);
        Date secondDate = sdf.parse(second);

        firstCal.setTime(firstDate);
        secondCal.setTime(secondDate);

        if(firstCal.compareTo(secondCal) >= 0){ // 같으면 0, 더 큰경우 1(first가 더큰 경우)
            return true;
        }

        return false;
    }
}
