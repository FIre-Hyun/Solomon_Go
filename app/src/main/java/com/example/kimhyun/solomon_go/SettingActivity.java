package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SettingActivity extends AppCompatActivity {

    CheckBox ckbox_disable, ckbox_push, ckbox_picture, ckbox_name, ckbox_age, ckbox_hobby, ckbox_job, ckbox_home, ckbox_etc, ckbox_sex;

    Button btn_logout;

    SharedPreferences sp_id;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ckbox_disable = (CheckBox) findViewById(R.id.ckbox_disable);
        ckbox_push = (CheckBox) findViewById(R.id.ckbox_push);
        ckbox_picture = (CheckBox) findViewById(R.id.ckbox_picture);
        ckbox_name = (CheckBox) findViewById(R.id.ckbox_name);
        ckbox_age = (CheckBox) findViewById(R.id.ckbox_age);
        ckbox_hobby = (CheckBox) findViewById(R.id.ckbox_hobby);
        ckbox_job = (CheckBox) findViewById(R.id.ckbox_job);
        ckbox_home = (CheckBox) findViewById(R.id.ckbox_home);
        ckbox_etc = (CheckBox) findViewById(R.id.ckbox_etc);
        ckbox_sex = (CheckBox) findViewById(R.id.ckbox_sex);

        btn_logout = (Button) findViewById(R.id.btn_logout);

        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 실행하고
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);


                deleteGPS(sp_id.getString("ID", ""));

                editor.clear();
                editor.commit();


                Toast.makeText(SettingActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });
    }


    private void deleteGPS(String id) {

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String id = (String) params[0];

                    String link = "http://jun123101.cafe24.com/deletegps.php";
                    String data = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");

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
                    return sb.toString();
                } catch (Exception e) {

                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(id);
    }



}
