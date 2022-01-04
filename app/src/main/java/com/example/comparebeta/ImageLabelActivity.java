package com.example.comparebeta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.comparebeta.Utils.Constants;
import com.example.comparebeta.Utils.FileManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class defines the activity functions in the labelling screen of the CompARe-beta app.
 *
 * @author Nisal Hemadasa
 */
public class ImageLabelActivity extends AppCompatActivity implements BoundingBoxLabelDialog
        .BoundingBoxLabelDialogListener, ImageLabelerView.BtnDeleteListener,
        ImageLabelerView.BtnConfirmListener {
    private ImageLabelerView imageLabelerView;

    private String currentPhotoNamePrefix;
    private String currentPhotoParentDir;

    private ImageButton btnAddBoundingBox;
    private ImageButton btnLabelImage;
    private ImageButton btnSelectImage;
    private ImageButton btnDone;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_label);
        btnAddBoundingBox = findViewById(R.id.activity_image_label_btn_add_bnd_box);
        btnLabelImage = findViewById(R.id.activity_image_label_btn_label_image);
        btnSelectImage = findViewById(R.id.activity_image_label_btn_select_image);
        btnDone = findViewById(R.id.activity_image_label_btn_done);
        btnDone.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_PATH)){
            imageLabelerView = new ImageLabelerView(this,
                    extras.getString(Constants.CURRENT_PHOTO_PATH));
            LinearLayout linearLayoutImageLabeler =
                    findViewById(R.id.activity_image_label_linear_layout);
            ConstraintLayout.LayoutParams layoutParams =
                    (ConstraintLayout.LayoutParams) linearLayoutImageLabeler.getLayoutParams();

            linearLayoutImageLabeler.setLayoutParams(setLayoutParameters(layoutParams));
            linearLayoutImageLabeler.addView(imageLabelerView);
        }

        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_NAME)){
            currentPhotoNamePrefix = FileManager.getNameWithoutExtension(
                    extras.getString(Constants.CURRENT_PHOTO_NAME));
        }

        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_PARENT_DIR)){
            currentPhotoParentDir = extras.getString(Constants.CURRENT_PHOTO_PARENT_DIR);
        }

        btnAddBoundingBox.setOnClickListener(v -> {
            setBtnVisibilityOnAddBoundingBox();
            imageLabelerView.spawnBoundingBox();
        });

        btnLabelImage.setOnClickListener(v -> {
            openBoundingBoxLabelDialog(imageLabelerView.getBoundingBoxCurrentLabel());
        });

        btnSelectImage.setOnClickListener(v -> {
            if(!checkReadPermission()){
                RequestPermission();
            }
            else{
                selectImageFromGallery();
            }
        });

        btnDone.setOnClickListener(v -> {
            if(saveJson()){
                openMainActivity();
            }
        });

        lockScreenRotation();
    }

    /**
     * Checks if the user has given permission to access external storage of the device.
     *
     * @return boolean to represent if the permission is already given or not.
     */
    boolean checkReadPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // For Android 11
            return Environment.isExternalStorageManager();
        } else {
            // For Android 10 and lower
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Requests the permission from the user to access the device's external storages.
     */
    void RequestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // For Android 11
            try{
                Intent fileAccessPermissionIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                fileAccessPermissionIntent.addCategory(Constants.INTENT_SET_CATEGORY_DEFAULT);
                fileAccessPermissionIntent.setData(Uri.parse(String.format(
                        Constants.INTENT_SET_DATA_FILE_ACCESS_PERMISSION_INTENT,
                        new Object[]{getApplicationContext().getPackageName()})));
                startActivityForResult(fileAccessPermissionIntent, Constants.REQUEST_CODE_SELECT_IMAGE);
            } catch (Exception e){
                Intent fileAccessPermissionIntent = new Intent();
                fileAccessPermissionIntent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(fileAccessPermissionIntent, Constants.REQUEST_CODE_SELECT_IMAGE);
            }
        } else {
            // For Android 10 or lower
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.STORAGE_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if (requestCode == Constants.STORAGE_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(this, Constants.TOAST_REQUEST_PERM_GALLERY,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Applies the label name (text) to the bounding box.
     *
     * @param boundingBoxLabel label name as a string.
     */
    @Override
    public void applyTexts(String boundingBoxLabel) {
        imageLabelerView.saveBoundingBox(boundingBoxLabel, btnAddBoundingBox.getTop());
        //imageLabelerView.setBtnDeleteColorChange();
        //imageLabelerView.setBoundingBoxNull();
        setBtnVisibilityOnLabelImage();
        setBtnVisibilityOnConfirm();
    }

    /**
     * Methods calls when the bounding box delete button is touched.
     */
    @Override
    public void onBtnDeleteTouch() {
        setBtnVisibilityOnDelete();
    }

    /**
     * Modifies the layout parameters of the app to fit the devices' full screen.
     *
     * @param layoutParams default layout parameters of the device's screen.
     * @return Modified layout parameters of the device screen.
     */
    private ConstraintLayout.LayoutParams setLayoutParameters(ConstraintLayout.LayoutParams layoutParams) {
        // get device screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // hides the action bar and make the title bar and navigation bar invisible
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);  //make title bar and navigation bar invisible
        getSupportActionBar().hide();   // action bar

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutParams.width = width;
            layoutParams.height = (int)(width/imageLabelerView.getCurrentPhotoAspectRatio());
            layoutParams.bottomMargin = btnAddBoundingBox.getTop();
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.height = height;
            layoutParams.width = (int)(height*imageLabelerView.getCurrentPhotoAspectRatio());
            layoutParams.rightMargin = btnAddBoundingBox.getLeft();
        }
        return layoutParams;
    }

    /**
     * Maintains the screen orientation unchanged during transition from image capturing screen
     * (takePictureIntent intent in MainActivity.java) to image Labelling screen (imageLabelIntent
     * in ImageLabelActivity.java).
     */
    private void lockScreenRotation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    /**
     * Pick an image from the shared storage (eg: Gallery) to label.
     */
    private void selectImageFromGallery() {
        File externalStorageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri photoURI = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + Constants.PROVIDER, externalStorageDir);
        Intent imageFromGallery = new Intent(Intent.ACTION_PICK, photoURI);
        imageFromGallery.setType(Constants.INTENT_SET_TYPE_ALL_FILES);
        if(getIntent().resolveActivity(getPackageManager()) != null){
            startActivityForResult(imageFromGallery, Constants.REQUEST_CODE_SELECT_IMAGE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String path = FileManager.getPathFromURI(selectedImageUri, getContentResolver());
                boolean isNewImageSet = true;
                if(path != null){
                    isNewImageSet = imageLabelerView.setNewImage(path);
                }
                if(!isNewImageSet){
                    Toast.makeText(getApplicationContext(), Constants.TOAST_ERROR_PICKING_IMAGE_FILE,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Opens the dialog to read the label of the bounding box as an input.
     *
     * @param currentLabel label of the bounding box.
     */
    private void openBoundingBoxLabelDialog(String currentLabel) {
        BoundingBoxLabelDialog boundingBoxLabelDialog = new BoundingBoxLabelDialog();
        if (currentLabel != null){
            Bundle args = new Bundle();
            args.putString(Constants.CURRENT_LABEL, currentLabel);
            boundingBoxLabelDialog.setArguments(args);
        }
        boundingBoxLabelDialog.show(getSupportFragmentManager(), Constants.BOUNDING_BOX_DIALOG_TAG);
    }

    /**
     * Save bounding box and their properties in JSON format in the app specific folder in the
     * storage.
     *
     * @return boolean to indicate whether the JSON file is saved successfully or not.
     */
    private boolean saveJson(){
        List <BoundingBox> boundingBoxList = imageLabelerView.getBoundingBoxesList();
        if(boundingBoxList != null && boundingBoxList.size() > 0){
            Gson gson = new Gson();
            File file = new File(currentPhotoParentDir + Constants.SLASH +
                    currentPhotoNamePrefix + Constants.JSON);
            try {
                FileWriter writer = new FileWriter(file);
                gson.toJson(boundingBoxList, writer);
                writer.flush();
                writer.close();
                Toast.makeText(getApplicationContext(), Constants.TOAST_SAVED_AS_JSON,
                        Toast.LENGTH_LONG).show();
                return true;
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getBaseContext(), Constants.TOAST_JSON_NOT_SAVED,
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Dispatches the mainActivityIntent.
     */
    private void openMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    /**
     * Method calls when the 'Confirm' button on the bounding box is touched.
     */
    public void onBtnConfirmTouch(){
        openBoundingBoxLabelDialog(imageLabelerView.getBoundingBoxCurrentLabel());
    }

    /**
     * Defines the changes of the UI once all the bounding boxes are deleted.
     */
    private void setBtnVisibilityOnDelete(){
        if(imageLabelerView.getBoundingBoxesList().size() == 0){
            btnAddBoundingBox.setVisibility(View.VISIBLE);
            btnLabelImage.setVisibility(View.VISIBLE);
            btnSelectImage.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.GONE);
        } else {
            setBtnVisibilityOnConfirm();
        }
    }

    /**
     * Defines the changes of the UI when 'Bounding Box Confirm' button is touched.
     */
    private void setBtnVisibilityOnConfirm(){
        btnAddBoundingBox.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);
    }

    /**
     * Defines the changes of the UI when 'Add Bounding Box' button is touched.
     */
    private void setBtnVisibilityOnAddBoundingBox(){
        btnAddBoundingBox.setVisibility(View.GONE);
        btnLabelImage.setVisibility(View.GONE);
        btnSelectImage.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
    }

    /**
     * Defines the changes of the UI when 'Image Label' button is touched.
     */
    private void setBtnVisibilityOnLabelImage(){
        btnLabelImage.setVisibility(View.GONE);
        btnSelectImage.setVisibility(View.GONE);
    }
}
