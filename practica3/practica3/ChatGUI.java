package practica3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userModel;
    private MySocket socket;
    private String username;

    public ChatGUI(String host, int port, String username) {
        this.username = username;
        setTitle("Chat - Usuario: " + username);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Área de mensajes
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // Área de usuarios conectados
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));

        // Campo de entrada y botón de envío
        inputField = new JTextField();
        sendButton = new JButton("Enviar");

        // Panel inferior para entrada de texto y botón
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Agregar componentes al panel principal
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(userScrollPane, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Agregar panel principal a la ventana
        add(mainPanel);

        // Acción del botón enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Acción al presionar Enter en el campo de texto
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Conectar al servidor
        try {
            socket = new MySocket(host, port);
            socket.send(username); // Enviar nombre al servidor
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar al servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setVisible(true);
    }

    // Método para enviar mensajes
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            socket.send(message);
            inputField.setText("");
        }
    }

    // Método para escuchar mensajes del servidor
    private void listenForMessages() {
        try {
            String message;
            while ((message = socket.receive()) != null) {
                if (message.startsWith("::USERS::")) {
                    updateUserList(message.substring(9)); // Actualizar lista de usuarios
                } else {
                    chatArea.append(message + "\n");
                }
            }
        } catch (IOException e) {
            chatArea.append("Conexión cerrada: " + e.getMessage() + "\n");
        } finally {
            closeConnection();
        }
    }

    // Método para actualizar la lista de usuarios
    private void updateUserList(String userList) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (String user : userList.split(",")) {
                userModel.addElement(user.trim());
            }
        });
    }

    // Método para cerrar la conexión
    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            chatArea.append("Error al cerrar la conexión: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        // Pedir información del servidor y nombre de usuario
        String host = JOptionPane.showInputDialog("Ingresa la dirección del servidor:");
        String portInput = JOptionPane.showInputDialog("Ingresa el puerto del servidor:");
        String username = JOptionPane.showInputDialog("Ingresa tu nombre de usuario:");

        if (host != null && portInput != null && username != null && !host.trim().isEmpty() && !portInput.trim().isEmpty() && !username.trim().isEmpty()) {
            try {
                int port = Integer.parseInt(portInput);
                new ChatGUI(host, port, username);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El puerto debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("No se puede iniciar el cliente sin la información requerida.");
        }
    }
}
