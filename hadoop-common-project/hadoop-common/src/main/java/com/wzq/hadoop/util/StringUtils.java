package com.wzq.hadoop.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * General String utils
 */
public class StringUtils {

    private static final DecimalFormat decimalFormat;

    static {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("#.##");
    }

    /**
     * Make a string representation of the exception
     *
     * @param e The exception to stringify
     * @return A string with exception name and call stack
     */
    public static String stringifyException(Throwable e) {
        StringWriter stm = new StringWriter();
        PrintWriter wrt = new PrintWriter(stm);
        e.printStackTrace(wrt);
        wrt.close();
        return stm.toString();
    }

    /**
     * Returns an arraylist of strings
     *
     * @param str the comma seperated string values
     * @return the arraylist of the comma seperated string values
     */
    public static String[] getStrings(String str) {
        Collection<String> values = getStringCollection(str);
        if (values.size() == 0) {
            return null;
        }
        return values.toArray(new String[values.size()]);
    }

    public static Collection<String> getStringCollection(String str) {
        List<String> values = new ArrayList<String>();
        if (str == null) {
            return values;
        }
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        while (tokenizer.hasMoreTokens()) {
            values.add(tokenizer.nextToken());
        }
        return values;
    }
}
