public class T1 {

    public static void main(String[] args) {

        for (int i = 1; i < 100; i++) {
            if (i * i / 100 == 18) {
                System.out.println(i * i - i);
            }
        }
    }
}