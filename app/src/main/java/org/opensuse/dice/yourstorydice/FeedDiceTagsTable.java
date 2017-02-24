package org.opensuse.dice.yourstorydice;

import android.provider.BaseColumns;

public class FeedDiceTagsTable {
    // To avoid having instances of the class
    private FeedDiceTagsTable() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "dice_tags";
        // Relationship
        public static final String COLUMN_NAME_RELATION_TAGS = FeedTagsTable.FeedEntry.TABLE_NAME + "_ID";
        // Relationship
        public static final String COLUMN_NAME_RELATION_DICE = FeedDiceTable.FeedEntry.TABLE_NAME + "_ID";
    }
}
