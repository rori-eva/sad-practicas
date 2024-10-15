/*
 * Esta interfaz define las constantes necesarias para la entrada desde el teclado 
 */

public interface InterfaceConstantes {

    // CONSTANTES   -----------------------------------------------------------
    // Teclas en ASCII
    public static final int ENTER            = 10;
    public static final int ESCAPE           = 27;
    public static final int FLECHA_ARRIBA    = -65;
    public static final int FLECHA_ABAJO     = -66;
    public static final int FLECHA_DERECHA   = -67;
    public static final int FLECHA_IZQUIERDA = -68;
    public static final int INICIO           = -72;
    public static final int FIN              = -70;
    public static final int INSERT           = 50;
    public static final int SUPRIMIR         = 51;
    public static final int BACKSPACE        = 2;
    public static final int CR               = 13;

    // Comandos del cursor
    public static final String DCHA_CMD      = "\u001b[1C";
    public static final String IZQ_CMD       = "\u001b[1D";
    public static final String INICIO_CMD    = "\u001b[0D";
    public static final String FIN_CMD       = "\u001b[$C";
    public static final String GUARDAR_POS   = "\u001b[s";
    public static final String RESTAURAR_POS = "\u001b[u";

    // Comandos de la pantalla
    public static final String LIMPIAR_PANTALLA = "\u001b[2J";
    public static final String LIMPIAR_LINEA =  "\u001b[2K";

    // FUNCIONES   ------------------------------------------------------------
    /**
     * Mueve el cursor en una posición determinada
     * 
     * @param fila
     * @param columna
     */
    public static void moveCursorPosition(int fila, int columna) {
        System.out.print("\u001b[" + fila + ";" + (columna + 1) + "H");
    }

}
