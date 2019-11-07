package com.akapro.codemau.mydestination.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akapro.codemau.mydestination.ActivityUtils;
import com.akapro.codemau.mydestination.R;
import com.akapro.codemau.mydestination.data.PlaceRepo;
import com.akapro.codemau.mydestination.data.model.Place;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.imgDetailAct_PlacePicture)
    ImageView imgPlacePicture;
    @BindView(R.id.edtDetailAct_PlaceName)
    EditText edtPlaceName;
    @BindView(R.id.edtDetailAct_PlaceAddress)
    EditText edtPlaceAddress;
    @BindView(R.id.edtDetailAct_PlaceDes)
    EditText edtPlaceDescription;

    private String placeID;
    private String categoryID;
    private PlaceRepo placeRepo;
    private Place place;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        placeID = getIntent().getStringExtra(ActivityUtils.PLACE_KEY_PUT_EXTRA);
        categoryID = getIntent().getStringExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA);
        placeRepo = PlaceRepo.getInstance(this);
        Log.d("Test", placeID + " " +categoryID);
        initProgressDialog();
        setPlace();
    }

    private void setPlace(){
        place = placeRepo.getPlace(categoryID,placeID);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (place.getPlaceImage() != null)
                {
                    Bitmap placeBitmap = BitmapFactory.decodeByteArray(place.getPlaceImage(),0, place.getPlaceImage().length);
                    imgPlacePicture.setImageBitmap(placeBitmap);
                }

                edtPlaceName.setText(place.getPlaceName());
                edtPlaceAddress.setText(place.getPlaceAddress());
                edtPlaceDescription.setText(place.getPlaceDescription());

            }
        },2000);

    }
    private void initProgressDialog(){
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.text_retrieving_data));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    @OnClick(R.id.imgBtnDetailAct_Delete)
    public void deletePlace(View v){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.text_warning));
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage(getResources().getString(R.string.warning_do_you_want_to_delete)
                + " '"
                + place.getPlaceName()
                + "' ?"
        );

        alertDialog.setPositiveButton(getResources().getString(R.string.text_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(),"Yes",Toast.LENGTH_SHORT).show();
                placeRepo.delete(placeID);
                Intent i = new Intent(DetailActivity.this, PlacesActivity.class);
                startActivity(i);
            }
        });

        alertDialog.setNegativeButton(getResources().getString(R.string.text_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"No",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }
    @OnClick(R.id.imgBtnDetailAct_Edit)
    public void editPlace(View v){
        Intent addEditActIntent = new Intent(DetailActivity.this, AddEditActivity.class);
        addEditActIntent.putExtra(ActivityUtils.PLACE_KEY_PUT_EXTRA, place.getPlaceID());
        addEditActIntent.putExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA, place.getCategoryID());
        startActivity(addEditActIntent);

    }
    @OnClick(R.id.imgBtnDetailAct_Direction)
    public void getDirection(View v){
    }
}
