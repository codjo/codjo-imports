/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * Classe BlankFieldImport.
 */
public class BlankFieldImport extends FieldImport {
    /**
     * Constructor pour des FieldImport non <code>Persistent</code>.
     *
     * @param dbName           Nom du champ DB de destination.
     * @param fieldType        type de fichier a importer
     * @param decimalSeparator seprateur utilise dans le fichier a importer
     * @param inputDateFormat  format des dates utilise dans le fichier a importer
     */
    public BlankFieldImport(String dbName, char fieldType, String decimalSeparator,
                            String inputDateFormat) {
        super(dbName, fieldType, decimalSeparator, inputDateFormat);
    }


    public BlankFieldImport(String dbName, char sourceFieldType, char destFieldType,
                            String decimalSeparator, String inputDateFormat) {
        super(dbName, sourceFieldType, destFieldType, decimalSeparator, inputDateFormat);
    }


    /**
     * Constructor pour des FieldImport non <code>Persistent</code>.
     *
     * @param dbName Nom du champ DB de destination.
     */
    public BlankFieldImport(String dbName) {
        super(dbName);
    }


    @Override
    public Object convertFieldToSQL(String ligneFichier) {
        return ligneFichier;
    }
}
