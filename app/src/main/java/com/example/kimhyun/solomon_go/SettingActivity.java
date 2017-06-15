package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SettingActivity extends AppCompatActivity {

    CheckBox ckbox_disable;

    ImageView btn_logout;

    SharedPreferences sp_id;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ckbox_disable = (CheckBox) findViewById(R.id.ckbox_disable);

        btn_logout = (ImageView) findViewById(R.id.btn_logout);

        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();

        ckbox_disable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.ckbox_disable:
                        if(isChecked) {
                            deleteGPS(sp_id.getString("ID", ""));
                            //Toast.makeText(this, "비활성화 되었습니다.\n이제 다른사람이 검색할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        });

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


    private void deleteGPS(String id) {             // 비활성화를 누르면 gps값을 db에서 지움으로써 상대방이 검색하지 못하게 한다

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
