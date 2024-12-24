package practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClient {
    public static void main(String[] args) {
        try {
            MySocket client = new MySocket("localhost", 12345);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Conectado al servidor. Escribe primero tu numbre, da a ENTER y puedes escribir tus mensajes:");

            // Hilo para manejar la entrada del servidor
            Thread inputThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = client.receive()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Error al recibir datos del servidor: " + e.getMessage());
                }
            });
            inputThread.start();

            // Hilo para manejar la entrada del usuario
            Thread outputThread = new Thread(() -> {
                try {
                    String input;
                    while ((input = console.readLine()) != null) {
                        if (input.equalsIgnoreCase("exit")) {
                            client.send("::EXIT::");
                            break;
                        } else {
                            client.send(input);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar datos al servidor: " + e.getMessage());
                }
            });
            outputThread.start();

            // Esperar que ambos hilos terminen
            inputThread.join();
            outputThread.join();

            client.close();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}
