package com.example.comparebeta.Utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * This class defines the operations and supporting functionalities related to files and directories
 * of the app.
 *
 * @author Nisal Hemadasa
 */
public class FileManager {

    /**
     * Create app specific folders.
     *
     * @param context the context of the application.
     */
    public void createAppSpecificFolders(Context context){
        // get external directory
        File externalStorageDir = context.getExternalFilesDir(null);
        File compareBetaBaseDir = new File(externalStorageDir.getAbsolutePath() +
                Constants.DIR_COMPAREBETA);
        if(!compareBetaBaseDir.isDirectory()){
            compareBetaBaseDir.mkdir();
        }

        File imageDir = new File(compareBetaBaseDir + Constants.DIR_IMAGES);
        if(!imageDir.isDirectory()){
            imageDir.mkdir();
        }

        File rawImageDir = new File(imageDir + Constants.DIR_ALL_IMAGES);

        if(!rawImageDir.isDirectory()){
            rawImageDir.mkdir();
        }

        File annotatedImageDir = new File(imageDir + Constants.DIR_TAGGED_IMAGES);
        if(!annotatedImageDir.isDirectory()){
            annotatedImageDir.mkdir();
        }
    }

    /**
     * Extract the part of the string before the dot. Used on extracting the name from a specific
     * file with an extension.
     *
     * @param fileName string with the dot in it.
     * @return extracted string without the dot.
     */
    public static String getNameWithoutExtension(String fileName) {
        if(fileName != null){
            int dotIndex = fileName.lastIndexOf(Constants.DOT);
            return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        }
        return null;
    }

    /**
     * Returns the path of a URI as a string.
     *
     * @param contentUri input URI which needs to be converted to a path as a string.
     * @param contentResolver content resolver.
     * @return URI path as a string.
     */
    public static String getPathFromURI(Uri contentUri, ContentResolver contentResolver) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
