/*
 * La clase Line maneja todas las acciones relacionadas con la línea de texto.
 * Model class
 */
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Line extends Observable {
    // Atributos de la clase Line
    private final StringBuilder line;    // La línea de texto que se está editando
    protected boolean modoInsert;        // Indica si está en modo de inserción o sustitución
    protected int cursorColumna;         // Posicion del cursor (columna actual)
    protected int maxFilas, maxCols;     // Dimensiones máximas de la ventana de texto
    
    /**
     * Constructor de la clase Line, inicializa una línea vacía
     */
    public Line() {
        this.line = new StringBuilder("");
        cursorColumna = 0;
        modoInsert = false;
    }

    /**
     * Constructor de la clase Line con texto inicial
     * 
     * @param string    El texto inicial de la línea
     */
    public Line(StringBuilder string) {
        this.line = string;
        cursorColumna = 0;
        modoInsert = false;
    }

    /**
     * Obtiene la posición actual del cursor en la línea
     *
     * @return    La posición del cursor (columna actual)
     */
    public int getCursorColumna() {
        return cursorColumna;
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
     * Comprueba si la linea está en modo INSERT
     *
     * @return    true si está en modo INSERT, false en caso contrario
     */
    public boolean isModoInsert() {
        return modoInsert;
    }

    /**
     * Establece las dimensiones de la terminal
     * 
     * @param maxFilas    El número máximo de filas de la terminal
     * @param maxCols     El número máximo de columnas de la terminal
     */
    public void setDimensions(int maxFilas, int maxCols) {
        this.maxFilas = maxFilas;
        this.maxCols = maxCols;
    }

    /**
     * Establece la nueva posición del cursorColumna
     * 
     * @param nuevoCursorColumna    La nueva posición del cursorColumna
     */
    public void setCursorColumna(int nuevoCursorColumna) {
        if (nuevoCursorColumna >= 0 && (nuevoCursorColumna <= this.getLineLength() || nuevoCursorColumna <= maxCols)) {
            this.cursorColumna = nuevoCursorColumna;
            setChanged();
            notifyObservers();
        }
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
     * Reemplaza el carácter existente si está en modo INSERT
     * Tecla INSERT
     * 
     * @param c  El carácter a insertar
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
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Alterna el modoInsert en cuanto es tecleada de nuevo la tecla INSERT
     */
    public void alternarModoInsert() {
        modoInsert = !modoInsert;
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
     * No afecta en la posición del cursorColumna
     */
    public void suprimir() {
        if (cursorColumna < line.length()) {
            line.deleteCharAt(cursorColumna);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Concatena una nueva linea al final de la línea actual
     * 
     * @param linia    La línea a concatenar
     */
    public void concatenarLineas(Line linia) {
        line.append(linia.toString());
        setChanged();
        notifyObservers();
    }

    /**
     * Recorta la linea actual desde una posición determinada
     * 
     * @param posicion    La posición desde donde se recortará la línea actual
     * @return            Nueva linea con el texto desde la posición indicada
     */
    public Line recortarDesde(int posicion) {
        String texto = line.substring(posicion, getLineLength());
        line.delete(posicion, getLineLength());

        return new Line(new StringBuilder(texto));
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
