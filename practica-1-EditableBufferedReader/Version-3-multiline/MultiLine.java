/*
 * La clase MultiLine maneja todas las acciones relacionadas con múltiples líneas de texto.
 * Siguiendo el patrón MVC, esta clase también es del tipo Model
 */
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MultiLine extends Line {
    private final List<Line> lineas;  // Lista de líneas de texto
    private int cursorFila;           // Indica la fila en la que se encuentra el cursor

    /**
     * Constructor de de la clase MultiLine
     * Inicializa una nueva lista de líneas con una línea vacía
     */
    public MultiLine() {
        lineas = new ArrayList<>();
        lineas.add(new Line());
        cursorFila = 0;
        this.cursorColumna = 0;  // Tendremos un cursorColumna para MultiLine y se irá actualizando
    }

    /**
     * Obtiene la posición de la fila en la que se encuentra el cursor
     * 
     * @return  La posición del cursor en la fila actual
     */
    public int getCursorFila() {
        return cursorFila;
    }

    /**
     * Obtiene la cantidad de líneas en la lista de líneas
     * 
     * @return La longitud de la lista de lineas
     */
    public int getLineasLength() {
        return lineas.size();
    }

    /**
     * Establece la nueva posición del cursorFila
     * 
     * @param nuevoCursorFila    La nueva posición del cursorFila
     */
    public void setCursorFila(int nuevoCursorFila) {
        if ((nuevoCursorFila >= 0) && (nuevoCursorFila <= lineas.size()-1 || nuevoCursorFila <= super.maxFilas)) {
            cursorFila = nuevoCursorFila;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Mueve el cursor una fila hacia arriba
     */
    public void moverArriba() {
        if (cursorFila > 0) {
            cursorFila--;
            this.cursorColumna = Math.min(this.cursorColumna, lineas.get(cursorFila).getLineLength());
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Mueve el cursor una fila hacia abajo
     */
    public void moverAbajo() {
        if (cursorFila < lineas.size()-1) {
            cursorFila++;
            this.cursorColumna = Math.min(this.cursorColumna, lineas.get(cursorFila).getLineLength());
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Mueve el cursor una columna a la derecha
     */
    @Override
    public void moverDerecha() {
        Line actual = lineas.get(cursorFila);
        int longitud = actual.getLineLength();
        if (this.cursorColumna < longitud) {
            actual.setCursorColumna(this.cursorColumna);
            actual.moverDerecha();
            this.cursorColumna = actual.getCursorColumna();
        } else if (cursorFila < lineas.size()-1) {
            cursorFila++;
            this.cursorColumna = 0;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Mueve el cursor una columna a la izquierda
     */
    @Override
    public void moverIzquierda() {
        if (this.cursorColumna > 0) {
            lineas.get(cursorFila).setCursorColumna(this.cursorColumna);
            lineas.get(cursorFila).moverIzquierda();
            this.cursorColumna = lineas.get(cursorFila).getCursorColumna();
        } else if ((this.cursorColumna == 0) && (cursorFila > 0)) {
            cursorFila--;
            this.cursorColumna = lineas.get(cursorFila).getLineLength();
            lineas.get(cursorFila).setCursorColumna(cursorColumna);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Movemos el cursor al inicio de la línea actual
     */
    @Override
    public void moverInicio() {
        lineas.get(cursorFila).moverInicio();
        this.cursorColumna = 0;
        setChanged();
        notifyObservers();
    }

    /**
     * Movemos el cursor al final de la línea actual
     */
    @Override
    public void moverFinal() {
        lineas.get(cursorFila).moverFinal();
        this.cursorColumna = lineas.get(cursorFila).getCursorColumna();
        setChanged();
        notifyObservers();
    }

    /**
     * Borra el caracter actual y retrocede una posición el cursorColumna
     * Si se está al inicio de la linea y se desea borrar, el contenido de la línea actual se concatena con la anterior
     */
    @Override
    public void borrar() {
        lineas.get(cursorFila).setCursorColumna(this.cursorColumna);  // Actualiza el cursorColumna de la linea actual

        if (cursorFila > 0 && this.cursorColumna == 0) {
            // Mover el contenido de la línea actual a la anterior y eliminar la línea actual
            Line actual = lineas.remove(cursorFila);
            Line lineaAnterior = lineas.get(cursorFila - 1);
            this.cursorColumna = lineaAnterior.getLineLength()-1;
            lineaAnterior.concatenarLineas(actual);
            cursorFila--;
            lineaAnterior.setCursorColumna(this.cursorColumna); 
        } else if (cursorFila >= 0 && this.cursorColumna > 0) {
            lineas.get(cursorFila).borrar();
        }
        this.cursorColumna = lineas.get(cursorFila).getCursorColumna();  // Actualizamos el cursorColumna de MultiLine
        setChanged();
        notifyObservers();
    }

    /**
     * Borra el carácter siguiente al cursorColumna
     * Si se está al final de la línea y se desea suprimir, la linea siguiente se concatena con la actual
     */
    @Override
    public void suprimir() {
        Line actual = lineas.get(cursorFila);

        if (actual.getCursorColumna() < actual.getLineLength()) {
            actual.suprimir();
        } else if ((actual.getCursorColumna() == actual.getLineLength()) && (cursorFila < lineas.size() - 1)) {
            Line siguienteLinea = lineas.remove(cursorFila+1);
            actual.concatenarLineas(siguienteLinea);
        }
        this.cursorColumna = actual.getCursorColumna();
        setChanged();
        notifyObservers();
    }

    /**
     * Inserta carácteres nuevos en la línea correspondiente
     * Actualiza el cursorColumna en caso que sea necesario
     * 
     * @param c    Carácter a insertar
     */
    @Override
    public void insertar(char c) {
        Line actual = lineas.get(cursorFila);
        actual.setCursorColumna(this.cursorColumna);  // Actualiza el cursorColumna de la linea actual

        if (this.cursorColumna >= 0 && (this.cursorColumna < maxCols || this.cursorColumna < actual.getLineLength())) {
            actual.insertar(c);
        } else if (this.cursorColumna == maxCols) {
            nuevaLinea();
            lineas.get(cursorFila).insertar(c);
        }
        this.cursorColumna = lineas.get(cursorFila).getCursorColumna();  // Actualiza cursorColumna de MultiLine
        setChanged();
        notifyObservers();
    }

    /**
     * Al teclear INSERT en una línea, se alterna el modoInsert en todas de lineas
     */
    @Override
    public void alternarModoInsert() {
        for(Line linea : lineas) {
            linea.alternarModoInsert();
        }
        this.modoInsert = !this.modoInsert;
    }

    /**
     * Crea una nueva linea cuando tecleamos el botón de ENTER
     * Recorta la linea actual si se presiona ENTER entre los carácteres
     */
    public void nuevaLinea() {
        Line actual = lineas.get(cursorFila);

        if (this.cursorColumna == actual.getLineLength()) {
            lineas.add(cursorFila+1, new Line());
        } else {
            Line nueva = actual.recortarDesde(this.cursorColumna);
            lineas.add(cursorFila+1, nueva);
        }

        if (this.modoInsert) {
            lineas.get(cursorFila+1).alternarModoInsert();
        }

        cursorFila++;
        this.cursorColumna=0;
        setChanged();
        notifyObservers();
    }

    /**
     * Devuelve la representación en cadena de las líneas
     * 
     * @return    La representación en cadena de la lista de líneas
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<lineas.size(); i++) {
            if (i == cursorFila) {
                sb.append("\u001b[43m");    // Solo la línea que estamos editando será amarillo
            } 
            sb.append('\r').append(lineas.get(i).toString()).append("\u001b[0m\n");
        }

        return sb.toString();
    }
}