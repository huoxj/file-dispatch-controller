package com.fdc.util;

import java.time.LocalDateTime;
import java.util.Date;

public class TimeUtil {
    public static Date localDateTimeToDate(LocalDateTime ldt) {
        return ldt == null ? null : Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}
