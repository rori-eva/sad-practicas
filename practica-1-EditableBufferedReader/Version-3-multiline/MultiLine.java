/*
 * La clase MultiLine maneja todas las acciones relacionadas con las líneas de texto.
 * Siguiendo el patrón MVC esta clase también es del tipo Model
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class MultiLine extends Line {
    private final List<Line> lineas;  // Lista de líneas
    private int cursorFila;    // Indica la posición de fila en la que está el cursor

    public MultiLine() {
        lineas = new ArrayList<>();
        lineas.add(new Line());
        cursorFila = 0;
    }

    public void nuevaLinea() {
        lineas.get(cursorFila).insertar('\n');
        cursorFila++;
        lineas.add(cursorFila, new Line());
        updateCursorColumnaHerencia();
        setChanged();
        notifyObservers();
    }

    public Line getLinea(int fila) {
        return lineas.get(fila);
    }

    public int getCursorFila() {
        return cursorFila;
    }

    public void setCursorFila(int nuevoCursorFila) throws IOException{
        int maxFilaTerm = Console.getScreenTerminalSize()[1];
        if ((nuevoCursorFila >= 0 && nuevoCursorFila <= lineas.size()-1) && (nuevoCursorFila <= maxFilaTerm)) {
            this.cursorFila = nuevoCursorFila;
            setChanged();
            notifyObservers();
        }
    }

    public void moverArriba() {
        if (cursorFila > 0) {
            cursorFila--;
            setChanged();
            notifyObservers();
        }
    }

    public void moverAbajo() {
        if (cursorFila < lineas.size()-1) {
            cursorFila++;
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public void moverDerecha() {
        if (cursorFila < lineas.size()-1) {
            cursorFila++;
            try { lineas.get(cursorFila).setCursorColumna(0); } catch (IOException e) { }
        } else {
            super.moverDerecha();
        }
        updateCursorColumnaHerencia();
        setChanged();
        notifyObservers();
    }

    @Override
    public void moverIzquierda() {
        if (cursorFila > 0) {
            cursorFila--;
        } else {
            super.moverIzquierda();
        }
        updateCursorColumnaHerencia();
        setChanged();
        notifyObservers();
    }

    @Override
    public void borrar() {
        if (cursorFila > 0) {
            // Mover el contenido de la línea actual a la anterior y eliminar la línea actual
            Line lineaAnterior = lineas.get(cursorFila - 1);
            Line lineaActual = lineas.get(cursorFila);
            lineaAnterior.concatenarConAnterior(lineaActual);
            lineas.remove(cursorFila);
            cursorFila--;
        } else if (lineas.get(cursorFila).getCursorColumna() > 0) {
            lineas.get(cursorFila).borrar();
        }
        updateCursorColumnaHerencia();
        setChanged();
        notifyObservers();
    }

    @Override
    public void suprimir() {
        Line lineaActual = lineas.get(cursorFila);
        if (lineaActual.getCursorColumna()< lineaActual.getLineLength()) {
            lineaActual.suprimir();
        } else if (lineaActual.getCursorColumna() == lineaActual.getLineLength() && cursorFila < lineas.size() - 1) {
            Line siguienteLinea = lineas.get(cursorFila + 1);
            lineaActual.concatenarConAnterior(siguienteLinea);
            lineas.remove(cursorFila+1);
        }
        updateCursorColumnaHerencia();
        setChanged();
        notifyObservers();
    }

    @Override
    public void insertar(char c) {
        Line currentLine = lineas.get(cursorFila);
        int maxCols;
        try {
            int[] sizeTerminal = Console.getScreenTerminalSize();
            maxCols = sizeTerminal[1];

            if (currentLine.getCursorColumna() >= currentLine.getLineLength()) {
                if((cursorFila == lineas.size()-1) && (currentLine.getCursorColumna() == maxCols)) {
                    Line newLine = new Line();
                    lineas.add(newLine);
                    cursorFila++;
                    newLine.insertar(c);
                } else {
                    lineas.get(cursorFila).insertar(c);
                }
            } else {
                currentLine.insertar(c);
            }
            updateCursorColumnaHerencia();
            setChanged();
            notifyObservers();
        } catch (IOException e) { }
    }

    /*
     * 
     */
    public void updateCursorColumnaHerencia() {
        this.cursorColumna = lineas.get(cursorFila).getCursorColumna();
    }

    /**
     * Devuelve la representación en cadena de las líneas
     * 
     * @return    La representación en cadena de la lista de líneas
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Line line: lineas) {
            sb.append('\r').append(line.toString());
        }

        return sb.toString();
    }
}
