package com.example.gprslocation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txtlocation, txtStatusGPS;

    LocationListener locationListener;
    LocationManager locationManager;
    double latitudeX1, latitudeX2, longtitudeY1, longtitudeY2, refLatitude, refLongtitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtlocation = findViewById(R.id.location);
        txtStatusGPS = findViewById(R.id.statusgps);

        getSupportActionBar().setTitle("STIKOM BALI");

        setLatitude();

        GetLocationUpdate();

    }

    private void setLatitude(){
        if (configLocation.LATITUDEX1 > configLocation.LATITUDEX2){
            latitudeX1 = configLocation.LATITUDEX1;
            latitudeX2 = configLocation.LATITUDEX2;
        }
        else {
            latitudeX1 = configLocation.LATITUDEX2;
            latitudeX2 = configLocation.LATITUDEX1;
        }

        if (configLocation.LONGTITUDEY1 > configLocation.LONGTITUDEY2){
            longtitudeY1 = configLocation.LONGTITUDEY1;
            longtitudeY2 = configLocation.LONGTITUDEY2;
        }
        else {
            longtitudeY1 = configLocation.LONGTITUDEY2;
            longtitudeY2 = configLocation.LONGTITUDEY1;
        }

        refLatitude = configLocation.REFLATITUDE;
        refLongtitude = configLocation.REFLONGTITUDE;
    }

    private void GetLocationUpdate(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }
        else {
            getLocation();
        }
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            getLocation();

        }
        else {

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null){
                        double lat = location.getLatitude();
                        double _long = location.getLongitude();
                        txtlocation.setText("LAT : " + String.valueOf(lat) + "\nLONG : " + String.valueOf(_long));
                        double jarak = checkJarak(refLatitude, lat, refLongtitude, _long);

                        if (checkLocation(lat, _long)){
                            txtStatusGPS.setText("Masuk Gan\nJarak : " + String.format("%.2f", jarak) + " M");
                        }
                        else {
                            txtStatusGPS.setText("Gak Masuk Gan\nJarak : " + String.format("%.2f", jarak) + " M");
                        }
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        }
    }


    private double checkJarak(double lat1, double lat2, double _long1, double _long2){

        double laDistance = toRad(lat2 - lat1);
        double longDistance = toRad(_long2 - _long1);

        double a = Math.sin(laDistance / 2) * Math.sin(laDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double jarak = 6371 * c * 1000;

        return jarak;
    }

    private double toRad(double value){
        return value * Math.PI / 180;
    }


    private boolean checkLocation(double lat, double _long){

        if (lat < latitudeX1 && lat > latitudeX2){
            if (_long < longtitudeY1 && _long > longtitudeY2){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private void onGPS(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }). setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
