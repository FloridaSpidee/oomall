package cn.edu.xmu.other.aftersale.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    static public LocalDateTime stringToLocalDateTime(String string){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt =null;
        if(string!=null){
            ldt=LocalDateTime.parse(string,df);
        }
        return ldt;
    }
    static public String localDateTimeToString(LocalDateTime ldt){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String string=null;
        string=df.format(ldt);
        return string;
    }
}

