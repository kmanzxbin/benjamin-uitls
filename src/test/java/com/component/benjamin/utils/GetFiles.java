package com.component.benjamin.utils;

import java.io.File;
import java.util.Set;

import com.component.utils.FileFinder;

public class GetFiles {

    public static void main(String[] args) {
        Set<File> fiels = FileFinder.find(
                new File(
                        "\\\\192.168.0.250\\Archive\\Telware\\R01V02\\D22\\C_FULL\\SMP-TRC"),
                ".*\\.zip");
        for (File file : fiels) {
            System.out.println(file.getName());
        }
    }
}
