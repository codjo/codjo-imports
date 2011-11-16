/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
/**
 * Classe permettant d'importer une chaîne de caractère.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.8 $
 */
class StringTranslator implements Translator {
    private int destSqlType;

    StringTranslator() {
        this.destSqlType = java.sql.Types.VARCHAR;
    }


    StringTranslator(int destSqlType) {
        this.destSqlType = destSqlType;
    }

    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.VARCHAR.
     */
    public int getSQLType() {
        return destSqlType;
    }


    /**
     * Traduction du champ en objet String.
     * 
     * <p></p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     */
    public Object translate(String field) {
        if ((field != null) && (!"".equals(field))) {
            return field;
        }
        return null;
    }
}
