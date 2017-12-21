package hefr.robotsolver;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Frans on 21/12/2017.
 */

public class ColorAnalyzer {

    /** Returns the rubik index of the color*/
    public static int returnCubeColor(int pixel) {

        int result = -1;

        float hsv[] = new float[3];
        Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv);

        //Log.println(Log.ERROR, "FRANS", "S:" + hsv[1] + " V: " + hsv[2]);

        if (hsv[1] < 0.35 && hsv[2] > 0.35)
            result = RubikCube.WHITE;
        else if (hsv[2] < 0.15)
            result = -1; //black
        else {
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
