package parcsys.com.utils.dao.sold_item_dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.utils.DBHelper;
import parcsys.com.utils.dao.Dao;

/**
 * Created by Иван on 05.04.2015.
 */
public class SoldItemDbDao implements Dao<SoldItem> {
    private SQLiteDatabase db;

    public SoldItemDbDao(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void addItem(SoldItem item) {
        ContentValues cv = getContentValues(item);

        db.beginTransaction();
        try {
            db.insert("SOLD_ITEM_TABLE", null, cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void updateItem(SoldItem item) {
        ContentValues cv = getContentValues(item);

        db.beginTransaction();
        try {
            db.update("SOLD_ITEM_TABLE", cv, "id = ?", new String[] {String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void removeItem(SoldItem item) {
        db.beginTransaction();
        try {
            db.delete("SOLD_ITEM_TABLE", "id = ?", new String[] {String.valueOf(item.getId())});
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
            Cursor cursor = db.query("SOLD_ITEM_TABLE", new String[]{"ID", "TITLE", "AMOUNT", "PRICE", "TYPE"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    Integer amount = cursor.getInt(cursor.getColumnIndex("AMOUNT"));
                    Double price = cursor.getDouble(cursor.getColumnIndex("PRICE"));
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));

                    SoldItem soldItem = new SoldItem();
                    soldItem.setId(UUID.fromString(id));
                    soldItem.setType(SoldDestinationType.getById(type));
                    soldItem.setAmount(amount);
                    soldItem.setTitle(title);
                    soldItem.setPrice(price);

                    items.add(soldItem);

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

    private ContentValues getContentValues(SoldItem item) {
        ContentValues cv = new ContentValues();
        cv.put("ID", item.getId().toString());
        cv.put("AMOUNT", item.getAmount());
        cv.put("PRICE", item.getPrice());
        cv.put("TYPE", item.getType().getId());
        cv.put("TITLE", item.getTitle());
        return cv;
    }
}
