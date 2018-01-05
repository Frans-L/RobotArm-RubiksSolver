package hefr.robotsolver.rubik;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import hefr.robotsolver.utils.Util;

/**
 * Created by Frans on 21/12/2017.
 */

public class ImageAnalyzer {

    public static Bitmap energyTable(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 6, bitmap.getHeight() / 6, false);
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        final int debugColor = Color.rgb(255, 0, 255);

        final Bitmap energyMap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        final int energyTable[][] = new int[width][height];

        float[] avgHSV = ImageAnalyzer.averageHSV(bitmap);
        Log.println(Log.ERROR, "FRANS", "AVG Hue: " + avgHSV[0] + "  AVG Saturation: " + avgHSV[1] + "  AVG Value: " + avgHSV[2]);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                final float pixelX1 = Util.greyscale(bitmap.getPixel(Util.clamp(x - 1, 0, width - 1), y));
                final float pixelX2 = Util.greyscale(bitmap.getPixel(Util.clamp(x + 1, 0, width - 1), y));
                final float pixelY1 = Util.greyscale(bitmap.getPixel(x, Util.clamp(y - 1, 0, height - 1)));
                final float pixelY2 = Util.greyscale(bitmap.getPixel(x, Util.clamp(y + 1, 0, height - 1)));

                int energy = (int) Math.sqrt(Math.pow((pixelX1 - pixelX2) / 2f, 2) + Math.pow((pixelY1 - pixelY2) / 2f, 2)) * 2;
                //energy = Math.min((energy / 50) * 255, 255);
                //energy = Math.max(energy - 25, 0) * 255;
                energyTable[x][y] = energy;
                //final int color = filterBlack(bitmap.getPixel(x, y), avgHSV);
                int color = bitmap.getPixel(x, y);

                int pixel;
                if (energy > 15 || Util.greyscale(color) < 20) {
                    pixel = debugColor;
                } else {
                    pixel = color;
                }

                //final int pixel = Color.rgb(Util.clamp(Color.red(color) + energy, 0, 255), Util.clamp(Color.green(color) + energy, 0, 255), Util.clamp(Color.blue(color) + energy, 0, 255));

                energyMap.setPixel(x, y, pixel);
                if (pixel == debugColor) {
                    energyMap.setPixel(Math.max(x - 1, 0), y, pixel);
                    energyMap.setPixel(Math.min(x + 1, width - 1), y, pixel);
                    energyMap.setPixel(x, Math.max(y, 0), pixel);
                    energyMap.setPixel(x, Math.min(y, height - 1), pixel);
                }
                //findSquare(energyMap, Color.rgb(255, 0, 255));
            }
        }


        BooleanMap mask = new BooleanMap(energyMap, debugColor, BooleanMap.A);
        mask.fillEdges(BooleanMap.A);
        mask.clearNoise(BooleanMap.A);
        mask.fillAreasUnder(23, BooleanMap.A);

        return mask.maskBitmap(energyMap, debugColor, BooleanMap.A);

    }

    public static int filterBlack(int pixel, float[] avgHSV) {
        final int black = Color.rgb(255, 0, 255);
        int result = pixel;

        final float hsv[] = new float[3];
        Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);

        //Log.println(Log.ERROR, "FRANS", "S:" + hsv[1] + " V: " + hsv[2]);
        if (hsv[2] < 0.25) result = black; //black


        return result;
    }

    public static int filterNotCubeColors(int pixel, float[] avgHSV) {
        final int black = Color.rgb(0, 0, 0);
        int result = pixel;

        final float hsv[] = new float[3];
        Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);

        //Log.println(Log.ERROR, "FRANS", "S:" + hsv[1] + " V: " + hsv[2]);
        if (!(hsv[1] < 0.2 && hsv[2] > avgHSV[2] * 1.4f || hsv[2] > 0.9f)) { //if not white
            if (hsv[2] < 0.15) result = black; //black
            else if (hsv[1] < avgHSV[1] * 1.5 && Math.abs(hsv[0] - 120) > 30)
                result = black; //too low saturation for the cube (not green)
            else if (hsv[1] < avgHSV[1] * 1.25 && Math.abs(hsv[0] - 120) < 30)
                result = black; //too low saturation for the cube (green only
            else if (hsv[0] >= 150 && hsv[0] < 210) result = black; //cyan
            else if (hsv[0] >= 270 && hsv[0] < 330) result = black; //magenta
        }


        return result;
    }

    public static float[] averageHSV(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        float[] sum = new float[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixel = bitmap.getPixel(x, y);
                final float hsv[] = new float[3];
                Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);
                for (int i = 0; i < 3; i++) sum[i] += hsv[i];
            }
        }

        for (int i = 0; i < 3; i++) sum[i] = sum[i] / (width * height); //average

        return sum;
    }

    /**
     * Returns the rubik index of the color
     */
    public static int returnCubeColor(int pixel) {

        int result = -1;

        final float hsv[] = new float[3];
        Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);

        //Log.println(Log.ERROR, "FRANS", "S:" + hsv[1] + " V: " + hsv[2]);

        if (hsv[1] < 0.25 && hsv[2] > 0.35)
            result = RubikCube.WHITE;
        else if (hsv[2] < 0.15)
            result = -1; //black
        else if (hsv[1] > 0.45) {
            float hue = hsv[0];
            if (hue >= 0 && hue < 10) result = RubikCube.RED;
            else if (hue >= 10 && hue < 30) result = RubikCube.ORANGE;
            else if (hue >= 30 && hue < 90) result = RubikCube.YELLOW;
            else if (hue >= 90 && hue < 150) result = RubikCube.GREEN;
            else if (hue >= 150 && hue < 210) result = -1; //cyan
            else if (hue >= 210 && hue < 270) result = RubikCube.BLUE;
            else if (hue >= 270 && hue < 330) result = -1; //magenta
            else result = RubikCube.RED;
        }

        return result;
    }

}
