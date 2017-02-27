package org.opensuse.dice.yourstorydice;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;


public class GalleryActivity extends AppCompatActivity {
    long[] dieImageIds = {1, 3};
    Integer[] defaultDice = {
            R.drawable.default1,
            R.drawable.default2,
            R.drawable.default3,
            R.drawable.default4,
            R.drawable.default5,
            R.drawable.default6,
            R.drawable.default7,
            R.drawable.default8,
            R.drawable.default9,
            R.drawable.default10,
            R.drawable.default11,
            R.drawable.default12,
            R.drawable.default13,
            R.drawable.default14,
            R.drawable.default15,
            R.drawable.default16,
            R.drawable.default17,
            R.drawable.default18,
            R.drawable.default19,
            R.drawable.default20,
            R.drawable.default21,
            R.drawable.default22,
            R.drawable.default23
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GalleryActivity.GalleryAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id){
                Log.d("bla", "bla");
            }

        });
    }

    private class GalleryAdapter extends BaseAdapter {
        private Context mContext;
        FeedYourStoryDiceDbHelper mDbHelper;

        public GalleryAdapter(Context context) {
            mContext = context;
            mDbHelper = new FeedYourStoryDiceDbHelper(GalleryActivity.this);
        }

        public int getCount() {
            return dieImageIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new Dice for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            // Set the size of the ImageView
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            //Resize the image to match the ImageView
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            setDiceImageView(position, imageView, dieImageIds);

            return imageView;
        }

        private void setDiceImageView(int position, ImageView imageView, long[] diceImages) {
            Log.d("bla:", "bla1");
            SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
            long customDiceCount = DatabaseUtils.queryNumEntries(readableDb, FeedDiceTable.FeedEntry.TABLE_NAME);

            if (diceImages[position] < customDiceCount) {
                Log.d("bla:", "bla2");
                Cursor cursor = readableDb.rawQuery(
                        "SELECT * FROM " + FeedDiceTable.FeedEntry.TABLE_NAME +
                                " LIMIT 1 OFFSET " + String.valueOf(diceImages[position])
                        , null
                );

                cursor.moveToFirst();
                String fileName = cursor.getString(cursor.getColumnIndex(FeedDiceTable.FeedEntry.COLUMN_NAME_FILE_NAME));

                File path = NewDieActivity.getStorageDir();
                File file = new File(path, fileName);
                Drawable drawable = Drawable.createFromPath(file.getPath());
                imageView.setImageDrawable(drawable);
            } else {
                Log.d("bla:", "bla3");
                int index = (int) (diceImages[position] - customDiceCount);
                imageView.setImageResource(defaultDice[index]);
            }
        }

    }
}
