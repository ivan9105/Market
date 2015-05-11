package parcsys.com.utils;

import parcsys.com.entity.SoldItem;
import parcsys.com.utils.dao.Dao;

/**
 * Created by Иван on 05.04.2015.
 */
public class DaoStaticUtils {
    private static Dao<SoldItem> dao;

    public DaoStaticUtils(Dao<SoldItem> dao) {
        DaoStaticUtils.dao = dao;
    }

    public static Dao<SoldItem> getDao() {
        return dao;
    }
}
