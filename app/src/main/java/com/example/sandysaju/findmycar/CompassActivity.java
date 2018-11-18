package com.example.sandysaju.findmycar;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    public MyDatabase myDatabase;
    public String longitude;
    public String latitude;
    public String isTracking;
    public double currentLongitude;
    public double currentLatitude;
    private LocationCallback mlocationCallback;
    private LocationRequest mlocationRequest;
    Boolean hasValues = false;
    double lastAngle;
    ImageView compass;
    private SensorManager mSensorManager;
    private Sensor mMagneticField;
    private Sensor mAccelerometer;
    float[] mGravity;
    float[] mGeomagnetic;
    float rotation;
    public TextView distanceTextView;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    RipplePulseLayout mRipplePulseLayout;

    private FusedLocationProviderClient mfusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        distanceTextView = findViewById(R.id.distance);
        setSupportActionBar(toolbar);
        compass = findViewById(R.id.compassImage);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mRipplePulseLayout = findViewById(R.id.layout_ripplepulse);

        myDatabase = MyDatabase.getDatabase(getApplicationContext());
        getValues();

        mlocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null){
                    return;
                }
                currentLatitude = locationResult.getLastLocation().getLatitude();
                currentLongitude = locationResult.getLastLocation().getLongitude();

                Log.d("", "onLocationResult: " + currentLongitude + " ");
            }
        };

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(2000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    Log.d("", "onSuccess: " + currentLongitude + " " + currentLatitude);
                }
            }
        });

        mfusedLocationProviderClient.requestLocationUpdates(mlocationRequest, mlocationCallback, null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle(getString(R.string.compassTitle));
                builder.setMessage(getString(R.string.compassMessage));
                builder.setPositiveButton(getString(R.string.compassOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                myDatabase.carLocation_DAO().set(new Configuration(getApplicationContext().getString(R.string.isTracking), false+""));
                            }
                        }).start();

                        Intent returnToMaps = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(returnToMaps);
                        finish();
                    }
                });
                builder.setNegativeButton(getString(R.string.compassCancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.show();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    public void getValues(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                longitude = myDatabase.carLocation_DAO().get(getApplicationContext().getString(R.string.longitude)).getValue();
                latitude = myDatabase.carLocation_DAO().get(getApplicationContext().getString(R.string.latitude)).getValue();
                isTracking = myDatabase.carLocation_DAO().get(getApplicationContext().getString(R.string.isTracking)).getValue();
                Log.d("", "DB: " + longitude + " "+ latitude + " " + isTracking);
                hasValues = true;
            }
        }).start();
    }

    public void updateCompass(){
        double angle = SphericalUtil.computeHeading(new LatLng(currentLatitude, currentLongitude), new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));

        //---------------------------
        Location location1 = new Location("");
        location1.setLatitude(Double.parseDouble(latitude));
        location1.setLongitude(Double.parseDouble(longitude));
        Location location2 = new Location("");
        location2.setLatitude(currentLatitude);
        location2.setLongitude(currentLongitude);

        double distance2 = location2.distanceTo(location1);
        double angle2 = location2.bearingTo(location1);
        //---------------------------
        angle = angle + rotation;
        double distance = SphericalUtil.computeDistanceBetween(new LatLng(currentLatitude, currentLongitude), new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        if(distance < 5){
            distanceTextView.setText(getString(R.string.foundCar));
            compass.setVisibility(View.INVISIBLE);
            mRipplePulseLayout.stopRippleAnimation();
        }else{
            compass.setVisibility(View.VISIBLE);
            mRipplePulseLayout.startRippleAnimation();
            ObjectAnimator.ofFloat(compass, getString(R.string.rotation), (float) lastAngle, (float) angle).start();
            distanceTextView.setText(df2.format(distance)+getString(R.string.meters));
        }

        lastAngle = angle;
        Log.d("", "updateCompass: " + lastAngle);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimut = orientation[0];
                rotation = -azimut * 360 / (2 * 3.14159f);
                Log.d("", "onSensorChanged: "+ rotation);
                if(hasValues) updateCompass();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
