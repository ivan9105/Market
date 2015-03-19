package parcsys.com.utils;

import java.text.DecimalFormat;

/**
 * Created by Иван on 19.03.2015.
 */
public class FormatterHelper {
    public static String doubleFormat(double price) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(price);
    }
}
