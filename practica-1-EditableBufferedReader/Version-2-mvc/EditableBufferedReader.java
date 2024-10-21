/*
 * This class is a extension of BufferedReader
 * Siguiendo el patrón MVC esta clase es del tipo Controller
 */

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class EditableBufferedReader extends BufferedReader implements InterfaceConstantes {
    private boolean modoInsert = false;
    private boolean modoSuprimir = false;
    private MultiLine multiLine;
    private Console view;

    public EditableBufferedReader(Reader in) {
        super(in);
    }

    public EditableBufferedReader(Reader in, Console view) {
        super(in);
        this.multiLine = new MultiLine();
        this.view = view;
        this.multiLine.addObserver(view);
    }

    /**
     * Lee carácter a carácter la entrada 
     * 
     * @return  el carácter leído en código ASCII o, -1 en caso de no reconocer el carácter o no insertar un carácter

     */
    @Override
    public int read() throws IOException {
        int charCode = System.in.read(); // Lee el primer carácter
        // Si detectamos una secuencia de escape (ASCII 27), podemos leer las siguientes teclas
        if (charCode == ESCAPE) {
            if (System.in.read() == '[') {
                int nextChar = System.in.read(); // Lee la tercera parte de la secuencia de escape
                // Manejar las teclas de flechas y otras especiales
                switch (nextChar) {
                    case 'A':    return FLECHA_ARRIBA;
                    case 'B':    return FLECHA_ABAJO;
                    case 'C':    return FLECHA_DERECHA;
                    case 'D':    return FLECHA_IZQUIERDA;
                    case 'H':    return INICIO;
                    case 'F':    return FIN;
                    case '2':
                        if (System.in.read() == '~') {
                            modoInsert = !modoInsert; // Alterna el modo de inserción
                            return -1; // Retorna -1 para indicar que no se inserta un carácter
                        }
                        break;
                    case '3':
                        if (System.in.read() == '~') {
                            modoSuprimir = true;
                            return SUPRIMIR;
                        }
                        break;
                    default:    return -1;  // Si no se reconoce la secuencia de escape, retorna -1
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
        int charCode = 0;
        view.setRaw();

        while (charCode != -1) {
            charCode = this.read();

            if (charCode == '\r') { // '\r' es el ENTER en modo raw
                multiLine.nuevaLinea();
            } else {
                switch (charCode) {
                    case FLECHA_ARRIBA:     multiLine.moverArriba();    break;
                    case FLECHA_ABAJO:      multiLine.moverAbajo();     break;
                    case FLECHA_DERECHA:    multiLine.moverDerecha();   break;
                    case FLECHA_IZQUIERDA:  multiLine.moverIzquierda(); break;
                    case INICIO:            multiLine.moverInicio();    break;
                    case FIN:               multiLine.moverFinal();     break;
                    case BACKSPACE:         multiLine.borrar();         break;
                    case SUPRIMIR:
                        if (modoSuprimir) {
                            multiLine.suprimir();
                            modoSuprimir = false;
                        } else {
                            // Agrega el carácter si no estamos en modo de suprimir
                            multiLine.agregar((char) charCode);
                        }
                        break;
                    default:
                        if (modoInsert) {
                            multiLine.insertar((char) charCode, modoInsert);
                        } else {
                            // Agrega el carácter si no estamos en modo de inserción
                            multiLine.agregar((char) charCode);
                        }
                        break;
                }
                multiLine.printMultiLinea();
            }
        }
        view.unsetRaw();

        return multiLine.toString();
    }
}
