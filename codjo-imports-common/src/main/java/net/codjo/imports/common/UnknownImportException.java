/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
/**
 * Exception levee de l'un import d'un fichier n'ayant pas de parametrage en base.
 */
public class UnknownImportException extends ImportFailureException {
    public UnknownImportException(File inpuFile) {
        super("Aucun paramétrage trouvé pour le fichier : " + inpuFile.getName());
        setInputFile(inpuFile);
    }
}
