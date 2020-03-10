package com.component.benjamin.utils;

public class Test {

    public static void main(String[] args) {

        System.out.println("[" + "abc".substring(0, 0) + "]");

        String columnName = "12345678901234567890123456";
        String SeqName = "_" + columnName + "_seq";
        int spaceleft = 31 - SeqName.length();
        if (spaceleft < 0) {
            throw new IllegalArgumentException(
                    "columnName is too long, must <= 26, " + columnName);
        }

        // System.out.println(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
        // .format(new Date(1490338112126l)));
        // System.out.println(new SimpleDateFormat().format(new
        // Date(1485364336599l)));

        // String target = "centos63:8042";
        // URI uri = URI.create("dummyscheme://" + target);
        // String host = uri.getHost();
        // int port = uri.getPort();
        // System.out.println(host + "---" + port);
    }
}
