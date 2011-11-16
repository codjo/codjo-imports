/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * Classe abstraite pour les exceptions de type {@link RuntimeException}. Cette classe
 * permet de gérer la cause de l'exception.
 *
 * @version $Revision: 1.2 $
 */
class FilterException extends RuntimeException {
    private Throwable cause;

    FilterException(String message) {
        super(message);
    }


    FilterException(String message, Throwable cause) {
        this(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }


    public void printStackTrace(PrintWriter writer) {
        synchronized (writer) {
            super.printStackTrace(writer);
            if (getCause() != null) {
                writer.println(" ---- cause ---- ");
                getCause().printStackTrace(writer);
            }
        }
    }


    public void printStackTrace() {
        printStackTrace(System.err);
    }


    public void printStackTrace(PrintStream stream) {
        synchronized (stream) {
            super.printStackTrace(stream);
            if (getCause() != null) {
                stream.println(" ---- cause ---- ");
                getCause().printStackTrace(stream);
            }
        }
    }
}
