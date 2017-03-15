package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.R;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieDataContract;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieDataDBHelper;


public class MovieDataContentProvider extends ContentProvider {

    public static final int MOVIES = 100;

    public static final int FAVORITES = 200;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDataDBHelper movieDataDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDataDBHelper = new MovieDataDBHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = movieDataDBHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case MOVIES:
                retCursor =  db.query(MovieDataContract.MovieDataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES:
                retCursor =  db.query(MovieDataContract.MovieDataEntry.TABLE_FAVORITE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_error) + uri);
        }


        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException(getContext().getString(R.string.notimplemented_error));
    }



    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = movieDataDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        long id = 0;
        switch (match) {
            case MOVIES:

                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieDataContract.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.insert_error) + uri);
                }
                break;
            case FAVORITES:
                try {


                    id = db.insert(MovieDataContract.MovieDataEntry.TABLE_FAVORITE, null, values);

                }catch(SQLiteException e){

                }

                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieDataContract.CONTENT_URI_FAVORITES, id);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.insert_error) + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_error) + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {


        final SQLiteDatabase db = movieDataDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch (match) {

            case MOVIES:

                moviesDeleted = db.delete(MovieDataContract.MovieDataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:

                moviesDeleted = db.delete(MovieDataContract.MovieDataEntry.TABLE_FAVORITE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_error) + uri);
        }


        if (moviesDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {

        final SQLiteDatabase db = movieDataDBHelper.getWritableDatabase();


        int match = sUriMatcher.match(uri);
        int returnCount=0; // URI to be returned

        switch (match) {
            case MOVIES:

                if ( whereArgs[0] != null ){
                    returnCount = db.update(MovieDataContract.MovieDataEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                    if ( returnCount < 0 )
                        throw new android.database.SQLException(getContext().getString(R.string.update_error) + uri);
                }
                break;
            case FAVORITES:

                if ( whereArgs[0] != null ){
                    returnCount = db.update(MovieDataContract.MovieDataEntry.TABLE_FAVORITE, contentValues, whereClause, whereArgs);
                    if ( returnCount < 0 )
                        throw new android.database.SQLException(getContext().getString(R.string.update_error) + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_error) + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnCount;

    }


    public static UriMatcher buildUriMatcher() {


        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieDataContract.AUTHORITY, MovieDataContract.PATH_MOVIES, MOVIES);
        //uriMatcher.addURI(MovieDataContract.AUTHORITY, MovieDataContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        uriMatcher.addURI(MovieDataContract.AUTHORITY, MovieDataContract.PATH_FAVORITES, FAVORITES);


        return uriMatcher;
    }


    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDataDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                db.beginTransaction();

                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieDataContract.MovieDataEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
