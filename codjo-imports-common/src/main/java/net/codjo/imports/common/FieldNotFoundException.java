/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * Cette exception est lancée par une opération FieldImport afin d'indiquer que le champ
 * est introuvable dans la ligne.
 * 
 * <p>
 * NB: c'est-à-dire la ligne est trop courte.
 * </p>
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.8 $
 */
public class FieldNotFoundException extends ImportFailureException {
    public FieldNotFoundException(String msg) {
        super(msg);
    }


    public FieldNotFoundException(String msg, Exception cause) {
        super(msg, cause);
    }
}
