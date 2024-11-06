/*
 * La clase Line maneja todas las acciones relacionadas con la línea de texto.
 */

public class Line {
    // Atributos de la clase Line
    private StringBuilder line;  // La linea a dibujar
    private int cursorPosition;  // Posicion del cursor
    private boolean modoInsert;  // Modo insertar o sustituir

    /**
     * Constructor de la clase Line
     */
    public Line() {
        this.line = new StringBuilder("");
        cursorPosition = 0;
        modoInsert = false;
    }

    /**
     * Obtiene la posición actual del cursor en la línea
     *
     * @return    La posición del cursor, la columna en la que está
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
            cursorPosition++;
            System.out.print("\u001b[1C");
        }
    }

    /**
     * Mueve el cursor una posición a la izquierda, tecla FLECHA IZQUIERDA
     */
    public void moverIzquierda() {
        if (cursorPosition > 0) {
            cursorPosition--;
            System.out.print("\u001b[1D");
        }
    }

    /**
     * Mueve el cursor al inicio de la línea, tecla INICIO
     */
    public void moverInicio() {
        cursorPosition = 0;
        System.out.print("\u001b[G");
    }

    /**
     * Mueve el cursor al final de la línea, tecla FIN
     */
    public void moverFinal() {
        cursorPosition = line.length();
        System.out.print("\u001b["+(cursorPosition+1)+"G");
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
            if (cursorPosition >= line.length()) {
                line.append(c);
                System.out.print(c);
            } else {
                // Reemplaza el carácter en la posición actual
                if (this.modoInsert) {
                    // Si estamos en modo de inserción, reemplazamos el carácter existente
                    line.setCharAt(cursorPosition, c);
                    System.out.print(c);
                } else {
                    // Si no estamos en modo de inserción, desplazamos hacia la derecha e insertamos
                    line.insert(cursorPosition, c);
                    for(int i=cursorPosition; i<line.length(); i++) System.out.print(line.charAt(i));
                    //System.out.print("\u001b["+cursorPosition+"G");
                }
            }
            cursorPosition++;   // Avanzamos el cursor después de la inserción/reemplazo
            System.out.print("\u001b["+(cursorPosition+1)+"G");
        }
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
        if (cursorPosition > 0) {
            line.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            System.out.print("\u001b[1D");
            for(int i=cursorPosition; i<line.length();i++)   System.out.print(line.charAt(i));
            System.out.print(" ");
            System.out.print("\u001b["+(line.length()-cursorPosition+1)+"D");
        }
    }

    /**
     * Suprime el carácter en la posición actual del cursor, tecla DEL (suprimir)
     */
    public void suprimir() {
        if (cursorPosition < line.length()) {
            line.deleteCharAt(cursorPosition);
            for(int i=cursorPosition; i<line.length();i++)   System.out.print(line.charAt(i));
            System.out.print(" ");
            System.out.print("\u001b["+(line.length()-cursorPosition+1)+"D");
        }
    }

    /**
     * Devuelve la representación en cadena de la línea, eliminando caracteres de escape
     * 
     * @return    La representación en cadena de la línea
     */
    @Override
    public String toString() {
        return line.toString();
    }
}
