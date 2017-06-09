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
    Button btn_Close;
    ImageView profile_Image;
    TextView profile_Name, profile_Sex, profile_Age, profile_Hobby, profile_Type, profile_Job, profile_Home;
    Bundle bundel;

    String gps_id;

    String mJsonString;

    Bitmap bmImg;

    picture task;

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
        btn_Close.setOnClickListener(this);

        Intent intent = getIntent();
        Log.d("Dial", "111");
//        profile_Image.setImageBitmap((Bitmap) intent.getParcelableExtra("img"));


        //String log = intent.getStringExtra("profile").toString();
        //Log.d("Dial", log);

        task = new picture();

        if(intent.getStringExtra("before").toString().equals("near")){

            Log.d("Dial", "222");
//            profile_Name.setText(intent.getStringExtra("profile"));
            Log.d("Dial-lover값 near", "");

            insertToDatabase(intent.getStringExtra("profile").toString());
        }
        else if(intent.getStringExtra("before").toString().equals("recently")){
//            profile_Name.setText(~~);

            Log.d("Dial", "333");
            SharedPreferences auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);

            String log = auto_login.getString("lover", "");
            Log.d("Dial-lover값", log);

            insertToDatabase(auto_login.getString("lover", ""));
        }
        else{

            Log.d("Dial", "333");
            Toast.makeText(getApplicationContext(), "데이터가 없어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void insertToDatabase(String id) {

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

                    String id = jsonObj.getJSONArray("result").getJSONObject(0).getString("id");
                    String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");
                    String sex = jsonObj.getJSONArray("result").getJSONObject(0).getString("sex");
                    String age = jsonObj.getJSONArray("result").getJSONObject(0).getString("age");
                    String hobby = jsonObj.getJSONArray("result").getJSONObject(0).getString("hobby");
                    String type = jsonObj.getJSONArray("result").getJSONObject(0).getString("type");
                    String job = jsonObj.getJSONArray("result").getJSONObject(0).getString("job");
                    String home = jsonObj.getJSONArray("result").getJSONObject(0).getString("home");



                    if(id != "null"){       //로그인이 성공한다면

                        task.execute("http://jun123101.cafe24.com/picture/picture_"+id+".png");

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

                    Log.d("Dialog", "Insert Data Complete");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
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

        Log.d("Dial-onclick", gps_id);
        gainGPS(gps_id);
        
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

                    String gps1 = jsonObj.getJSONArray("gaingps").getJSONObject(0).getString("gps1");
                    String gps2 = jsonObj.getJSONArray("gaingps").getJSONObject(0).getString("gps2");
        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@여기있는 gps1, gps2값 쓰면 돼
                    Toast.makeText(getApplicationContext(), "gps1 = "+gps1+"\ngps2 = "+gps2, Toast.LENGTH_SHORT).show();


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

                    wr.write(data);
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

    private class picture extends AsyncTask<String, Integer, Bitmap> {

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