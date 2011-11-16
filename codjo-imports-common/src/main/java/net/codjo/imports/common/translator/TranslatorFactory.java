/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import net.codjo.imports.common.FieldType;
import java.sql.Types;
/**
 * Classe Factory pour les objets {@link Translator}.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.10 $
 */
public final class TranslatorFactory {
    private TranslatorFactory() {}

    /**
     * Création d'un nouveau  {@link Translator}.
     *
     * @param fieldType DESTINATION_FIELD_TYPE
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     *
     * @return un nouveau Translator
     */
    public static Translator newTranslator(char fieldType, String decimalSeparator,
        String inputDateFormat) {
        return newTranslator(fieldType, fieldType, decimalSeparator, inputDateFormat);
    }


    public static Translator newTranslator(char sourceFieldType, char destFieldType,
        String decimalSeparator, String inputDateFormat) {
        int destSqlType;

        destSqlType = determineDestSqlType(destFieldType, decimalSeparator);

        if (sourceFieldType == '\u0000') {
            sourceFieldType = destFieldType;
        }
        return determineTranslator(sourceFieldType, destSqlType, decimalSeparator,
            inputDateFormat);
    }


    private static Translator determineTranslator(char sourceFieldType, int destSqlType,
        String decimalSeparator, String inputDateFormat) {
        Translator translator;
        switch (sourceFieldType) {
            case FieldType.CLASS_FIELD:
            case FieldType.STRING_FIELD:
                translator = new StringTranslator(destSqlType);
                break;
            case FieldType.BOOLEAN_FIELD:
                translator = new BooleanTranslator(destSqlType);
                break;
            case FieldType.NUMERIC_FIELD:
                if (decimalSeparator == null) {
                    translator = new NumberTranslator(destSqlType);
                }
                else {
                    translator =
                        new NumberTranslator(destSqlType, decimalSeparator.charAt(0));
                }
                break;
            case FieldType.DATE_FIELD:
                translator = new DateTranslator(destSqlType, inputDateFormat);
                break;
            default:
                throw new IllegalArgumentException("Type de champ inconnu : '"
                    + sourceFieldType + "'");
        }
        return translator;
    }


    private static int determineDestSqlType(char destFieldType, String decimalSeparator) {
        int destSqlType;
        switch (destFieldType) {
            case FieldType.CLASS_FIELD:
            case FieldType.STRING_FIELD:
                destSqlType = Types.VARCHAR;
                break;
            case FieldType.BOOLEAN_FIELD:
                destSqlType = Types.BIT;
                break;
            case FieldType.NUMERIC_FIELD:
                if (decimalSeparator == null) {
                    destSqlType = Types.INTEGER;
                }
                else {
                    destSqlType = Types.NUMERIC;
                }
                break;
            case FieldType.DATE_FIELD:
                destSqlType = Types.DATE;
                break;
            default:
                throw new IllegalArgumentException("Type de champ inconnu : '"
                    + destFieldType + "'");
        }
        return destSqlType;
    }


    /**
     * Convertit un type Sql (java.sql.Types) en type de champ importable.
     *
     * @param sqlType un type SQL
     *
     * @return un type de champ
     *
     * @throws IllegalArgumentException Si le type n'est pas supporté
     */
    public static char convertSqlTypeToFieldType(int sqlType) {
        switch (sqlType) {
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.CHAR:
                return FieldType.STRING_FIELD;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return FieldType.DATE_FIELD;
            case Types.FLOAT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.INTEGER:
            case Types.NUMERIC:
                return FieldType.NUMERIC_FIELD;
            case Types.BIT:
                return FieldType.BOOLEAN_FIELD;
            default:
                throw new IllegalArgumentException("Type SQL non importable : " + sqlType);
        }
    }
}
