package hefr.robotsolver.rubik;

import android.graphics.Color;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Frans on 21/12/2017.
 */

public class RubikCube {

    private int[][] cube = new int[6][9];

    public RubikCube() {
        reset();
    }


    public void update(int side, int locationX, int locationY, int color) {
        this.update(side, locationX + locationY * 3, color);
    }

    public void update(int side, int location, int color) {
        cube[side][location] = color;
    }

    public int get(int side, int locationX, int locationY) {
        return cube[side][locationX + locationY * 3];
    }

    //** Return the color of the piece*/
    public int getColor(int side, int locationX, int locationY) {
        //Log.println(Log.ERROR, "Frans", "" + (locationX + locationY*3));
        return getColorValue(cube[side][locationX + locationY * 3]);
    }

    /**
     * Resets the cube
     */
    public void reset() {
        for (int s = 0; s < 6; s++) {
            for (int i = 0; i < 9; i++) {
                cube[s][i] = s;
            }
        }
    }


    //Colors index
    public static final int WHITE = 0;
    public static final int ORANGE = 1;
    public static final int YELLOW = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;
    public static final int BLUE = 5;

    //Side index
    public static final int FRONT = 0;
    public static final int RIGHT = 1;
    public static final int BACK = 2;
    public static final int LEFT = 3;
    public static final int TOP = 4;
    public static final int BOTTOM = 5;

    //Location index
    public static final int NORTHWEST = 0;
    public static final int NORTH = 1;
    public static final int NORTHEAST = 2;
    public static final int WEST = 3;
    public static final int CENTER = 4;
    public static final int EAST = 5;
    public static final int SOUTHWEST = 6;
    public static final int SOUTH = 7;
    public static final int SOUTHEAST = 8;


    public static String getColorName(int colorI) {
        switch (colorI) {
            case WHITE:
                return "White";
            case ORANGE:
                return "Orange";
            case YELLOW:
                return "Yellow";
            case RED:
                return "Red";
            case GREEN:
                return "Green";
            case BLUE:
                return "Blue";
            default:
                return "Unknown";
        }
    }

    public static int getColorValue(int colorI) {
        switch (colorI) {
            case WHITE:
                return Color.WHITE;
            case ORANGE:
                return -5285847; //Orange
            case YELLOW:
                return Color.YELLOW;
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            default:
                return Color.BLACK;
        }
    }


}
