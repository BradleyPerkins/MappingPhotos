package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// ItemsMapFragment.Java

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class ItemsMapFragment extends MapFragment implements OnMapReadyCallback, LocationListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = "MapFragment";
    private static final int REQUEST_LOCATION_PERMISSION = 0x01001;
    private boolean requestUpdates = false;

    public static final String LAT_EXTRA = "ItemsMapFragment.LAT_EXTRA";
    public static final String LNG_EXTRA = "ItemsMapFragment.LNG_EXTRA";
    public static final String MARKER_POS_EXTRA = "ItemsMapFragment.MARKER_POS__EXTRA";

    private double currLongitude;
    private double currLatitude;

    private ArrayList<MapItem> itemsList;

    private GoogleMap mMap;
    LocationManager locationManager;


    public ItemsMapFragment() {
        // Required empty public constructor
    }

    public static ItemsMapFragment newInstance() {

        Bundle args = new Bundle();
        ItemsMapFragment fragment = new ItemsMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            Location lastKnown = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            if (lastKnown != null){
                currLongitude = lastKnown.getLongitude();
                currLatitude = lastKnown.getLatitude();
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(LAT_EXTRA, currLatitude);
                intent.putExtra(LNG_EXTRA, currLongitude);
                startActivity(intent);
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(true);

        //Load Markers
        loadMarkers();

        //Zoom to current location
        zoomCamera();
    }

    //Load map markers
    private void loadMarkers(){
        itemsList = FileHelper.readData(getActivity());

        for (int i=0; i<itemsList.size(); i++){
            LatLng markerLatLng = new LatLng(itemsList.get(i).getCurrLatitude(), itemsList.get(i).getCurrLongitude());
//            String image = itemsList.get(i).getPhotoTaken();
//            File imgFile = new  File(image);
//            BitmapDescriptor photo = BitmapDescriptorFactory.fromFile(image);

            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(itemsList.get(i).getTitle())
                    .snippet(itemsList.get(i).getNote()));
        }

    }

    //Current Location
    private void zoomCamera(){
        if (mMap == null){
            return;
        }
        LatLng currentLatLng = new LatLng(currLatitude, currLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currLatitude, currLongitude)).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnInfoWindowClickListener(this);
    }

    //Gets the Current Location
    public void updateLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && !requestUpdates){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    2000, 16, (LocationListener) this);
            requestUpdates = true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation();

        currLongitude = location.getLongitude();
        currLatitude = location.getLatitude();

        LatLng currentLatLng = new LatLng(currLatitude, currLongitude);
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You Are Here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        //Center cameera on Current Position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currLatitude, currLongitude)).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View contents = LayoutInflater.from(getActivity())
                .inflate(R.layout.custom_info_window, null);
        ((TextView)contents.findViewById(R.id.title)).setText(marker.getTitle());
        ((TextView)contents.findViewById(R.id.note)).setText(marker.getSnippet());

        return contents;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        updateLocation();
        //Intent to details
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        String id = marker.getId();
        intent.putExtra(MARKER_POS_EXTRA, id);
        startActivity(intent);
    }
}
