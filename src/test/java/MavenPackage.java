import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;

public class MavenPackage {

    public static void main(String[] args) {

        try {
            List<String> lines = FileUtils.readLines(
                    new File("src/test/resources/loggerPrinter.txt"), "utf-8");

            while (true) {
                System.out.println(
                        lines.get(RandomUtils.nextInt(0, lines.size())));
                Thread.sleep(RandomUtils.nextInt(0, 200));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
