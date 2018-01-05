package hefr.robotsolver.rubik;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by FransL on 29/12/2017.
 */

public class BooleanMap {

    private boolean[][] map;

    public BooleanMap(int width, int height) {
        map = new boolean[width][height];
    }

    public BooleanMap(Bitmap bm, int maskColor, boolean target) {
        this(bm.getWidth(), bm.getHeight());

        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                map[x][y] = ((bm.getPixel(x, y) == maskColor) == target);
            }
        }

    }

    public void clearNoise(boolean target) {
        //clear pixels that aren't connected to same color enough often
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                //if under 3 connections to same color-> it's probably noise
                if (map[x][y] != target && getParallelConnections(x, y, !target) < 3) {
                    map[x][y] = target;
                }
            }
        }
    }

    public void fillAreasUnder(int size, boolean target) {
        BooleanMap testFiller = this.copy();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int areaSize = testFiller.fill(x, y, target); //calculates area size
                Log.println(Log.ERROR, "FRANS", "areaSize: " + areaSize);
                if (areaSize < size && areaSize > 0) {
                    this.fill(x, y, target); //fill the original
                }
            }
        }
    }

    //return amount of parallel connections
    private int getParallelConnections(int x, int y, boolean target) {
        boolean[] connections = new boolean[8];

        //all 8 directions
        connections[0] = (x > 0 && y < getHeight() - 1 && map[x - 1][y + 1] == target); //south-west
        connections[1] = (x > 0 && map[x - 1][y] == target); //west
        connections[2] = (x > 0 && y > 0 && map[x - 1][y - 1] == target); //north-west
        connections[3] = (y > 0 && map[x][y - 1] == target); //north
        connections[4] = (x < getWidth() - 1 && y > 0 && map[x + 1][y - 1] == target); //north-east
        connections[5] = (x < getWidth() - 1 && map[x + 1][y] == target); //east
        connections[6] = (x < getWidth() - 1 && y < getHeight() - 1 && map[x + 1][y + 1] == target); //south-east
        connections[7] = (y < getHeight() - 1 && map[x][y + 1] == target); //south

        //find the amount of parallel connections
        int parallel = 0;
        int parallelMax = 0;
        for (int i = 0; i < connections.length * 2; i++) {
            if (connections[i % connections.length]) {
                parallel++;
                parallelMax = Math.max(parallelMax, parallel);
            } else {
                parallel = 0;
            }
        }

        return parallelMax;

    }

    public int calculateFillArea(int x, int y, boolean target) {

        BooleanMap temp = this.copy();
        return temp.fill(x, y, target);
    }

    //recursive flood fill algorithm
    public int fill(int x, int y, boolean target) {
        int result = 0;
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight() && map[x][y] != target) {
            map[x][y] = target;
            result += fill(x - 1, y, target);
            result += fill(x + 1, y, target);
            result += fill(x, y - 1, target);
            result += fill(x, y + 1, target);
            result ++;
        }
        return result;
    }


    //fills the areas near edges
    public void fillEdges(boolean target) {
        //horizontal
        for (int y = 0; y <= 1; y++) {
            for (int x = 0; x < getWidth(); x++) {
                fill(x, y * (getHeight() - 1), target);
            }
        }
        //verticals
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y < getHeight(); y++) {
                fill(x * (getWidth() - 1), y, target);
            }
        }
    }


    //Returns bitmap that is masked with this BooleanMap
    public Bitmap maskBitmap(Bitmap bm, int color, boolean target) {
        Bitmap bitmap = bm.copy(bm.getConfig(), true);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (map[x][y] == target) {
                    bitmap.setPixel(x, y, color);
                }
            }
        }
        return bitmap;
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }


    public BooleanMap copy() {
        BooleanMap result = new BooleanMap(getWidth(), getHeight());

        //deep copy 2D array
        boolean[][] resultMap = new boolean[getWidth()][getHeight()];
        for(int x = 0; x < getWidth(); x ++){
            resultMap[x] = Arrays.copyOf(map[x], getHeight());
        }
        result.map = resultMap;

        return result;
    }

    public static boolean A = true;
    public static boolean B = false;

}
