package practica2;

import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket implements AutoCloseable {
    private final ServerSocket serverSocket;

    public MyServerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public MySocket accept() throws IOException {
        return new MySocket(serverSocket.accept());
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
