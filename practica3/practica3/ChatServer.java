package practica3;

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

                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

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
                broadcastUserList();
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
                broadcastUserList();
            }
        }
    }

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

    private void broadcastUserList() {
        synchronized (clients) {
            StringBuilder userList = new StringBuilder("::USERS::");
            clients.keySet().forEach(user -> userList.append(user).append(","));

            String userListMessage = userList.toString();
            clients.values().forEach(client -> {
                client.send(userListMessage);
            });
        }
    }

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
