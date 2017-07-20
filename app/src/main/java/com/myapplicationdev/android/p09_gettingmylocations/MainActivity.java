 package com.myapplicationdev.android.p09_gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

 public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
     private GoogleApiClient mGoogleApiClient;

     TextView tvLat,tvLong;
     Button btnStart,btnStop,btnCheckR;

     String folderLocation;
     private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLat = (TextView)this.findViewById(R.id.tvLat);
        tvLong = (TextView)this.findViewById(R.id.tvLong);

        btnStart = (Button)this.findViewById(R.id.btnStart);
        btnStop = (Button)this.findViewById(R.id.btnStop);
        btnCheckR  = (Button)this.findViewById(R.id.checkR);

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Test";

        File folder = new File(folderLocation);
        if(folder.exists() == false){
            boolean result = folder.mkdir();
            if(result == true){
                Log.d("File Read/Write","Folder created");
            }
        }


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MainActivity.this, MyService.class);
                startService(i);

                Toast.makeText(MainActivity.this, "Service is running", Toast.LENGTH_SHORT).show();


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);

                Toast.makeText(MainActivity.this, "Service is stopped", Toast.LENGTH_SHORT).show();
            }
        });

        btnCheckR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File targetFile = new File(folderLocation, "data.txt");

                if (targetFile.exists() == true){
                    String data ="";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null){
                            data += line + "\n";
                            line = br.readLine();
                            Toast.makeText(MainActivity.this, data,
                                    Toast.LENGTH_LONG).show();

                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to read!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            }
        });



        int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = PermissionChecker.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck != PermissionChecker.PERMISSION_GRANTED && permissionCheck2 != PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

            return;
        }



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
     public void onConnected(@Nullable Bundle bundle){
         int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
         int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

         if(permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED){
             mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

             LocationRequest mLocationRequest = LocationRequest.create();
             mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
             mLocationRequest.setInterval(10000);
             mLocationRequest.setFastestInterval(5000);
             mLocationRequest.setSmallestDisplacement(100);
             LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
         } else{
             mLocation = null;
             Toast.makeText(MainActivity.this,"Permission not granted to retrieve location info",Toast.LENGTH_SHORT).show();
         }
         if(mLocation != null){
             tvLat.setText(String.valueOf(mLocation.getLatitude()));
             tvLong.setText(String.valueOf(mLocation.getLongitude()));
         } else{
             Toast.makeText(this,"Location not Detected",Toast.LENGTH_LONG).show();
         }

     }

     @Override
     public void onConnectionSuspended(int i) {}

     @Override
     public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

     @Override
     protected void onStart() {
         super.onStart();
         mGoogleApiClient.connect();
     }

     @Override
     protected void onStop() {
         super.onStop();
         if (mGoogleApiClient.isConnected()) {
             mGoogleApiClient.disconnect();
         }
     }

     @Override
     public void onLocationChanged(Location location){
         tvLat.setText(String.valueOf(location.getLatitude()));
         tvLong.setText(String.valueOf(location.getLongitude()));
     }





}
