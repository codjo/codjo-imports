/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * Champs d'import pour des valeurs constantes (ex: Systeme Source).
 */
public class ConstantField implements Field {
    private final int sqlType;
    private final String databaseFieldName;
    private Object value;

    public ConstantField(int sqlType, String databaseFieldName, Object fieldValue) {
        this.sqlType = sqlType;
        this.databaseFieldName = databaseFieldName;
        value = fieldValue;
    }

    public void setValue(Object fieldValue) {
        value = fieldValue;
    }


    public Object convertFieldToSQL(String fileName)
            throws ImportFailureException {
        return value;
    }


    public String getDBDestFieldName() {
        return databaseFieldName;
    }


    public int getSQLType() {
        return sqlType;
    }
}
