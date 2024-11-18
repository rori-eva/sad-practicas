/*
 * Siguiendo el patrón MVC esta clase es del tipo View
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        if(o instanceof MultiLine multiLine) {
            System.out.print("\u001b[H\u001b[2J");
            System.out.print(multiLine.toString());
            System.out.print("\u001b["+(multiLine.getCursorFila()+1)+";"+(multiLine.cursorColumna+1)+"f");
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

    /**
     * @return size    Array con la dimensión de la terminal
     * @throws IOException
     */
    public static int[] getScreenTerminalSize() throws IOException {
        int[] size = new int[2];
        size[0] = Integer.parseInt(new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "tput lines 2> /dev/tty"}).getInputStream())).readLine());
        size[1] = Integer.parseInt(new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "tput cols 2> /dev/tty"}).getInputStream())).readLine());

        return size;
    }
}
