package org.opensuse.dice.yourstorydice;

import android.provider.BaseColumns;

public final class FeedDiceTable {
    // To avoid having instances of the class
    private FeedDiceTable() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "dice";
        // String
        public static final String COLUMN_NAME_FILE_NAME = "file_name";
        // String
        public static final String COLUMN_NAME_MD5 = "md5sum";
        // Values: "default", "custom"
        public static final String COLUMN_NAME_TYPE = "type";
    }
}
