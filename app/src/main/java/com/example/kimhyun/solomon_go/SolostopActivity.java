package com.example.kimhyun.solomon_go;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SolostopActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GoogleMap map;

    int count=0;
    float latitude_position,longitude_position;
    double d_latitude,d_longitude;
    double latitude_solo,longitude_solo;

    Intent intent;
    String id;
    SharedPreferences sp_id;
    SharedPreferences.Editor editor;
    int select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solostop);
        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        intent = getIntent();
        sp_id = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = sp_id.edit();
        select = intent.getIntExtra("map",0);
        id = sp_id.getString("ID", "");
        // 현재 위치의 지도를 보여주기 위해 정의한 메소드 호출


        //구글 맵 객체 참조
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");
                map = googleMap;

                latitude_position = sp_id.getFloat("latitude",30.4512074f);
                longitude_position = sp_id.getFloat("longitude",127.1277899f);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_position,longitude_position),17));
                switch (select) {
                    case 1:
                        break;
                    case 2:
                        latitude_solo = intent.getDoubleExtra("latitude_solo",0);
                        longitude_solo = intent.getDoubleExtra("longitude_solo",0);
                        Toast.makeText(getApplicationContext(),latitude_solo + "\n" + longitude_solo,Toast.LENGTH_LONG).show();
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude_solo, longitude_solo))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.solo_location))
                        ).showInfoWindow();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_solo,longitude_solo),17));
                        break;
                }

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Toast.makeText(getApplicationContext(), "마커 클릭 확인용 토스트", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setMapToolbarEnabled(false);
            }
        });//end of fragment.getMapAsync



        // 일부 초기버전 단말의 문제로 인해 초기화 코드 추가
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }//end of try

        // 위치 정보 확인을 위해 정의한 메소드 호출
        startLocationService();


    }//end of onCreate

    // 위치 정보 확인을 위해 정의한 메소드
    private void startLocationService() {
        // 위치 관리자 객체 참조

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 위치 리스너 객체 생성

        GPSListener gpsListener = new GPSListener();

        long minTime = 1000;//GPS정보 전달 시간 지정 - 1초마다 위치정보 전달
        float minDistance = 0;//이동거리 지정 - 이동하면 무조건 갱신

        //showCurrentLocation((double)latitude_position,(double)longitude_position);//위치이동
        //위치정보는 위치 프로바이더(Location Provider)를 통해 얻는다
        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, //위치 정보 확인 방법 설정
                    minTime, // 위치 정보 갱신 시간 설정
                    minDistance, //위치 정보 갱신을 위한 최소 이동거리 설정
                    gpsListener);//위치가 변동될 때마다 위치 정보 갱신을 위한 리스너 설정

            // 네트워크(기지국)를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
                //Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : "
                //        + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
                LatLng nowPoint = new LatLng(latitude,longitude);
            }

        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "위치 확인 시작함.", Toast.LENGTH_SHORT).show();
    }//end of startLocationService()


    //위치 정보 확인을 위한 위치 리스너 정의
    private class GPSListener implements LocationListener {
        //위치 정보가 확인(수신)될 때마다 자동 호출되는 메소드
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            latitude_position = (float)location.getLatitude();
            longitude_position = (float)location.getLongitude();
            Intent intent = getIntent();
            select = intent.getIntExtra("map",0);
            switch (select){
                case 1:
                    if(location.getAccuracy()<1000||count==0){
                        map.clear();
                        String msg = "Latitude : "+ latitude.toString() + "\nLongitude:"+ longitude.toString();
                        Log.i("GPSLocationService", msg);

                        insertToDatabase(latitude.toString(), longitude.toString());
                        editor.putFloat("latitude",latitude_position);
                        editor.putFloat("longitude",longitude_position);
                        editor.commit();
                        latitude_position = (float)location.getLatitude();

                        LatLng nowPoint = new LatLng(latitude,longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nowPoint,17));
                        // 현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
                        showCurrentLocation(latitude, longitude);//위치이동
                        count++;
                        //editor.putString("ID", ID);

                        LatLng makerPoint = new LatLng(latitude-0.000225f,longitude);
                        MarkerOptions optFirst = new MarkerOptions();
                        optFirst.position(makerPoint);// 위도 • 경도
                        optFirst.icon(BitmapDescriptorFactory.fromResource(
                                R.mipmap.ic_launcher_round));

                        map.addMarker(optFirst).showInfoWindow();
                        map.addCircle(new CircleOptions()
                                .center(nowPoint)
                                .radius(100)
                                .strokeColor(Color.parseColor("#88FF4081"))
                                .fillColor(Color.parseColor("#55ec068d")));
                    }

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(37.4515900, 127.1276563))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.solo_location))
                    ).showInfoWindow();
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(37.3986291, 127.1051303))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.solo_location))
                    ).showInfoWindow();
                    break;
                case 2:
                    if(location.getAccuracy()<1000||count==0){
                        map.clear();
                        String msg = "Latitude : "+ latitude.toString() + "\nLongitude:"+ longitude.toString();
                        Log.i("GPSLocationService", msg);
                        insertToDatabase(latitude.toString(), longitude.toString());

                        latitude_position = (float)location.getLatitude();
                        editor.putFloat("latitude",latitude_position);
                        editor.putFloat("longitude",longitude_position);
                        editor.commit();
                        LatLng nowPoint = new LatLng(latitude,longitude);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nowPoint,17));
                        // 현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
                        showCurrentLocation(latitude, longitude);//위치이동
                        count++;
                        //editor.putString("ID", ID);

                        LatLng makerPoint = new LatLng(latitude-0.000225f,longitude);
                        MarkerOptions optFirst = new MarkerOptions();
                        optFirst.position(makerPoint);// 위도 • 경도
                        optFirst.icon(BitmapDescriptorFactory.fromResource(
                                R.mipmap.ic_launcher_round));

                        map.addMarker(optFirst).showInfoWindow();
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude_solo, longitude_solo))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.solo_location))
                        ).showInfoWindow();
                    }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }// end of GPSListener

    /**
     * 현재 위치의 지도를 보여주기 위해 정의한 메소드
     * @param latitude
     * @param longitude
     */
    private void showCurrentLocation(Double latitude, Double longitude) {
        /* 현재 위치를 이용해 LatLng 객체 생성 - 위도와 경도로 좌표를 표시
           - 전달받은 위도와 경도 값으로 LatLng 객체를 만들면 지도 위에 표시될 수 있는
             새로운 포인트가 만들어짐(포인트 객체 생성)
         */
        LatLng curPoint = new LatLng(latitude, longitude);


        /* 구글맵 객체의 animateCamera() 메소드를 이용하여 그 위치(curPoint)를 중심으로 지도를 보여줌
            @param curPoint
            @param level(배율)
         */


        /* 지도 유형 설정
           - 일반지도인 경우 : GoogleMap.MAP_TYPE_NORMAL
           - 지형도인 경우 : GoogleMap.MAP_TYPE_TERRAIN,
           - 위성 지도인 경우 : GoogleMap.MAP_TYPE_SATELLITE
         */

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }// end of showCurrentLocation

    // 사용자의 권한 확인 후 사용자의 권한에 대한 응답 결과를 확인하는 콜백 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }//end of onRequestPermissionsResult

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void insertToDatabase(String latitude, String longitude) {

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
                    String latitude = (String) params[0];
                    String longitude = (String) params[1];

                    String link = "http://jun123101.cafe24.com/insertgps.php";
                    String data = URLEncoder.encode("latitude", "UTF-8") + "="
                            + URLEncoder.encode(latitude, "UTF-8");
                    data += "&" + URLEncoder.encode("longitude", "UTF-8") + "="
                            + URLEncoder.encode(longitude, "UTF-8");
                    data += "&" + URLEncoder.encode("id", "UTF-8") + "="
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
        task.execute(latitude, longitude);
    }



}//end of MainActivity
