package com.akapro.codemau.mydestination.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.akapro.codemau.mydestination.ActivityUtils;
import com.akapro.codemau.mydestination.R;
import com.akapro.codemau.mydestination.data.PlaceRepo;
import com.akapro.codemau.mydestination.data.model.Place;
import com.akapro.codemau.mydestination.map.ServiceAPI;
import com.akapro.codemau.mydestination.map.direction.DirectionRoot;
import com.akapro.codemau.mydestination.map.geocoding.GeocodingRoot;
import com.akapro.codemau.mydestination.map.geocoding.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.akapro.codemau.mydestination.ActivityUtils.BASE_URL;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private List<LatLng> latLngs = new ArrayList<>();
    private Marker currentMaker;
    private android.location.Location currLocation;



    private String categoryID;
    private PlaceRepo placeRepo;
    private List<Place> places = new ArrayList<>();
    private Retrofit retrofit;
    private ProgressDialog progressDialog;
    private static final int GPS_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void init(){
        categoryID = getIntent().getStringExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA);
        placeRepo = PlaceRepo.getInstance(this);
        initProgressDialog();
        initRetrofit();
        getPlaces();
    }

    private void initProgressDialog(){
        progressDialog = new ProgressDialog(MapActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.text_retrieving_data));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    private void initRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void getPlaces(){
        places = placeRepo.getPlaces(categoryID);
    }


    private void buildApiClient(){
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
    }

    private void displayPlacesOnMap(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Place place : places){
                    LatLng placeLocation = new LatLng(place.getPlaceLat(), place.getPlaceLng());
                    googleMap.addMarker(new MarkerOptions()
                            .position(placeLocation)
                            .title(place.getPlaceName()));

                }
                progressDialog.dismiss();
            }
        }, 4000);
    }
    private void getDirection(LatLng origin, LatLng destination){
        ServiceAPI serviceAPI = retrofit.create(ServiceAPI.class);
        String originAddress = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
        String destinationAddress = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);
        //String destinationAddress = "10.7756586, 106.7004239";
        Call<DirectionRoot> call = serviceAPI.getDirection(originAddress, destinationAddress);
        call.enqueue(new Callback<DirectionRoot>() {
            @Override
            public void onResponse(Call<DirectionRoot> call, Response<DirectionRoot> response) {
                DirectionRoot directionRoot = response.body();
                String polylines = directionRoot.getRoutes().get(0).getOverview_polyline().getPoints();

                List<LatLng> decodePath = PolyUtil.decode(polylines);
                googleMap.addPolyline(new PolylineOptions().addAll(decodePath));
            }

            @Override
            public void onFailure(Call<DirectionRoot> call, Throwable t) {

            }
        });
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                        currLocation = location;
                        if (apiClient != null){
                            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient,this);
                        }
                }
            });
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case GPS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        if (apiClient == null){
                            buildApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }else {
                        Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        displayPlacesOnMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildApiClient();
            this.googleMap.setMyLocationEnabled(true);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GPS_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng currLatLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
        getDirection(currLatLng, marker.getPosition());
        return false;
    }
}
