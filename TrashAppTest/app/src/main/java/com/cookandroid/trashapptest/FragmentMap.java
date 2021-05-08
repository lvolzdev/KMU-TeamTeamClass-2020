package com.cookandroid.trashapptest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;

import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.makeText;

public class FragmentMap extends Fragment implements OnMapReadyCallback {
    private Context context;
    private MapView mapView = null;
    GoogleMap mMap;
    LocationManager locationManager;

    FusedLocationProviderClient mFusedLocationClient;

    double longitude;
    double latitude;
    String mark[];
    String pointRQ;
    String refresh[];
    public FragmentMap(){

    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView)layout.findViewById(R.id.map);
        mapView.getMapAsync(this);
        context = container.getContext();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        refresh = null;
        final String[] error = {"error"};
        new Thread(){
            public void run(){

                Json json = new Json();
                TokenStorage ts = new TokenStorage();
                json.putJson("token", ts.getToken());
                json.putJson("rtoken", ts.getrToken());
                String msg = json.sendREST("http://10.0.2.2:8000/refresh", "PUT", json.getJson());
                if(msg.equals("401")){
                    refresh = error;
                } else {
                    refresh = json.orderDict(msg);
                    ts.setToken(refresh[0]);
                    ts.setrToken(refresh[1]);
                }
            }
        }.start();

        while(refresh == null) Log.d("test", "mang");

        return layout;
    }

    LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d("test", Double.toString(longitude));
            Log.d("test", Double.toString(latitude));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//액티비티가 처음 생성될 때 실행되는 함수

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getContext().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }//권한 요청
        else{
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location location = locationManager.getLastKnownLocation(locationProvider);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        } // 권한 요청 -> 성공 -> 테스트 결과: 내 위치(위도 경도) 계속 업데이트

        final LatLng SEOUL = new LatLng(37.56, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 16)); //지도 초기 화면을 서울로 설정

        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            //@Override
           public boolean onMyLocationButtonClick() {

               mark = null;
               final String error[] = {"error"};
               new Thread(){
                   public void run(){
                       Json json = new Json();
                       TokenStorage ts = new TokenStorage();
                       String arr[];
                       json.putJson("token", ts.getToken());
                       json.putJson("latitude", Double.toString(latitude));
                       json.putJson("longitude", Double.toString(longitude));
                       String msg = json.sendREST("http://10.0.2.2:8000/gps", "PUT", json.getJson());
                       if(msg.equals(401)){
                           // token 만료됨
                           arr = error;
                       } else {
                           arr = json.orderDict(msg);
                       }
                       while(arr == null) Log.d("test", "mang");
                       mark = arr;
                   }
               }.start();

               while(mark == null) Log.d("test", "mang");
               mMap.clear();
               Log.d("test1", "asdf");
               if(mark != error) manyMarker(mark);
               Log.d("test1", "qwer");

               return false;
           }
        });

    }

    private void manyMarker(String[] marker) {
        // 여기 수정 부탁드립니다 했던걸로(clear)
        // 주소1 위도1 경도1 주소2 위도2 경도2 주소3 위도3 경도3(clear)
        for(int i = 0; i < 9; i++){ //'""'
            marker[i] = marker[i].substring(1, marker[i].length()-1);
            Log.d("test3", marker[i]);
        }
        //marker[0], marker[3], marker[6] 한글 주소 값
        LatLng[] address = {new LatLng(Double.parseDouble(marker[1]), Double.parseDouble(marker[2])), new LatLng(Double.parseDouble(marker[4]), Double.parseDouble(marker[5])), new LatLng(Double.parseDouble(marker[7]), Double.parseDouble(marker[8])) };
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


        for(int i = 0; i < 3; i++){
            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(address[i]).title(marker[0 + i * 3]);

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.markera);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            mMap.addMarker(makerOptions);
        }



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                pointRQ = null;
                final String[] error = {"error"};
                new Thread(){
                    public void run(){

                        Json json = new Json();
                        TokenStorage ts = new TokenStorage();
                        json.putJson("token", ts.getToken());
                        json.putJson("where", "me");
                        String msg = json.sendREST("http://10.0.2.2:8000/users/point", "PUT", json.getJson());
                        if(msg.equals("401")){
                            // 역시 토큰 에러죠?
                            pointRQ = msg;
                        } else if(msg.equals("date error")) {
                            // 이미 출첵을 함 오늘
                            pointRQ = msg;
                        } else {
                            // 포인트 +1 시키기 성공함
                            pointRQ = msg;
                        }
                    }
                }.start();

                while(pointRQ == null) Log.d("test", "mang");

                return false;
            }
        });
    }

}