package com.example.th03;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FormatUtils {
    private static final DecimalFormat MONEY;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        MONEY = new DecimalFormat("#,##0", symbols);
    }

    private FormatUtils() {}

    public static String money(long value) {
        return MONEY.format(value) + " đ";
    }
}
