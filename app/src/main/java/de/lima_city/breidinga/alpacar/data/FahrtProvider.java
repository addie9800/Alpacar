package de.lima_city.breidinga.alpacar.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry;

import static android.R.attr.id;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry.COLUMN_ABFAHRTSORT;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry.COLUMN_ANKUNFTSORT;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry.COLUMN_DATUM;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry.COLUMN_FREIE_PLAETZE;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry.TABLE_NAME;

/**
 * Created by Addie on 13.09.2017.
 */

public class FahrtProvider extends ContentProvider {

    private Database db;
    public static final int FAHRT = 100;
    public static final int FAHRT_ID = 101;
    public static final String LOG_TAG = FahrtProvider.class.getSimpleName();
    public static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(FahrtContract.CONTENT_AUTHORITY, FahrtContract.PATH_FAHRT, FAHRT);
        matcher.addURI(FahrtContract.CONTENT_AUTHORITY, FahrtContract.PATH_FAHRT + "/#", FAHRT_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        // This cursor will hold the result of the query
        Cursor cursor;
        // Get readable database
        SQLiteDatabase database = db.getReadableDatabase();


        // Figure out if the URI matcher can match the URI to a specific code
        int match = matcher.match(uri);
        switch (match) {
            case FAHRT:

                cursor = database.query(FahrtContract.FahrtEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAHRT_ID:
                selection = FahrtContract.FahrtEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(FahrtContract.FahrtEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        String abfahrtsOrt = contentValues.getAsString(COLUMN_ABFAHRTSORT);
        if (abfahrtsOrt.isEmpty() || abfahrtsOrt == null){
            throw new IllegalArgumentException("Es wird ein Abfahrtsort benötigt");
        }
        String ankunftsOrt = contentValues.getAsString(COLUMN_ANKUNFTSORT);
        if (ankunftsOrt.isEmpty() || ankunftsOrt == null){
            throw new IllegalArgumentException("Es wird ein Ankunftsort benötigt");
        }
        Integer sitze = contentValues.getAsInteger(COLUMN_FREIE_PLAETZE);
        if (sitze == null || sitze < 0){
            throw new IllegalArgumentException("Es wird eine Sitzangabe benötigt");
        }
        String date = contentValues.getAsString(COLUMN_DATUM);
        if(date == null){
            throw new IllegalArgumentException("Es wird ein Datum benötigt");
        }
        if (contentValues.size() == 0){
            return 0;
        }
        final int match = matcher.match(uri);
        switch (match) {
            case FAHRT:
                return updateFahrt(uri, contentValues, s, strings);
            case FAHRT_ID:
                s = FahrtEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFahrt(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case FAHRT:
                return FahrtEntry.CONTENT_LIST_TYPE;
            case FAHRT_ID:
                return FahrtEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = db.getWritableDatabase();

        final int match = matcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case FAHRT:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(TABLE_NAME, s, strings);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case FAHRT_ID:
                // Delete a single row given by the ID in the URI
                s = FahrtEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, s, strings);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
    }

    @Override
    public boolean onCreate() {
        db = new Database(getContext());
        return true;
    }
    private Uri insertFahrt(Uri uri, ContentValues values){
        SQLiteDatabase pdb = db.getWritableDatabase();
        pdb.insert(FahrtEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    private int updateFahrt(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase pdb = db.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        return pdb.update(TABLE_NAME, values, selection, selectionArgs);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String abfahrtsOrt = contentValues.getAsString(COLUMN_ABFAHRTSORT);
        if (abfahrtsOrt.isEmpty() || abfahrtsOrt == null){
            throw new IllegalArgumentException("Es wird ein Abfahrtsort benötigt");
        }
        String ankunftsOrt = contentValues.getAsString(COLUMN_ANKUNFTSORT);
        if (ankunftsOrt.isEmpty() || ankunftsOrt == null){
            throw new IllegalArgumentException("Es wird ein Ankunftsort benötigt");
        }
        Integer sitze = contentValues.getAsInteger(COLUMN_FREIE_PLAETZE);
        if (sitze == null || sitze < 0){
            throw new IllegalArgumentException("Es wird eine Sitzangabe benötigt");
        }
        String date = contentValues.getAsString(COLUMN_DATUM);
        if(date == null){
            throw new IllegalArgumentException("Es wird ein Datum benötigt");
        }
        final int match = matcher.match(uri);
        switch (match) {
            case FAHRT:
                return insertFahrt(uri, contentValues);
            case FAHRT_ID:
                String path = uri.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                int id = Integer.parseInt(idStr);
                String[] selectionArgs = {Integer.toString(id)};
                updateFahrt(uri, contentValues, "_id=?", selectionArgs);
                return uri;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
}
