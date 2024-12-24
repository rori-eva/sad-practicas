package practica3;

import java.io.*;
import java.net.*;

public class MySocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructor que crea un nuevo socket cliente y estable conexión con el servidor
     * 
     * @param host          La dirección IP o nombre del host del servidor
     * @param port          El puerto en el servidor al que conectarse
     * @throws IOException  Si hay algún problema al crear el socket o establecer la conexión
     */
    public MySocket(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new IOException("Error al crear el socket: " + e.getMessage());
        }
    }

    /**
     * Constructor que inicializa la clase a partir de un socket existente
     * 
     * @param socket        Un socket ya conectado con su dirección y puerto
     * @throws IOException  Si hay algún problema al inicializar la clase / conexión
     */
    public MySocket(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new IOException("Error al inicializar el socket: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje al servidor o cliente conectado
     * 
     * @param  message  El mensaje a enviar
     */
    public void send(String message) {
        out.println(message);
    }

    /**
     * Recibe un mensaje del servidor o cliente conectado
     * 
     * @return              El mensaje recibido
     * @throws IOException  Si ocurre un error al leer el mensaje
     */
    public String receive() throws IOException {
        return in.readLine();
    }

    /**
     * Cierra el socket y los flujos de entrada/salida asociados
     * 
     * @throws IOException  Si ocurre un error al cerrar el socket y las conexiones
     */
    public void close() throws IOException {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new IOException("Error al cerrar el socket: " + e.getMessage());
        }
    }
}
