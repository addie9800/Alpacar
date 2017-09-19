package de.lima_city.breidinga.alpacar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.lima_city.breidinga.alpacar.data.FahrtContract.FahrtEntry;

/**
 * Created by Addie on 13.09.2017.
 */

public class Database extends SQLiteOpenHelper{
    private static final String databaseName = "db_373834_1.db";
    private static final int databaseVer = 1;
    public Database (Context context) {super(context, databaseName, null, databaseVer);}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create = "CREATE TABLE fahrt (" + FahrtContract.FahrtEntry.COLUMN_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," + FahrtEntry.COLUMN_ABFAHRTSORT + "TEXT NOT NULL," + FahrtEntry.COLUMN_ANKUNFTSORT + "TEXT NOT NULL," + FahrtEntry.COLUMN_DATUM + "DATE NOT NULL," + FahrtEntry.COLUMN_FREIE_PLAETZE + "INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(create);

    }
}
