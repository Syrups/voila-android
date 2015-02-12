package com.tenveux.theglenn.tenveux.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.tenveux.theglenn.tenveux.ApplicationController;
import com.tenveux.theglenn.tenveux.NavigationDrawerFragment;
import com.tenveux.theglenn.tenveux.R;
import com.tenveux.theglenn.tenveux.UserPreferences;
import com.tenveux.theglenn.tenveux.camera.CameraPreview;
import com.tenveux.theglenn.tenveux.fragment.FriendsFragment;
import com.tenveux.theglenn.tenveux.models.Proposition;
import com.tenveux.theglenn.tenveux.models.User;
import com.tenveux.theglenn.tenveux.widget.BadgeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FriendsFragment.FrienSelectedListner {

    private static final int badgeColor = Color.parseColor("#df006e");

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.button_capture)
    Button captureButton;

    @InjectView(R.id.button_save_media)
    Button saveButton;

    @InjectView(R.id.button_switch_camera)
    Button switchCameraButton;

    @InjectView(R.id.button_cancel)
    ImageButton cancelButton;

    @InjectView(R.id.camera_preview)
    FrameLayout preview;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    private TypedFile imageToSend;

    private Camera camera;
    private int cameraId = 0;

    private Camera mCamera;
    private CameraPreview mPreview;

    private static boolean isFrontCameraAvalaible;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            switchPreviewMode(data);

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // do we have a camera?
        //this.checkCameraHardware();

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //Drawer init
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout, mToolbar);
        mDrawerList = ButterKnife.findById(mNavigationDrawerFragment.getView(), android.R.id.list);


        //User datas
        User u = UserPreferences.getSessionUser();

        if (u != null) {
            //TODO switch with PENDINGS
            ApplicationController.userApi().getPendingProposition(u.getId(), new Callback<List<Proposition>>() {
                @Override
                public void success(List<Proposition> propositions, Response response) {

                    Log.d("success", "getReceivedPropostion");
                    for (Proposition p : propositions) {
                        Log.d("propostion", p.getImage());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        } else {
            Log.d("User", "none");
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);

        preview.addView(mPreview);


        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraMode();
            }
        });
    }


    /**
     * Check if this device has a camera
     */
    private void checkCameraHardware() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d("TenveuxCAM", "Camera found");
                cameraId = i;
                break;
            }
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d("TenveuxCAM", "Camera found");
                cameraId = i;
                isFrontCameraAvalaible = true;
                break;

            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);
        }
    }



    private void releaseMediaRecorder() {
       /* if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }*/
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);

            try {
                mCamera.release();        // release the camera for other applications
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch (position) {
            case 0:
                break;
            case 1:
                break;
        }
    }

    public void onSectionAttached(int number) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //restoreActionBar();

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_go_propositions) {
            goToPropositions();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void goToPropositions() {

        Intent mainIntent = new Intent(this, PropositionsActivity.class);
        this.startActivity(mainIntent);
    }

    @OnClick(R.id.button_save_media)
    void saveMedia() {

        Toast.makeText(this, "saveMedia", Toast.LENGTH_LONG).show();
    }


    @OnClick(R.id.button_switch_camera)
    void switchCamera() {

        mPreview.switchCamera(this);
        Toast.makeText(this, "switchCamera", Toast.LENGTH_LONG).show();
    }


    public void onPropositionReceived(View v) {

        BadgeView badge = new BadgeView(this, v);
        badge.setText("1");
        badge.setBadgeBackgroundColor(badgeColor);
        badge.show();


        badge.bringToFront();
    }


    public void openMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onPropositionSent(JsonElement jsonElement) {
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        this.switchCameraMode();
        Log.d("JsonElement", jsonElement.toString());
    }

    private void onPropositionReceived() {
        //CameraFragment cf = (CameraFragment) getSupportFragmentManager().findFragmentByTag("camera");
        //cf.onPropositionReceived(this);
        mDrawerLayout.openDrawer(mDrawerLayout);
    }

    private void openReceivedPropositions() {
        mDrawerLayout.openDrawer(mDrawerLayout);
    }

    public void switchCameraMode() {

        cancelButton.setVisibility(View.GONE);

        mToolbar.setVisibility(View.VISIBLE);

        saveButton.setVisibility(View.VISIBLE);
        switchCameraButton.setVisibility(View.VISIBLE);

        captureButton.setVisibility(View.VISIBLE);
        captureButton.setSelected(false);

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        mCamera.startPreview();
    }

    private void switchPreviewMode(byte[] byteArray) {


        Bitmap photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        captureButton.setSelected(true);
        captureButton.setClickable(false);


        cancelButton.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.GONE);

        saveButton.setVisibility(View.GONE);
        switchCameraButton.setVisibility(View.GONE);

        //sendButton.setVisibility(View.VISIBLE);

        //captureButton.setVisibility(View.GONE);

        File imageFileFolder = new File(this.getCacheDir(), "Image");
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
        final User session = UserPreferences.getSessionUser();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session != null) {
                    ApplicationController.userApi().friends(session.getId(), new Callback<List<User>>() {
                        @Override
                        public void success(List<User> users, Response response) {
                            Log.d("users", "done");

                            for (User u : users) {
                                Log.d("users", u.getName());
                            }
                            // DialogFragment.show() will take care of adding the fragment
                            // in a transaction.  We also want to remove any currently showing
                            // dialog, so make our own transaction and take care of that here.
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                            if (prev != null) {
                                ft.remove(prev);
                            }

                            ft.addToBackStack(null);

                            // Create and show the dialog.
                            FriendsFragment newFragment = FriendsFragment.newInstance(users, imagetoSend);
                            newFragment.show(ft, "dialog");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });

                }
            }
        });

        captureButton.setClickable(true);
    }


}
