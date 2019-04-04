package com.bradleyperkins.mappingphotos;

// Date 10/24/18
// Bradley Perkins
// MDF# - 1811
// AddFragment.Java

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 0x01001;
    private static final int REQUEST_GALLERY_PICK = 0x03001;
    private static final int REQUEST_CAMERA_PERMISSION = 0x02001;
    private static final int REQUEST_STORAGE_PERMISSION = 0x04001;

    private static final String ARG_RESULT_OK = "ARG_RESULT_OK";

    private int RESULT_OK;
    private Uri selectedImage = null;


    private static final String TAG = "AddFragment";

    private String note;
    private String title;

    private EditText noteET;
    private EditText titleET;
    private Button addBtn;
    private File photoFile = null;
    private String mCurrentPhotoPath;

    private MapItemListener mListener;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance(int result) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESULT_OK, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RESULT_OK = getArguments().getInt(ARG_RESULT_OK);
        noteET = getActivity().findViewById(R.id.note_et);
        titleET = getActivity().findViewById(R.id.title_et);
        addBtn = getActivity().findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);

        noteET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (!noteET.getText().toString().isEmpty() && mCurrentPhotoPath != null && !titleET.getText().toString().isEmpty()){
                        note = noteET.getText().toString().trim();
                        title = titleET.getText().toString().trim();
                        mListener.addItem(note, mCurrentPhotoPath, title);
                        handled = true;
                    }
                }
                return handled;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView cameraImageView = (ImageView) getActivity().findViewById(R.id.image_iv);
        switch(requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK &&
                            data != null && data.hasExtra("data")){
                        Bitmap cameraImage = (Bitmap)data.getParcelableExtra("data");
                        try {
                            cameraImage = ImageUtils.modifyOrientation(cameraImage, mCurrentPhotoPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        selectedImage = getImageUri(getContext(), cameraImage);
                        Glide.with(this).load(photoFile.getAbsolutePath()).into(cameraImageView);

                    }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK &&
                            (data == null || !data.hasExtra("data"))){
                        Bitmap cameraImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        try {
                            cameraImage = ImageUtils.modifyOrientation(cameraImage, mCurrentPhotoPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        selectedImage = getImageUri(getContext(), cameraImage);
                        Glide.with(this).load(photoFile.getAbsolutePath()).into(cameraImageView);
                    }
                }
                break;
            case REQUEST_GALLERY_PICK:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    mCurrentPhotoPath = String.valueOf(selectedImage);
                    Glide.with(this).load(selectedImage).into(cameraImageView);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (photoFile != null) {
                            Uri contentUri = FileProvider.getUriForFile(getActivity(),
                                    "com.bradleyperkins.mappingphotos.fileprovider",
                                    photoFile);

                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                }
                return true;

            case R.id.gallery:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , REQUEST_GALLERY_PICK);
                }else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);

                }
                return true;
            default:
                break;
        }
        return false;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        mCurrentPhotoPath = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(mCurrentPhotoPath);
    }

    //Create Image File
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onClick(View view) {
        if (!noteET.getText().toString().isEmpty() && mCurrentPhotoPath != null && !titleET.getText().toString().isEmpty()){
            note = noteET.getText().toString().trim();
            title = titleET.getText().toString().trim();
            mListener.addItem(note, mCurrentPhotoPath, title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapItemListener){
            mListener = (MapItemListener) context;
        }
    }

    public interface MapItemListener {
        void addItem(String note, String photoPath, String title);
    }

}
