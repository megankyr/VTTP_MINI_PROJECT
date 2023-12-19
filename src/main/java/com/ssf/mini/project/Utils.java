package com.ssf.mini.project;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {
    public static final String[] GENRE_CODE = {
            "28", "12", "16", "35", "80", "99", "18", "10751", "14", "36", "27", "10402", "9648",
            "10749", "878", "10770", "53", "10752", "37"
    };

    public static String getCodeAsCSV(){
        return Arrays.asList(GENRE_CODE).stream().collect(Collectors.joining(","));
    }
}

