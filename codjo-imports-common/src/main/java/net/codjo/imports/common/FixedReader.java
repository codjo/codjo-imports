/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.IOException;
import java.io.Reader;
/**
 * Classe specifique a l'import permettant de lire des fichier de taille fixe.
 *
 * <p> <b>Attention</b> : La methode <code>getLine</code> retourne une "ligne" de la taille specifiee dans le
 * constructeur. </p>
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.8 $
 */
final class FixedReader extends java.io.BufferedReader {
    private char[] line;
    private int lineNumber = 0;
    private int lineSize;


    /**
     * Constructor for the FixedReader object
     *
     * @param reader   Description of Parameter
     * @param lineSize Description of Parameter
     */
    FixedReader(Reader reader, int lineSize) {
        super(reader);
        setLineSize(lineSize);
    }


    /**
     * Gets the LineSize attribute of the FixedReader object
     *
     * @return The LineSize value
     */
    public final int getLineSize() {
        return lineSize;
    }


    /**
     * Positionne la taille d'une ligne.
     *
     * @param newLineSize The new LineSize value
     */
    public final void setLineSize(int newLineSize) {
        lineSize = newLineSize;
        line = new char[lineSize];
    }


    /**
     * Retourne une ligne de taille fixe.
     *
     * <p> La ligne retourne est de taille fixe. </p>
     *
     * @return une "ligne" du fichier.
     *
     * @throws java.io.IOException si IO exception + si ligne incomplete (taille ligne != de celle indique)
     * @throws java.io.IOException Erreur de lecture.
     */
    @Override
    public String readLine() throws java.io.IOException {
        int size = read(line, 0, lineSize);

        if (size < 0) {
            return null;
        }
        lineNumber++;

        if (size != lineSize) {
            throw new IOException("Ligne " + lineNumber + " du fichier trops courte ("
                                  + size + " charactères au lieu de " + lineSize + ")");
        }

        return String.valueOf(line);
    }
}
