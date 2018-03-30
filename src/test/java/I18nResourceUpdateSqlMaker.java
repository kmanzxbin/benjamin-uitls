import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * 检索脚本当中的国际化资源 并生成update语句
 * @author Benjamin
 *
 */
public class I18nResourceUpdateSqlMaker {

    public static void main(String[] args) {

        new I18nResourceUpdateSqlMaker().process(args);
    }

    public void process(String[] args) {

        // 检索指定目录下的sql文件
        File folder = new File(args[0]);
        File outputFolder = new File(folder, "output");
        FileUtils.deleteQuietly(outputFolder);
        try {
            FileUtils.forceMkdir(outputFolder);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        File[] files = folder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".sql");
            }
        });

        // 遍历文件
        for (File file : files) {

            System.out.println("process file " + file);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                List<String> lines = IOUtils.readLines(fileInputStream,
                        "utf-8");
                List<String> outputLines = new ArrayList<String>();

                // 检索文件 当中的i18n_resource相关的语句
                for (int i = 0; i < lines.size(); i++) {

                    String line = lines.get(i);
                    if (line.startsWith("insert into com_i18n_resource")) {
                        // 如果是insert语句，需要转化成update语句然后输出

                        // 检查结果返现一行未完成 要读取完所有的语句
                        if (!line.endsWith(";")) {
                            SqlContext sqlContext = getLine(lines, i, line);
                            line = sqlContext.content;
                            i = sqlContext.index;
                        }
                        if (!line.contains("'zh_CN'")) {
                            continue;
                        }
                        String updateSql = "update com_i18n_resource set textContent = '${textContent}' where resourceKey = '${resourceKey}' and languageId = 'zh_CN';";

                        String subStringStr = "";
                        if (line.indexOf("values (") != -1) {
                            subStringStr = "values (";
                        } else if (line.indexOf("values(") != -1) {
                            subStringStr = "values(";

                        }
                        line = line.substring(line.indexOf(subStringStr)
                                + subStringStr.length());

                        String splitor = "";
                        if (line.contains("','")) {
                            splitor = "','";
                        } else if (line.contains("', '")) {
                            splitor = "', '";
                        }
                        String[] strings = line.split(splitor);
                        String resourceKey = strings[0].trim();
                        if (resourceKey.startsWith("'")) {
                            resourceKey = resourceKey.substring(1);
                        }
                        String textContent = strings[2];
                        if (textContent.endsWith("');")) {
                            textContent = textContent.substring(0,
                                    textContent.length() - 3);
                        }

                        updateSql = updateSql.replace("${resourceKey}",
                                resourceKey);
                        updateSql = updateSql.replace("${textContent}",
                                textContent);
                        outputLines.add(updateSql);

                    } else if (line
                            .startsWith("update com_i18n_resource set ")) {
                        // 如果是update语句 直接输出

                        // 检查结果返现一行未完成 要读取完所有的语句
                        if (!line.endsWith(";")) {
                            SqlContext sqlContext = getLine(lines, i, line);
                            line = sqlContext.content;
                            i = sqlContext.index;
                        }
                        if (!line.contains("'zh_CN'")) {
                            continue;
                        }

                        if (line.contains(" || ")) {
                            continue;
                        }
                        outputLines.add(line);

                    } else if (line.contains("com_i18n_resource")) {
                        System.err.println("unprocessd line: " + line);
                    } else {
                    }

                }

                if (outputLines.size() > 0) {

                    outputLines.add("");
                    outputLines.add(
                            "update com_memdb set version = version + 1 where table_name = 'com_i18n_resource';");
                    outputLines.add("commit;");

                    File outputFile = new File(outputFolder, file.getName());
                    FileUtils.writeLines(outputFile, "utf-8", outputLines,
                            "\n");
                }

                // 转眼之间13年都过去了
                // 拿起手机 找到了Jolin的《就是爱》
                // 在那个无忧无虑的年龄，能谈一场恋爱

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class SqlContext {
        String content;
        int index;

    }

    private SqlContext getLine(List<String> lines, int index, String line) {
        for (int i = index + 1; i < lines.size(); i++) {
            String content = lines.get(i);
            line += content;
            if (content.endsWith(";")) {
                SqlContext lineContext = new SqlContext();
                lineContext.index = i;
                lineContext.content = line;
                return lineContext;
            }
        }

        System.err.println(index + " " + line);
        return null;
    }
}
