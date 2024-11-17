/*
 * Siguiendo el patrón MVC esta clase es del tipo View
 */

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class Console implements Observer {

    /**
     * Actualiza el objeto observado cada vez que cambia de valor
     * 
     * @param o     El objeto observado
     * @param arg   Argumento enviado al método de notifyObservers
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Line line) {
            System.out.print("\u001b[2K\u001b[G");  // Borramos la línea y nos situamos al inicio
            System.out.print(line.toString());      // Imprimimos la linea
            System.out.print("\u001b["+(line.getCursorPosition()+1)+"G");   // Nos situamos en la posición de la línea
        }
    }

    /**
     * Configura la consola en modo raw, permitiendo la lectura de teclas especiales, y carácteres
     * individuales sin buffering
     * Lee carácter a carácter sin esperar el ENTER
     * 
     * @throws  IOException
     */
    public void setRaw() throws IOException {
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "stty -echo raw < /dev/tty" });
    }

    /**
     * Configura la consola en modo cooked, permitiendo la lectura de caracteres
     * Los carácteres son leídos después del ENTER
     * 
     * @throws  IOException
     */
    public void unsetRaw() throws IOException {
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "stty echo cooked < /dev/tty" });
    }
}
