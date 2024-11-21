/*
 * This class is a extension of BufferedReader
 * Controller class
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class EditableBufferedReader extends BufferedReader {
    // Constantes privadas
    private static final int ENTER            = 13;
    private static final int ESCAPE           = 27;
    private static final int EOF              = 4;
    private static final int FLECHA_ARRIBA    = -65;
    private static final int FLECHA_ABAJO     = -66;
    private static final int FLECHA_DERECHA   = -67;
    private static final int FLECHA_IZQUIERDA = -68;
    private static final int INICIO           = -72;
    private static final int FIN              = -70;
    private static final int INSERT           = -50;
    private static final int SUPRIMIR         = -51;
    private static final int BACKSPACE        = 127;

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
     * @return    El carácter leído en código ASCII o, -1 en caso de no reconocer el carácter o no insertar un carácter
     */
    @Override
    public int read() throws IOException {
        int charCode = super.read(); // Lee el primer carácter
        // Si detectamos una secuencia de escape (ASCII 27), podemos leer las siguientes teclas
        if (charCode == ESCAPE) {
            if (super.read() == '[') {
                // Lee la tercera parte de la secuencia de escape
                // Manejar las teclas de flechas y otras especiales
                //  Si no se reconoce la secuencia de escape, retorna -1
                switch (super.read()) {
                    case 'A':   return FLECHA_ARRIBA;
                    case 'B':   return FLECHA_ABAJO;
                    case 'C':   return FLECHA_DERECHA;
                    case 'D':   return FLECHA_IZQUIERDA;
                    case 'H':   return INICIO;
                    case 'F':   return FIN;
                    case '2':   return (super.read() == '~') ? INSERT : -1;
                    case '3':   return (super.read() == '~') ? SUPRIMIR : -1;
                    default:    return -1;  //Si no se reconoce la secuencia de escape, retorna -1
                }
            }
        }
        // Retornará también BACKSPACE, EOF y ENTER
        return charCode;
    }

    /**
     * Lee la línea de entrada del usuario y la procesa
     * Permite la edición de línea mientras se escribe
     * 
     * @return    La línea de entrada procesada
     */
    @SuppressWarnings("deprecation")
    @Override
    public String readLine() throws IOException {
        Console view = new Console();
        MultiLine lines = new MultiLine();
        int charCode;

        lines.addObserver(view);
        view.setRaw();
        lines.setDimensions(view.getScreenTerminalSize()[0], view.getScreenTerminalSize()[1]);

        while ((charCode = this.read()) != EOF) {    // -1 es Ctrl+D (EOF)
            switch (charCode) {
                case FLECHA_ARRIBA:     lines.moverArriba();             break;
                case FLECHA_ABAJO:      lines.moverAbajo();              break;
                case FLECHA_DERECHA:    lines.moverDerecha();            break;
                case FLECHA_IZQUIERDA:  lines.moverIzquierda();          break;
                case INICIO:            lines.moverInicio();             break;
                case FIN:               lines.moverFinal();              break;
                case BACKSPACE:         lines.borrar();                  break;
                case SUPRIMIR:          lines.suprimir();                break;
                case INSERT:            lines.alternarModoInsert();      break;
                case ENTER:             lines.nuevaLinea();              break;
                case -1:  continue;    // Ignora teclas no reconocidas
                default:                lines.insertar((char) charCode); break;
            }
            System.out.flush();
        }

        lines.setCursorFila(lines.getLineasLength());
        view.unsetRaw();

        return lines.toString();
    }
}
