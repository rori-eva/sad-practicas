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
        lineas.get(actualLinea).borrar();
    }

    @Override
    public void suprimir() {
        lineas.get(actualLinea).suprimir();
    }

    @Override
    public String toString() {
        return lineas.get(actualLinea).toString();
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
            cursorPosition = Math.min(cursorPosition, lineas.get(actualLinea).toString().length());
        }
        printMultiLinea();
    }

    public void moverAbajo() {
        if (actualLinea < lineas.size() - 1) {
            actualLinea++;
            cursorPosition = Math.min(cursorPosition, lineas.get(actualLinea).toString().length());
        }
        printMultiLinea();
    }
}
