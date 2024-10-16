/*
 * Siguiendo el patrón MVC esta clase es del tipo View
 */

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class Console implements Observer, InterfaceConstantes {

    /**
     * Actualiza el objeto observado cada vez que cambia de valor
     * 
     * @param o     El objeto observado
     * @param arg   Argumento enviado al método de notifyObservers
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Line) {
            Line line = (Line) o;
            line.printLinea();
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