package hefr.robotsolver.rubik;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Frans on 21/12/2017.
 */

public class CubePreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();
    private RubikCube cube;

    public CubePreviewView(Context context) {
        super(context);
        init();
    }

    public CubePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CubePreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CubePreviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false); //Have to called!
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (cube != null) {
            drawCube(canvas);
        }

    }

    /**
     * Draws the all sides of the cube
     */
    private void drawCube(Canvas canvas) {

        final float size = canvas.getHeight() / 4f;
        final float sideSize = size * 0.80f;
        final float offset = (size - sideSize) * 0.5f;

        drawSide(RubikCube.TOP, canvas, 1 * size + offset, 0 * size + offset, sideSize);
        drawSide(RubikCube.LEFT, canvas, 0 * size + offset, 1 * size + offset, sideSize);
        drawSide(RubikCube.FRONT, canvas, 1 * size + offset, 1 * size + offset, sideSize);
        drawSide(RubikCube.RIGHT, canvas, 2 * size + offset, 1 * size + offset, sideSize);
        drawSide(RubikCube.BACK, canvas, 3 * size + offset, 1 * size + offset, sideSize);
        drawSide(RubikCube.BOTTOM, canvas, 1 * size + offset, 2 * size + offset, sideSize);

    }

    /**
     * Draws on side of the cube
     */
    private void drawSide(int side, Canvas canvas, float posX, float posY, float size) {
        final float pieceSize = size / 3f * 0.9f;
        final float offset = (size / 3f - pieceSize) * 0.5f;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(cube.getColor(side, x, y));
                canvas.drawRect(posX + x * size / 3f + offset, posY + y * size / 3f + offset,
                        posX + x * size / 3f + pieceSize + offset, posY + y * size / 3f + pieceSize + offset, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                canvas.drawRect(posX + x * size / 3f + offset, posY + y * size / 3f + offset,
                        posX + x * size / 3f + pieceSize + offset, posY + y * size / 3f + pieceSize + offset, paint);
                ;
            }
        }
    }


    public void setRubikCube(RubikCube cube) {
        this.cube = cube;
        this.postInvalidate();
    }

    public void update() {
        this.postInvalidate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
