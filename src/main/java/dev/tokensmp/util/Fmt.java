package dev.tokensmp.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Fmt {

    private static final NumberFormat NF = NumberFormat.getIntegerInstance(Locale.US);
    private static final long MAX_AMOUNT = 1_000_000_000_000_000L;

    private Fmt() {}

    public static synchronized String num(long value) {
        return NF.format(value);
    }

    /** Parses "500", "1,500", "2k", "1.5m". Returns -1 when the input is not a valid positive amount. */
    public static long parseAmount(String input) {
        if (input == null || input.isBlank()) return -1;
        String s = input.trim().toLowerCase(Locale.ROOT).replace(",", "").replace("_", "");
        long mult = 1;
        if (s.endsWith("k")) {
            mult = 1_000;
            s = s.substring(0, s.length() - 1);
        } else if (s.endsWith("m")) {
            mult = 1_000_000;
            s = s.substring(0, s.length() - 1);
        }
        try {
            long value = new BigDecimal(s).multiply(BigDecimal.valueOf(mult)).longValueExact();
            return value > 0 && value <= MAX_AMOUNT ? value : -1;
        } catch (ArithmeticException | NumberFormatException e) {
            return -1;
        }
    }
}
