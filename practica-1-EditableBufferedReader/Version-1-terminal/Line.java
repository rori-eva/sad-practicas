/*
 * La clase Line maneja todas las acciones relacionadas con la línea de texto.
 */

public class Line {
    private StringBuilder line;  // La linea a dibujar
    private int cursorPosition;  // Posicion del cursor

    public Line() {
        this.line = new StringBuilder("");
        cursorPosition = 0;
    }

    /**
    * Establece la posición del cursor en la línea
    *
    * @param cursorPosition    La nueva posición del cursor
    */
    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    /**
     * Obtiene la posición actual del cursor en la línea
     *
     * @return    La posición del cursor
     */
    public int getCursorPosition() {
        return cursorPosition;
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
        if (cursorPosition < line.length()) {
            System.out.print(InterfaceConstantes.DCHA_CMD);
            cursorPosition++;
        }
    }

    /**
     * Mueve el cursor una posición a la izquierda, tecla FLECHA IZQUIERDA
     */
    public void moverIzquierda() {
        if (cursorPosition > 0) {
            System.out.print(InterfaceConstantes.IZQ_CMD);
            cursorPosition--;
        }
    }

    /**
     * Mueve el cursor al inicio de la línea, tecla INICIO
     */
    public void moverInicio() {
        System.out.print(InterfaceConstantes.INICIO_CMD);
        cursorPosition = 0;
    }

    /**
     * Mueve el cursor al final de la línea, tecla FIN
     */
    public void moverFinal() {
        System.out.print(InterfaceConstantes.FIN_CMD);
        cursorPosition = line.length();
    }

    /**
     * Agrega un carácter en la posición actual del cursor
     * Y avanzamos una posición del cursor
     * 
     * @param c    El carácter a agregar
     */
    public void agregar(char c) {
        line.insert(cursorPosition, c);
        cursorPosition++;
    }

    /**
     * Inserta un carácter en la posición actual del cursor,
     * reemplazando el carácter existente si se encuentra en modo INSERT
     * Tecla INSERT
     * 
     * @param c             El carácter a insertar
     * @param modoInsert    Indica si se encuentra en modo INSERT
     */
    public void insertar(char c, boolean modoInsert) {
        // Si el cursor está al final de la línea, simplemente añadimos el carácter
        if (cursorPosition >= line.length()) {
            line.append(c);
        } else {
            // Reemplaza el carácter en la posición actual
            if (modoInsert) {
                // Si estamos en modo de inserción, reemplazamos el carácter existente
                line.setCharAt(cursorPosition, c);
            } else {
                // Si no estamos en modo de inserción, desplazamos hacia la derecha e insertamos
                line.insert(cursorPosition, c);
            }
        }
        // Avanzamos el cursor después de la inserción/reemplazo
        cursorPosition++;
    }

    /**
     * Borra el carácter anterior al cursor, tecla BACKSPACE
     * Y retrocedemos una posición el cursor
     */
    public void borrar() {
        if (cursorPosition > 0) {
            line.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }

    /**
     * Suprime el carácter en la posición actual del cursor, tecla DEL (suprimir)
     */
    public void suprimir() {
        if (cursorPosition < line.length()) {
            line.deleteCharAt(cursorPosition);
        }
    }

    /**
     * Devuelve la representación en cadena de la línea, eliminando caracteres de escape
     * 
     * @return    La representación en cadena de la línea
     */
    @Override
    public String toString() {
        return line.toString().replaceAll("\\u001b\\[[^m]*m", "");
    }

    public void printLinea() {
        System.out.print(InterfaceConstantes.LIMPIAR_PANTALLA);
        InterfaceConstantes.moveCursorPosition(1, this.cursorPosition);
        System.out.print(InterfaceConstantes.GUARDAR_POS);
        System.out.print("\r" + this.line.toString());
        System.out.print(InterfaceConstantes.RESTAURAR_POS);
    }
}
