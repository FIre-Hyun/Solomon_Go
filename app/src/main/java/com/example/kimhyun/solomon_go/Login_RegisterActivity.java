package com.example.kimhyun.solomon_go;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Login_RegisterActivity extends AppCompatActivity {

    Button btn_register;

    EditText et_id, et_password, et_name, et_hobby, et_type, et_job, et_home;   //생년월일 하는거 어떻게하는지 몰라서 아직 안함

    RadioButton rb_man, rb_girl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__register);

        btn_register = (Button) findViewById(R.id.btn_register);

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_hobby = (EditText) findViewById(R.id.et_hobby);
        et_type = (EditText) findViewById(R.id.et_type);
        et_job = (EditText) findViewById(R.id.et_job);
        et_home = (EditText) findViewById(R.id.et_home);

        rb_man = (RadioButton) findViewById(R.id.rb_man);
        rb_girl = (RadioButton) findViewById(R.id.rb_girl);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id 중복이면 짤
                //id, 비밀번호 입력 안햇으면 짤

                //모두 만족하면 db에 insert 성공

                if(et_id != null && et_password != null && et_name != null && et_hobby != null &&
                        et_type != null && et_job != null && et_home != null && (rb_man.isChecked() || rb_girl.isChecked())) {
                    //모두 만족하면


                    String id = et_id.getText().toString();
                    String password = et_password.getText().toString();
                    String name = et_name.getText().toString();
                    String hobby = et_hobby.getText().toString();
                    String type = et_type.getText().toString();
                    String job = et_job.getText().toString();
                    String home = et_home.getText().toString();
                    String sex;
                    if(rb_man.isChecked())
                        sex = "0";
                    else
                        sex = "1";

                    insertToDatabase(id, password, name, hobby, type, job, home, sex);

                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }


    private void insertToDatabase(String id, String password, String name, String hobby, String type, String job, String home, String sex){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(Login_RegisterActivity.this,
                        "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String id = (String)params[0];
                    String password = (String)params[1];
                    String name = (String)params[2];
                    String hobby = (String)params[3];
                    String type = (String)params[4];
                    String job = (String)params[5];
                    String home = (String)params[6];
                    String sex = (String) params[7];

                    String link="http://jun123101.cafe24.com/send.php";
                    String data  = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "="
                            + URLEncoder.encode(password, "UTF-8");
                    data += "&" + URLEncoder.encode("name", "UTF-8") + "="
                            + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("hobby", "UTF-8") + "="
                            + URLEncoder.encode(hobby, "UTF-8");
                    data += "&" + URLEncoder.encode("type", "UTF-8") + "="
                            + URLEncoder.encode(type, "UTF-8");
                    data += "&" + URLEncoder.encode("job", "UTF-8") + "="
                            + URLEncoder.encode(job, "UTF-8");
                    data += "&" + URLEncoder.encode("home", "UTF-8") + "="
                            + URLEncoder.encode(home, "UTF-8");
                    data += "&" + URLEncoder.encode("sex", "UTF-8") + "="
                            + URLEncoder.encode(sex, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){

                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(id, password, name, hobby, type, job, home, sex);
    }
}