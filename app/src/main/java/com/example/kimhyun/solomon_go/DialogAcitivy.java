package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by LEDPEAR on 2017-06-03.
 */

public class DialogAcitivy extends Activity implements View.OnClickListener {
    Button btn_Close,btn_find;
    ImageView profile_Image;
    TextView profile_Name, profile_Sex, profile_Age, profile_Hobby, profile_Type, profile_Job, profile_Home;

    String gps_id;

    Bitmap bmImg;

    picture task;

    Intent intent;

    double latitude,longitude;
    int point;

    SharedPreferences sp_id;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        profile_Image = (ImageView) findViewById(R.id.profile_Image);
        profile_Name = (TextView) findViewById(R.id.profile_Name);
        profile_Sex = (TextView) findViewById(R.id.profile_Sex);
        profile_Age = (TextView) findViewById(R.id.profile_Age);
        profile_Hobby = (TextView) findViewById(R.id.profile_Hobby);
        profile_Type = (TextView) findViewById(R.id.profile_Type);
        profile_Job = (TextView) findViewById(R.id.profile_Job);
        profile_Home = (TextView) findViewById(R.id.profile_Home);
        btn_Close = (Button) findViewById(R.id.btn_Close);
        btn_find = (Button)findViewById(R.id.btn_find) ;
        btn_find.setOnClickListener(this);
        btn_Close.setOnClickListener(this);

        task = new picture();

        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        point = sp_id.getInt("item",0);

        Intent intent = getIntent();
        if(intent.getStringExtra("before").toString().equals("near")){

            insertToDatabase(intent.getStringExtra("profile").toString());  // 이전 엑티비티가 nearsolomon 이면 클릭한 profile을 db에 넣는다.
        }
        else if(intent.getStringExtra("before").toString().equals("recently")){

            SharedPreferences auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);

            insertToDatabase(auto_login.getString("lover", ""));    // 이전 엑티비티가 recentlyactivity 이면 Preference 에 넣어둔 lover 데이터를 db에 넣는다
        }
        else{

            Toast.makeText(getApplicationContext(), "데이터가 없어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void insertToDatabase(String id) {      // id값을 넣고 프로필 리스트 값을 받아온다.

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(DialogAcitivy.this,
                        "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                loading.dismiss();

                try {

                    JSONObject jsonObj = new JSONObject(result);    // json 형식으로 받아온다

                    String id = jsonObj.getJSONArray("result").getJSONObject(0).getString("id");
                    String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");
                    String sex = jsonObj.getJSONArray("result").getJSONObject(0).getString("sex");
                    String age = jsonObj.getJSONArray("result").getJSONObject(0).getString("age");
                    String hobby = jsonObj.getJSONArray("result").getJSONObject(0).getString("hobby");
                    String type = jsonObj.getJSONArray("result").getJSONObject(0).getString("type");
                    String job = jsonObj.getJSONArray("result").getJSONObject(0).getString("job");
                    String home = jsonObj.getJSONArray("result").getJSONObject(0).getString("home");



                    if(id != "null"){       //로그인이 성공한다면

                        task.execute("http://jun123101.cafe24.com/picture/picture_"+id+".png");     // 서버에서 id값을 통해 이미지파일을 받아온다.

                        SharedPreferences auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto_login.edit();

                        editor.putString("lover", id);
                        editor.commit();

                        gps_id = id;

                        profile_Name.setText(name);
                        profile_Age.setText(age);
                        profile_Hobby.setText(hobby);
                        profile_Type.setText(type);
                        profile_Job.setText(job);
                        profile_Home.setText(home);

                        if(sex.equals("0")){
                            profile_Sex.setText("남자");
                        }
                        else
                            profile_Sex.setText("여자");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "불러오기 실패! ㅠㅠ ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String link = "http://jun123101.cafe24.com/detail.php";

                try {
                    String id =  params[0];

                    String data = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);     //detail.php에 접속하고, id값을 보낸다.
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("sb", sb.toString());
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(id);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_find:
                if(point>0){
                    intent = new Intent(getApplicationContext(), SolostopActivity.class);
                    Log.d("Dial-onclick", gps_id);
                    gainGPS(gps_id);        // 상대방 위치와 내 위치를 검색하기위해 gps값을 받아온다
                    point--;
                    Toast.makeText(getApplicationContext(), "솔로포인트를 사용해 위치를 찾습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "솔로포인트가 없어요! ㅠㅠ ", Toast.LENGTH_SHORT).show();
                }
                editor.putInt("item",point);
                break;
            case R.id.btn_Close:
                finish();
        }
    }

    private void gainGPS(String id) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(DialogAcitivy.this,
                        "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                loading.dismiss();

                try {

                    Log.d("Dial-Json값", result);

                    JSONObject jsonObj = new JSONObject(result);

                    latitude = jsonObj.getJSONArray("gaingps").getJSONObject(0).getDouble("gps1");      // gaingps을 json형식으로 받아오고 db에서 gps1 값을 출력한다.
                    longitude = jsonObj.getJSONArray("gaingps").getJSONObject(0).getDouble("gps2");
                    intent.putExtra("latitude_solo",latitude);
                    intent.putExtra("longitude_solo",longitude);
                    intent.putExtra("map",2);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                String link = "http://jun123101.cafe24.com/gaingps.php";

                try {
                    String id =  params[0];

                    String data = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");

                    Log.d("Dialog", "Insert Data Complete");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);         //gaingps.php 에 id 값을 넣는다.
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("sb", sb.toString());
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }


            }
        }

        InsertData task = new InsertData();
        task.execute(id);
    }

    private class picture extends AsyncTask<String, Integer, Bitmap> {      //사진을 서버에서 받아오기위해 AsyncTask를 돌린다

        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            Log.d("Main", "back 들어옴");
            try {
                Log.d("Main", "try 들어옴");
                URL myFileUrl = new URL(urls[0]);

                Log.d("Main", "들어옴");
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img) {
            profile_Image.setImageBitmap(getRoundedBitmap(bmImg));
        }

    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


}