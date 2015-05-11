package parcsys.com.utils.dao;

import java.util.List;

import parcsys.com.entity.SoldItem;

/**
 * Created by Иван on 05.04.2015.
 */
public interface Dao<T> {
    public void addItem(T item);
    public void updateItem(T item);
    public void removeItem(T item);
    public List<T> getItems();
    public boolean buyItem(T item);
}
