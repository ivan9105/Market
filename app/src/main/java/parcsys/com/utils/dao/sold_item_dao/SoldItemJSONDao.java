package parcsys.com.utils.dao.sold_item_dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.utils.dao.Dao;

/**
 * Created by Иван on 09.04.2015.
 */
public class SoldItemJSONDao implements Dao<SoldItem>{

    private SQLiteDatabase db;

    public SoldItemJSONDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void addItem(SoldItem item) {
        ContentValues cv = null;

        try {
            cv = getContentValues(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.beginTransaction();
        try {
            db.insert("SOLD_ITEM_JSON_TABLE", null, cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void updateItem(SoldItem item) {
        ContentValues cv = null;
        try {
            cv = getContentValues(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.beginTransaction();
        try {
            db.update("SOLD_ITEM_JSON_TABLE", cv, "id = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void removeItem(SoldItem item) {
        db.beginTransaction();
        try {
            db.delete("SOLD_ITEM_JSON_TABLE", "id = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<SoldItem> getItems() {
        List<SoldItem> items = new ArrayList<SoldItem>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query("SOLD_ITEM_JSON_TABLE", new String[]{"ID", "JSON"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String json = cursor.getString(cursor.getColumnIndex("JSON"));
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    SoldItem item = new SoldItem();
                    try {
                        JSONObject object = new JSONObject(json);
                        String title = object.getString("title");
                        Integer amount = object.getInt("amount");
                        Double price = object.getDouble("price");
                        String type = object.getString("type");

                        item.setId(UUID.fromString(id));
                        item.setTitle(title);
                        item.setPrice(price);
                        item.setAmount(amount);
                        item.setType(SoldDestinationType.getById(type));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    items.add(item);

                    cursor.moveToNext();
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return items;
    }

    @Override
    public boolean buyItem(SoldItem item) {
        boolean result = true;

        db.beginTransaction();
        try {
            Cursor isExistCursor = db.rawQuery("SELECT id, AMOUNT FROM SOLD_ITEM_TABLE WHERE id = ?", new String[]{item.getId().toString()});
            if (isExistCursor.getCount() > 0) {

                int amount = 0;
                isExistCursor.moveToFirst();
                do {
                    amount = isExistCursor.getInt(isExistCursor.getColumnIndex("AMOUNT"));
                } while (isExistCursor.moveToNext());

                if (amount > 0) {
                    if (amount == 1) {
                        removeItem(item);
                    } else {
                        item.setAmount(amount - 1);
                        updateItem(item);
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }

            isExistCursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    private ContentValues getContentValues(SoldItem item) throws JSONException {
        ContentValues cv = new ContentValues();
        JSONObject object = new JSONObject();
        object.put("amount", item.getAmount());
        object.put("title", item.getTitle());
        object.put("price", item.getPrice());
        object.put("type", item.getType().getId());

        cv.put("ID", item.getId().toString());
        cv.put("JSON", object.toString());
        return cv;
    }
}
