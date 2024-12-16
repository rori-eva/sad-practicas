package practica2;

import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket implements AutoCloseable {
    private ServerSocket serverSocket;

    public MyServerSocket(int port) throws IOException {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IOException("Error al crear el servidor: " + e.getMessage());
        }
    }

    public MySocket accept() throws IOException {
        try {
            return new MySocket(serverSocket.accept());
        } catch (IOException e) {
            throw new IOException("Error al aceptar conexión: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
