package hefr.robotsolver.rubik;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Frans on 21/12/2017.
 */

public class RubikAnalyzer {

    private RubikCube cube;

    private int rubikCenterX = 0;
    private int rubikCenterY = 0;
    private int rubikSize = 0;


    public RubikAnalyzer(RubikCube cube) {
        this.cube = cube;
    }

    public RubikAnalyzer(RubikCube cube, float rubikCenterX, float rubikCenterY, float rubikSize) {
        this.cube = cube;
        setRubikLocation(rubikCenterX, rubikCenterY, rubikSize);
    }

    //** Returns the index of the updated side */
    public int analyzeSide(Bitmap image) {

        //check the middle part first
        int pixel = image.getPixel(rubikCenterX, rubikCenterY);

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);
        Log.println(Log.ERROR, "FRANS", "Hue: " + hsv[0] + "  Saturation: " + hsv[1] + "  Value: " + hsv[2]);

        /*
        Log.println(Log.ERROR, "FRANS", "Color: " + RubikCube.getColorName(ImageAnalyzer.returnCubeColor(pixel)));
        Log.println(Log.ERROR, "FRANS", "Pixel: " + pixel);
        Log.println(Log.ERROR, "FRANS", "ColorI: " + ImageAnalyzer.returnCubeColor(pixel));
        Log.println(Log.ERROR, "FRANS", "R: " + Color.red(pixel) + " G: " + Color.green(pixel) + " B: " + Color.blue(pixel));
        */
        int colorI = ImageAnalyzer.returnCubeColor(pixel);

        //if the color wasn't found
        if (colorI == -1) {
            return -1;

        } else { //if the color was found

            final int side = colorI;

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    pixel = image.getPixel(rubikCenterX + rubikSize / 3 * (x - 1), rubikCenterY + rubikSize / 3 * (y - 1));
                    cube.update(side, x, y, ImageAnalyzer.returnCubeColor(pixel));
                }

            }

            return side;
        }


    }


    public void calculateColor() {

    }

    /**
     * Sets the position and the size of the cube on the image
     */
    public void setRubikLocation(float rubikCenterX, float rubikCenterY, float rubikSize) {
        this.rubikCenterX = (int) rubikCenterX;
        this.rubikCenterY = (int) rubikCenterY;
        this.rubikSize = (int) rubikSize;
    }

}
