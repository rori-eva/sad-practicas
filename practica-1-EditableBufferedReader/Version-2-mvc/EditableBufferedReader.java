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
    private Line line;
    private Console view;

    public EditableBufferedReader(Reader in) {
        super(in);
    }

    public EditableBufferedReader(Reader in, Console view) {
        super(in);
        this.line = new Line();
        this.view = view;
        this.line.addObserver(view);
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
                    case 'C':
                        return FLECHA_DERECHA;
                    case 'D':
                        return FLECHA_IZQUIERDA;
                    case 'H':
                        return INICIO;
                    case 'F':
                        return FIN;
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
                    default:
                        return -1;  //  Si no se reconoce la secuencia de escape, retorna -1
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

        while ((charCode = this.read()) != '\r') { // '\r' es el ENTER en modo raw
            if (charCode == -1) {
                // Cuando read() devuelve -1, indica que el modo de inserción fue activado/desactivado
                continue; // Continua con el siguiente ciclo sin insertar nada
            }

            switch (charCode) {
                case FLECHA_DERECHA:    line.moverDerecha();    break;
                case FLECHA_IZQUIERDA:  line.moverIzquierda();  break;
                case INICIO:            line.moverInicio();     break;
                case FIN:               line.moverFinal();      break;
                case BACKSPACE:         line.borrar();          break;
                case SUPRIMIR:
                    if (modoSuprimir) {
                        line.suprimir();
                        modoSuprimir = false;
                    } 
                    break;
                default:
                    if (modoInsert) {
                        line.insertar((char) charCode, modoInsert);
                    } else {
                        line.agregar((char) charCode);  // Agrega el carácter si no estamos en modo de inserción
                    }
                    break;
            }
            line.printLinea();
        }
        view.unsetRaw();

        return line.toString();
    }
}
