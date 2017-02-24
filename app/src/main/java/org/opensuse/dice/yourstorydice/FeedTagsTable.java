package org.opensuse.dice.yourstorydice;

import android.provider.BaseColumns;

public final class FeedTagsTable {
    // To avoid having instances of the class
    private FeedTagsTable() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        // String
        public static final String COLUMN_NAME_TAG_NAME = "tag_name";
    }
}
