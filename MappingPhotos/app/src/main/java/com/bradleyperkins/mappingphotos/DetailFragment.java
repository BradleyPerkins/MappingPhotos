package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// DetailFragment.Java

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailFragment extends Fragment {


    private static final String ARG_POSITION = "ARG_POSITION";
    private int position;

    private ArrayList<MapItem> itemsList;


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Delete Item
            case R.id.delete:
                itemsList = FileHelper.readData(getActivity());
                itemsList.remove(position);
                FileHelper.writeData(itemsList, getContext());
                //Nav Back to Map after Removal
                Intent mapIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(mapIntent);
        }

        return false;
    }

    public static DetailFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemsList = FileHelper.readData(getActivity());
        position = getArguments().getInt(ARG_POSITION);

        TextView noteTV = getView().findViewById(R.id.note_tv);
        TextView titleTV = getView().findViewById(R.id.title_tv);
        ImageView imageIV = getView().findViewById(R.id.photo_taken_iv);

        noteTV.setText(itemsList.get(position).getNote());
        titleTV.setText(itemsList.get(position).getTitle());
        String image = itemsList.get(position).getPhotoTaken();
        Glide.with(this).load(image).into(imageIV);


    }
}
