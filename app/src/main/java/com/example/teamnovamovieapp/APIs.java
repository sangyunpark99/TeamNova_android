package com.example.teamnovamovieapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class APIs {

    private ArrayList<SearchMovieData> boxOfficeMovie = new ArrayList<>();
    private ArrayList<MovieTheaterData> movieTheater = new ArrayList<>();
    private DBHelper dh;

    APIs(Context context){
        this.dh = new DBHelper(context);
    }

    public ArrayList<SearchMovieData> boxOfficeApi(){

        String key = "0889ca66137547293966fbb13c317259";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Calendar c1 = Calendar.getInstance();

        String today = sdf.format(c1.getTime());

        int beforeDay = Integer.valueOf(today)-1; // 당일날 데이터를 요청했을 때, BoxOffice API의 응답에 아무런 데이터가 없어, 전날로 정정

        String strToday = String.valueOf(beforeDay); // String으로 변경

        try {
            // 박스오피스 영화 순위 받아오기
            String apiURL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=" + key + "&targetDt=" + strToday;

            URL url = new URL(apiURL);

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
            StringBuffer sb = new StringBuffer();

            String inputline;

            while ((inputline = in.readLine()) != null) {
                sb.append(inputline);
            }

            String response = sb.toString();

            JSONObject root = new JSONObject(response);

            JSONArray movielist = root.getJSONObject("boxOfficeResult").getJSONArray("dailyBoxOfficeList");

            ArrayList<String> boxOfficeTitle = new ArrayList<>();

            for (int i = 0; i < movielist.length(); i++) {
                String movieTitle = movielist.getJSONObject(i).getString("movieNm");
                boxOfficeTitle.add(movieTitle);
            }

            boxOfficeMovie = NaverMovieSearchApi(boxOfficeTitle);

        }catch (Exception e){
            e.printStackTrace();
        }

        return boxOfficeMovie;
    }

    public ArrayList<SearchMovieData> NaverMovieSearchApi(ArrayList<String> boxOfficeTitle) throws MalformedURLException, UnsupportedEncodingException {
        ArrayList<SearchMovieData> naverSearchMovie = new ArrayList<>();

        String clientId = "IFFkWbOyZOkteamNdUO0";
        String clientSecret = "keGwlTDxQx";
        String display = "";

        for(int i = 0; i < boxOfficeTitle.size(); i++){
            String movieTitle = URLEncoder.encode(boxOfficeTitle.get(i), "UTF-8");

            if(boxOfficeTitle.size() == 1){
                display = "20";
            }else{
                display = "1";
            }
            String NaverApiURL = "https://openapi.naver.com/v1/search/movie.json?" + "query=" + movieTitle + "&display=" + display;
            URL naverUrl = new URL(NaverApiURL);

            try{
                HttpURLConnection con = (HttpURLConnection) naverUrl.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-id", clientId);
                con.setRequestProperty("X-Naver-Client-secret", clientSecret);

                int responseCode = con.getResponseCode();
                BufferedReader br;

                if (responseCode == 200) { // 응답 완료
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }

                String inputLine = br.readLine();

                StringBuffer naverResponse = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    naverResponse.append(inputLine);
                    naverResponse.append("\n");
                }

                br.close();

                String result = naverResponse.toString();

                try {
                    JSONObject jsonObj = new JSONObject("{" + result + "}");

                    JSONArray moviesArray = jsonObj.getJSONArray("items");

                    for (int j = 0; j < moviesArray.length(); j++) {

                        JSONObject movieObject = moviesArray.getJSONObject(j);

                        String title = movieObject.getString("title");
                        String director = movieObject.getString("director");


                        title = title.replaceAll("<b>", "");
                        title = title.replaceAll("</b>", "");
                        title = title.replaceAll("&lt;", "<");
                        title = title.replaceAll("&gt;", ">");
                        title = title.replaceAll("&amp;", "&");
                        title = title.replaceAll("&apos;",",");

                        director = director.replaceAll("<b>","");
                        director = director.replaceAll("</b>","");

                        SearchMovieData item = new SearchMovieData(
                                title,
                                movieObject.getString("link"),
                                movieObject.getString("image"),
                                movieObject.getString("subtitle"),
                                movieObject.getString("pubDate"),
                                director,
                                movieObject.getString("actor"),
                                movieObject.getString("userRating"));

                        naverSearchMovie.add(item);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return naverSearchMovie;
    }

    public ArrayList<MovieTheaterData> googlePlaceApi(String latitude, String longitude) throws IOException { // 구글 장소 검색 api

            String apiURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "&location="+latitude+"+%2C"+longitude+
                    "&radius=9000" +
                    "&type=movie_theater" +
                    "&key=AIzaSyBGOu-07GT1c6J3-Amv9Qe75HDTWpJuI5U"; // 주변 영화관에 대한 데이터를 호출하는 apiURL

            URL url = new URL(apiURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // "GET"방식을 요청
            connection.connect(); // 연결

            String line;
            String result = "";

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // 응답받은 데이터 읽기
            StringBuffer response = new StringBuffer();

            while ((line = reader.readLine()) != null) { // 라인별로 읽기
                response.append(line); // String 형태로 response에 줄별로 추가하기
            }

            result = response.toString();

            Log.i("result",result);

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject = new JSONObject(result); // string으로 전달받은 데이터를 jsonObject화 하기

                JSONArray movieTheaters = jsonObject.getJSONArray("results");

                for (int i = 0; i < movieTheaters.length(); i++) {

                    String lat = movieTheaters.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat"); // JSON데이터에서 위도값 가져오기
                    String lon = movieTheaters.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng"); // JSON데이터에서 경도값 가져오기
                    String name = movieTheaters.getJSONObject(i).getString("name"); // JSON 데이터에서 이름값 가져오기

                    MovieTheaterData item = new MovieTheaterData(lat, lon, name); // 영화 데이터 클래스를 사용해서 데이터 모델 만들기

                    movieTheater.add(item); // MovieTheater데이터 모델을 arrayList에 추가하기
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return movieTheater;

    }

    public String youtubeApi(String title) throws IOException, JSONException {
        String movieId = "";

        String originUrl = "https://www.googleapis.com/youtube/v3/search?" + "part=snippet&q=" + title + " 예고편" + "&key=AIzaSyBGOu-07GT1c6J3-Amv9Qe75HDTWpJuI5U"+"&maxResults=10";
        // 전달받은 title 값을 통해서 url 작성

        String myUrl = String.format(originUrl);

        URL url = new URL(myUrl);

        // HTTP 요청에 필요한 설정
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        String line;
        String result = "";

        // 요청에 대한 응답 결과를 String으로 변환
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();

        while((line = reader.readLine())!=null){
            response.append(line);
        }

        result = response.toString();

        JSONObject jsonObject = new JSONObject();

        // String으로 변환된 것을 JSONObject로 변환
        jsonObject = new JSONObject(result);

        // JSONObject에서 videoId에 관한 정보 추출
        movieId = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");

        connection.disconnect(); // 연결 끊기


        return movieId;
    }
}
