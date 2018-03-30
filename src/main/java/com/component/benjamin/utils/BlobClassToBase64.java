package com.component.benjamin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class BlobClassToBase64 {

    public static void main(String[] args) throws IOException {

        System.out.println((char) 0);
        // process(new File(
        // "D:\\IdeaProjects\\ltms\\lt-openapi\\src\\main\\java\\com\\ltms\\service\\openapi\\model\\LtDistributorWithBLOBs.java"));
    }

    public static void process(File file) throws IOException {

        if (!file.getName().endsWith("WithBLOBs.java")) {
            throw new IllegalArgumentException("invalided file name "
                    + file.getName() + ", must end with WithBLOBs.java");
        }

        List<String> lines = FileUtils.readLines(file, "utf-8");

        File outputFile = new File(file.getParentFile(),
                file.getName().replace("WithBLOBs", "Base64"));

        boolean imported = false;
        List<String> newLines = new ArrayList();

        int stage = 0;
        for (String line : lines) {
            if (line.length() == 0) {
                newLines.add(line);
            } else if (line.startsWith("import ") && !imported) {
                line = "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n"
                        + "import lombok.Data;\n" + line;
                imported = true;
            } else if (line.startsWith("public class ")) {
                stage = 1;
                line = "@Data\n"
                        + "@JsonIgnoreProperties(ignoreUnknown = true)\n"
                        + line;
                line = line.replace("WithBLOBs", "Base64");
            } else if (line.contains("private byte[] ")) {
                line = line.replace("byte[]", "String");
            } else {
                if (stage == 0) {
                    newLines.add(line);

                }
            }
        }

        FileUtils.write(outputFile, StringUtils.join(newLines, "\n"), "utf-8");

    }
}
