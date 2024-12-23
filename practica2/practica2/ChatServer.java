package practica2;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatServer {
    private final Map<String, MySocket> clients;
    private final ReentrantReadWriteLock lock;

    public ChatServer() {
        // Crear un Map sincronizado
        clients = Collections.synchronizedMap(new HashMap<>());
        lock = new ReentrantReadWriteLock();
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
        String nick = null;
        try {
            // Leer el nick del cliente
            nick = client.receive();
            if (nick == null || nick.isEmpty()) {
                client.close();
                return;
            }
            // Añadir el cliente al Map

            lock.writeLock().lock();
            try {
                clients.put(nick, client);
                broadcast("Servidor", "Se ha conectado " + nick);
            } finally {
                lock.writeLock().unlock();
                System.out.println("Nuevo cliente registrado con nick: " + nick);
            }

            // Escuchar mensajes del cliente
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
                broadcast("Servidor", nick + " se ha desconectado.");
            }
        }
    }

    private void broadcast(String senderNick, String message) {
        lock.readLock().lock();
        try {
            clients.forEach((nick, socket) -> {
                if (!nick.equals(senderNick)) {
                    try {
                        socket.send(senderNick + ": " + message);
                    } catch (Exception e) {
                        System.out.println("Error enviando a " + nick + ": " + e.getMessage());
                    }
                }
            });
        } finally {
            lock.readLock().unlock();
        }
    }

    private void removeClient(String nick) {
        lock.writeLock().lock();
        try {
            MySocket client = clients.remove(nick);
            if (client != null) {
                client.close();
                broadcast("Servidor", nick + " se ha desconectado");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el cliente: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(12345);
    }
}
