/*
 * La clase Line maneja todas las acciones relacionadas con la línea de texto.
 * Model class
 */
import java.io.IOException;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Line extends Observable {
    // Atributos de la clase Line
    private final StringBuilder line;     // La linea a dibujar
    private boolean modoInsert;     // Modo insertar o sustituir
    protected int cursorColumna;  // Posicion del cursor (indica la columna en la que está el cursor)

    /**
     * Constructor de la clase Line
     */
    public Line() {
        this.line = new StringBuilder("");
        cursorColumna = 0;
        modoInsert = false;
    }

    /**
     * Obtiene la posición actual del cursor en la línea
     *
     * @return    La posición del cursor, la columna en la que está
     */
    public int getCursorColumna() {
        return cursorColumna;
    }

    public void setCursorColumna(int nuevoCursorColumna) throws IOException {
        int maxColumnaTerm = Console.getScreenTerminalSize()[1];
        if ((nuevoCursorColumna >= 0 && nuevoCursorColumna <= this.getLineLength()-1) && (nuevoCursorColumna <= maxColumnaTerm)) {
            this.cursorColumna = nuevoCursorColumna;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Obtiene la longitud de la línea
     *
     * @return    La longitud de la línea
     */
    public int getLineLength() {
        return line.length();
    }

    /**
     * Mueve el cursor una posición a la derecha, tecla FLECHA DERECHA
     */
    public void moverDerecha() {
        if (cursorColumna < line.length()) {
            cursorColumna++;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Mueve el cursor una posición a la izquierda, tecla FLECHA IZQUIERDA
     */
    public void moverIzquierda() {
        if (cursorColumna > 0) {
            cursorColumna--;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Mueve el cursor al inicio de la línea, tecla INICIO
     */
    public void moverInicio() {
        cursorColumna = 0;
        setChanged();
        notifyObservers();
    }

    /**
     * Mueve el cursor al final de la línea, tecla FIN
     */
    public void moverFinal() {
        cursorColumna = line.length();
        setChanged();
        notifyObservers();
    }

    /**
     * Inserta un carácter en la posición actual del cursor,
     * reemplazando el carácter existente si se encuentra en modo INSERT
     * Tecla INSERT
     * 
     * @param c             El carácter a insertar
     */
    public void insertar(char c) {
        if(c>=32 && c<=126) {
            // Si el cursor está al final de la línea, simplemente añadimos el carácter
            if (cursorColumna >= line.length()) {
                line.append(c);
            } else {
                // Reemplaza el carácter en la posición actual
                if (this.modoInsert) {
                    // Si estamos en modo de inserción, reemplazamos el carácter existente
                    line.setCharAt(cursorColumna, c);
                } else {
                    // Si no estamos en modo de inserción, desplazamos hacia la derecha e insertamos
                    line.insert(cursorColumna, c);
                }
            }
            cursorColumna++;   // Avanzamos el cursor después de la inserción/reemplazo

        } else if (c == '\n') {
            line.append('\n');
            cursorColumna=0;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Alterna el boolean modoInsert en cuanto es tecleada de nuevo la tecla INSERT
     */
    public void alternarModoInsert() {
        this.modoInsert = !this.modoInsert;
    }

    /**
     * Borra el carácter anterior al cursor, tecla BACKSPACE
     * Retrocedemos una posición el cursor y,
     * movemos los carácteres de la derecha un paso a la izquierda
     */
    public void borrar() {
        if (cursorColumna > 0) {
            line.deleteCharAt(cursorColumna - 1);
            cursorColumna--;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Suprime el carácter en la posición actual del cursor, tecla DEL (suprimir)
     */
    public void suprimir() {
        if (cursorColumna < line.length()) {
            line.deleteCharAt(cursorColumna);
            setChanged();
            notifyObservers();
        }
    }

    public void concatenarConAnterior(Line newLine) {
        line.append(newLine.toString());
        setChanged();
        notifyObservers();
    }

    /**
     * Devuelve la representación en cadena de la línea
     * 
     * @return    La representación en cadena de la línea
     */
    @Override
    public String toString() {
        return line.toString();
    }
}
