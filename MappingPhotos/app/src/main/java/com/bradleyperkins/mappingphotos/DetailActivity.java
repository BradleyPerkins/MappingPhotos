package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// DetailActivity.Java

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String idString = getIntent().getStringExtra(ItemsMapFragment.MARKER_POS_EXTRA);

            String newId = null;
            if (idString != null && idString.length() > 1) {
                newId = idString.substring(1, idString.length());
                pos = Integer.valueOf(newId);
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_placeholder, DetailFragment.newInstance(pos)).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


}
