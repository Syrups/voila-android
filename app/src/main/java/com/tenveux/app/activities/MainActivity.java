package com.tenveux.app.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tenveux.app.ApplicationController;
import com.tenveux.app.NavigationDrawerFragment;
import com.tenveux.app.R;
import com.tenveux.app.UserPreferences;
import com.tenveux.app.activities.menu.friends.NetworkActivity;
import com.tenveux.app.activities.menu.profile.ProfileActivity;
import com.tenveux.app.camera.CameraPreview;
import com.tenveux.app.fragment.FriendsFragment;
import com.tenveux.app.models.User;
import com.tenveux.app.widget.ExifUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final int SELECT_PHOTO = 2567;

    JsonArray mPropositons;
    JsonArray mAnswers;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.button_capture)
    Button captureButton;

    @InjectView(R.id.button_load_media)
    Button mLoadButton;

    @InjectView(R.id.button_switch_camera)
    Button switchCameraButton;

    @InjectView(R.id.button_cancel)
    ImageButton cancelButton;

    @InjectView(R.id.camera_preview)
    FrameLayout preview;

    @InjectView(R.id.image_preview)
    ImageView mPickedImage;


    private int numberOfFriendRequest;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    private TypedFile mTypedFileToSend;
    private Bitmap mPhotoToSend;

    private Camera camera;
    private int cameraId = 0;

    private Camera mCamera;
    private CameraPreview mPreview;

    private static boolean isFrontCameraAvalaible;
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            //MainActivity.this.photoToSend
            Bitmap toSend = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.d("taken", toSend.getWidth() + " x " + toSend.getHeight());

            switchPreviewMode(true, toSend);
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

            String id = u.getId();

            ApplicationController.userApi().pendingall(id, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {

                    mAnswers = jsonObject.get("answers").getAsJsonArray();
                    mPropositons = jsonObject.get("propositions").getAsJsonArray();

                    MainActivity.this.invalidateOptionsMenu();
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });

            ApplicationController.userApi().requests(id, new Callback<List<User>>() {
                @Override
                public void success(List<User> users, Response response) {
                    numberOfFriendRequest = users.size() - 1;
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {
            Log.e("User", "none");
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
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                }
        );

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCameraMode();
            }
        });


        //From ext picture
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                try {
                    handleSendImage(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imageUri = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);

                        Bitmap b = BitmapFactory.decodeStream(imageStream);
                        Bitmap photoToSend = ExifUtils.rotateBitmap(this, imageUri, b);

                        switchPreviewMode(false, photoToSend);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    void handleSendImage(Intent intent) throws FileNotFoundException {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);

            Bitmap b = BitmapFactory.decodeStream(imageStream);
            Bitmap photoToSend = ExifUtils.rotateBitmap(this, imageUri, b);

            switchPreviewMode(false, photoToSend);
        }
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
            Log.d("onresume", "mCamera == null");
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

        Intent menuIntent = null;

        switch (position) {
            case 1:
                menuIntent = new Intent(this, ProfileActivity.class);
                break;
            case 2:
                //menuIntent = new Intent(this, ReceptionActivity.class);
                break;
            case 3:
                menuIntent = new Intent(this, NetworkActivity.class);
                menuIntent.putExtra("numberOfFriendRequest", numberOfFriendRequest);
                break;
            case 4:
                //menuIntent = new Intent(this, PopularActivity.class);
                //menuIntent.putExtra("numberOfFriendRequest", numberOfFriendRequest)
                break;
        }

        if (menuIntent != null)
            startActivity(menuIntent);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_go_propositions);

        if (item != null)
            if ((mPropositons != null && mPropositons.size() > 0) || (mAnswers != null && mAnswers.size() > 0)) {
                item.setIcon(R.drawable.ic_menu_received_notif);
            } else {
                item.setIcon(R.drawable.ic_menu_received);
            }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    public void openMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.button_load_media)
    void loadPhoneMedias() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @OnClick(R.id.button_switch_camera)
    void switchCamera() {
        mPreview.switchCamera(this);
    }

    @Override
    public void onPropositionSent(JsonElement jsonElement) {
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                }
        );

        this.switchCameraMode();
        //Log.d("JsonElement", jsonElement.toString());
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

        mLoadButton.setVisibility(View.VISIBLE);
        switchCameraButton.setVisibility(View.VISIBLE);

        mPickedImage.setVisibility(View.GONE);

        captureButton.setVisibility(View.VISIBLE);
        captureButton.setSelected(false);


        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                }
        );

        mCamera.startPreview();
    }

    private void switchPreviewMode(boolean fromCamera, Bitmap photoToSend) {
        float ratio = photoToSend.getWidth() / 350;

        int width = (int) (photoToSend.getWidth() / ratio);
        int height = (int) (photoToSend.getHeight() / ratio);

        Log.d("taken", width + " XxX " + height);

        this.mPhotoToSend = Bitmap.createScaledBitmap(photoToSend, width, height, true);

        captureButton.setSelected(true);
        captureButton.setClickable(false);

        cancelButton.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.GONE);

        mLoadButton.setVisibility(View.GONE);
        switchCameraButton.setVisibility(View.GONE);

        if (!fromCamera) {
            mPickedImage.setVisibility(View.VISIBLE);
            mPickedImage.setImageBitmap(photoToSend);
        }

        captureButton.setOnClickListener(showFriends);
        captureButton.setClickable(true);
    }

    View.OnClickListener showFriends = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            File imageFileFolder = new File(getFilesDir(), "Image");
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdir();
            }


            FileOutputStream fos = null;
            File imageFileName = new File(imageFileFolder, "img-" + System.currentTimeMillis() + ".jpg");

            try {
                fos = new FileOutputStream(imageFileName);
                //fos.write(byteArray);

                mPhotoToSend.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                //mPhotoToSend = ExifUtils.exifBitmap(imageFileName.getName());
                //mPhotoToSend.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();

                showFriendsForFile(imageFileName);

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
        }
    };


    private void showFriendsForFile(File imageToSend) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        FriendsFragment newFragment = FriendsFragment.newInstance(imageToSend);
        newFragment.show(ft, "dialog");
    }
}
