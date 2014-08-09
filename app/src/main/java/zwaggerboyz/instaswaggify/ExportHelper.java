package zwaggerboyz.instaswaggify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    ExportHelper.java
 * This file contains the dialog that is shown when exporting a photo.
 */

public class ExportHelper {
    private final Context mContext;
    private boolean mShare = true;
    private SavePictureTask mAsyncTask = null;
    private Bitmap mBitmap;

    public ExportHelper(Context context) {
        mContext = context;
    }

    public void exportPicture (boolean share, Bitmap bitmap) {
        mShare = share;
        mBitmap = bitmap;

        if (mAsyncTask != null && mAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
            return;

        mAsyncTask = new SavePictureTask();
        mAsyncTask.execute();
    }

    private class SavePictureTask extends AsyncTask<Void, Void, Uri> {
        ProgressDialog progressDialog;

        @Override
        protected Uri doInBackground(Void... params) {
            int count = 1;

            if (count == 0) {
                return null;
            }
            else {
                Uri result =  savePicture();
                return result;
            }

        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (isCancelled())
                return;

            if (uri == null)
                Toast.makeText(ExportHelper.this.mContext, "Something went wrong, trying again ain't gonna fix it...", Toast.LENGTH_SHORT).show();

            if (mShare) {
                Intent share = new Intent(Intent.ACTION_SEND);

                if (uri == null)
                    return;

                share.setType("image/png");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                mContext.startActivity(Intent.createChooser(share, "Share Image"));
            } else {
                Toast.makeText(mContext, "Picture successfully exported", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Synchronizing, please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
            }
        }

        protected Uri savePicture() {
            File folder, file;
            FileOutputStream output;
            String state = Environment.getExternalStorageState();
            String extension = ".png";
            Uri fileUri;

            boolean externalIsAvailable = true;

            if(mBitmap == null)
                return null;

            if (!Environment.MEDIA_MOUNTED.equals(state))
                externalIsAvailable = false;

            /* Try to open a file to export the picture. */
            try {

                /* filename is made with a timestamp */
                SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                String date = s.format(new Date());

                if (!externalIsAvailable)
                    folder = new File("InstasSwaggify");
                else
                    folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "InstaSwaggify");

                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        return null;
                    }
                }

                folder = new File(folder, "Swaggified pictures");
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        return null;
                    }
                }

                file = new File(folder, date + extension);
                if (!file.exists()) {
                    file.createNewFile();
                }
                else {
                    return null;
                }

                fileUri = Uri.fromFile(file);
                output = new FileOutputStream(file);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
            /* The media scanner has to scan the newly made image, for it to be visible
             * in the pictures folder.
             */

                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                MediaScannerConnection.scanFile(mContext,
                        new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {

                            public void onScanCompleted(String path, Uri uri) {
                            }
                        }
                );
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            finally {
                try {
                    output.flush();
                    output.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return fileUri;
        }

    }

}