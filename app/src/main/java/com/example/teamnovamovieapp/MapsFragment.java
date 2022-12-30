package com.example.teamnovamovieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;

public class MapsFragment extends Fragment {

    private Context context;
    public static final int MY_PERMISSION_REQUEST_LOCATION = 99;

    private ArrayList<MovieTheaterData> movieTheater = new ArrayList<>();

    private Thread getNearbyPlaceThread;

    private Handler mapHandler;

    private Double latitude;
    private Double longitude;

    private APIs apis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        apis = new APIs(getContext());

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                googleMap.clear();

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // 위치정보에 대한 permission이 허가 되어 있는 경우

                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location!=null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                    latitude = 37.4829; // 위도 가져오기
                    longitude = 126.9743; // 경도 가져오기

                    getNearbyLocation(String.valueOf(latitude),String.valueOf(longitude)); // 근처 영화관 데이터 가져오기

                    mapHandler = new MapHanlder(googleMap);

                } else { // 위치정보에 대한 permission이 허가되어 있지 않은 경우
                    checkLocationPermissionWithRationale(); // GPS 권한 확인 요청
                }
            }
        });

        return view;
    }

    private void checkLocationPermissionWithRationale() { // GPS 권한 확인
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) { // 사용자가 권한 요청을 명시적으로 거부한 경우
                new AlertDialog.Builder(context) // Dialogue 생성
                        .setTitle("위치정보")// 제목 설정
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.") // 메시지 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() { // 확인 버튼을 누른 경우
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(((Activity) context).getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else { // 사용자가 권한 요청을 처음보는 경우, 다시 묻지 않음을 선택한 경우, 권한을 허용한 경우
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResult, GoogleMap googleMap) { // requestPermission을 통해 받아온 사용자의 응답에 따라 동작을 정의
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }

        return;
    }

    public void getNearbyLocation(String latitude, String longitude) { // 지역 근방의 영화관에 대한 데이터를 가져오는 메소드

        movieTheater.clear();

        getNearbyPlaceThread = new Thread() { // Thread 생성
            @Override
            public void run() {
                super.run();

                try {
                    movieTheater = apis.googlePlaceApi(latitude,longitude); // API 호출
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mapHandler.sendEmptyMessage(0);

            }
        };

        getNearbyPlaceThread.start();

    }

    @Override
    public void onAttach(@NonNull Context context) { // context 가져오기
        super.onAttach(context);

        this.context = context;
    }

    class MapHanlder extends Handler { // 영화관 위치에 관련된 데이터를 다 가져온 경우 handler를 사용해서 UI 변경

        private GoogleMap googleMap;

        MapHanlder(GoogleMap googleMap) {
            this.googleMap = googleMap;
        }

        @SuppressLint("MissingPermission") // 핸들러 선언전에 조건문에서 이미 permission 확인함
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            LatLng currentLocation = new LatLng(latitude, longitude); // 팀노바 학원 위도, 경도

            for (int i = 0; i < movieTheater.size(); i++) { // 영화관의 위치를 googleMap에 표시하기 위한 메소드
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(movieTheater.get(i).name); // 영화관 정보가 들어있는 list에서 영화관 이름 정보 가져와서 marker에 제목으로 지정

                LatLng movieLocation = new LatLng(Double.parseDouble(movieTheater.get(i).lat), Double.parseDouble(movieTheater.get(i).lng)); // 영화관 정보가 들어있는 list에서 위도, 경도를 가져와 위치를 지정
                markerOptions.position(movieLocation); // 지정해 놓은 위치를 makerOption에 추가
                googleMap.addMarker(markerOptions); // 구글 맵에 마커 추가하기
            }

            googleMap.setMyLocationEnabled(true); // 현재 나의 위치정보를 표현하게 하기

            googleMap.getUiSettings().setZoomControlsEnabled(true); // 구글 맵 화면을 확대, 축소가 가능하게 하기

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13)); // 구글 맵의 기본 확대 상태
        }
    }
}