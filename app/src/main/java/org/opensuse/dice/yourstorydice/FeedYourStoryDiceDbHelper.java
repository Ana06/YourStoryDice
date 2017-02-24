package org.opensuse.dice.yourstorydice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * SQL Lite Helper for the test database.
 *
 * @author Ana María Martínez Gómez (@Ana06), Björn Geuken (@bgeuken)
 * @see FeedYourStoryDiceDbHelper, FeedTagsTable, FeedTagsTable
 */
public class FeedYourStoryDiceDbHelper extends SQLiteOpenHelper {
    // Increment version if schema changes
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "YouStoryDice.db";
    private static final String COMMA_SEP = ", ";
    private static final String STRING_TYPE = " STRING";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " INTEGER PRIMARY KEY";
    private static final String SQL_CREATE_DICE_TABLE =
            "CREATE TABLE " + FeedDiceTable.FeedEntry.TABLE_NAME + "(" +
                    FeedDiceTable.FeedEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    FeedDiceTable.FeedEntry.COLUMN_NAME_FILE_NAME     + STRING_TYPE + COMMA_SEP +
                    FeedDiceTable.FeedEntry.COLUMN_NAME_MD5           + STRING_TYPE + COMMA_SEP +
                    FeedDiceTable.FeedEntry.COLUMN_NAME_TYPE          + STRING_TYPE +
            ")";
    private static final String SQL_CREATE_TAGS_TABLE =
            "CREATE TABLE " + FeedTagsTable.FeedEntry.TABLE_NAME + "(" +
                    FeedTagsTable.FeedEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    FeedTagsTable.FeedEntry.COLUMN_NAME_TAG_NAME + STRING_TYPE +
            ")";
    private static final String SQL_CREATE_DICE_TAGS_TABLE =
            "CREATE TABLE " + FeedDiceTagsTable.FeedEntry.TABLE_NAME + "(" +
                    FeedDiceTagsTable.FeedEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    FeedDiceTagsTable.FeedEntry.COLUMN_NAME_RELATION_DICE + INTEGER_TYPE + COMMA_SEP +
                    FeedDiceTagsTable.FeedEntry.COLUMN_NAME_RELATION_TAGS + INTEGER_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + FeedDiceTagsTable.FeedEntry.COLUMN_NAME_RELATION_DICE + ") REFERENCES " +
                        FeedDiceTable.FeedEntry.TABLE_NAME + "(" + FeedDiceTable.FeedEntry._ID + ")" +  COMMA_SEP +
                    "FOREIGN KEY(" + FeedDiceTagsTable.FeedEntry.COLUMN_NAME_RELATION_TAGS + ") REFERENCES " +
                        FeedTagsTable.FeedEntry.TABLE_NAME + "(" + FeedTagsTable.FeedEntry._ID + ")" +
             ")";
    private static final String SQL_DELETE_DICE =
            "DROP TABLE IF EXISTS " + FeedDiceTable.FeedEntry.TABLE_NAME;
    private static final String SQL_DELETE_TAGS =
            "DROP TABLE IF EXISTS " + FeedTagsTable.FeedEntry.TABLE_NAME;
    private static final String SQL_DELETE_DICE_TAGS =
            "DROP TABLE IF EXISTS " + FeedDiceTagsTable.FeedEntry.TABLE_NAME;

    /**
     * Class constructor to call parent constructor with the proper params
     *
     * @param context Its context
     * @see SQLiteOpenHelper#SQLiteOpenHelper(android.content.Context, java.lang.String, android.database.sqlite.SQLiteDatabase.CursorFactory, int)
     */
    public FeedYourStoryDiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DICE_TABLE);
        db.execSQL(SQL_CREATE_TAGS_TABLE);
        db.execSQL(SQL_CREATE_DICE_TAGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_DICE_TAGS);
        db.execSQL(SQL_DELETE_DICE);
        db.execSQL(SQL_DELETE_TAGS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
