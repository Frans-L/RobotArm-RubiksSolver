package hefr.robotsolver.utils;

import android.graphics.Color;

/**
 * Created by Frans on 22/12/2017.
 */

public class Util {

    /**
     * Keeps value between min and max
     */
    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Returns pixel's greyscaled rgb value
     */
    public static float greyscale(int pixel) {
        return (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f;
    }
}
