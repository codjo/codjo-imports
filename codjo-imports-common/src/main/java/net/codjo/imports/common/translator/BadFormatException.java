/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Cette exception est lancée par une opération de {@link Translator} afin d'indiquer que le champ n'est pas
 * au bon format.
 *
 * <p> Exemple: Type attendu est une date, type recu est un réel. </p>
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.5 $
 */
public class BadFormatException extends Exception {
    private Exception causedByException;


    /**
     * Constructeur
     *
     * @param translator Le {@link Translator} ayant génére l'erreur
     * @param msg        message d'explication
     */
    public BadFormatException(Translator translator, String msg) {
        super(translator.getClass().getName() + " : Mauvais format : " + msg);
    }


    /**
     * Constructeur
     *
     * @param translator Le {@link Translator} ayant génére l'erreur
     * @param msg        message d'explication
     * @param cause      source de l'erreur
     */
    public BadFormatException(Translator translator, String msg, Exception cause) {
        super(translator.getClass().getName() + " : Mauvais format : " + msg);
        causedByException = cause;
    }


    /**
     * Constructor
     *
     * @param msg   message d'explication
     * @param cause source de l'erreur
     */
    public BadFormatException(String msg, Exception cause) {
        super(msg);
        causedByException = cause;
    }


    public Exception getCausedByException() {
        return causedByException;
    }


    @Override
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getCausedByException() != null) {
            writer.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(writer);
        }
    }


    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (getCausedByException() != null) {
            System.err.println(" ---- cause ---- ");
            getCausedByException().printStackTrace();
        }
    }


    @Override
    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (getCausedByException() != null) {
            stream.println(" ---- cause ---- ");
            getCausedByException().printStackTrace(stream);
        }
    }
}
