package hefr.robotsolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.TextView;

/**
 * Created by Frans on 21/11/2017.
 */

public class PreviewOverlayView extends SurfaceView implements SurfaceHolder.Callback{

    private Paint paint = new Paint();

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

    private void init(){
        setWillNotDraw(false); //Have to called!
    }



    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.RED);
        paint.setTextSize(20);
        canvas.drawText("Working", 10, 25, paint);

        paint.setStyle(Paint.Style.STROKE);
        final float centerY = canvas.getHeight()/2f;
        final float centerX = canvas.getWidth()/2f;
        final float length = canvas.getWidth()*0.75f;
        canvas.drawRect(centerX - length/2, centerY - length/2 , centerX + length/2, centerY + length/2, paint);
        canvas.drawRect(1, 1 , canvas.getWidth(), canvas.getHeight(), paint);
        for(int y=0; y<3; y ++){
            for(int x=0; x<3; x ++){
                canvas.drawCircle(centerX - length*4/12 + x*length*4/12, centerY - length*4/12 + y*length*4/12, 3, paint);
            }
        }

        Log.println(Log.ERROR, "FRANS", "onDraw: " + canvas.getHeight()*1.0 / (canvas.getWidth()*1.0));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

