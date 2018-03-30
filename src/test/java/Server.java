import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static int counter = 0;

    static int sameTimes = 0;
    static int lastCounter = 0;

    public static void main(String[] args) {

        new Thread() {

            @Override
            public void run() {
                while (true) {

                    System.out.println(counter);

                    if (counter > 0) {
                        if (lastCounter != counter) {
                            lastCounter = counter;
                            sameTimes = 0;
                        } else {
                            sameTimes++;
                        }
                    }

                    if (sameTimes > 30) {
                        System.exit(0);
                    }
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }.start();
        try {

            InetSocketAddress inetSocketAddress = new InetSocketAddress(args[0],
                    Integer.valueOf(args[1]));
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(inetSocketAddress);
            Socket socket = serverSocket.accept();

            byte[] bytes = new byte[1000];
            while (true) {
                socket.getOutputStream().write(bytes);
                // socket.getOutputStream().flush();
                counter++;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
