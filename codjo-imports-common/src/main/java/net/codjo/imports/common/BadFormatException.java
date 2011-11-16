/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * Cette exception est lancée par une opération FieldImport afin d'indiquer que le champ
 * n'est pas au bon format.
 * 
 * <p>
 * Exemple: Type attendu est une date, type recu est un reel.
 * </p>
 *
 * @author $Author: crego $
 * @version $Revision: 1.8 $
 */
public class BadFormatException extends ImportFailureException {
    /**
     * Constructor for the BadFormatException object
     *
     * @param fi Le FieldImport ayant genere l'erreur
     * @param cause source de l'erreur
     */
    public BadFormatException(FieldImport fi,
        net.codjo.imports.common.translator.BadFormatException cause) {
        super("Mauvais format de [" + fi.getDBDestFieldName() + "]", cause);
    }
}
