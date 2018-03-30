import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.0.137", 3344));
            // System.out.println(socket.getReceiveBufferSize());
            DataInputStream inputStream = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            int count = 0;
            while (inputStream.read() != -1) {
                count++;
                if (count % 10000000 == 0) {
                    System.out.println(count);
                    System.in.read();
                }
            }

            System.out.println(count);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
