package com.example.comparebeta.Utils;

import android.graphics.Color;

/**
 * This class includes the public constant values used in the code base.
 *
 * @author Nisal Hemadasa
 */
public class Constants {
    // Request Codes
    public static final int REQUEST_CODE_CAMERA = 111;
    public static final int REQUEST_CODE_SELECT_IMAGE = 112;

    // Permission Codes
    public static final int CAMERA_PERM_CODE = 101;
    public static final int STORAGE_PERM_CODE = 102;

    // Preference Manager related
    public static final String PREFERENCE_NAME = "PREFS_NAME";
    public static final String FIRST_RUN = "FIRST_RUN";

    // Toast messages
    // MainActivity
    public static final String TOAST_REQUEST_PERM_CAMERA = "Permission required to use camera.";
    public static final String TOAST_REQUEST_PERM_GALLERY = "Permission required to access Gallery.";
    public static final String TOAST_IMAGE_CANNOT_BE_OPENED = "Selected image could not be opened.";
    public static final String TOAST_PHOTO_CREATION_FAILED = "Photo file creation failed.";
    public static final String TOAST_PHOTO_FILE_NULL = "Photo file null reported.";
    // ImageDisplayActivity
    public static final String TOAST_PHOTO_SAVED_TO_INTERNAL_STORAGE = "Photo saved to internal storage";
    public static final String TOAST_PHOTO_URI_NOT_FOUND = "Photo uri not found for saving";
    // ImageLabelActivity
    public static final String TOAST_SAVED_AS_JSON = "Saved as JSON";
    public static final String TOAST_JSON_NOT_SAVED = "Blue bounding boxes are not labelled. Json file is not saved.";
    public static final String TOAST_ERROR_PICKING_IMAGE_FILE = "Error picking the image file.";

    // Date and time
    public static final String DATE_TIME = "yyyyMMdd_HHmmss";

    // File formats
    public static final String JPG = ".jpg";
    public static final String JPEG = "JPEG_";
    public static final String JSON = ".json";

    // Miscellaneous characters
    public static final String UNDERSCORE = "_";
    public static final String DOT = ".";
    public static final String SLASH = "/";

    // Intent related
    // keys
    public static final String CURRENT_PHOTO_PATH = "currentPhotoPath";
    public static final String CURRENT_PHOTO_NAME = "currentPhotoName";
    public static final String CURRENT_PHOTO_PARENT_DIR = "currentPhotoParentDir";
    // intent set types
    public static final String INTENT_SET_TYPE_ALL_FILES = "*/*";
    // intent set categories
    public static final String INTENT_SET_CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    // intent set data
    public static final String INTENT_SET_DATA_FILE_ACCESS_PERMISSION_INTENT = "package:%s";

    // directory folder names
    public static final String DIR_COMPAREBETA = "/compAReBeta/";
    public static final String DIR_IMAGES = "/Images/";
    public static final String DIR_ALL_IMAGES = "/All Images/";
    public static final String DIR_TAGGED_IMAGES = "/Tagged Images/";

    // Access providers
    public static final String PROVIDER = ".provider";

    // MainActivity: openImageDisplayActivity() request codes
    public static final int CAPTURED_IMAGE_URI = 91;
    public static final int SELECTED_IMAGE_URI = 92;

    // imageLabelerView: Bounding box parameters
    public static final String BOX_LEFT = "left";
    public static final String BOX_TOP = "top";
    public static final String BOX_RIGHT = "right";
    public static final String BOX_BOTTOM = "bottom";
    // imageLabelerView: pixel touch offset
    public static final int BOUNDING_BOX_TOUCH_OFFSET = 25;
    //imageLabelerView: boundingBox stroke width
    public static final int BOUNDING_BOX_STROKE_WIDTH = 20;
    // imageLabelerView: boundingBox initial-default side length
    public static final int BOUNDING_BOX_DEFAULT_SIDE_LENGTH = 400;
    // imageLabelerView: boundingBox vertex circle radius
    public static final float BOUNDING_BOX_VERTEX_RADIUS = 30f;
    // imageLabelerView: confirmed bounding box colors
    public static final int MAROON = Color.rgb(128, 0, 0);
    // imageLabelerView: bounding box delete button width and height
    public static final int DELETE_RED_ICON_SIZE = 120;
    // imageLabelerView: location offsets of delete button icon from the bounding box's vertex
    public static final int DELETE_RED_ICON_OFFSET_X = 20;
    public static final int DELETE_RED_ICON_OFFSET_Y = 20;
    // imageLabelerView: bounding box confirm button width and height
    public static final int CONFIRM_GREEN_ICON_SIZE = 120;
    // imageLabelerView: location offsets of confirm button icon from the bounding box's vertex
    public static final int CONFIRM_GREEN_ICON_OFFSET_X = 160;
    public static final int CONFIRM_GREEN_ICON_OFFSET_Y = 20;
    // imageLabelerView: label text
    public static final int LABEL_TEXT_SIZE = 100;
    public static final int LABEL_TEXT_OFFSET_X = 20;
    public static final int LABEL_TEXT_OFFSET_Y = 20;
    // Colors
    public static final int DARK_GREEN = Color.parseColor("#039129");

    // BoundingBoxLabelDialog: exception
    public static final String LABEL_DIALOG_LISTENER_NOT_IMPLEMENTED = " must implement BndBoxDialogListener";
    // BoundingBoxLabelDialog: Dialog text fields
    public static final String ENTER_OBJECT_CLASS_NAME = "Enter object class name";
    public static final String OK = "ok";
    public static final String CANCEL = "cancel";

    // ImageLabelActivity
    public static final String BOUNDING_BOX_DIALOG_TAG = "bounding box label dialog";

    // ImageLabelActivity: Exceptions
    public static final String NULL_INTENT_KEYS_IMAGE_LABEL_INTENT = "Null values in imageLabelIntent.puExtra() in imageLabelIntent";

    // BoundingBoxLabelDialog:     // Intent keys/arguments
    public static final String CURRENT_LABEL = "currentLabel";


}

//its prott
