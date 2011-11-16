/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import net.codjo.expression.FunctionHolder;
import net.codjo.imports.common.translator.TranslatorFactory;
import java.sql.Types;
/**
 * Classe Factory pour les objets FieldImport.
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.12 $
 */
public final class FieldFactory implements FieldType {
    private FieldFactory() {}

    /**
     * Convertit un type Sql (java.sql.Types) en type de champ importable.
     *
     * @param sqlType un type SQL
     *
     * @return un type de champ
     */
    public static char convertSqlTypeToFieldType(int sqlType) {
        return TranslatorFactory.convertSqlTypeToFieldType(sqlType);
    }


    /**
     * Converti un type de champ importable en type Sql (java.sql.Types).
     *
     * @param fieldType un type de champ
     *
     * @return un type sql
     *
     * @throws IllegalArgumentException Si le type n'est pas connu
     */
    public static int convertFieldTypeToSqlType(char fieldType) {
        switch (fieldType) {
            case CLASS_FIELD:
            case STRING_FIELD:
                return Types.VARCHAR;
            case DATE_FIELD:
                return Types.DATE;
            case NUMERIC_FIELD:
                return Types.NUMERIC;
            case BOOLEAN_FIELD:
                return Types.BIT;
            default:
                throw new IllegalArgumentException("Type de champ non importable : "
                    + fieldType);
        }
    }


    /**
     * Creation d'un nouveau FieldImport.
     *
     * @param dbName DB_DESTINATION_FIELD_NAME
     * @param position POSITION
     * @param length LENGTH
     * @param fieldType DESTINATION_FIELD_TYPE
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     * @param removeLeftZeros REMOVE_LEFT_ZEROS
     * @param fixedLength
     * @param fieldSeparator
     *
     * @return un nouveau FieldImport (non enregistre en Base)
     */
    public static Field newField(String dbName, int position, int length, char fieldType,
        String decimalSeparator, String inputDateFormat, boolean removeLeftZeros,
        boolean fixedLength, String fieldSeparator) {
        return newField(dbName, position, length, fieldType, fieldType, decimalSeparator,
            inputDateFormat, removeLeftZeros, fixedLength, fieldSeparator);
    }


    public static Field newField(String dbName, int position, int length,
        char sourceFieldType, char destFieldType, String decimalSeparator,
        String inputDateFormat, boolean removeLeftZeros, boolean fixedLength,
        String fieldSeparator) {
        FieldImport fieldImport = null;

        if (destFieldType == CLASS_FIELD) {
            return new BlankFieldImport(dbName, sourceFieldType, destFieldType,
                decimalSeparator, inputDateFormat);
        }

        if (position > 0) {
            switch (destFieldType) {
                case STRING_FIELD:
                case BOOLEAN_FIELD:
                case NUMERIC_FIELD:
                case DATE_FIELD:
                    fieldImport =
                        new FieldImport(dbName, sourceFieldType, destFieldType,
                            decimalSeparator, inputDateFormat);
                    break;
                default:
                    throw new IllegalArgumentException("Type de champ inconnu : '"
                        + destFieldType + "'");
            }

            fieldImport.setPosition(position);
            fieldImport.setLength(length);
            fieldImport.setRemoveLeftZeros(removeLeftZeros);
            fieldImport.setFixedLength(fixedLength);
            fieldImport.setSeparator(fieldSeparator);
        }

        return fieldImport;
    }


    /**
     * Creation d'un nouveau FieldImport.
     *
     * @param dbName DB_DESTINATION_FIELD_NAME
     * @param position POSITION
     * @param length LENGTH
     * @param fieldType DESTINATION_FIELD_TYPE
     * @param decimalSeparator DECIMAL_SEPARATOR
     * @param inputDateFormat INPUT_DATE_FORMAT
     * @param removeLeftZeros REMOVE_LEFT_ZEROS
     * @param fixedLength
     * @param fieldSeparator
     * @param expression EXPRESSION
     * @param functionHolders
     *
     * @return un nouveau FieldImport (non enregistre en Base)
     *
     * @exception ImportExpressionException Si le fieldType est inconnue
     */
    public static Field newField(String dbName, int position, int length, char fieldType,
        String decimalSeparator, String inputDateFormat, boolean removeLeftZeros,
        boolean fixedLength, String fieldSeparator, String expression,
        FunctionHolder... functionHolders) throws ImportExpressionException {
        Field fieldImport =
            newField(dbName, position, length, fieldType, decimalSeparator,
                inputDateFormat, removeLeftZeros, fixedLength, fieldSeparator);

        if ((expression != null) && (!"".equals(expression))) {
            return new FieldWithExpression(dbName, fieldType, fieldImport, expression,
                functionHolders);
        }
        else {
            return fieldImport;
        }
    }


    public static Field newField(String dbName, int position, int length,
        char sourceFieldType, char destFieldType, String decimalSeparator,
        String inputDateFormat, boolean removeLeftZeros, boolean fixedLength,
        String fieldSeparator, String expression, FunctionHolder... functionHolders)
            throws ImportExpressionException {
        Field fieldImport =
            newField(dbName, position, length, sourceFieldType, destFieldType,
                decimalSeparator, inputDateFormat, removeLeftZeros, fixedLength,
                fieldSeparator);

        if ((expression != null) && (!"".equals(expression))) {
            return new FieldWithExpression(dbName, sourceFieldType, destFieldType,
                fieldImport, expression, functionHolders);
        }
        else {
            return fieldImport;
        }
    }
}
