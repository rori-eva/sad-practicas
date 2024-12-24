package practica2;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private final Map<String, MySocket> clients;

    /**
     * Constructor que inicializa el servidor de chat con una lista de clientes
     */
    public ChatServer() {
        // Crear un Map sincronizado
        clients = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Inicia el servidor en el puerto especificado y espera conexiones de clientes
     * 
     * @param port  El puerto en el que el servidor escuchará conexiones
     */
    public void start(int port) {
        try (MyServerSocket server = new MyServerSocket(port)) {
            System.out.println("Servidor iniciado en el puerto " + port);

            while (true) {
                MySocket client = server.accept();
                System.out.println("Cliente conectado");

                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * Maneja las iteracciones con un cliente conectado
     * 
     * @param client  El socket del cliente conectado
     */
    private void handleClient(MySocket client) {
        String nick = null;
        try {
            nick = client.receive();
            if (nick == null || nick.isEmpty()) {
                client.close();
                return;
            }

            synchronized (clients) {
                clients.put(nick, client);
                broadcast("Servidor", "Se ha conectado " + nick);
                System.out.println("Nuevo cliente registrado con nick: " + nick);
            }

            String message;
            while ((message = client.receive()) != null) {
                if(message.equals("::EXIT::")) {
                    System.out.println(nick + " se ha desconectado.");
                    break;
                }
                broadcast(nick, message);
            }
        } catch (IOException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        } finally {
            if (nick != null) {
                removeClient(nick);
            }
        }
    }

    /**
     * Envía un mensaje a todos los cliene conectados
     * 
     * @param senderNick    El nick del remitente del mensaje
     * @param message       El contenido del mensaje a enviar
     */
    private void broadcast(String senderNick, String message) {
        synchronized (clients) {
            clients.forEach((nick, socket) -> {
                try {
                    socket.send(senderNick + ": " + message);
                } catch (Exception e) {
                    System.out.println("Error enviando a " + nick + ": " + e.getMessage());
                }
            });
        }
    }

    /**
     * Elimina un cliente de la lista y cierra su conexión
     * 
     * @param nick  El nick del cliente a eliminar
     */
    private void removeClient(String nick) {
        try {
            synchronized (clients) {
                MySocket client = clients.remove(nick);
                if (client != null) {
                    client.close();
                    broadcast("Servidor", nick + " se ha desconectado");
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(12345);
    }
}
