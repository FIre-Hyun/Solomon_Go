package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    String mJsonString;

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
        profile_Image.setImageBitmap((Bitmap) intent.getParcelableExtra("img"));
        profile_Name.setText(intent.getStringExtra("profile"));

        Log.d("name", profile_Name.getText().toString());
        insertToDatabase(profile_Name.getText().toString());
    }

    private void insertToDatabase(String name) {

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

                    Log.d("Login", result);

                    JSONObject jsonObj = new JSONObject(result);

                    String name = jsonObj.getJSONArray("result").getJSONObject(0).getString("name");
                    String sex = jsonObj.getJSONArray("result").getJSONObject(0).getString("sex");
                    String age = jsonObj.getJSONArray("result").getJSONObject(0).getString("age");
                    String hobby = jsonObj.getJSONArray("result").getJSONObject(0).getString("hobby");
                    String type = jsonObj.getJSONArray("result").getJSONObject(0).getString("type");
                    String job = jsonObj.getJSONArray("result").getJSONObject(0).getString("job");
                    String home = jsonObj.getJSONArray("result").getJSONObject(0).getString("home");



                    if(name != "null"){       //로그인이 성공한다면

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
                        Toast.makeText(getApplicationContext(), "로그인 실패! ㅠㅠ ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String link = "http://jun123101.cafe24.com/detail.php";

                try {
                    String name =  params[0];

                    String data = URLEncoder.encode("name", "UTF-8") + "="
                            + URLEncoder.encode(name, "UTF-8");

                    Log.d("Login", "Insert Data Complete");

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
                    Log.d("sbbb", sb.toString());
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }


            }
        }

        InsertData task = new InsertData();
        task.execute(name);
    }


    @Override
    public void onClick(View v) {

        finish();
    }
}