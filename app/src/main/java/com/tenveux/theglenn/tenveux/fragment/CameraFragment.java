package com.tenveux.theglenn.tenveux.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.activities.MainActivity;
import com.tenveux.theglenn.tenveux.widget.BadgeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.mime.TypedFile;

public class CameraFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private Camera mCamera;

    CameraPreview mPreview;

    @InjectView(R.id.button_capture)
    Button captureButton;

    @InjectView(R.id.button_close)
    ImageButton xButton;

    @InjectView(R.id.button_send)
    ImageButton sendButton;

    @InjectView(R.id.button_open_menu)
    ImageButton buttonOpenMenu;

    @InjectView(R.id.button_notif)
    ImageButton buttonNotif;


    @InjectView(R.id.fake_bar)
    RelativeLayout fakeBar;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();

        return fragment;
    }

    public CameraFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActivity().getActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        FrameLayout preview = ButterKnife.findById(rootView, R.id.camera_preview);


        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this.getActivity(), mCamera);
        preview.addView(mPreview);
        //mPreview.setVisibility(View.INVISIBLE);

        ButterKnife.inject(this, preview);

        captureButton.bringToFront();
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        buttonNotif.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                       // ((MainActivity) getActivity()).goToPropositions();
                    }
                }
        );


        xButton.bringToFront();
        sendButton.bringToFront();
        buttonOpenMenu.bringToFront();
        buttonNotif.bringToFront();

        fakeBar.bringToFront();

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraMode();
            }
        });

        onPropositionReceived(container.getContext());

        return rootView;
    }


    @OnClick(R.id.button_open_menu)
    public void setButtonOpenMenu() {
        ((MainActivity) getActivity()).openMenu();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //releaseCamera();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releaseCamera();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCamera = getCameraInstance();
        Log.d("onAttach", "ATTACHING");

        ((MainActivity) activity).onSectionAttached(1);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    public void onPropositionReceived(Context context) {

        //TODO: call prents activty
    }


    private void releaseMediaRecorder() {
        /*if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }*/
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }


    public void switchCameraMode() {

        xButton.setVisibility(View.GONE);
        sendButton.setVisibility(View.GONE);

        captureButton.setVisibility(View.VISIBLE);
        fakeBar.setVisibility(View.VISIBLE);

        mCamera.startPreview();
    }

    private void switchPreviewMode(byte[] byteArray) {


        Bitmap photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        xButton.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.VISIBLE);

        captureButton.setVisibility(View.GONE);
        fakeBar.setVisibility(View.GONE);

        File imageFileFolder = new File(getActivity().getCacheDir(), "Image");
        if (!imageFileFolder.exists()) {
            imageFileFolder.mkdir();
        }

        FileOutputStream fos = null;
        File imageFileName = new File(imageFileFolder, "img-" + System.currentTimeMillis() + ".jpg");

        try {
            fos = new FileOutputStream(imageFileName);
            //fos.write(byteArray);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e("ErrTenveux", "Failed to close output stream", e);
            }
        }

        Log.d("taken", byteArray.length + " / ");

        final TypedFile imagetoSend = new TypedFile("image/jpeg", imageFileName);
        // final Session session = Session.getActiveSession();

            /*sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    if (session != null) {
                        Log.d("sessiion", session.getState().isOpened() + "");
                        if (session.getState().isOpened()) {
                            //TODO : SHOW FRIEND LIST !!!!!
                            Log.d("users", "launch");

                            Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
                                @Override
                                public void onCompleted(List<GraphUser> users, com.facebook.Response response) {

                                    Log.d("users", "done");
                                    for (GraphUser u : users) {
                                        Log.d("users", u.getName());
                                    }
                                    // DialogFragment.show() will take care of adding the fragment
                                    // in a transaction.  We also want to remove any currently showing
                                    // dialog, so make our own transaction and take care of that here.
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                                    if (prev != null) {
                                        ft.remove(prev);
                                    }

                                    ft.addToBackStack(null);

                                    // Create and show the dialog.
                                    FriendsFragment newFragment = FriendsFragment.newInstance(users, imagetoSend);
                                    newFragment.show(ft, "dialog");
                                }
                            }).executeAsync();
                        }
                    }
                }
            });*/
    }


    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] byteArray, Camera camera) {

            switchPreviewMode(byteArray);
            //imagePreview.setImageBitmap(bmp);

        }
    };


    /**
     * A basic Camera preview class
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private final List<Camera.Size> mSupportedPreviewSizes;
        private SurfaceHolder mHolder;
        private Camera mCamera;

        Camera.Size mPreviewSize;


        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                //Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                // Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
