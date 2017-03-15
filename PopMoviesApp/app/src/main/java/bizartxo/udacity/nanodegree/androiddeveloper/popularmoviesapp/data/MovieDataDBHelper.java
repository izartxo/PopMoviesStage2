package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieDataContract;


public class MovieDataDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "PopularMoviesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    MovieDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + MovieDataContract.MovieDataEntry.TABLE_NAME + " (" +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_ID                + " TEXT PRIMARY KEY, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE    + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_TYPE    + " TEXT NOT NULL" + ");";

        db.execSQL(CREATE_TABLE);

        final String CREATE_TABLE_FAVORITE = "CREATE TABLE "  + MovieDataContract.MovieDataEntry.TABLE_FAVORITE + " (" +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_ID                + " TEXT PRIMARY KEY, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE    + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE   + " TEXT NOT NULL, " +
                MovieDataContract.MovieDataEntry.COLUMN_NAME_TYPE    + " TEXT NOT NULL" + ");";

        db.execSQL(CREATE_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDataContract.MovieDataEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieDataContract.MovieDataEntry.TABLE_FAVORITE);

        onCreate(db);
    }
}
