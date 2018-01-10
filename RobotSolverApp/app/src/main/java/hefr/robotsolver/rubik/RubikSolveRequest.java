package hefr.robotsolver.rubik;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hefr.robotsolver.utils.Util;

/**
 * Created by Frans on 10/01/2018.
 */

public class RubikSolveRequest extends AsyncTask<Void, Void, Void> {

    private final static String URL = "http://rubiksolve.com/cubesolve.php";
    private final static String METHOD = "POST";
    private final static String ENCODE = "UTF-8";
    private final static String PARAM1 = "solvesubmit";
    private final static String VALUE1 = "Solve+Cube";
    private final static String PARAM2 = "cubevariablevalue"; //value is the position of the rubik

    private final static Pattern movePattern = Pattern.compile("MOVE.+?\\(([ULFRBD][2']?)\\)"); //solution is in matching group 1
    private final static int movePatternGroup = 1;
    private final static String NOANSWER = "No solution!";

    private RubikCube cube;

    public RubikSolveRequest(RubikCube cube) {
        this.cube = cube;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            //url settings
            URL url = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true); //use input
            urlConnection.setDoOutput(true); //save output
            urlConnection.setRequestMethod(METHOD);

            //write parameters
            final OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(getParams());
            writer.flush();

            //connect
            urlConnection.connect();
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) { //success
                String answer = parseAnswer(Util.inputStreamToString(urlConnection.getInputStream()));
                Log.wtf("Frans", "Solution: \n" + answer);
            } else {
                Log.wtf("frans", "Statuscode: " + statusCode);
            }

        } catch (Exception e) {
            Log.e("frans", "WTF: \n" + e.getMessage());
        }

        return null;
    }


    //Parses the html and returns the answer
    private String parseAnswer(String html) {

        StringBuilder answerBuilder = new StringBuilder();
        Matcher m = movePattern.matcher(html); //find the answer

        while (m.find()) {
            answerBuilder.append(m.group(movePatternGroup));
            answerBuilder.append(" ");
        }

        if (answerBuilder.length() > 0) return answerBuilder.toString();
        else return NOANSWER;
    }

    //Return paramaters of the POST command
    private String getParams() {
        return PARAM2 + "=" + getCubeURLData()
                + "&" +
                PARAM1 + "=" + VALUE1;
    }


    //return the cube position data encoded
    private String getCubeURLData() {
        try {
            return URLEncoder.encode(" " +
                            "U:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.TOP))) + " " +
                            "L:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.LEFT))) + " " +
                            "F:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.FRONT))) + " " +
                            "R:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.RIGHT))) + " " +
                            "B:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.BACK))) + " " +
                            "D:" + Util.intArrayToString(colorToURLColor(cube.getSide(RubikCube.BOTTOM))),
                    ENCODE);
        } catch (UnsupportedEncodingException e) {
            return "";
        }

    }


    //Rubiksolve.com uses different indexing
    private static final int WHITE = 4;
    private static final int ORANGE = 6;
    private static final int YELLOW = 1;
    private static final int RED = 2;
    private static final int GREEN = 5;
    private static final int BLUE = 3;

    //returns rubiksolve.com color index from rubikcube index
    public static int colorToURLColor(int color) {
        switch (color) {
            case RubikCube.WHITE:
                return WHITE;
            case RubikCube.ORANGE:
                return ORANGE;
            case RubikCube.YELLOW:
                return YELLOW;
            case RubikCube.RED:
                return RED;
            case RubikCube.GREEN:
                return GREEN;
            case RubikCube.BLUE:
                return BLUE;
            default:
                return color;
        }
    }

    //changes all colors with colorToURLColor
    public static int[] colorToURLColor(int[] colors) {
        int[] answer = new int[colors.length];
        for (int i = 0; i < answer.length; i++)
            answer[i] = colorToURLColor(colors[i]);
        return answer;
    }

}
