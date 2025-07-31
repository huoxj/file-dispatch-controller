package com.fdc.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static List<String> splitByLength(String str, int length) {
        int size = str.length();
        int pos = 0;
        List<String> result = new ArrayList<>();
        while (pos < size) {
            int end = Math.min(pos + length, size);
            result.add(str.substring(pos, end));
            pos = end;
        }
        return result;
    }
}
