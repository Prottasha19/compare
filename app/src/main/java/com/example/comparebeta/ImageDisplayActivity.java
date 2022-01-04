package com.example.comparebeta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.comparebeta.Utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class defines the activity to display an image picked from the device.
 *
 * @author Nisal Hemadasa
 */
public class ImageDisplayActivity extends AppCompatActivity {
    private ImageView imageViewDisplay;
    private Button btnLabelImage;
    private Button btnSaveImage;
    private Bitmap bitmap;
    private Uri currentPhotoUri;
    private String currentPhotoName;
    private String currentPhotoParentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        imageViewDisplay = findViewById(R.id.activity_image_display_img_view);
        btnLabelImage = findViewById(R.id.activity_image_display_btn_label);
        btnSaveImage = findViewById(R.id.activity_image_display_btn_save);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_PATH)){
            currentPhotoUri = Uri.parse(extras.getString(Constants.CURRENT_PHOTO_PATH));
            imageViewDisplay.setImageURI(currentPhotoUri);
        }

        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_NAME)){
            currentPhotoName = extras.getString(Constants.CURRENT_PHOTO_NAME);
        }

        if(extras != null && extras.containsKey(Constants.CURRENT_PHOTO_PARENT_DIR)){
            currentPhotoParentDir = extras.getString(Constants.CURRENT_PHOTO_PARENT_DIR);
        }

        btnLabelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageLabelActivity();
            }
        });

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
    }

    /**
     * Opens the image label activity.
     */
    private void openImageLabelActivity(){
        Intent imageLabelIntent = new Intent(this, ImageLabelActivity.class);
        if(currentPhotoUri != null && currentPhotoName != null){
            imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_PATH, currentPhotoUri.toString());
            imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_NAME, currentPhotoName);
            imageLabelIntent.putExtra(Constants.CURRENT_PHOTO_PARENT_DIR, currentPhotoParentDir);
        } else {
            Toast.makeText(this, Constants.NULL_INTENT_KEYS_IMAGE_LABEL_INTENT,
                    Toast.LENGTH_SHORT).show();
        }

        startActivity(imageLabelIntent);
    }

    /**
     * Saves the image to app specific folder in JPEG format.
     */
    private void saveImage(){
        if(currentPhotoUri != null) {
            File photoFile = new File(String.valueOf(currentPhotoUri));
            OutputStream out;
            bitmap = ((BitmapDrawable)imageViewDisplay.getDrawable()).getBitmap();

            try {
                out = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(this, Constants.TOAST_PHOTO_SAVED_TO_INTERNAL_STORAGE,
                        Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                Toast.makeText(this, Constants.TOAST_PHOTO_URI_NOT_FOUND,
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
