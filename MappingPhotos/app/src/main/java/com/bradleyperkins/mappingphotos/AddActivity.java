package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// AddActivity.Java

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements AddFragment.MapItemListener {

    public static final String TAG = "AddActivity";

    private double currLongitude;
    private double currLatitude;

    private ArrayList<MapItem> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        int result = RESULT_OK;

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            currLatitude = getIntent().getDoubleExtra(ItemsMapFragment.LAT_EXTRA, 0);
            currLongitude = getIntent().getDoubleExtra(ItemsMapFragment.LNG_EXTRA, 0);
        }

        if (savedInstanceState == null) {
            AddFragment frag = AddFragment.newInstance(result);
            getSupportFragmentManager().beginTransaction().replace(R.id.add_placeholder, frag).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void addItem(String note, String photoPath, String title) {
        //Save note, title , lat, lng, and photoPath to ArrayList
        Toast.makeText(this, "New Item Added", Toast.LENGTH_SHORT).show();
        itemsList = FileHelper.readData(this);
        itemsList.add(new MapItem(currLongitude, currLatitude, note, photoPath, title));
        FileHelper.writeData(itemsList, this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
