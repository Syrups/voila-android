package com.tenveux.theglenn.tenveux;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tenveux.theglenn.tenveux.activities.PropositionsActivity;
import com.tenveux.theglenn.tenveux.apimodel.CreateUserResponse;
import com.tenveux.theglenn.tenveux.apimodel.Proposition;
import com.tenveux.theglenn.tenveux.fragment.FriendsFragment;
import com.tenveux.theglenn.tenveux.widget.BadgeView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
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


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FriendsFragment.FrienSelectedListner {
//FriendsFragment.FrienSelectedListner

    private static final int badgeColor = Color.parseColor("#df006e");

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private TypedFile imageToSend;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
        mDrawerList = ButterKnife.findById(mNavigationDrawerFragment.getView(), android.R.id.list);

        CreateUserResponse u = UserPreferences.getSessionUser();

        if (u != null) {

            //TODO switch with PENDINGS
            ApplicationController.api().getReceivedPropostion(u.getId(), new Callback<List<Proposition>>() {
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

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = PlaceholderFragment.newInstance(position + 1);
        String tag = "camera";
        switch (position) {
            case 0:
                fragment = CameraFragment.newInstance();
                break;
            case 1:
                fragment = PlaceholderFragment.newInstance(position + 1);
                tag = "profile";
                break;
        }

        fragmentManager.beginTransaction()

                .replace(R.id.container, fragment, tag)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        getActionBar().hide();
                        //hideStatus();
                    }
                });

                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                getActionBar().show();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                getActionBar().show();
                break;
        }
    }


    private void hideStatus() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.

            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        //if (actionBar.isShowing()) {
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //restoreActionBar();
            return true;
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPropositionSent(JsonElement jsonElement) {
        CameraFragment cf = (CameraFragment) getFragmentManager().findFragmentByTag("camera");
        cf.switchCameraMode();
        Log.d("JsonElement", jsonElement.toString());
    }

    private void onPropositionReceived() {
        CameraFragment cf = (CameraFragment) getFragmentManager().findFragmentByTag("camera");
        cf.onPropositionReceived(this);
        //mDrawerLayout.openDrawer(mDrawerLayout);
    }

    private void goToPropositions() {

        Intent mainIntent = new Intent(this, PropositionsActivity.class);
        this.startActivity(mainIntent);

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CameraFragment extends Fragment {
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
                            ((MainActivity)getActivity()).goToPropositions();
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

            BadgeView badge = new BadgeView(context, buttonNotif);
            badge.setText("1");
            badge.setBadgeBackgroundColor(badgeColor);
            badge.show();


            badge.bringToFront();
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


        private void switchCameraMode() {

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
            final Session session = Session.getActiveSession();

            sendButton.setOnClickListener(new View.OnClickListener() {
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
            });
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

    private void openMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void openReceivedPropositions() {
        //mDrawerLayout.openDrawer(mDrawerLayout);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
