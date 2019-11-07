package com.akapro.codemau.mydestination.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akapro.codemau.mydestination.R;
import com.akapro.codemau.mydestination.data.PlaceRepo;
import com.akapro.codemau.mydestination.data.model.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.akapro.codemau.mydestination.ActivityUtils.CATEGORY_KEY_PUT_EXTRA;

public class CategoriesActivity extends AppCompatActivity {
    @BindView(R.id.txtCategoryAct_Restaurant)
    TextView txtRestaurant;
    @BindView(R.id.txtCategoryAct_Cinema)
    TextView txtCinema;
    @BindView(R.id.txtCategoryAct_Fashion)
    TextView txtFashion;
    @BindView(R.id.txtCategoryAct_ATM)
    TextView txtAtm;


    private PlaceRepo placeRepo;
    private List<Category> categories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ButterKnife.bind(this);
        init();
    }
    private void init()
    {
        placeRepo = PlaceRepo.getInstance(this);
        categories = placeRepo.getCategories();
        setCategories();
    }

    private void setCategories()
    {
        txtRestaurant.setText(categories.get(0).getCategoryName());
        txtCinema.setText(categories.get(1).getCategoryName());
        txtFashion.setText(categories.get(2).getCategoryName());
        txtAtm.setText(categories.get(3).getCategoryName());
    }
    private void startPlacesAct(String categoryID)
    {
        Intent placesActIntent = new Intent(CategoriesActivity.this, PlacesActivity.class);
        placesActIntent.putExtra(CATEGORY_KEY_PUT_EXTRA, categoryID);
        startActivity(placesActIntent);
    }
    @OnClick(R.id.layoutCategories_Restaurant)
    public void clickOnRestaurant(View v)
    {
        String categoryID = categories.get(0).getCategoryID();
        startPlacesAct(categoryID);
    }
    @OnClick(R.id.layoutCategories_Cinema)
    public void clickOnCinema(View v)
    {
        String categoryID = categories.get(1).getCategoryID();
        startPlacesAct(categoryID);
    }
    @OnClick(R.id.layoutCategories_Fashion)
    public void clickOnFashion(View v)
    {
        String categoryID = categories.get(2).getCategoryID();
        startPlacesAct(categoryID);
    }
    @OnClick(R.id.layoutCategories_ATM)
    public void clickOnATM(View v)
    {
        String categoryID = categories.get(3).getCategoryID();
        startPlacesAct(categoryID);
    }

}
