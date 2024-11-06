/*
 * This class is a extension of BufferedReader
 */

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

public class EditableBufferedReader extends BufferedReader {
    // Constantes privadas
    private static final int ENTER            = 10;
    private static final int ESCAPE           = 27;
    private static final int FLECHA_ARRIBA    = -65;
    private static final int FLECHA_ABAJO     = -66;
    private static final int FLECHA_DERECHA   = -67;
    private static final int FLECHA_IZQUIERDA = -68;
    private static final int INICIO           = -72;
    private static final int FIN              = -70;
    private static final int INSERT           = -50;
    private static final int SUPRIMIR         = -51;
    private static final int BACKSPACE        = -127;

    /**
     * Constructor de la clase EditableBufferedReader
     * 
     * @param in    El flujo de entrada de caracteres
     */
    public EditableBufferedReader(Reader in) {
        super(in);
    }

    /**
     * Lee carácter a carácter la entradam
     * Es una función genérica
     * 
     * @return  el carácter leído en código ASCII o, -1 en caso de no reconocer el carácter o no insertar un carácter
     */
    @Override
    public int read() throws IOException {
        int charCode = super.read(); // Lee el primer carácter
        // Si detectamos una secuencia de escape (ASCII 27), podemos leer las siguientes teclas
        if (charCode == ESCAPE) {
            if (super.read() == '[') {
                 // Lee la tercera parte de la secuencia de escape
                // Manejar las teclas de flechas y otras especiales
                switch (super.read()) {
                    case 'C':    return FLECHA_DERECHA;
                    case 'D':    return FLECHA_IZQUIERDA;
                    case 'H':    return INICIO;
                    case 'F':    return FIN;
                    case '2':
                        if (super.read() == '~')    return INSERT;
                        break;
                    case '3':
                        if (super.read() == '~')    return SUPRIMIR;
                        break;
                    default:    return -1;  //  Si no se reconoce la secuencia de escape, retorna -1
                }
            }
        } else if (charCode == 127) {
            return BACKSPACE;
        }

        return charCode;
    }

    /**
     * Lee la línea de entrada del usuario y la procesa
     * Permite la edición de línea mientras se escribe
     * 
     * @return  La línea de entrada procesada
     */
    @Override
    public String readLine() throws IOException {
        Console view = new Console();
        Line line = new Line();
        line.addObserver(view);

        int charCode = 0;
        view.setRaw();

        while ((charCode = this.read()) != '\r') { // '\r' es el ENTER en modo raw
            if (charCode == -1) {
                // Cuando read() devuelve -1, indica que el modo de inserción fue activado/desactivado
                continue; // Continua con el siguiente ciclo sin insertar nada
            }

            switch (charCode) {
                case FLECHA_DERECHA:    line.moverDerecha();            break;
                case FLECHA_IZQUIERDA:  line.moverIzquierda();          break;
                case INICIO:            line.moverInicio();             break;
                case FIN:               line.moverFinal();              break;
                case BACKSPACE:         line.borrar();                  break;
                case SUPRIMIR:          line.suprimir();                break;
                case INSERT:            line.alternarModoInsert();      break;
                default:                line.insertar((char) charCode); break;
            }
            System.out.flush();
        }
        view.unsetRaw();

        return line.toString();
    }
}
