
public class ExceptionTest {

    public static void main(String[] args) {

        try {
            Class clazz = Class.forName("a.b.c");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            throw new RuntimeException("aa");
        }

    }
}
