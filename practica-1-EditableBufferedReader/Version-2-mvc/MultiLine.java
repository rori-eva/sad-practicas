/*
 * La clase MultiLine maneja todas las acciones relacionadas con las líneas de texto.
 * Siguiendo el patrón MVC esta clase también es del tipo Model
 */
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Observable;

@SuppressWarnings("deprecation")
public class MultiLine extends Line {
    private List<Line> lineas;
    private int actualLinea;
    protected int cursorPosition;
    
    public MultiLine() {
        this.lineas = new ArrayList<>();
        this.lineas.add(new Line());
        this.actualLinea = 0;
        this.cursorPosition = 0;
    }

    @Override
    public void agregar(char c) {
        lineas.get(actualLinea).agregar(c);
    }

    @Override
    public void insertar(char c, boolean modoInsert) {
        lineas.get(actualLinea).insertar(c, modoInsert);
    }

    @Override
    public void borrar() {
        if (cursorPosition == 0 && actualLinea > 0) {
            // Mover el contenido de la línea actual a la anterior y eliminar la línea actual
            Line lineaAnterior = lineas.get(actualLinea - 1);
            Line lineaActual = lineas.get(actualLinea);
            cursorPosition = lineaAnterior.toString().length();
            lineaAnterior.concatenarConAnterior(lineaActual);
            lineas.remove(actualLinea);
            actualLinea--;
        } else if (cursorPosition > 0) {
            lineas.get(actualLinea).borrar();
            cursorPosition--;
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public void suprimir() {
        Line lineaActual = lineas.get(actualLinea);
        if (cursorPosition < lineaActual.getLineLength()) {
            lineaActual.suprimir();
        } else if (cursorPosition == lineaActual.getLineLength() && actualLinea < lineas.size() - 1) {
            Line siguienteLinea = lineas.get(actualLinea + 1);
            lineaActual.concatenarConAnterior(siguienteLinea);
            lineas.remove(actualLinea+1);
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Line line: lineas) {
            sb.append(line.toString()).append("\n");
        }
        return sb.toString();
    }

    public void printMultiLinea() {
        System.out.print(InterfaceConstantes.LIMPIAR_PANTALLA);
        // Imprimimos todas las líneas y restauramos la posición del cursor después de cada línea
        for (int i = 0; i < lineas.size(); i++) {
            Line currentLine = lineas.get(i);
            InterfaceConstantes.moveCursorPosition(i + 1, 1);
            System.out.print(currentLine.toString());
        }
        // Movemos el cursor a la línea y columna correspondiente según la posición del cursor actual
        InterfaceConstantes.moveCursorPosition(actualLinea + 1, cursorPosition + 1);
        System.out.print(InterfaceConstantes.GUARDAR_POS);
    }

    public void moverArriba() {
        if (actualLinea > 0) {
            actualLinea--;
            //cursorPosition = Math.min(cursorPosition, lineas.get(actualLinea).toString().length());
            setChanged();
            notifyObservers();
        }
        //printMultiLinea();
    }

    public void moverAbajo() {
        if (actualLinea < lineas.size() - 1) {
            actualLinea++;
            //cursorPosition = Math.min(cursorPosition, lineas.get(actualLinea).toString().length());
            setChanged();
            notifyObservers();
        }
        //printMultiLinea();
    }

    public void nuevaLinea() {
        lineas.add(actualLinea+1, new Line());
        actualLinea++;
        super.cursorPosition = 0;
        setChanged();
        notifyObservers();
    }

    public Line getActualLinea(int actualLinea) {
        return lineas.get(actualLinea);
    }
}
