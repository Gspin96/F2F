package today.challengerproject.face2face.f2f;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static today.challengerproject.face2face.f2f.HelperMethods.dipToPixels;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private boolean mVisible;

    private TextInputLayout name;
    private TextInputLayout bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mContentView = findViewById(R.id.fullscreen_content);

        FloatingActionButton addImgButton = findViewById(R.id.add_image);

        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                mContentView.getWindowVisibleDisplayFrame(r);

                int heightDiff = mContentView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > dipToPixels(mContentView.getContext(), 100)) {
                    show();
                }else{
                    hide();
                }
            }
        });

        addImgButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmEntry();
            }
        });

        this.name = findViewById(R.id.inputName);
        this.bio = findViewById(R.id.inputBio);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    show();
                } else {
                    hide();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    private void hide() {
        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void addImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ImageSetterFragment fragment = ImageSetterFragment.newInstance(data, this);
            fragmentTransaction.add(R.id.imageContainer, fragment);
            fragmentTransaction.commit();
        }

        hide();
    }

    public List<Fragment> getVisibleImageFragments() {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Fragment> visibleFragments = new ArrayList<>();
        for (Fragment fragment : allFragments) {
            if (fragment.isVisible() && fragment instanceof ImageSetterFragment ) {
                visibleFragments.add(fragment);
            }
        }
        return visibleFragments;
    }

    private void confirmEntry() {
        List<Fragment> imageFragments = getVisibleImageFragments();

        ArrayList<String> imageUris = new ArrayList<>();

        for (Fragment imageFragment:imageFragments ) {
            ImageSetterFragment f = (ImageSetterFragment) imageFragment;
            Intent i = f.getData();
            if ( i.getData() != null) {
                imageUris.add(i.getData().toString());
            }
        }

        String name = "";
        String bio = "";

        if (this.name.getEditText() != null) {
            name = this.name.getEditText().getText().toString();
        }
        if (this.bio.getEditText() != null) {
            bio = this.bio.getEditText().getText().toString();
        }

        Intent intent = new Intent(this, HelloActivity.class);
        intent.putExtra("NAME", name);
        intent.putExtra("BIO", bio);
        intent.putExtra("IMAGES", imageUris);
        startActivity(intent);
    }

}
