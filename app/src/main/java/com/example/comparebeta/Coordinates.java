package com.example.comparebeta;

/**
 * This class defines a 2D coordinate pair as an object.
 *
 * @author Nisal Hemadasa
 */
class Coordinates {
    private float x;
    private float y;

    Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    float getX() {
        return x;
    }

    void setX(float x) {
        this.x = x;
    }

    float getY() {
        return y;
    }

    void setY(float y) {
        this.y = y;
    }
}
