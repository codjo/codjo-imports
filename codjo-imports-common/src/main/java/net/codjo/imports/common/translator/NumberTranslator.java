/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
/**
 * Classe permettant d'importer un nombre.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.7 $
 */
class NumberTranslator implements Translator {
    private String decimalSeparator = null;
    private boolean isInteger = false;
    private int destSqlType;


    /**
     * Constructeur dédié à l'import d'entiers (pour test).
     */
    NumberTranslator() {
        this(java.sql.Types.INTEGER);
    }


    NumberTranslator(int destSqlType) {
        this.destSqlType = destSqlType;
        isInteger = true;
    }


    NumberTranslator(char separator) {
        this(java.sql.Types.NUMERIC, separator);
    }


    /**
     * Constructeur dédié à l'import de rééls (pour test).
     *
     * @param destSqlType TODO
     * @param separator   Séparateur de décimal.
     */
    NumberTranslator(int destSqlType, char separator) {
        this.destSqlType = destSqlType;
        init(separator);
    }


    /**
     * Retourne le separateur de decimal
     *
     * @return "." ou ","
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.INTEGER ou FLOAT.
     */
    public int getSQLType() {
        return destSqlType;
    }


    /**
     * Traduction du champ en objet Integer ou Float.
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @throws BadFormatException Mauvais format de nombre
     */
    public Object translate(String field) throws BadFormatException {
        if (field == null) {
            return null;
        }

        field = removeAllCharOccurrence(field, ' ');
        if ("".equals(field)) {
            return null;
        }

        try {
            if (isInteger) {
                return new Integer(field);
            }
            else {
                return parseDecimal(field);
            }
        }
        catch (NumberFormatException ex) {
            String message = ex.getMessage();
            if (message == null) {
                message = field;
            }
            throw new BadFormatException(this, message, ex);
        }
    }


    /**
     * Inialise.
     *
     * @param separator separator de decimal.
     *
     * @throws IllegalArgumentException si le séparateur décimal n'est pas supporté.
     */
    private void init(char separator) {
        if (separator != '.' && separator != ',') {
            throw new IllegalArgumentException("Separateur de décimal "
                                               + "non supporté (" + separator + ")");
        }

        decimalSeparator = "" + separator;

        isInteger = false;
    }


    /**
     * DOCUMENT ME!
     */
    private Number parseDecimal(String field) throws BadFormatException {
        if (",".equals(decimalSeparator)) {
            if (field.contains(".")) {
                throw new BadFormatException(this, field);
            }
            field = field.replace(',', '.');
        }
        return new java.math.BigDecimal(field);
    }


    static String removeAllCharOccurrence(String str, char aChar) {
        StringBuilder tmp = new StringBuilder(str);
        int index = 0;
        while (index < tmp.length()) {
            if (tmp.charAt(index) == aChar) {
                tmp.deleteCharAt(index);
            }
            else {
                index++;
            }
        }
        return tmp.toString();
    }
}
