package practica3;

import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket implements AutoCloseable {
    private final ServerSocket serverSocket;

    /**
     * Constructor que inicializa un nuevo socket de servidor
     * 
     * @param port          El puerto en el que el servidor escuchará conexiones
     * @throws IOException  Si ocurre un error al crear el socket de servidor
     */
    public MyServerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * Acepta una conexión entrante y devuelve un objeto MySocket
     * 
     * @return              Una instancia MySocket
     * @throws IOException  Si ocurre un error al aceptar la conexión
     */
    public MySocket accept() throws IOException {
        return new MySocket(serverSocket.accept());
    }

    /**
     * Cierra el socket del servidor
     * 
     * @throws IOException Si ocurre un error al cerrar el socket del servidor
     */
    @Override
    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
