/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
/**
 * SQL Field. Un champs SQL est definie par un nom physique de colonne, un type SQL et
 * eventuellement une valeur.
 */
class SQLField {
    private String name;
    private int sqlType;
    private Object value;

    SQLField(int sqlType, String fieldName) {
        this.sqlType = sqlType;
        this.name = fieldName;
    }


    SQLField(int sqlType, String name, Object value) {
        this.sqlType = sqlType;
        this.name = name;
        setValue(value);
    }

    /**
     * Retourne le nom physique du champs (colonne).
     *
     * @return The Name value
     */
    public String getName() {
        return name;
    }


    public int getSQLType() {
        return sqlType;
    }


    public Object getValue() {
        return value;
    }


    public void setValue(Object value) {
        this.value = value;
    }
}
