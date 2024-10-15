/*
 * Siguiendo el patrón MVC esta clase es del tipo View
 */

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
        Line line = (Line) arg;
        System.out.println("Texto actualizado: " + line.toString());
    }
}
