package org.opensuse.dice.yourstorydice;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class NewDieActivity extends AppCompatActivity {
    private FeedYourStoryDiceDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbHelper = new FeedYourStoryDiceDbHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_die);
    }

    /**
     * Saves the image drawn in the canvas. Informs the user that it was correctly saved or there
     * was an error and then restarts the canvas. It also checks that the media is available and the
     * storage permissions (asks the user to enable them if needed).
     */
    public void saveDie(View view) {
        if (externalStorageAvailability() == 2 && hasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            storeImage();
        }
    }

    /**
     * Checks if external storage is available for read and/or write.
     *
     * @return <code>0</code> if external storage is not available, <code>1</code> if it is only
     * available for reading and <code>2</code> if it is available for both reading and writing.
     */
    private int externalStorageAvailability() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return 2;
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            return 1;
        return 0;
    }

    private boolean hasPermissions(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result array is empty
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Snackbar.make(findViewById(R.id.canvas), getString(R.string.error_save_permissions), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.d("callback", "You have no permissions to save the image, please enable them");
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void storeImage() {
        DrawingDiceView canvas = (DrawingDiceView) findViewById(R.id.canvas);

        try {
            File file = new File(getStorageDir(), UUID.randomUUID().toString() + ".png");

            FileOutputStream out = new FileOutputStream(file);
            canvas.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // Make the file available to the user immediately
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);

            storeDiceInDb(file);

            Snackbar.make(findViewById(R.id.canvas), getString(R.string.successfully_saved) + " " + file.getPath(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            Log.d("storeImage", "Your image was successfully saved in: " + file.getPath());
            canvas.clean();
        } catch (IOException e) {
            Snackbar.make(findViewById(R.id.canvas), getString(R.string.error_save_fails), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            Log.d("storeImage", "Something went wrong, your image couldn't be saved");
        }
    }

    private void storeDiceInDb(File file) {
        SQLiteDatabase writable_db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedDiceTable.FeedEntry.COLUMN_NAME_FILE_NAME, file.getName());
        values.put(FeedDiceTable.FeedEntry.COLUMN_NAME_TYPE, "custom");
        // FIXME: store md5sum

        writable_db.insert(FeedDiceTable.FeedEntry.TABLE_NAME, null, values);
    }

    // FIXME: Move to helper class
    public static File getStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourStoryDice");
        file.mkdirs();
        return file;
    }
}
