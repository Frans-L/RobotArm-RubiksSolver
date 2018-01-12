package hefr.robotsolver;

/**
 * Created by Frans on 20/11/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.Snackbar;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hefr.robotsolver.bluetooth.FindNXT;
import hefr.robotsolver.bluetooth.NXTTalker;
import hefr.robotsolver.rubik.CubePreviewView;
import hefr.robotsolver.rubik.ImageAnalyzer;
import hefr.robotsolver.rubik.RubikAnalyzer;
import hefr.robotsolver.rubik.RubikCube;
import hefr.robotsolver.rubik.RubikShowSolution;
import hefr.robotsolver.rubik.RubikSolveRequest;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {

    private Size previewSize; // The size data of the output the camera
    private ImageView imageView; //The component that is being drawn saved preview image
    private TextureView previewView; //The component that is being drawn preview
    private CubePreviewView cubePreviewView; //The component that is being drawn preview

    private CameraDevice cameraDevice; //The camera that is used
    private CaptureRequest.Builder previewBuilder; //Draws the preview on its surface
    private CameraCaptureSession previewSession; //Camera's session that handles incoming data

    private Button takePicButton;
    private Button findNXTButton;
    private Button sendButton;
    private Button solutionButton;

    private static final int REQUEST_CAMERA = 0; //ID to ask camera permissions
    private static final int REQUEST_ENABLE_BT = 1; //ID to ask bluetooth permissions
    private static final int REQUEST_CONNECT_DEVICE = 2; //ID to ask connect to devices

    private RubikCube rubikCube;
    private RubikAnalyzer rubikAnalyzer;

    private BluetoothAdapter bluetoothAdapter;
    private List<NXTTalker> nxtTalker = new ArrayList<>(); //Handles sending messages to the nxt

    @Override
    /** Initializes the view*/
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera); //set the current layout
        imageView = (ImageView) findViewById(R.id.imageView); //a captured image will be shown here

        previewView = (TextureView) findViewById(R.id.previewView); //set preview window
        previewView.setSurfaceTextureListener(surfaceTextureListener);

        rubikCube = new RubikCube();
        rubikCube.testPosition();
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

        takePicButton = (Button) findViewById(R.id.takePictureButton);
        takePicButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Update preview Image
                imageView.setImageBitmap(ImageAnalyzer.energyTable(previewView.getBitmap()));

                int side = rubikAnalyzer.analyzeSide(previewView.getBitmap());
                cubePreviewView.update();


            }
        });

        findNXTButton = (Button) findViewById(R.id.findNXTButton);
        findNXTButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {
                    findNXT();
                }
            }
        });

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (nxtTalker.size() > 0) {
                    for (NXTTalker talker : nxtTalker) {
                        if (talker.getState() == NXTTalker.STATE_CONNECTED) {
                            talker.sendLine();
                        } else if (talker.getState() == NXTTalker.STATE_NONE) {
                            Log.wtf("Frans", "Disconneted?");
                        }
                    }
                }

            }
        });

        solutionButton = (Button) findViewById(R.id.solutionButton);
        solutionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSolution();
            }
        });

        //Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    /*
    cubevariablevalue=+U%3A123400500+L%3A000000000+F%3A000000000+R%3A000000000+B%3A000000000+D%3A000000000&solvesubmit=Solve+Cube
    solvesubmit=Solve Cube&cubevariablevalue=+U%3A555555555+L%3A222222222+F%3A444444444+R%3A666666666+B%3A111111111+D%3A333333333
    */

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * After all "views" are drawn, this class method doFrame is called
     */
    private class onLoaded implements Choreographer.FrameCallback {

        public void doFrame(long frameTimeNanos) {
            updateRubikImageLocation();
        }

    }

    private void showSolution() {
        RubikSolveRequest request = new RubikSolveRequest(new AfterRequest(), rubikCube);
        request.execute();
        //Intent intent = new Intent(this, RubikShowSolution.class);
    }

    private class AfterRequest implements RubikShowSolution.Callable {
        public void updateSolution(String solution) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainView), solution, Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.wtf("Frans", "main: " + solution);
        }
    }

    public interface Callable {
        void updateSolution(String solution);
    }

    //starts find nxt activity
    private void findNXT() {
        if (!FindNXT.running) {
            cleanDeadNXT(); //clean dead connections
            Intent intent = new Intent(this, FindNXT.class);
            if (nxtTalker.size() > 0) //add connected device (if possible)
                intent.putExtra(FindNXT.EXTRA_CONNECTED_ADDRESSES, getNXTAddresses());

            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        }
    }

    //returns all nxt addresses
    private String[] getNXTAddresses() {
        String[] addresses = new String[nxtTalker.size()];
        for (int i = 0; i < nxtTalker.size(); i++) {
            addresses[i] = nxtTalker.get(i).address;
        }
        return addresses;
    }

    //removes dead connections
    private void cleanDeadNXT() {
        for (int i = nxtTalker.size() - 1; i >= 0; i--) {
            if (nxtTalker.get(i).getState() == NXTTalker.STATE_NONE) { //is dead
                nxtTalker.remove(i);
            }
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
        restartCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    openCamera(); //try to open camera again
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    findNXT();
                }
                break;

            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(FindNXT.EXTRA_DEVICE_ADDRESS);

                    //if old device, disconnect
                    boolean newDevice = true;
                    for (int i = nxtTalker.size() - 1; i >= 0; i--) {
                        if (Objects.equals(nxtTalker.get(i).address, address)) {
                            nxtTalker.get(i).stop();
                            nxtTalker.remove(i);
                            newDevice = false;
                            break;
                        }
                    }

                    //if new device, connect
                    if (newDevice) {
                        NXTTalker talker = new NXTTalker();
                        talker.connect(address);
                        nxtTalker.add(talker);
                    }

                }
                break;
        }
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
            //Log.println(Log.ERROR, "FRANS", "True Height: " + previewSize.getHeight() + " width: " + previewSize.getWidth());

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                manager.openCamera(cameraID, cameraStateCallBack, null);
            } else {
                requestCameraPermission();
            }

        } catch (Exception e) {
            Log.println(Log.ERROR, "MainActivity", e.getMessage());
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

    //Restarts the camera
    private void restartCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            openCamera();
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