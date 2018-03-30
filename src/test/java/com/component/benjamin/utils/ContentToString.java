package com.component.benjamin.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 将内容转化为java源码String对象
 * 
 * @author Benjamin
 *
 */
public class ContentToString {

    public static void main(String[] args) {
        contentToString(new File("c11_queryBalance.xml"));
        // contentToString(new File("content2.txt"));
    }

    public static void contentToString(File file) {

        String className = file.getName();
        int pos = className.indexOf(".");
        if (pos != -1) {
            className = className.substring(0, pos);
            className = className.substring(0, 1).toUpperCase()
                    + className.substring(1);
        }

        String classStr = "public class " + className + "{\n";
        String stringStr = "    private static String template = \"\"\n";

        try {
            List<String> lines = FileUtils.readLines(file, "utf-8");
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                line = line.replace("\\", "\\\\");
                line = line.replace("\"", "\\\"");
                stringStr += "        + \"" + line + "\\n\"";
                if (i == lines.size() - 1) {
                    stringStr += ";\n";
                } else {
                    stringStr += "\n";
                }
            }

            classStr += stringStr;
            classStr += "}\n";

            FileUtils.write(new File("./src/test/java/" + className + ".java"),
                    classStr, "utf-8");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
