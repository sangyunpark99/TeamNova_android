package com.example.teamnovamovieapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MovieRecordCreateActivity extends AppCompatActivity {

    private static final String TAG = "MovieRecordCreateActivity";
    private static final String strSeparator = ",";

    private int REQUST_IMAGE_CAPTURE = 1000;
    private int REQUST_IMAGE_RECAPTURE = 2000;

    private RecyclerView addImageRecyclerView;
    private ImageView addIcon;
    private TextView createBtn;
    private PagerSnapHelper snapHelper = new PagerSnapHelper();
    private EditText movieTitle;
    private EditText movieContent;
    private TextView modifyImage;
    private TextView deleteBtn;

    private MovieRecordCreateAddImageAdapter adapter;

    private ArrayList<Uri> imageUris = new ArrayList<>();

    private DBHelper dh;
    private SPHelper sh;

    private Boolean checkAddButton = false;

    private Utills utills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_record_create);

            dh = new DBHelper(this);
            sh = new SPHelper(this);

            addImageRecyclerView = findViewById(R.id.movie_create_recyclerView); // 이미지 추가 recyclerView
            addIcon = findViewById(R.id.movie_create_add_icon); // 이미지 추가 아이콘
            createBtn = findViewById(R.id.movie_create_create_button); // 게시글 생성 버튼
            movieTitle = findViewById(R.id.movie_create_title);// 영화 감상문 제목 editText
            movieContent = findViewById(R.id.movie_create_content);//영화 감상문 내용 editText
            modifyImage = findViewById(R.id.movie_create_modify_image);// 영화 감상문 이미지 수정 Text
            deleteBtn = findViewById(R.id.movie_create_delete_button);// 영화 감상문 삭제 버튼

            utills = new Utills();

        Integer id = getIntent().getIntExtra("recordKey",0); // 전달받은 RecordKey

        createBtn.setOnClickListener(new View.OnClickListener() { // 생성 버튼 클릭시
            @Override
            public void onClick(View view) {

                if(getIntent().hasExtra("title")){ // 게시물을 수정하는 경우
                    checkAddButton = true;
                    String newTitle = movieTitle.getText().toString(); // 수정된 title 값
                    String newContent = movieContent.getText().toString(); // 수정된 content 값

                    dh.updateMovieRecord(newTitle,utills.arrayToString(imageUris,strSeparator),newContent,id); // 게시글 recordKey로 sqlite table 수정하기

                    finish();
                }else{ // 게시물을 생성하는 경우

                    if(imageUris.size()==0){
                        Toast.makeText(getBaseContext(),"이미지를 추가해주세요",Toast.LENGTH_SHORT).show();
                    }else{
                        checkAddButton = true;
                        String title = movieTitle.getText().toString();
                        String content = movieContent.getText().toString();
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 현재 시간 받아오기
                        String imageUri = utills.arrayToString(imageUris,","); // imageUri들을 하나의 string으로 변환

                        sh.MovieBoardEditorClear(); // SharedPreference에 임시 저장되었던 게시물 내용 삭제

                        ArrayList<String> account = sh.getAccountData(); // 계정에 관련된 데이터 받아오기

                        int accountKey = dh.getAccountKey(account.get(0)); // id,password로 account 테이블의 key값 가져오기

                        dh.insertMovieRecord(title,imageUri,content,currentTime,accountKey); // accountKey 값으로 영화 감상 기록 sqlite에 추가하기

                        finish();
                    }

                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() { // 게시글 삭제 버튼 클릭시
            @Override
            public void onClick(View view) {

                dh.deleteMovieRecord(id); // 게시글 recordKey로 영화 게시글 삭제하기
                finish();
            }

        });

        modifyImage.setOnClickListener(new View.OnClickListener() { // 사진 수정 버튼 클릭시
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true); // 여러장 선택 가능
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent,REQUST_IMAGE_RECAPTURE);
            }
        });

        if(getIntent().hasExtra("title")){ // 게시물을 수정하는 경우

            addIcon.setVisibility(View.GONE); // 이미지 추가 아이콘 제거
            deleteBtn.setVisibility(View.VISIBLE); // 삭제 버튼 제거

            createBtn.setText("수정"); // 생성 버튼 text값 수정으로 변경

            // putExtra로 데이터 받아오기
            String title = getIntent().getStringExtra("title"); // 전달받은 title
            String content = getIntent().getStringExtra("content"); // 전달받은 content
            String imageUri = getIntent().getStringExtra("image"); // 전달받은 image

            ArrayList<String> uris = utills.stringToArray(imageUri); // Stirng 을 Array로 변경

            ArrayList<Uri> stringToUris = new ArrayList<>();

            for(String item : uris){ // 각각의 String을 Uri로 변경
                stringToUris.add(Uri.parse(item));
            }

            adapter = new MovieRecordCreateAddImageAdapter(stringToUris); // 게시물 recyclerView adapter
            movieTitle.setText(title); // 영화 감상문 제목 설정
            movieContent.setText(content); // 영화 감상문 내용 설정
            imageUris = stringToUris;

            addImageRecyclerView.setAdapter(adapter);
            addImageRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            // 마지막에 등록한것 최신으로 보여주기

            snapHelper.attachToRecyclerView(addImageRecyclerView);

        }else{ // 게시물을 생성하는 경우

            if(sh.MovieBoardCheckData("title") || sh.MovieBoardCheckData("content") || sh.MovieBoardCheckData("image")){
                // SharedPreference에서 제목, 내용, 이미지에 대한 값이 존재하는 경우

                if(sh.MovieBoardCheckData("title")){ // SharedPreference에 title 값이 존재하는 경우
                    movieTitle.setText(sh.MovieBoardGetData("title")); // 영화 제목 설정
                }

                if(sh.MovieBoardCheckData("content")){ // SharedPreference에 content 값이 존재하는 경우
                    movieContent.setText(sh.MovieBoardGetData("content")); // 영화 내용 설정
                }

                if(sh.MovieBoardCheckData("image")){ // SharedPreference에 이미지 값이 존재하는 경우
                    for(String s : utills.stringToArray(sh.MovieBoardGetData("image"))){ //
                        imageUris.add(Uri.parse(s));
                    }

                    adapter = new MovieRecordCreateAddImageAdapter(imageUris); // image가 추가되는 recyclerView에 이미지 Uris 제공
                    addImageRecyclerView.setAdapter(adapter);
                    addImageRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

                    snapHelper.attachToRecyclerView(addImageRecyclerView);
                }

                addIcon.setVisibility(View.GONE); // 추가 아이콘(+) 안보이게 하기
            }

            modifyImage.setVisibility(View.GONE); // 수정 text 안보이게 하기
            deleteBtn.setVisibility(View.GONE); // 삭제 버튼 안보이게 하기

            addIcon.setOnClickListener(new View.OnClickListener() { // 이미지 추가 클릭시
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true); // 여러장 선택 가능
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,REQUST_IMAGE_CAPTURE);
                }
            });

        }

    }


    @SuppressLint("LongLogTag")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 이미지를 선택한후, 호출되는 메소드
        // start
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUST_IMAGE_CAPTURE || requestCode == REQUST_IMAGE_RECAPTURE && resultCode == RESULT_OK) { // 올바른 요청 + 응답이 온 경우
            if (data == null) { // 이미지를 하나도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
            } else { // 이미지를 최소 하나를 선택한 경우

                modifyImage.setVisibility(View.VISIBLE);

                if(requestCode == REQUST_IMAGE_RECAPTURE){ // 이미지를 재선택하는 경우 초기화
                    imageUris = new ArrayList<>();
                }

                if (data.getClipData() == null) { // 이미지를 하나만 선택한 경우

                    Uri imageUri = data.getData();

                    imageUris.add(imageUri);
                    adapter = new MovieRecordCreateAddImageAdapter(imageUris);

                    addImageRecyclerView.setAdapter(adapter);
                    addImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    // 마지막에 등록한것 최신으로 보여주기

                    snapHelper.attachToRecyclerView(addImageRecyclerView);

                    addIcon.setVisibility(View.GONE); // 플러스 아이콘 이미지 사라지게하기

                } else { // 이미지를 여러개 선택한 경우
                    ClipData clipData = data.getClipData();


                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();

                        try {
                                imageUris.add(imageUri);

                        } catch (Exception e) {
                            Log.e(TAG, "File Select error", e);
                        }
                    }

                    adapter = new MovieRecordCreateAddImageAdapter(imageUris);

                    addImageRecyclerView.setAdapter(adapter);
                    addImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                    snapHelper.attachToRecyclerView(addImageRecyclerView);

                    addIcon.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       if(!checkAddButton && !getIntent().hasExtra("title")){ // 게시물을 수정하는 페이지가 아닌경우 + 게시물 생성 버튼이 눌리지 않은 경우
           sh.MovieBoardSaveData("title",movieTitle.getText().toString());
           sh.MovieBoardSaveData("content",movieContent.getText().toString());
           sh.MovieBoardSaveData("image", utills.arrayToString(imageUris,","));
       }
    }
}