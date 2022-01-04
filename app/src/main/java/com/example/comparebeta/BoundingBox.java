package com.example.comparebeta;

import android.graphics.drawable.Drawable;

import com.example.comparebeta.Utils.Constants;

import java.util.Hashtable;

/**
 * This class defined the attributes and methods inherent to the BoundingBox.
 *
 * @author Nisal Hemadasa
 */
public class BoundingBox {
    private Hashtable<String, Float> vertices;
    private float width;
    private float height;
    private String label;
    private boolean isCompleteImage;
    private Drawable btnDelete;

    public BoundingBox(){
        vertices = new Hashtable<>();
    }

    public void setLeft(float left){
        vertices.put("left", left);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setTop(float top){
        vertices.put("top", top);
    }

    public void setRight(float right){
        vertices.put("right", right);
    }

    public void setBottom(float bottom){
        vertices.put("bottom", bottom);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCompleteImage(boolean completeImage) {
        isCompleteImage = completeImage;
    }

    public void setBtnDelete(Drawable btnDelete) {
        this.btnDelete = btnDelete;
    }

    public float getLeft() {
        if (vertices.containsKey("left")) {
            return vertices.get("left");
        } else{
            return Float.parseFloat(null);
        }
    }

    public float getTop() {
        if (vertices.containsKey("top")) {
            return vertices.get("top");
        } else{
            return Float.parseFloat(null);
        }
    }

    public float getRight() {
        if (vertices.containsKey("right")) {
            return vertices.get("right");
        } else{
            return Float.parseFloat(null);
        }
    }

    public float getBottom() {
        if (vertices.containsKey("bottom")) {
            return vertices.get("bottom");
        } else{
            return Float.parseFloat(null);
        }
    }

    public String getLabel() {
        return label;
    }

    public boolean isCompleteImage() {
        return isCompleteImage;
    }

    public Drawable getBtnDelete() {
        return btnDelete;
    }

    /**
     * Checks if the given x,y pixel coordinates lies inside the top-left vertex (with an offset)
     * boundary.
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @return boolean to indicate whether the touched coordinate lies inside the top-left vertex
     *  (with an offset) boundary.
     */
    public boolean topLeftVertexContains(float x, float y){
        return Math.pow((x - this.getLeft()), 2) +
                Math.pow((y - this.getTop()), 2) <=
                (Constants.BOUNDING_BOX_VERTEX_RADIUS + Constants.BOUNDING_BOX_TOUCH_OFFSET) *
                        (Constants.BOUNDING_BOX_VERTEX_RADIUS + Constants.BOUNDING_BOX_TOUCH_OFFSET);
    }

    /**
     * Checks if the given x,y pixel coordinates lies within the boundary of the bottom-right vertex
     * and its assigned offset margin in pixels.
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @return boolean to indicate whether the touched coordinate lies inside the bottom-right
     * vertex offset boundary.
     */
    public boolean bottomRightVertexContains(float x, float y){
        return Math.pow((x - this.getRight()), 2) +
                Math.pow((y - this.getBottom()), 2) <=
                (Constants.BOUNDING_BOX_VERTEX_RADIUS + Constants.BOUNDING_BOX_TOUCH_OFFSET) *
                        (Constants.BOUNDING_BOX_VERTEX_RADIUS + Constants.BOUNDING_BOX_TOUCH_OFFSET);
    }

    /**
     * Checks if the edges of the bounding box is touched.
     *
     * This operation is done by determining if the touch coordinate lies inside the area, bounded
     * by the pixels marking the outer margin of the bounding box, plus with an additional offset,
     * AND if the touch coordinates are NOT inside the empty (square shaped )space enclosed by the
     * bounding box, plus with an additional offset.
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @return boolean to indicate whether the touched coordinate lies on the edges (with an offset)
     * the bounding box.
     */
    public boolean boxContains(float x, float y){
        if(this.getLeft() < this.getRight() && this.getTop() < this.getBottom()){
            return (!innerOffsetRectContains(x, y, this.getLeft(), this.getRight(), this.getTop(),
                    this.getBottom())
                    && outerOffsetRectContains(x, y, this.getLeft(), this.getRight(), this.getTop(),
                    this.getBottom()));
        }else if(this.getLeft() > this.getRight() && this.getTop() < this.getBottom()){
            return (!innerOffsetRectContains(x, y, this.getRight(), this.getLeft(), this.getTop(),
                    this.getBottom())
                    && outerOffsetRectContains(x, y, this.getRight(), this.getLeft(), this.getTop(),
                    this.getBottom()));
        } else if(this.getLeft() < this.getRight() && this.getTop() > this.getBottom()){
            return (!innerOffsetRectContains(x, y, this.getLeft(), this.getRight(), this.getBottom(),
                    this.getTop())
                    && outerOffsetRectContains(x, y, this.getLeft(), this.getRight(), this.getBottom(),
                    this.getTop()));
        } else {
            return (!innerOffsetRectContains(x, y, this.getRight(), this.getLeft(), this.getBottom(),
                    this.getTop())
                    && outerOffsetRectContains(x, y, this.getRight(), this.getLeft(), this.getBottom(),
                    this.getTop()));
        }
    }

    /**
     * Checks if the coordinates of the touch is located in the empty area enclosed by the inside
     * boundaries bounding box edges (excluding the edges).
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @param leftmost x pixel value of the bounding box's left edge.
     * @param rightmost x pixel value of the bounding box's right edge.
     * @param topmost y pixel value of the bounding box's top edge.
     * @param bottommost y pixel value of the bounding box's bottom edge.
     * @return boolean value indicating of the touch point lies in the empty area enclosed by the
     * inner bounding box.
     */
    private boolean innerOffsetRectContains(float x, float y, float leftmost, float rightmost,
                                            float topmost, float bottommost){
        return (leftmost + Constants.BOUNDING_BOX_TOUCH_OFFSET < x &&
                rightmost - (Constants.BOUNDING_BOX_STROKE_WIDTH
                        + Constants.BOUNDING_BOX_TOUCH_OFFSET) > x &&
                topmost + Constants.BOUNDING_BOX_TOUCH_OFFSET  < y &&
                bottommost - (Constants.BOUNDING_BOX_STROKE_WIDTH
                        + Constants.BOUNDING_BOX_TOUCH_OFFSET ) > y);
    }

    /**
     * Checks if the coordinates of the touch is located in the area enclosed by the outer boundaries
     * bounding box edges (including the edges).
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @param leftmost x pixel value of the bounding box's left edge.
     * @param rightmost x pixel value of the bounding box's right edge.
     * @param topmost y pixel value of the bounding box's top edge.
     * @param bottommost y pixel value of the bounding box's bottom edge.
     * @return boolean value indicating of the touch point lies in the area enclosed by the outer
     * boundaries bounding box.
     */
    private boolean outerOffsetRectContains(float x, float y, float leftmost, float rightmost,
                                            float topmost, float bottommost){
        return (leftmost - (Constants.BOUNDING_BOX_STROKE_WIDTH
                + Constants.BOUNDING_BOX_TOUCH_OFFSET ) < x &&
                rightmost + Constants.BOUNDING_BOX_TOUCH_OFFSET  > x &&
                topmost - (Constants.BOUNDING_BOX_STROKE_WIDTH
                        + Constants.BOUNDING_BOX_TOUCH_OFFSET ) < y &&
                bottommost + Constants.BOUNDING_BOX_TOUCH_OFFSET  > y);
    }

    /**
     * Checks if the 'Delete' button is touched.
     *
     * @param x x pixel value of the touch point.
     * @param y y pixel value of the touch point.
     * @return boolean to indicate whether the Delete button is touched.
     */
    public boolean btnDeleteContains(int x, int y){
        if(btnDelete != null){
            return btnDelete.getBounds().contains(x,y);
        } else {
            // btnDelete is null when the complete image is labelled
            return false;
        }
    }
}
