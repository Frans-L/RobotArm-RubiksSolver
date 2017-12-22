package hefr.robotsolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Frans on 21/11/2017.
 */

public class PreviewOverlayView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();

    private float rubikCenterX = 0;
    private float rubikCenterY = 0;
    private float rubikSize = 0;

    public PreviewOverlayView(Context context) {
        super(context);
        init();
    }

    public PreviewOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PreviewOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false); //Have to called!
    }


    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.RED);
        paint.setTextSize(20);
        canvas.drawText("Working", 10, 25, paint);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rubikCenterX - rubikSize / 2, rubikCenterY - rubikSize / 2, rubikCenterX + rubikSize / 2, rubikCenterY + rubikSize / 2, paint);
        canvas.drawRect(1, 1, canvas.getWidth(), canvas.getHeight(), paint);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                canvas.drawCircle(rubikCenterX - rubikSize / 3 * (x - 1), rubikCenterY - rubikSize / 3 * (y - 1), 3, paint);
            }
        }

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

    /**
     * Sets the position and the size of the cube on the image
     */
    public void setRubikLocation(float rubikCenterX, float rubikCenterY, float rubikSize) {
        this.rubikCenterX = rubikCenterX;
        this.rubikCenterY = rubikCenterY;
        this.rubikSize = rubikSize;
        Log.println(Log.ERROR, "FRANS", "HMMM: " + rubikCenterX);
        this.postInvalidate();
    }

}

