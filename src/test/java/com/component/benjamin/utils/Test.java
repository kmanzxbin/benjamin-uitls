package com.component.benjamin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        System.out.println(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                .format(new Date(1490338112126l)));
        // System.out.println(new SimpleDateFormat().format(new
        // Date(1485364336599l)));

        // String target = "centos63:8042";
        // URI uri = URI.create("dummyscheme://" + target);
        // String host = uri.getHost();
        // int port = uri.getPort();
        // System.out.println(host + "---" + port);
    }
}
