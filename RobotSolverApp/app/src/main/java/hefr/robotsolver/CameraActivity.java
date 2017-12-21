package hefr.robotsolver;

/**
 * Created by Frans on 20/11/2017.
 */

import android.Manifest;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Choreographer;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class CameraActivity extends AppCompatActivity {

    private Size previewSize; // The size data of the output the camera
    private ImageView imageView; //The component that is being drawn saved preview image
    private TextureView previewView; //The component that is being drawn preview
    private CubePreviewView cubePreviewView; //The component that is being drawn preview

    private CameraDevice cameraDevice; //The camera that is used
    private CaptureRequest.Builder previewBuilder; //Draws the preview on its surface
    private CameraCaptureSession previewSession; //Camera's session that handles incoming data

    private Button takePicButton;
    private static final int REQUEST_CAMERA = 0; //ID to ask camera permissions

    private RubikCube rubikCube;
    private RubikAnalyzer rubikAnalyzer;

    @Override
    /** Initializes the view*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera); //set the current layout
        imageView = (ImageView) findViewById(R.id.imageView); //a captured image will be shown here

        previewView = (TextureView) findViewById(R.id.previewView); //set preview window
        previewView.setSurfaceTextureListener(surfaceTextureListener);

        rubikCube = new RubikCube();
        rubikAnalyzer = new RubikAnalyzer(rubikCube);

        //sets the mask over the preview
        PreviewOverlayView previewMask = (PreviewOverlayView) findViewById(R.id.previewViewMask);
        previewMask.setZOrderOnTop(true);
        previewMask.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //initializes the cube preview
        cubePreviewView = (CubePreviewView) findViewById(R.id.cubePreviewView);
        cubePreviewView.setZOrderOnTop(true);
        cubePreviewView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        cubePreviewView.setRubikCube(rubikCube);

        Choreographer.getInstance().postFrameCallbackDelayed(new onLoaded(), 200);

        //Set actions to the button
        takePicButton = (Button) findViewById(R.id.takePictureButton);
        takePicButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.println(Log.WARN, "FRANS", "Button Pressed");

                //Update preview Image
                imageView.setImageBitmap(previewView.getBitmap());

                int side = rubikAnalyzer.analyzeSide(previewView.getBitmap());
                cubePreviewView.update();
                Log.println(Log.ERROR, "FRANS", "KESKI YLÄ:" + RubikCube.getColorName(rubikCube.get(RubikCube.FRONT,0,0)));


            }
        });


    }

    /**
     * After all "views" are drawn, this class method doFrame is called
     */
    private class onLoaded implements Choreographer.FrameCallback {

        public void doFrame(long frameTimeNanos) {
            updateRubikImageLocation();
        }

    }

    /**
     * Updates the location and the size of the cube on the image onto rubikAnalyzer and onto previewMask
     */
    private void updateRubikImageLocation() {
        PreviewOverlayView previewMask = (PreviewOverlayView) findViewById(R.id.previewViewMask);
        final float rubikCenterX = previewMask.getWidth() / 2f;
        final float rubikCenterY = previewMask.getHeight() / 2f;
        final float rubikSize = previewMask.getWidth() * 0.75f;

        rubikAnalyzer.setRubikLocation(rubikCenterX, rubikCenterY, rubikSize);
        previewMask.setRubikLocation(rubikCenterX, rubikCenterY, rubikSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Initializes the camera
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraID = manager.getCameraIdList()[0]; //get camera number 0

            //Get previewSize
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            Log.println(Log.ERROR, "FRANS", "True Height: " + previewSize.getHeight() + " width: " + previewSize.getWidth());


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                manager.openCamera(cameraID, cameraStateCallBack, null);
            } else {
                requestCameraPermission();
            }

        } catch (Exception e) {
            Log.println(Log.ERROR, "CameraActivity", e.getMessage());
        }

    }

    //Requests camera permissions
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }


    //SurfaceTextureListener that openCamera when ready
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };


    //Will be called when the state of the camera changes
    private CameraDevice.StateCallback cameraStateCallBack = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera; //saves the camera that is open
            startCamera();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };


    @Override
    //When user is not using the app
    protected void onPause() {
        super.onPause();
        if (cameraDevice != null) {
            cameraDevice.close();
        }

    }

    // Starts the preview
    private void startCamera() {

        //Check that everything is configured
        if (cameraDevice == null || !previewView.isAvailable() || previewSize == null) {
            return;
        }

        SurfaceTexture texture = previewView.getSurfaceTexture();
        if (texture == null) {
            return;
        }

        //Make sure that previewView's buffer has correct size
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

        //Add preview onto the surface of the previewView
        //Camera's preview is set to preview on getChangedPreview()
        Surface surface = new Surface(texture);
        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (Exception e) {
        }
        previewBuilder.addTarget(surface);


        //Create a CaptureSession to get CaptureRequest from CameraDevice
        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession = session; //save the capture session, so it's data can be used
                    setPreviewUpdate();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);
        } catch (Exception e) {
        }
    }

    //Commands cameraDevice's previewSession (CaptureSession) to build previewBuilder continuously
    private void setPreviewUpdate() {

        if (cameraDevice == null) { //check that everything is configured right
            return;
        }

        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //Thread that builds the preview image all the time
        HandlerThread thread = new HandlerThread("changed Preview");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        //Set the previewSession of the cameraDevice to build preview continuously using handler
        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        } catch (Exception e) {
        }
    }

}