package org.opensuse.dice.yourstorydice;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class GalleryListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);

        ListView listView = (ListView) findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                FeedYourStoryDiceDbHelper mDbHelper = new FeedYourStoryDiceDbHelper(GalleryActivity.this);
                SQLiteDatabase readableDb = mDbHelper.getReadableDatabase();
                long totalItems = DatabaseUtils.queryNumEntries(readableDb, FeedDiceTable.FeedEntry.TABLE_NAME);

                switch(position){
                    case 1:
                        long[] foo = new long[];
                        [1..totalItems]
                        break;
                    case 2:
                        break;
                    default:


                }
                Intent intent = new Intent(GalleryListActivity.this, GalleryActivity.class);
                intent.putExtra("images", images_array);
                startActivity(intent);
            }
        });
    }
}
