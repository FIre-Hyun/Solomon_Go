package com.gachon.kimhyun.solomon_go;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LoginRegisterActivity extends AppCompatActivity {

    Button btn_gallery;

    EditText et_id, et_password, et_name, et_hobby, et_type, et_job;

    Spinner spin_home, spin_year;

    ImageView imageView_picture, btn_register;

    RadioButton rb_man, rb_girl;

    Bitmap imageBitmap = null;

    private int PICK_IMAGE_REQUEST = 1;

    SharedPreferences auto_login;
    SharedPreferences.Editor editor;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        btn_register = (ImageView) findViewById(R.id.btn_register);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_hobby = (EditText) findViewById(R.id.et_hobby);
        et_type = (EditText) findViewById(R.id.et_type);
        et_job = (EditText) findViewById(R.id.et_job);

        imageView_picture = (ImageView) findViewById(R.id.imageView_picture);

        spin_home = (Spinner) findViewById(R.id.spinner_home);
        spin_year = (Spinner) findViewById(R.id.spinner_year);

        rb_man = (RadioButton) findViewById(R.id.rb_man);
        rb_girl = (RadioButton) findViewById(R.id.rb_girl);

        auto_login = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = auto_login.edit();

        intent = getIntent();

        if(intent.getStringExtra("before").toString().equals("change")) {   // 이전에 온 값이 메인화면이면 수정하기 버튼으로 바꿔주고, 회원가입에서 왔으면 회원가입 버튼을 설정
            et_id.setText(auto_login.getString("ID", ""));
            btn_register.setImageResource(R.drawable.login_register_change_button);
        }


            ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.home, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        ArrayAdapter adapter_year = ArrayAdapter.createFromResource(
                this, R.array.birth_year, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spin_home.setAdapter(adapter);
        spin_year.setAdapter(adapter_year);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_id.getText().toString() != null &&
                        et_password.getText().toString() != null &&
                        et_name.getText().toString() != null &&
                        et_hobby.getText().toString() != null &&
                        et_type.getText().toString() != null &&
                        et_job.getText().toString() != null &&
                        (rb_man.isChecked() || rb_girl.isChecked()) &&
                        imageBitmap != null ) {     // 값들을 모두 입력햇으면

                    ProgressDialog loading = ProgressDialog.show(LoginRegisterActivity.this,
                            "Please Wait", null, true, true);

                    Log.d("이미지", String.valueOf(imageView_picture.getResources()));

                    String id = et_id.getText().toString();
                    String password = et_password.getText().toString();
                    String name = et_name.getText().toString();
                    String hobby = et_hobby.getText().toString();
                    String type = et_type.getText().toString();
                    String job = et_job.getText().toString();

                    String home = spin_home.getSelectedItem().toString();
                    String sex;

                    String age = String.valueOf(spin_year.getSelectedItemPosition());

                    if (rb_man.isChecked())
                        sex = "0";  //남자
                    else
                        sex = "1";  //여자


                    ByteArrayOutputStream stream = new ByteArrayOutputStream(); // 이미지 업로드를 위한 작업
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    byte[] byte_arr = stream.toByteArray();
                    String encodedString = Base64.encodeToString(byte_arr, 0);

                    upload("http://jun123101.cafe24.com/ImageUpload.php","POST",getEncoded64ImageStringFromBitmap(imageBitmap), id);
                    //ImageUpload.php에 사진을 보내기 위한 upload 메소드


                    insertToDatabase(id, password, name, hobby, type, job, home, sex, age);     //register.php와 연결된 db에 업로드

                    if(intent.getStringExtra("before").toString().equals("change")){
                        Toast.makeText(getApplicationContext(), "프로필 수정이 완료되었습니다. ", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    loading.dismiss();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "모두다 빠짐없이 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //갤러리에서 가져오기 버튼을 누르면 선택에따라 갤러리/포토/네이버 클라우드로 intent한다.

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_PICK);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                imageBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);      //선택한 이미지를 Bitmap 형태로 받아온다

                imageView_picture.setImageBitmap(imageBitmap);  //받아온 이미지 설정
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertToDatabase(String id, String password, String name, String hobby, String type, String job, String home, String sex, String  age) {

                    //db에 회원정보 등록

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
                    String password = (String) params[1];
                    String name = (String) params[2];
                    String hobby = (String) params[3];
                    String type = (String) params[4];
                    String job = (String) params[5];
                    String home = (String) params[6];
                    String sex = (String) params[7];
                    String age = (String) params[8];

                    String link = "http://jun123101.cafe24.com/register.php";   // data에 id, password,,,, 값들을 넣는다.
                    String data = URLEncoder.encode("id", "UTF-8") + "="
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
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "="
                            + URLEncoder.encode(age, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();      //register.php와 연결

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
        task.execute(id, password, name, hobby, type, job, home, sex, age); // 실행
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }
    public void upload(String Url, String method, String imageString, String id) {  // 사진 업로드를 위한 메소드
        new AsyncTask<String, String, String>() {
            String method;
            int tmp;
            String data="";

            protected void onPreExecute() {
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    method = params[1];
                    String urlParams = params[2];
                    String id_data = params[3];

                    String id = URLEncoder.encode("id", "UTF-8") + "="
                            + URLEncoder.encode(id_data, "UTF-8");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod(method);

                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setAllowUserInteraction(false);

                    OutputStream os = httpURLConnection.getOutputStream();

                    String full_data = urlParams + "&" + id;    // ImageUpload.php 에 id 값을 보낸다.

                    os.write(full_data.getBytes());
                    os.flush();
                    os.close();

                    InputStream is = httpURLConnection.getInputStream();
                    while((tmp=is.read())!=-1){
                        data+= (char)tmp;
                    }

                    is.close();
                    httpURLConnection.disconnect();

                    return data;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "Exception: "+e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Exception: "+e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(Url, method, imageString, id);        //실행
    }


}
