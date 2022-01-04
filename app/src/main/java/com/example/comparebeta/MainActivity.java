package com.example.comparebeta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.comparebeta.Utils.Constants;
import com.example.comparebeta.Utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines the initial activity with which activity the application initiates (Either by
 * opening the camera, or selecting an image from the gallery).
 *
 * @author Nisal Hemadasa
 */
public class MainActivity extends Activity {
    private Button btnCaptureImage;
    private Button btnSelectImage;
    private String currentPhotoPath;
    private String currentPhotoName;
    private String currentPhotoParentDir;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCaptureImage = findViewById(R.id.activity_main_btn_capture);
        btnSelectImage = findViewById(R.id.activity_main_btn_select);
        simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME);

        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_NAME, 0);
        boolean isNotFirstRun = settings.getBoolean(Constants.FIRST_RUN, false);
        if (!isNotFirstRun) {
            // create the folder structure for the first run after the app is installed
            FileManager fileManager = new FileManager();
            fileManager.createAppSpecificFolders(this);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.FIRST_RUN, true);
            editor.commit();
        }else{
            captureImage();
        }

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAccessPermission(v);
            }
        });
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAccessPermission(v);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults){
        switch (requestCode){
            case Constants.CAMERA_PERM_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dispatchTakePictureIntent();
                }else{
                    Toast.makeText(this, Constants.TOAST_REQUEST_PERM_CAMERA,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.STORAGE_PERM_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    selectImageFromGallery();
                }else{
                    Toast.makeText(this, Constants.TOAST_REQUEST_PERM_GALLERY,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == RESULT_OK){
            capturedImageDisplayActivity();
        }

        if(requestCode == Constants.REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                selectedImageDisplayActivity(selectedImageUri);
            }
        }
    }

    /***
     * Requests user permission to access the camera and external storage.
     * @param v view
     */
    private void requestAccessPermission(View v){
        switch (v.getId()){
            case R.id.activity_main_btn_capture:
                captureImage();
                break;
            case R.id.activity_main_btn_select:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERM_CODE);
                }else{
                    selectImageFromGallery();
                }
                break;
            default:
                break;
        }
    }

    /***
     * Request user permission and creates an intent to capture image from the camera.
     */
    private void captureImage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}
                    , Constants.CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }
    }

    /***
     * Create and dispatches ImageLabelActivity intent and passes the captured image and the extended
     * data.
     */
    private void capturedImageDisplayActivity(){
        Intent imageLabelIntent = new Intent(this, ImageLabelActivity.class);
        imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_PATH, currentPhotoPath);
        imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_NAME, currentPhotoName);
        imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_PARENT_DIR, currentPhotoParentDir);
        startActivity(imageLabelIntent);
    }

    /***
     * Picks an image from the shared storages of the device and passes to imageDisplayIntent.
     * @param selectedImageUri URI to the targeted image to be picked.
     */
    private void selectedImageDisplayActivity(Uri selectedImageUri){
        Intent imageDisplayIntent = new Intent(this, ImageDisplayActivity.class);
        imageDisplayIntent.putExtra(Constants.CURRENT_PHOTO_PATH, selectedImageUri.toString());
        startActivity(imageDisplayIntent);
    }


    /***
     * Picks an image from gallery.
     */
    private void selectImageFromGallery() {
        Intent imageFromGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(getIntent().resolveActivity(getPackageManager()) != null){
            startActivityForResult(imageFromGallery, Constants.REQUEST_CODE_SELECT_IMAGE);
        }
    }

    /***
     * Creates and image file.
     * @return the image file.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = simpleDateFormat.format(new Date());
        String imageFileName = Constants.JPEG + timeStamp + Constants.UNDERSCORE;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, Constants.JPG, storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        currentPhotoName = image.getName();
        currentPhotoParentDir = image.getParent();
        return image;
    }


    /***
     * Dispatches an intent to capture image from the camera.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            String photoPath = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Toast.makeText(this, Constants.TOAST_PHOTO_CREATION_FAILED,
                        Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + Constants.PROVIDER,
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA);
            } else{
                Toast.makeText(this, Constants.TOAST_PHOTO_FILE_NULL,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
