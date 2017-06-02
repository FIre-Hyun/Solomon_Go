package com.example.kimhyun.solomon_go;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginRegisterActivity extends AppCompatActivity {

    Button btn_register, btn_gallery;

    EditText et_id, et_password, et_name, et_hobby, et_type, et_job;   //생년월일 하는거 어떻게하는지 몰라서 아직 안함

    Spinner spin_home, spin_year, spin_month, spin_day;

    ImageView imageView_picture;

    RadioButton rb_man, rb_girl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_hobby = (EditText) findViewById(R.id.et_hobby);
        et_type = (EditText) findViewById(R.id.et_type);
        et_job = (EditText) findViewById(R.id.et_job);

        imageView_picture = (ImageView) findViewById(R.id.imageView_picture);

        spin_home= (Spinner) findViewById(R.id.spinner_home);
        spin_year= (Spinner) findViewById(R.id.spinner_year);
        spin_month= (Spinner) findViewById(R.id.spinner_month);
        spin_day= (Spinner) findViewById(R.id.spinner_day);

        rb_man = (RadioButton) findViewById(R.id.rb_man);
        rb_girl = (RadioButton) findViewById(R.id.rb_girl);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.home, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spin_home.setAdapter(adapter);

        ArrayAdapter adapter_year = ArrayAdapter.createFromResource(
                this, R.array.birth_year, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spin_home.setAdapter(adapter);

        ArrayAdapter adapter_month = ArrayAdapter.createFromResource(
                this, R.array.birth_month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spin_home.setAdapter(adapter);

        ArrayAdapter adapter_day = ArrayAdapter.createFromResource(
                this, R.array.birth_day, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spin_home.setAdapter(adapter);
        spin_year.setAdapter(adapter_year);
        spin_month.setAdapter(adapter_month);
        spin_day.setAdapter(adapter_day);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_id.getText().toString() != null &&
                        et_password.getText().toString() != null &&
                        et_name.getText().toString() != null &&
                        et_hobby.getText().toString() != null &&
                        et_type.getText().toString() != null &&
                        et_job.getText().toString() != null &&
                        (rb_man.isChecked() || rb_girl.isChecked())) {
                    //모두 만족하면


                    String id = et_id.getText().toString();
                    String password = et_password.getText().toString();
                    String name = et_name.getText().toString();
                    String hobby = et_hobby.getText().toString();
                    String type = et_type.getText().toString();
                    String job = et_job.getText().toString();

                    String home = spin_home.getSelectedItem().toString();

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
                else{
                    Toast.makeText(getApplicationContext(), "모두다 빠짐없이 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                // Gallery 호출
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                // 잘라내기 셋팅
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 150);
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), 2);   // PICK FROM GALLERY
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                imageView_picture.setImageBitmap(photo);
            }
        }

    }

    private void insertToDatabase(String id, String password, String name, String hobby, String type, String job, String home, String sex){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(LoginRegisterActivity.this,
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

                    String link="http://jun123101.cafe24.com/register.php";
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