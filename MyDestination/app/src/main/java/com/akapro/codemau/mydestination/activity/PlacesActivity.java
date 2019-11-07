package com.akapro.codemau.mydestination.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akapro.codemau.mydestination.ActivityUtils;
import com.akapro.codemau.mydestination.R;
import com.akapro.codemau.mydestination.adapter.PlacesAdapter;
import com.akapro.codemau.mydestination.data.PlaceRepo;
import com.akapro.codemau.mydestination.data.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesActivity extends AppCompatActivity {
    @BindView(R.id.lvPlaceAct)
    ListView lvPlace;
    @BindView(R.id.txtPlaceAct_NoData)
    TextView txtNoData;

    private String categoryID;
    private PlaceRepo placeRepo;
    private List<Place> places = new ArrayList<>();
    private PlacesAdapter placesAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);
        init();
        Toast.makeText(this,"hello", Toast.LENGTH_SHORT).show();
    }

    private void init()
    {
        categoryID = getIntent().getStringExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA);
        placeRepo = PlaceRepo.getInstance(this);
        placesAdapter = new PlacesAdapter(this, places);

        initProgressDialog();

        getPlaces();
        onPlaceClick();
    }

    private void getPlaces(){
        places = placeRepo.getPlaces(categoryID);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (!places.isEmpty())
                {
                    txtNoData.setVisibility(View.GONE);
                }
                lvPlace.setAdapter(placesAdapter);
                placesAdapter.updatePlaces(places);

            }
        },2000);
    }
    private void initProgressDialog()
    {
        progressDialog = new ProgressDialog(PlacesActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.text_retrieving_data));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void onPlaceClick()
    {
        lvPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = places.get(position);
                Intent detailActIntent = new Intent(PlacesActivity.this, DetailActivity.class);
                detailActIntent.putExtra(ActivityUtils.PLACE_KEY_PUT_EXTRA, place.getPlaceID());
                detailActIntent.putExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA, place.getCategoryID());
                startActivity(detailActIntent);
            }
        });
    }
    @OnClick(R.id.fabPlaceAtc_AddNewPlace)
    public void addNewPlace(View view)
    {
        Intent addEditActIntent = new Intent(PlacesActivity.this, AddEditActivity.class);
        addEditActIntent.putExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA, categoryID);
        startActivity(addEditActIntent);
    }
    @OnClick(R.id.btnPlaceAct_ShowAllOnMap)
    public void showAllOnMap(View view){
        Intent mapActIntent = new Intent(PlacesActivity.this, MapActivity.class);
        mapActIntent.putExtra(ActivityUtils.CATEGORY_KEY_PUT_EXTRA, categoryID);
        startActivity(mapActIntent);
    }
}
