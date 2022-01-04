package com.example.comparebeta;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.comparebeta.Utils.Constants;
import com.example.comparebeta.Utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines canvas/graphical operations and functionality on available in labelling screen.
 *
 * @author Nisal Hemadasa
 */
public class ImageLabelerView extends View {
    private static final Paint boundingBoxPaint;
    private static final Paint boundingBoxListPaint;
    private static final Paint boundingBoxUnlabelledPaint;
    private static final Paint vertexPaint;
    private static final Paint labelTextPaint;
    private static final Paint labelTextContainerPaint;

    private Coordinates firstTouch;
    private Coordinates lastTouch;

    private Drawable currentPhoto;
    private float currentPhotoAspectRatio;

    BoundingBox boundingBox;
    private List<BoundingBox> boundingBoxesList;

    private boolean isBoundingBoxOnMove;
    private boolean isBoundingBoxOnReshape;
    private boolean isActionMove;

    private boolean unlabelledBoxesExist;

    private float btnAddBoundingBoxTop;

    private Drawable btnConfirm;

    final float boundingBoxMinSize;

    static {
        // Paint properties of the currently selected bounding box
        boundingBoxPaint = new Paint();
        boundingBoxPaint.setStyle(Paint.Style.STROKE);
        boundingBoxPaint.setColor(Color.GREEN);
        boundingBoxPaint.setStrokeWidth(Constants.BOUNDING_BOX_STROKE_WIDTH);
        // Paint properties of the already drawn bounding box
        boundingBoxListPaint = new Paint();
        boundingBoxListPaint.setStyle(Paint.Style.STROKE);
        boundingBoxListPaint.setColor(Constants.DARK_GREEN);
        boundingBoxListPaint.setStrokeWidth(Constants.BOUNDING_BOX_STROKE_WIDTH);
        // Paint properties of unlabelled bounding boxes
        boundingBoxUnlabelledPaint = new Paint();
        boundingBoxUnlabelledPaint.setStyle(Paint.Style.STROKE);
        boundingBoxUnlabelledPaint.setColor(Color.BLUE);
        boundingBoxUnlabelledPaint.setStrokeWidth(Constants.BOUNDING_BOX_STROKE_WIDTH);
        // Paint properties of label text
        labelTextPaint = new Paint();
        labelTextPaint.setColor(Color.WHITE);
        labelTextPaint.setStyle(Paint.Style.FILL);
        labelTextPaint.setAntiAlias(true);
        labelTextPaint.setTextSize(Constants.LABEL_TEXT_SIZE);
        // Paint properties of the rectangular container of label text
        labelTextContainerPaint = new Paint();
        labelTextContainerPaint.setColor(Constants.DARK_GREEN);
        labelTextPaint.setStyle(Paint.Style.FILL);
        // Paint properties of the circles at the vertices of the annotating rectangle
        vertexPaint = new Paint();
        vertexPaint.setColor(Color.GREEN);
    }

    enum boundingBoxConfirmOrDeleteBtn{
        DELETE,
        CONFIRM
    }

    public ImageLabelerView(Context context, String imageUri){
        super(context);
        Bitmap bitmap = getRectifiedImageOrientation(imageUri);
        currentPhotoAspectRatio = (float)bitmap.getWidth()/(float)bitmap.getHeight();
        currentPhoto = new BitmapDrawable(bitmap);
        boundingBoxesList = new ArrayList<>();
        isBoundingBoxOnMove = false;
        isBoundingBoxOnReshape = false;
        btnConfirm = getResources().getDrawable(R.drawable.button_confirm_green);
        boundingBoxMinSize = Constants.CONFIRM_GREEN_ICON_SIZE + Constants.DELETE_RED_ICON_SIZE
                + 3 * Constants.DELETE_RED_ICON_OFFSET_X;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            currentPhoto.setBounds(0, 0, getWidth(), (int)(getWidth()/currentPhotoAspectRatio));
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            currentPhoto.setBounds(0, 0, (int)(getHeight()*currentPhotoAspectRatio), getHeight());
        }
        currentPhoto.draw(canvas);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (boundingBoxesList != null) {
                if (!unlabelledBoxesExist) {
                    for (BoundingBox bndBox : boundingBoxesList) {
                        float labelTextContainerBottom;

                        if(bndBox.isCompleteImage()){
                            labelTextContainerBottom = this.btnAddBoundingBoxTop
                                    - Constants.LABEL_TEXT_OFFSET_Y;
                        } else {
                            canvas.drawRect(bndBox.getLeft(), bndBox.getTop(), bndBox.getRight(),
                                    bndBox.getBottom(), boundingBoxListPaint);
                            labelTextContainerBottom = bndBox.getBottom();
                        }

                        float labelTextContainerleft = bndBox.getRight()
                                - labelTextPaint.measureText(bndBox.getLabel())
                                - 2 * Constants.LABEL_TEXT_OFFSET_X;
                        float labelTextConteinerTop = labelTextContainerBottom
                                - Constants.LABEL_TEXT_OFFSET_Y
                                - labelTextPaint.getTextSize();
                        float labelTextContainerRight = bndBox.getRight();

                        canvas.drawRect(labelTextContainerleft, labelTextConteinerTop,
                                labelTextContainerRight, labelTextContainerBottom,
                                labelTextContainerPaint);

                        float labelTextX = bndBox.getRight()
                                - labelTextPaint.measureText(bndBox.getLabel())
                                - Constants.LABEL_TEXT_OFFSET_X;
                        float labelTextY = labelTextContainerBottom - Constants.LABEL_TEXT_OFFSET_Y;
                        canvas.drawText(bndBox.getLabel(), labelTextX, labelTextY, labelTextPaint);

                        if(bndBox.getBtnDelete() != null){
                            bndBox.getBtnDelete().setBounds((int) bndBox.getRight() - (Constants.DELETE_RED_ICON_SIZE
                                            + Constants.DELETE_RED_ICON_OFFSET_X),
                                    (int) bndBox.getTop() + Constants.DELETE_RED_ICON_OFFSET_Y,
                                    (int) bndBox.getRight() - Constants.DELETE_RED_ICON_OFFSET_X,
                                    (int) bndBox.getTop() + (Constants.DELETE_RED_ICON_SIZE
                                            + Constants.DELETE_RED_ICON_OFFSET_Y));
                            bndBox.getBtnDelete().draw(canvas);
                        }
                    }
                } else {
                    for (BoundingBox bndBox : boundingBoxesList) {
                        if (bndBox.getLabel() == null) {
                            canvas.drawRect(bndBox.getLeft(), bndBox.getTop(), bndBox.getRight(),
                                    bndBox.getBottom(), boundingBoxUnlabelledPaint);
                        } else {
                            canvas.drawRect(bndBox.getLeft(), bndBox.getTop(), bndBox.getRight(),
                                    bndBox.getBottom(), boundingBoxListPaint);
                        }
                    }
                }
            }

            if(boundingBox != null){
                canvas.drawRect(firstTouch.getX(), firstTouch.getY(), lastTouch.getX(),
                        lastTouch.getY(), boundingBoxPaint);
                canvas.drawCircle(firstTouch.getX(), firstTouch.getY(),
                        Constants.BOUNDING_BOX_VERTEX_RADIUS,
                        vertexPaint);
                canvas.drawCircle(lastTouch.getX(), lastTouch.getY(),
                        Constants.BOUNDING_BOX_VERTEX_RADIUS,
                        vertexPaint);
                btnConfirm.setBounds((int) lastTouch.getX()
                                - (Constants.CONFIRM_GREEN_ICON_SIZE
                                + Constants.CONFIRM_GREEN_ICON_OFFSET_X),
                        (int) firstTouch.getY() + Constants.CONFIRM_GREEN_ICON_OFFSET_Y,
                        (int) lastTouch.getX() - Constants.CONFIRM_GREEN_ICON_OFFSET_X,
                        (int) firstTouch.getY() + (Constants.CONFIRM_GREEN_ICON_SIZE
                                + Constants.CONFIRM_GREEN_ICON_OFFSET_Y));
                btnConfirm.draw(canvas);
                boundingBox.getBtnDelete().setBounds((int) lastTouch.getX() - (Constants.DELETE_RED_ICON_SIZE
                                + Constants.DELETE_RED_ICON_OFFSET_X),
                        (int) firstTouch.getY() + Constants.DELETE_RED_ICON_OFFSET_Y,
                        (int) lastTouch.getX() - Constants.DELETE_RED_ICON_OFFSET_X,
                        (int) firstTouch.getY() + (Constants.DELETE_RED_ICON_SIZE
                                + Constants.DELETE_RED_ICON_OFFSET_Y));
                boundingBox.getBtnDelete().draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean isCurrentBoundingBoxTouched = false;
                if (boundingBox != null){
                    if (boundingBox.getBtnDelete().getBounds().contains((int) event.getX(), (int) event.getY())){
                        boundingBox = null;
                        isCurrentBoundingBoxTouched = true;
                        attachToImageLabelActivity(boundingBoxConfirmOrDeleteBtn.DELETE);
                    } else if (btnConfirm.getBounds().contains((int) event.getX(), (int) event.getY())){
                        isCurrentBoundingBoxTouched = true;
                        attachToImageLabelActivity(boundingBoxConfirmOrDeleteBtn.CONFIRM);
                    } else if (boundingBox.bottomRightVertexContains(event.getX(), event.getY())) {
                        isCurrentBoundingBoxTouched = true;
                        isBoundingBoxOnReshape = true;
                        firstTouch = new Coordinates(boundingBox.getLeft(), boundingBox.getTop());
                    } else if (boundingBox.topLeftVertexContains(event.getX(), event.getY())) {
                        isCurrentBoundingBoxTouched = true;
                        isBoundingBoxOnMove = true;
                        firstTouch = new Coordinates(boundingBox.getLeft(), boundingBox.getTop());
                    }
                }
                if(!isCurrentBoundingBoxTouched){
                    // Delete the confirmed bounding boxes on corresponding btnDelete touch
                    onConfirmedBoxBtnDeleteTouch((int) event.getX(), (int) event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isBoundingBoxOnMove) {
                    if(event.getX() < 0){
                        firstTouch.setX(0);
                    } else if(event.getX() + boundingBox.getWidth() > getWidth()){
                        firstTouch.setX(getWidth() - boundingBox.getWidth());
                    } else {
                        firstTouch.setX(event.getX());
                    }

                    if(event.getY() < 0){
                        firstTouch.setY(0);
                    }  else if(event.getY() + boundingBox.getHeight() > getHeight()){
                        firstTouch.setY(getHeight() - boundingBox.getHeight());
                    } else {
                        firstTouch.setY(event.getY());
                    }

                    lastTouch = new Coordinates(firstTouch.getX() + boundingBox.getWidth(),
                            firstTouch.getY() + boundingBox.getHeight());
                } else if(isBoundingBoxOnReshape) {
                    if(event.getX() > getWidth()){
                        lastTouch.setX(getWidth());
                    } else if(!isLessThanBoundingBoxMinSize(event.getX(), firstTouch.getX())){
                        lastTouch.setX(event.getX());
                    } else {
                        lastTouch.setX(boundingBox.getLeft() + boundingBoxMinSize);
                    }

                    if(event.getY() > getHeight()){
                        lastTouch.setY(getHeight());
                    } else if(!isLessThanBoundingBoxMinSize(event.getY(), firstTouch.getY())){
                        lastTouch.setY(event.getY());
                    } else {
                        lastTouch.setY(boundingBox.getTop() + boundingBoxMinSize);
                    }
                }
                isActionMove = true;
                break;
            case MotionEvent.ACTION_UP:
                if(boundingBox != null){
                    if (isActionMove) {
                        boundingBox.setLeft(firstTouch.getX());
                        boundingBox.setTop(firstTouch.getY());
                        boundingBox.setRight(lastTouch.getX());
                        boundingBox.setBottom(lastTouch.getY());
                        boundingBox.setHeight(lastTouch.getY() - firstTouch.getY());
                        boundingBox.setWidth(lastTouch.getX() - firstTouch.getX());
                    }
                }
                isBoundingBoxOnMove = false;
                isBoundingBoxOnReshape = false;
                isActionMove = false;
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    /**
     * Inflates the image given by the URI as an input parameter to the device's screen.
     *
     * @param imageUri URI of the image.
     * @return boolean to indicate whether the URI is successfully inflated or not.
     */
    public boolean setNewImage(String imageUri){
        Bitmap bitmap = getRectifiedImageOrientation(imageUri);
        if(bitmap != null){
            currentPhotoAspectRatio = (float)bitmap.getWidth()/(float)bitmap.getHeight();
            currentPhoto = new BitmapDrawable(bitmap);
            String jsonFilePath = FileManager.getNameWithoutExtension(imageUri);
            File jsonFile = new File(jsonFilePath + Constants.JSON);
            if(jsonFile.exists()){
                // Implement the operation of recreating the labelled image, in case JSON file of
                // the given name exists.
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Correcting the orientation mismatch occurred in the captured image, when inflating to the
     * imageLabelIntent.
     *
     * @param imageUri URI of the captured image.
     * @return Bitmap of the orientation-corrected image.
     */
    private Bitmap getRectifiedImageOrientation(String imageUri) {
        Bitmap rawBitmap = BitmapFactory.decodeFile(imageUri);
        Bitmap rotatedBitmap;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(exifInterface != null){
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(rawBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(rawBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(rawBitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = rawBitmap;
            }
        } else {
            rotatedBitmap = rawBitmap;
        }
        return rotatedBitmap;
    }

    /**
     * Rotates (Changes the orientation) of a given bitmap by a given amount of degrees.
     *
     * @param source input Bitmap.
     * @param angle angle in degrees by which the orientation needs to be changed.
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * Method calls when the 'Delete' button of an already confirmed box is touched. (Removes the
     * targeted bounding box from the boundingBoxesList).
     *
     * @param x x coordinate of the touched pixel.
     * @param y y coordinate of the touched pixel.
     */
    private void onConfirmedBoxBtnDeleteTouch(int x, int y) {
        for (int i = boundingBoxesList.size(); i-- > 0; ) {
            if(boundingBoxesList.get(i).btnDeleteContains(x, y)){
                boundingBoxesList.remove(i);
                break;
            }
        }
        if(boundingBox == null && boundingBoxesList.size() == 0){
            attachToImageLabelActivity(boundingBoxConfirmOrDeleteBtn.DELETE);
        }
    }

    /**
     * Checks the condition whether the finger (touch) is dragged more/less than a given constant
     * threshold 'boundingBoxMinSize'.
     *
     * @param lastTouchCoordinate currently touched pixel coordinate (either x or y)
     * @param firstTouchCoordinate previously touched pixel coordinate (either x or y)
     * @return boolean whether the finger drag distance is less thant the threshold (true) or not.
     */
    private boolean isLessThanBoundingBoxMinSize(float lastTouchCoordinate,
                                                 float firstTouchCoordinate) {
        return lastTouchCoordinate - firstTouchCoordinate <= boundingBoxMinSize;
    }

    /**
     * Returns the aspect ratio of the bitmap of the image(which is currently inflated on the screen).
     *
     * @return aspect ratio of the bitmap of the current image.
     */
    public float getCurrentPhotoAspectRatio(){return currentPhotoAspectRatio;}

    /**
     * Stores the current boundingBox instance in the boundingBoxList.
     *
     * @param boundingBoxLabel label of the bounding box.
     * @param btnAddBoundingBoxTop top pixel of the button view 'addBoundingBox'.
     */
    public void saveBoundingBox(String boundingBoxLabel, float btnAddBoundingBoxTop) {
        if(boundingBox != null){
            boundingBox.setLabel(boundingBoxLabel);
        } else {
            // Assign the complete image as the bounding box boundaries
            boundingBox = new BoundingBox();
            boundingBox.setLeft(currentPhoto.getBounds().left);
            boundingBox.setTop(currentPhoto.getBounds().top);
            boundingBox.setRight(currentPhoto.getBounds().right);
            boundingBox.setBottom(currentPhoto.getBounds().bottom);
            boundingBox.setWidth(currentPhoto.getBounds().width());
            boundingBox.setHeight(currentPhoto.getBounds().height());
            boundingBox.setLabel(boundingBoxLabel);
            boundingBox.setCompleteImage(true);
            this.btnAddBoundingBoxTop = btnAddBoundingBoxTop;
        }
        boundingBoxesList.add(boundingBox);
        boundingBox = null;
        postInvalidate();
    }

    public List<BoundingBox> getBoundingBoxesList() {
        if (getUnlabelledBoundingBoxes().size() > 0) {
            unlabelledBoxesExist = true;
            postInvalidate();
            return null;
        } else {
            return boundingBoxesList;
        }
    }

    /**
     * Returns a list of BoundingBox objects whose labels are not assigned.
     *
     * @return list of BoundingBox objects whose labels are not assigned.
     */
    private List<BoundingBox> getUnlabelledBoundingBoxes() {
        List<BoundingBox> unlabelledBoundingBoxes = new ArrayList<>();
        for (BoundingBox bndBox : this.boundingBoxesList) {
            if (bndBox.getLabel() == null) {
                unlabelledBoundingBoxes.add(bndBox);
            }
        }
        return unlabelledBoundingBoxes;
    }

    /**
     * Returns the label of the current instance of the bounding box.
     *
     * @return label of the current instance of the bounding box.
     */
    public String getBoundingBoxCurrentLabel() {
        return (boundingBox != null) ? boundingBox.getLabel() : null;
    }

    /**
     * Generates a bounding box in the center of the screen, in imageLabelIntent/
     * image labelling screen.
     */
    public void spawnBoundingBox(){
        int displayWidth= this.getResources().getDisplayMetrics().widthPixels;
        int displayHeight= this.getResources().getDisplayMetrics().heightPixels;
        if (boundingBox == null) {
            boundingBox = new BoundingBox();
            boundingBox.setLeft((displayWidth - Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH)/2);
            boundingBox.setTop((displayHeight - Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH)/2);
            boundingBox.setRight((displayWidth + Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH)/2);
            boundingBox.setBottom((displayHeight + Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH)/2);
            boundingBox.setWidth(Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH);
            boundingBox.setHeight(Constants.BOUNDING_BOX_DEFAULT_SIDE_LENGTH);
            boundingBox.setCompleteImage(false);
            boundingBox.setBtnDelete(getResources().getDrawable(R.drawable.button_delete_green));
            firstTouch = new Coordinates(boundingBox.getLeft(), boundingBox.getTop());
            lastTouch = new Coordinates(boundingBox.getRight(), boundingBox.getBottom());
            invalidate();
        }
    }

    /**
     * Method calls when 'Confirm' and 'Delete' buttons are touched, via the interfaces
     * BtnDeleteListener and BtnConfirmListener, which are implemented in ImageLabelActivity.java.
     *
     * @param btn DELETE or CONFIRM of type enumerator.
     */
    private void attachToImageLabelActivity(boundingBoxConfirmOrDeleteBtn btn) {
        switch (btn){
            case DELETE:
                BtnDeleteListener btnDeleteListener = (BtnDeleteListener)getContext();
                btnDeleteListener.onBtnDeleteTouch();
                break;
            case CONFIRM:
                BtnConfirmListener btnConfirmListener = (BtnConfirmListener) getContext();
                btnConfirmListener.onBtnConfirmTouch();
                break;
            default:
                break;
        }
    }

    public interface BtnDeleteListener {
        void onBtnDeleteTouch();
    }

    public interface BtnConfirmListener {
        void onBtnConfirmTouch();
    }
}
