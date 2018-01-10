package hefr.robotsolver.utils;

import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    /**
     * Returns a string made with strings
     */
    public static String stringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Returns intArray as a String
     */
    public static String intArrayToString(int[] array){
        StringBuilder answer = new StringBuilder();
        for (int i : array) {
            answer.append(i);
        }
        return answer.toString();
    }

    /**
     * Returns inputStream as a String
     */
    public static String inputStreamToString(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        StringBuilder answerBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null)
            answerBuilder.append(inputLine);
        br.close();

        return answerBuilder.toString();
    }

}

