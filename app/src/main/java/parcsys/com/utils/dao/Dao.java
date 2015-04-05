package parcsys.com.utils.dao;

import java.util.List;

/**
 * Created by Иван on 05.04.2015.
 */
public interface Dao<T> {
    public void addItem(T item);
    public void updateItem(T item);
    public void removeItem(T item);
    public List<T> getItems();
}
