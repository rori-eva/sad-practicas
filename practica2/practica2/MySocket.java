package practica2;

import java.io.*;
import java.net.*;

public class MySocket {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public MySocket(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new IOException("Error al crear el socket: " + e.getMessage());
        }
    }

    public MySocket(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new IOException("Error al inicializar el socket: " + e.getMessage());
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IOException("Error al cerrar el socket: " + e.getMessage());
        }
    }
}
