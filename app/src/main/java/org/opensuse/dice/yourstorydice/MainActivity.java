package org.opensuse.dice.yourstorydice;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.view.ViewGroup;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FeedYourStoryDiceDbHelper mDbHelper;
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
    int numDice = 4;
    long[] dieImageIds;
    long customDiceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDbHelper = new FeedYourStoryDiceDbHelper(this);

        if (savedInstanceState == null) {
            dieImageIds = getRandomDiceNumbers();
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new DiceAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id){
                dice();
            }

        });
    }

    @Override
    public void onDestroy() {
        SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
        readableDb.close();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);

        instanceState.putLong("customDiceCount", customDiceCount);
        instanceState.putLongArray("dieImageIds", dieImageIds);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            dieImageIds = savedInstanceState.getLongArray("dieImageIds");
            customDiceCount = savedInstanceState.getLong("customDiceCount");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_die) {
            intent = new Intent(this, NewDieActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            intent = new Intent(this, GalleryListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_information) {
            intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void setDiceImageView(int position, ImageView imageView, long[] diceImages) {
        if (diceImages[position] < customDiceCount) {
            SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
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
            int index = (int) (diceImages[position] - customDiceCount);
            imageView.setImageResource(defaultDice[index]);
        }
    }

    /**
     * Fetch a number of dice and render them in the activity.
     * Dice images get stored internally for later usage.
     *
     */
    private void dice() {
        dieImageIds = getRandomDiceNumbers();
        drawDice(dieImageIds);
    }

    /**
     * Draw a given number of dice images
     *
     * @param dieImageIds Array of die image ids
     */
    private void drawDice(long[] dieImageIds) {
        GridView gridview = (GridView) findViewById(R.id.gridview);

        for (int position = 0; position < numDice; position++) {
            ImageView imageView = (ImageView) gridview.getChildAt(position);
            setDiceImageView(position, imageView, dieImageIds);
        }
    }

    /**
     * Get array of random numbers of dice images. Each number repesents either
     * a default or custom dice image.
     *
     * @return an array of numbers which represents the images to be used as dice
     */
    private long[] getRandomDiceNumbers() {
        Random random = new Random();
        SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();

        customDiceCount = DatabaseUtils.queryNumEntries(readableDb, FeedDiceTable.FeedEntry.TABLE_NAME);
        long totalDiceNum = defaultDice.length + customDiceCount;

        long[] fetchedNumbers = new long[numDice];
        for (int position = 0; position < numDice; position++) {
            long randomNum = Math.abs(random.nextLong() % totalDiceNum);
            for (int index = 0; index < position; index++) {
                if (randomNum == fetchedNumbers[index]) {
                    randomNum = Math.abs(random.nextLong() % totalDiceNum);
                    index = -1;
                }
            }
            fetchedNumbers[position] = randomNum;
        }

        return fetchedNumbers;
    }

    private class DiceAdapter extends BaseAdapter {
        private Context mContext;

        public DiceAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return numDice;
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
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            //Resize the image to match the ImageView
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setBackgroundResource(R.drawable.square_die);
            setDiceImageView(position, imageView, dieImageIds);

            return imageView;
        }
    }
}
