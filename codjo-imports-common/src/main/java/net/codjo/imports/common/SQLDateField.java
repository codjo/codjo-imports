/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * SQL Field specific pour le type SQL date et assimile (TIME, TIMESTAMP,...).
 */
class SQLDateField extends SQLField {
    SQLDateField(int sqlDateType, String name) {
        super(sqlDateType, name);
    }


    /**
     * Positionne la valeur. Si la valeur est de type <code>java.util.Date</code> elle est convertit en
     * <code>java.sql.Date</code>
     *
     * @param value The new Value value
     */
    @Override
    public void setValue(Object value) {
        if (value != null && value.getClass() == java.util.Date.class) {
            super.setValue(new java.sql.Timestamp(((java.util.Date)value).getTime()));
        }
        else {
            super.setValue(value);
        }
    }
}
