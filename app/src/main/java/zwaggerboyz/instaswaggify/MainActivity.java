package zwaggerboyz.instaswaggify;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zwaggerboyz.instaswaggify.dialogs.ExportDialog;
import zwaggerboyz.instaswaggify.dialogs.FavoritesDialog;
import zwaggerboyz.instaswaggify.dialogs.FilterDialog;
import zwaggerboyz.instaswaggify.dialogs.OverlayDialog;
import zwaggerboyz.instaswaggify.dialogs.SavePresetDialog;
import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;
import zwaggerboyz.instaswaggify.filters.BrightnessFilter;
import zwaggerboyz.instaswaggify.filters.ColorizeFilter;
import zwaggerboyz.instaswaggify.filters.ContrastFilter;
import zwaggerboyz.instaswaggify.filters.GaussianBlurFilter;
import zwaggerboyz.instaswaggify.filters.IFilter;
import zwaggerboyz.instaswaggify.filters.InvertColorsFilter;
import zwaggerboyz.instaswaggify.filters.NoiseFilter;
import zwaggerboyz.instaswaggify.filters.RotationFilter;
import zwaggerboyz.instaswaggify.filters.SaturationFilter;
import zwaggerboyz.instaswaggify.filters.SepiaFilter;
import zwaggerboyz.instaswaggify.viewpager.FilterListAdapter;
import zwaggerboyz.instaswaggify.viewpager.ListViewPagerAdapter;
import zwaggerboyz.instaswaggify.viewpager.OverlayListAdapter;
import zwaggerboyz.instaswaggify.viewpager.SlidingTabLayout;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    MainActivity.java
 * This file contains the main-activity for the app.
 */

public class MainActivity extends FragmentActivity
        implements FilterListAdapter.FilterListInterface,
                   FilterDialog.OnAddFilterListener,
                   OverlayDialog.OnAddOverlayListener {
    private ShareActionProvider mShareActionProvider;
    private FilterListAdapter mFilterAdapter;
    private OverlayListAdapter mOverlayAdapter;
    private CanvasView mCanvasView;
    private ViewPager mViewPager;
    private RSFilterHelper mRSFilterHelper;
    private DialogFragment mDialog;
    private Menu mMenu;
    private ExportDialog mExportDialog;

    private Uri mImageUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExportDialog = new ExportDialog();

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout)findViewById(R.id.activity_main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        mCanvasView = (CanvasView) findViewById(R.id.activity_main_canvasview);

        mExportDialog.setCanvasView(mCanvasView);

        mRSFilterHelper = new RSFilterHelper();
        mRSFilterHelper.createRS(this);
        mRSFilterHelper.setCanvasView(mCanvasView);
        mRSFilterHelper.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.data), true);
        mRSFilterHelper.generateBitmap(new ArrayList<IFilter>(), this);

        List<CanvasDraggableItem> overlays = new ArrayList<CanvasDraggableItem>();
        mFilterAdapter = new FilterListAdapter(this, this, new ArrayList<IFilter>());
        mOverlayAdapter = new OverlayListAdapter(this, mCanvasView, overlays);
        mCanvasView.setOverlays(overlays);
        FragmentStatePagerAdapter pagerAdapter = new ListViewPagerAdapter(
                getSupportFragmentManager(),
                mFilterAdapter,
                mOverlayAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(pagerAdapter);
        slidingTabLayout.setViewPager(mViewPager);

        /* plays a sound without blocking the app's execution */
        SoundThread soundThread = new SoundThread(this, R.raw.instafrenchecho);
        soundThread.start();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu: add items to the action bar. */
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            //TODO: hebben we settings wel nodig?
            case R.id.action_settings: {
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blazeit);
                //mCanvasView.addDraggable(new CanvasDraggableItem(bitmap, 100, 100));
                //mOverlayAdapter.addItem(new CanvasDraggableItem(bitmap,));
                //mCanvasView.invalidate();
                return true;
            }

            /* Adds a filter to the list. */
            case R.id.action_add: {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");

                if (prev != null)
                    fragmentTransaction.remove(prev);

                fragmentTransaction.addToBackStack(null);

                /* Show the filter-dialog. */
                /*FilterDialog dialog = new FilterDialog(this, mFilterAdapter.getItems());
                dialog.setOnAddFilterListener(this);
                mDialog = dialog;
                mDialog.show(fragmentTransaction, "dialog");*/
                DialogFragment dialog = null;
                int curItem = mViewPager.getCurrentItem();
                if (curItem == ListViewPagerAdapter.PAGE_FILTERS) {
                    dialog = new FilterDialog(this, mFilterAdapter.getItems());
                    ((FilterDialog)dialog).setOnAddFilterListener(this);
                } else if (curItem == ListViewPagerAdapter.PAGE_OVERLAYS) {
                    dialog = new OverlayDialog(this);
                    ((OverlayDialog)dialog).setOnAddOverlayListener(this);
                }
                mDialog = dialog;
                mDialog.show(fragmentTransaction, "dialog");
                return true;
            }

            /* Take a photo with the camera. */
            case R.id.action_take_photo: {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                /* Create a folder to store the pictures if it does not exist yet. */
                File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "InstaSwaggify/Original Pictures");
                if (imagesFolder.exists() == false) {
                    if (imagesFolder.mkdirs() == false) {
                        return true;
                    }
                }

                /* Get the current time and date to use in the filename. */
                Date now = new Date();
                SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

                String date = simpleFormat.format(now);

                File image = new File(imagesFolder, date + ".jpg");
                mImageUri = Uri.fromFile(image);

                /* The intent is started. */
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                return true;
            }

            /* Select a photo from the filesystem. */
            case R.id.action_select_photo: {
                Intent pickPic_intent = new Intent();
                pickPic_intent.setType("image/*");
                pickPic_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickPic_intent, "Select Picture"), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);

                return true;
            }

            case R.id.action_favorites: {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                    fragmentTransaction.remove(prev);
                fragmentTransaction.addToBackStack(null);

                mDialog = new FavoritesDialog();
                mDialog.show(fragmentTransaction, "dialog");

                return true;
            }

            case R.id.action_add_favorite: {
                SavePresetDialog dialog = new SavePresetDialog();
                dialog.setAdapter(mFilterAdapter);
                dialog.show(getFragmentManager(), "Save Preset");
                return true;
            }

            case R.id.action_undo: {
                mFilterAdapter.undo();
                return true;
            }

            case R.id.action_save_picture: {
                mExportDialog.setShare(false);
                mExportDialog.show(getFragmentManager(), "Export Dialog");
                return true;

            }

            case R.id.action_share: {
                mExportDialog.setShare(true);
                mExportDialog.show(getFragmentManager(), "Share Dialog");
                return true;
            }

            case R.id.action_clear: {
                mFilterAdapter.clearFilters();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Get image taken by the camera. */
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    /* The image is converted to a bitmap and send to the FilterHelper object. */
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    mRSFilterHelper.setBitmap(bitmap, true);
                    updateImage(mFilterAdapter.getItems());
                }
                catch (Exception e) {
                }
            }
            else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, "Could not capture image", Toast.LENGTH_SHORT).show();
            }
        }

        /* Get image selected from the file-system. */
        else if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mImageUri = data.getData();
                try {
                    /* The image is converted to a bitmap and send to the FilterHelper object. */
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    mRSFilterHelper.setBitmap(bitmap, true);
                    updateImage(mFilterAdapter.getItems());
                }
                catch (Exception e) {
                }
            }
            else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(this, "Could not select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Sets the filter list as this preset. */
    public void setFilter(String fav_key) {
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        String favoritesString = prefs.getString("Favorites", "");
        JSONObject favoritesObject = null;
        JSONObject jsonFilter = null;
        JSONArray favortiesArray = null;
        String filterId = "";
        List<IFilter> filterArray = new ArrayList<IFilter>();
        AbstractFilterClass.FilterID id;
        IFilter filter = null;

        try {
            favoritesObject = new JSONObject(favoritesString);
            favortiesArray = favoritesObject.getJSONArray(fav_key);

            for (int i = 0; i < favortiesArray.length(); i++) {
                jsonFilter = new JSONObject(favortiesArray.get(i).toString());
                filterId = jsonFilter.getString("id");
                id = AbstractFilterClass.FilterID.valueOf(filterId);

                /* create according filter and add to filter list */
                switch (id) {
                    case BRIGHTNESS:
                        filter = new BrightnessFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case CONTRAST:
                        filter = new ContrastFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case GAUSSIAN:
                        filter = new GaussianBlurFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case ROTATION:
                        filter = new RotationFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case SATURATION:
                        filter = new SaturationFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case SEPIA:
                        filter = new SepiaFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filter.setValue(1, jsonFilter.getInt("value1"));
                        filterArray.add(filter);
                        break;
                    case NOISE:
                        filter = new NoiseFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filterArray.add(filter);
                        break;
                    case INVERT:
                        filter = new InvertColorsFilter();
                        filterArray.add(filter);
                        break;
                    case COLORIZE:
                        filter = new ColorizeFilter();
                        filter.setValue(0, jsonFilter.getInt("value0"));
                        filter.setValue(1, jsonFilter.getInt("value1"));
                        filter.setValue(2, jsonFilter.getInt("value2"));
                        filterArray.add(filter);
                        break;
                }
            }
            mFilterAdapter.setItems(filterArray);
            mFilterAdapter.updateList();
            mDialog.dismiss();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUndoState(boolean state) {
        mMenu.findItem(R.id.action_undo).setEnabled(state);
    }

    @Override
    public void updateImage(List<IFilter> filters) {
        mRSFilterHelper.generateBitmap(filters, this);
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (imageUri == null)
            return;

        try {
            /* The image is converted to a bitmap and send to the FilterHelper object. */
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            mRSFilterHelper.setBitmap(bitmap, true);
            updateImage(mFilterAdapter.getItems());
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error occurred while opening picture",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnAddOverlayListener(int resourceId) {
        mDialog.dismiss();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        CanvasDraggableItem overlay = new CanvasDraggableItem(bitmap, mCanvasView.getWidth() / 2, mCanvasView.getHeight() / 2);
        mOverlayAdapter.addItem(overlay);
    }

    @Override
    public void OnAddFilterListener(IFilter filter) {
        mDialog.dismiss();
        mFilterAdapter.addItem(filter);
    }

    public List<IFilter> getAdapterItems () {
        return mFilterAdapter.getItems();
    }
}
