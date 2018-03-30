package poi;

import java.io.FileInputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;

public class WordToTxt {

    public static void main(String args[]) throws Exception {
        FileInputStream in = new FileInputStream(
                "D:\\Project\\LTMS\\彩票管理系统开放接口规范.doc");
        WordExtractor extractor = new WordExtractor(in);
        String str = extractor.getText();
        extractor.close();
        // System.out.println("the result length is"+str.length());
        System.out.println(str);
    }
}
