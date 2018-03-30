package com.component.benjamin.utils;

import java.io.File;
import java.util.Set;

import com.component.utils.FileFinder;

public class GetFiles {

    public static void main(String[] args) {

        args = new String[] { "\\\\192.168.0.250\\TEMP Version" };
        for (String string : args) {

            Set<File> fiels = FileFinder.find(new File(string),
                    ".+TelwareR01.+SQL.+\\.zip");
            for (File file : fiels) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }
}
