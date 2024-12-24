package practica3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;

public class ChatGUI extends JFrame {
    private DefaultListModel<String> userModel;
    private JButton sendButton;
    private JList<String> userList;
    private JTextArea chatArea;
    private JTextField inputField;
    private MySocket socket;
    private String username;

    /**
     *  Constructor que inicializa la interfaz gráfica y establece la conexió con el servidor
     * 
     * @param host      La dirección del servidor al que el cliente se conectará
     * @param port      El puerto del servidor
     * @param username  El nombre de usuario del cliente
     */
    public ChatGUI(String host, int port, String username) {
        this.username = username;
        setTitle("Chat - Usuario: " + username);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        setupActions();
        connectToServer(host, port);

        setVisible(true);
    }

    /**
     * Inicializa los componentes gráficos de la interfaz
     */
    private void initializeComponents() {
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
    }

    /**
     * Establece las acciones de los botones y campos de texto
     */
    private void setupActions() {
        // Acción del botón enviar
        sendButton.addActionListener((ActionEvent e) -> {
            sendMessage();
        });

        // Acción al presionar Enter en el campo de texto
        inputField.addActionListener((ActionEvent e) -> {
            sendMessage();
        });
    }

    /**
     * Conecta al servidor usando el host y el puerto proporcionado
     * 
     * @param host  La dirección del servidor
     * @param port  El puerto del servidor
     */
    private void connectToServer(String host, int port) {
        // Conectar al servidor
        try {
            socket = new MySocket(host, port);
            socket.send(username); // Enviar nombre al servidor
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar al servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Envía el mensaje escrito en el campo de texto al servidor
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            socket.send(message);
            inputField.setText("");
        }
    }

    /** 
     * Escucha los mensajes enviados desde el servidor y los muestra en el área de chat
     * Si se conecta un nuevo usuario, la lista de usuarios conectados se actualiza
     */
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

    /**
     * Actualiza la lista de usuarios conectados en la interfaz gráfica
     * 
     * @param userList  La lista de usuarios conectados
     */
    private void updateUserList(String userList) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (String user : userList.split(",")) {
                userModel.addElement(user.trim());
            }
        });
    }

    /**
     * Cierra la conexión con el servidor
     */
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
