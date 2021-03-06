package parcsys.com.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Иван on 28.03.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DB_NAME = "market_db";

    private String DB_DIRECTORY = "db";

    public DBHelper(Context context, int DB_VERSION) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();

        try {
            AssetManager assetManager = context.getAssets();
            String[] fileList = assetManager.list(DB_DIRECTORY);
            for (String name : fileList) {
                boolean isExist = true;

                Cursor cursor = db.query("DB_LOG", new String[] {"SCRIPT_NAME"}, "SCRIPT_NAME = '" + name + "'", null, null, null, null);
                if (!cursor.moveToFirst()){
                    isExist = false;
                }
                cursor.close();

                if (!isExist) {
                    String script = null;
                    BufferedReader reader = null;

                    try {
                        reader = new BufferedReader(new InputStreamReader
                                (context.getAssets().open(DB_DIRECTORY + File.separator + name)));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while (((line = reader.readLine()) != null)) {
                            sb.append(line);
                        }
                        script = sb.toString();
                    } catch (FileNotFoundException e) {
                        Log.i("dbtag", e.toString());
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }

                    if (script != null && !script.equals("")) {
                        String[] scriptItems = script.split("\\^");
                        for (String scriptItem : scriptItems) {
                            db.execSQL(scriptItem);
                        }

                        ContentValues cv = new ContentValues();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        cv.put("CREATE_TS", dateFormat.format(DateUtil.now()));
                        cv.put("CREATED_BY", "admin");
                        cv.put("SCRIPT_NAME", name);

                        db.insert("DB_LOG", null, cv);
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.i("dbtag", e.toString());
        } finally {
            db.endTransaction();
        }
    }
}
