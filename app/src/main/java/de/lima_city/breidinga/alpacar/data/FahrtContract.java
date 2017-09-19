package de.lima_city.breidinga.alpacar.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static de.lima_city.breidinga.alpacar.data.FahrtContract.CONTENT_AUTHORITY;
import static de.lima_city.breidinga.alpacar.data.FahrtContract.PATH_FAHRT;

/**
 * Created by Addie on 13.09.2017.
 */

public final class FahrtContract {
    public static final String CONTENT_AUTHORITY = "de.lima_city.breidinga.alpacar";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAHRT = "fahrt";

    public static final class FahrtEntry implements BaseColumns {
        public static final String TABLE_NAME = "fahrt";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DATUM = "date";
        public static final String COLUMN_ABFAHRTSORT = "departure";
        public static final String COLUMN_ANKUNFTSORT = "arrival";
        public static final String COLUMN_FREIE_PLAETZE = "seats";
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAHRT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAHRT;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAHRT);
    }
}