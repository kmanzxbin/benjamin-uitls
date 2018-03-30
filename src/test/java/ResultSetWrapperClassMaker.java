import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ResultSetWrapperClassMaker {

    // TODO 读取非public class开头的 public行
    // TODO 获取方法名，参数名
    // TODO 读取内容 如果有ruturn 返回proxy的return
    // TODO 解决参数跨行的问题

    public static void main(String[] args) {

        try {
            File inputFile = new File("./JDBCResultSet.java");
            File outputFile = new File(
                    "../component-memdb/src/main/java/com/component/memdb/jdbc/JDBCResultSet.java");

            outputFile.delete();
            List<String> strings = FileUtils.readLines(inputFile, "utf-8");

            for (int i = 0; i < strings.size(); i++) {

                String str = strings.get(i);
                String trimedStr = str.trim();

                if (trimedStr.startsWith("public ")
                        && !trimedStr.startsWith("public class ")) {
                    Method method = new Method();
                    method.defineLines = str;
                    method.name = trimedStr.substring(0,
                            trimedStr.indexOf("("));
                    method.name = method.name
                            .substring(method.name.lastIndexOf(' '),
                                    method.name.length())
                            .trim();
                    method.needReturn = !trimedStr.contains(" void ");
                    while (!str.endsWith("{")) {
                        str = strings.get(++i);
                        method.defineLines += "\n" + str;
                    }

                    String paramsStr = method.defineLines.replace("\n", "");
                    paramsStr = paramsStr.substring(paramsStr.indexOf("(") + 1,
                            paramsStr.indexOf(")"));
                    while (paramsStr.contains("<")) {
                        paramsStr = paramsStr.replaceAll("\\<.+\\>", "");
                    }
                    String[] params = paramsStr.split(",");
                    if (params.length > 0) {

                        for (String param : params) {
                            if (param.length() > 0) {

                                param = param.trim();
                                method.paraNames.add(param.substring(
                                        param.lastIndexOf(' ') + 1,
                                        param.length()));
                            }
                        }
                    }

                    while (!strings.get(++i).trim().equals("}")) {
                    }
                    // TODO 写出行
                    FileUtils.write(outputFile, method.output(), "utf-8", true);

                } else {

                    // TODO 写出行
                    FileUtils.write(outputFile, str + "\n", "utf-8", true);
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static class Method {
        String name;
        boolean needReturn;
        List<String> paraNames = new ArrayList<String>();

        String defineLines;

        public String output() {
            return defineLines + "\n      " + (needReturn ? "return " : "")
                    + "resultSet." + name
                    + "(" + (paraNames.size() > 0
                            ? StringUtils.join(paraNames, ", ") : "")
                    + ");" + "\n    }\n";
        }
    }

}
