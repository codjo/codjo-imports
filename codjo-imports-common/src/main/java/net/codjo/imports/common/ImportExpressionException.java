/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * Exception lors de la manipulation d'une expression.
 */
public class ImportExpressionException extends ImportFailureException {
    public ImportExpressionException(Exception cause) {
        super(cause);
    }


    public ImportExpressionException(String msg, RuntimeException cause) {
        super(msg, cause);
    }
}
