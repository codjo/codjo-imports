/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Classe permettant d'importer une Date.
 *
 * <p> Le format de date en entrée peut être de 6 types différents. </p>
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.10 $
 */
class DateTranslator implements Translator {
    private static final String DATE_CHARACTERS = "GyMdhHmsSEDFwWakKz";
    private static final String DATE_SYMBOLS = ":,-/. ";
    /**
     * Type de format de date en entrée
     */
    private DateFormat formatIN;
    private String inputDateFormat;
    /**
     * Date NULL. ex: pour le format YYYY_MM_DD_SLASH la nullDate est "0000/00/00"
     */
    private String nullDate;
    private int destSqlType;


    DateTranslator(String dateFormat) {
        this(java.sql.Types.DATE, dateFormat);
    }


    /**
     * Constructor for test.
     *
     * @param destSqlType TODO
     * @param dateFormat  Format de date en entrée.
     */
    DateTranslator(int destSqlType, String dateFormat) {
        this.destSqlType = destSqlType;
        init(dateFormat);
    }


    /**
     * Retourne le Format de Date en entree
     *
     * @return Le INPUT_DATE_FORMAT.
     */
    public String getInputDateFormat() {
        return inputDateFormat;
    }


    /**
     * Retourne le type SQL de l'objet produit par convertFieldToSQL.
     *
     * @return java.sql.Types.DATE.
     */
    public int getSQLType() {
        return destSqlType;
    }


    /**
     * Traduction du champ en objet Date SQL.
     *
     * <p> La date retournée est de type java.sql.Date. </p>
     *
     * @param field Champ à traduire.
     *
     * @return Le champ en format SQL.
     *
     * @throws BadFormatException Mauvais format de date.
     */
    public Object translate(String field) throws BadFormatException {
        if (field == null) {
            return null;
        }
        if ("".equals(field)) {
            return null;
        }
        if (field.equals(nullDate)) {
            return null;
        }

        try {
            Date date = formatIN.parse(field);
            detectUndectedError(field);
            return new java.sql.Date(date.getTime());
        }
        catch (java.text.ParseException ex) {
            throw new BadFormatException(this, ex.getMessage(), ex);
        }
    }


    /**
     * Detecte quelques erreurs non vue par le formater.
     *
     * <p> Voir les tests : test_translateField_ErrorFormatYear et test_translateField_ErrorALaCon </p>
     *
     * @param field
     *
     * @throws BadFormatException
     */
    private void detectUndectedError(String field)
          throws BadFormatException {
        if (nullDate.length() != field.length()) {
            throw new BadFormatException(this,
                                         "Mauvais format de date " + "(format " + nullDate + " mais " + field
                                         + " )");
        }
        for (int i = 0; i < field.length(); i++) {
            if ((nullDate.charAt(i) != '0' || field.charAt(i) == ' ')
                && nullDate.charAt(i) != field.charAt(i)) {
                throw new BadFormatException(this,
                                             "Mauvais format de date " + "(format " + nullDate + " mais "
                                             + field
                                             + " )");
            }
        }
    }


    private void init(String dateFormat) {
        for (int index = 0; index < dateFormat.length(); index++) {
            String currentSymbol = dateFormat.substring(index, index + 1);
            if (DATE_CHARACTERS.indexOf(currentSymbol) < 0
                && DATE_SYMBOLS.indexOf(currentSymbol) < 0) {
                throw new IllegalArgumentException("Mauvais format de date " + dateFormat
                                                   + "{" + currentSymbol + "}");
            }
        }
        formatIN = new SimpleDateFormat(dateFormat);
        nullDate = dateFormat;
        for (int index = 0; index < DATE_CHARACTERS.length(); index++) {
            char dateCharacter = DATE_CHARACTERS.substring(index, index + 1).charAt(0);
            nullDate = nullDate.replace(dateCharacter, '0');
        }
        inputDateFormat = dateFormat;
        formatIN.setLenient(false);
    }
}
