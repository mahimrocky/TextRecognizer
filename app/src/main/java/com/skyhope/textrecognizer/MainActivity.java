package com.skyhope.textrecognizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skyhope.textrecognizerlibrary.TextScanner;
import com.skyhope.textrecognizerlibrary.callback.TextExtractCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonGallery, buttonCamera;
    TextView recognizeText;
    ImageView captureImage;

    public static final int REQUEST_FOR_IMAGE_FROM_GALLERY = 101;
    public static final int REQUEST_FOR_IMAGE_FROM_CAMERA = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonGallery = findViewById(R.id.button_gallery);
        buttonCamera = findViewById(R.id.button_camera);
        recognizeText = findViewById(R.id.text);
        captureImage = findViewById(R.id.imageView);


        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MainActivity.this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

            }
        });

    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final Uri uri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            captureImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextScanner.getInstance(this)
                .init()
                .load(uri)
                .getCallback(new TextExtractCallback() {
                    @Override
                    public void onGetExtractText(List<String> textList) {
                        // Here ypu will get list of text

                        final StringBuilder text = new StringBuilder();
                        for (String s : textList) {
                            text.append(s).append("\n");
                        }
                        recognizeText.post(new Runnable() {
                            @Override
                            public void run() {
                                recognizeText.setText(text.toString());
                            }
                        });

                    }
                });
    }

    /**
     * Method for Open device default Camera and take snap
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri mImageFileUri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    mImageFileUri = FileProvider.getUriForFile(this,
                            getResources().getString(R.string.file_provider_authority),
                            imageFile);
                } else {
                    mImageFileUri = Uri.parse("file:" + imageFile.getAbsolutePath());
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
                cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


                startActivityForResult(cameraIntent, REQUEST_FOR_IMAGE_FROM_CAMERA);
            }
        }
    }

    /**
     * Method for Open default device gallery
     */
    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_FOR_IMAGE_FROM_GALLERY);
    }

    /**
     * Create a file for save photo from camera
     *
     * @return File
     * @throws IOException Input output error
     */
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            storageDirectory.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDirectory);
    }
}
