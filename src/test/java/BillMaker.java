import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.RandomUtils;

public class BillMaker {

    private static final long DEFAULT_CALLING = 13000000000l;
    private static final long DEFAULT_CALLED = 13500000000l;

    public static void main(String[] args) {

        long calling = DEFAULT_CALLING;
        long called = DEFAULT_CALLED;
        for (int i = 1; i <= 10; i++) {
            makeFile(i, calling, called);
            // calling += 10000000;
            // called += 10000000;
        }
    }

    public static void makeFile(int index, long calling, long called) {

        File file = new File("cdpbill/CDPCALL_010_20170324112233_"
                + String.format("%02d", index) + ".txt");

        file.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            long callingNumber = calling;
            long calledNumber = called;
            // 循环次数
            for (int k = 0; k < 1000000; k++) {
                callingNumber = calling + RandomUtils.nextInt(0, 1000000);
                calledNumber = called + RandomUtils.nextInt(0, 1000000);
                String record = "" + callingNumber + "|" + callingNumber
                        + "|1|0|44|755|" + calledNumber + "|" + calledNumber
                        + "|2||||44|759|0755|1||12@132@40|4|20170309144813|0||\n";

                // 非0结尾的为多次呼叫（降低离散度）
                if (!(callingNumber + "").endsWith("0")) {

                    // // 呼叫次数（记录数）随机
                    int count = RandomUtils.nextInt(3, 9);
                    for (int j2 = 0; j2 < count; j2++) {
                        bufferedWriter.write(record);
                    }

                } else {

                    bufferedWriter.write(record);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        System.out.println("make file successfully! " + file);

    }
}
