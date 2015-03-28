package parcsys.com.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Иван on 28.03.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "market_db";

    private String DB_DIRECTORY = "db";
    private File dbDirectory;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);

        ClassLoader classLoader = getClass().getClassLoader();
        dbDirectory = new File(classLoader.getResource(DB_DIRECTORY).getFile());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS DB_LOG (" +
                    "CREATE_TS datetime," +
                    "CREATED_BY text," +
                    "SCRIPT_NAME text" +
                    ");");


        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
