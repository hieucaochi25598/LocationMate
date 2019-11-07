package com.akapro.codemau.mydestination.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akapro.codemau.mydestination.ActivityUtils;
import com.akapro.codemau.mydestination.R;
import com.akapro.codemau.mydestination.data.PlaceRepo;
import com.akapro.codemau.mydestination.data.model.Place;
import com.akapro.codemau.mydestination.map.ServiceAPI;
import com.akapro.codemau.mydestination.map.geocoding.GeocodingRoot;
import com.akapro.codemau.mydestination.map.geocoding.Location;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.akapro.codemau.mydestination.ActivityUtils.BASE_URL;
import static com.akapro.codemau.mydestination.ActivityUtils.CATEGORY_KEY_PUT_EXTRA;

public class AddEditActivity extends AppCompatActivity {
    @BindView(R.id.imgAddEditAct_PlacePicture)
    ImageView imgPlacePicture;
    @BindView(R.id.edtAddEditAct_PlaceName)
    EditText edtPlaceName;
    @BindView(R.id.edtAddEditAct_PlaceAddress)
    EditText edtPlaceAddress;
    @BindView(R.id.edtAddEditAct_PlaceDes)
    EditText edtPlaceDescription;

    private String placeID;
    private String categoryID;
    private PlaceRepo placeRepo;
    private Retrofit retrofit;
    private Location location;
    private boolean hasImage = false;
    private boolean allowSave = false;

    private ProgressDialog progressDialog;
    private  static final int IMAGE_CAPTURE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);

        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                if (placeID == null){
                    hasImage = false;
                    allowSave = false;
                }else {
                    hasImage = true;
                }

            } else {
                hasImage = true;
                allowSave = true;
                Bitmap placeImage = (Bitmap) data.getExtras().get("data");
                imgPlacePicture.setImageBitmap(placeImage);
            }
        }
    }

    private void init(){
        placeID = getIntent().getStringExtra(ActivityUtils.PLACE_KEY_PUT_EXTRA);
        categoryID = getIntent().getStringExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA);
        placeRepo = PlaceRepo.getInstance(this);
        initRetrofit();
        initProgessDialog();


        if (placeID != null){
            hasImage = true;
            setPlace(placeID, categoryID);
        }
    }

    private void initRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    private void initProgessDialog(){
        progressDialog = new ProgressDialog(AddEditActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.text_saving));
        progressDialog.setCanceledOnTouchOutside(false);

    }
    private void setPlace(String placeID, String categoryID){
        Place place = placeRepo.getPlace(categoryID, placeID);
        Bitmap placeImage = BitmapFactory.decodeByteArray(place.getPlaceImage(), 0, place.getPlaceImage().length);
        imgPlacePicture.setImageBitmap(placeImage);
        edtPlaceName.setText(place.getPlaceName());
        edtPlaceAddress.setText(place.getPlaceAddress());
        edtPlaceDescription.setText(place.getPlaceDescription());
    }

    private void getLatLng(String address)
    {
        ServiceAPI serviceAPI = retrofit.create(ServiceAPI.class);

        Call<GeocodingRoot> call = serviceAPI.getLocation(address);
        call.enqueue(new Callback<GeocodingRoot>() {
            @Override
            public void onResponse(Call<GeocodingRoot> call, Response<GeocodingRoot> response) {
                GeocodingRoot geocodingRoot = response.body();
                double lat = geocodingRoot.getResults().get(0).getGeometry().getLocation().getLat();
                double lng = geocodingRoot.getResults().get(0).getGeometry().getLocation().getLng();
                location = new Location(lat,lng);
            }

            @Override
            public void onFailure(Call<GeocodingRoot> call, Throwable t) {

            }
        });
    }

    private byte[] convertImageViewToByteArray(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    private void redirectPlacesAct(){
        Intent placesActIntent = new Intent(AddEditActivity.this, PlacesActivity.class);
        placesActIntent.putExtra(CATEGORY_KEY_PUT_EXTRA, categoryID);
        startActivity(placesActIntent);
        finish();
    }
    private void redirectDetailAct(){
        Intent detailActIntent = new Intent(AddEditActivity.this, DetailActivity.class);
        detailActIntent.putExtra(ActivityUtils.PLACE_KEY_PUT_EXTRA, placeID);
        detailActIntent.putExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA, categoryID);
        startActivity(detailActIntent);
        finish();

    }
    @OnClick(R.id.btnAddEditActSave)
    public void savePlace(View v)
    {
        String placeName = edtPlaceName.getText().toString();
        String placeAddress = edtPlaceAddress.getText().toString();
        String placeDescription = edtPlaceDescription.getText().toString();

        if (Place.validateInput(placeName, placeAddress, placeDescription,categoryID))
        {
            allowSave = true;
            getLatLng(placeName + ", " + placeAddress);


        }else
        {
            Toast.makeText(getApplicationContext(),"pls fill information",Toast.LENGTH_SHORT).show();
        }
        if (allowSave){
            progressDialog.show();
            if (hasImage && placeID == null){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Place place = new Place.Builder()
                                .setPlaceID(UUID.randomUUID().toString())
                                .setCategoryID(categoryID)
                                .setPlaceName(placeName)
                                .setPlaceAddress(placeAddress)
                                .setPlaceDescription(placeDescription)
                                .setPlaceImage(convertImageViewToByteArray(imgPlacePicture))
                                .setPlaceLat(location.getLat())
                                .setPlaceLng(location.getLng())
                                .build();
                        placeRepo.insert(place);
                        progressDialog.dismiss();
                        redirectPlacesAct();

                    }
                },2000);


            }
            if (placeID != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Place place = new Place.Builder()
                                .setPlaceID(placeID)
                                .setCategoryID(categoryID)
                                .setPlaceName(placeName)
                                .setPlaceAddress(placeAddress)
                                .setPlaceDescription(placeDescription)
                                .setPlaceImage(convertImageViewToByteArray(imgPlacePicture))
                                .setPlaceLat(location.getLat())
                                .setPlaceLng(location.getLng())
                                .build();
                        placeRepo.update(place);
                        progressDialog.dismiss();
                        redirectDetailAct();

                    }
                },2000);
            }
        }
    }
    @OnClick(R.id.imgAddEditAct_PlacePicture)
    public void openCamera(View v)
    {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }
}
