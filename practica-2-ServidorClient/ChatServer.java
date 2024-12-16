package practica2;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private final Map<String, MySocket> clients;

    public ChatServer() {
        // Crear un Map sincronizado
        clients = Collections.synchronizedMap(new HashMap<>());
    }

    public void start(int port) {
        try (MyServerSocket server = new MyServerSocket(port)) {
            System.out.println("Servidor iniciado en el puerto " + port);

            while (true) {
                MySocket client = server.accept();
                System.out.println("Cliente conectado");

                // Manejar al cliente en un nuevo hilo
                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    private void handleClient(MySocket client) {
        try {
            // Leer el nick del cliente
            String nick = client.receive();
            if (nick == null || nick.isEmpty()) {
                client.close();
                return;
            }

            // Añadir el cliente al Map
            clients.put(nick, client);
            System.out.println("Cliente registrado con nick: " + nick);

            // Escuchar mensajes del cliente
            String message;
            while ((message = client.receive()) != null) {
                broadcast(nick, message);
            }
        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        } finally {
            removeClient(client);
        }
    }

    private void broadcast(String senderNick, String message) {
        synchronized (clients) {
            clients.forEach((nick, socket) -> {
                if (!nick.equals(senderNick)) {
                    try {
                        socket.send(senderNick + ": " + message);
                    } catch (Exception e) {
                        System.out.println("Error enviando a " + nick + ": " + e.getMessage());
                    }
                }
            });
        }
    }

    private void removeClient(MySocket client) {
        synchronized (clients) {
            clients.values().remove(client);
        }
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(12345);
    }
}
