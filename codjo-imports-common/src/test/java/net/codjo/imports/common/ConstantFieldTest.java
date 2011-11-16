/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.sql.Types;
import java.util.Date;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ConstantField}.
 */
public class ConstantFieldTest extends TestCase {
    private static final String FIELD_NAME = "DESTINATION_FIELD_NAME";

    public void test_getSQLType() {
        ConstantField bitConstantField =
            new ConstantField(Types.BIT, FIELD_NAME, Boolean.TRUE);
        assertEquals(Types.BIT, bitConstantField.getSQLType());

        ConstantField stringConstantField =
            new ConstantField(Types.VARCHAR, "SOURCE_FILE", "AGF");
        assertEquals(Types.VARCHAR, stringConstantField.getSQLType());
    }


    public void test_getDBDestFieldName() {
        ConstantField field =
            new ConstantField(FieldType.STRING_FIELD, FIELD_NAME, "...");

        assertEquals(FIELD_NAME, field.getDBDestFieldName());
    }


    public void test_setValue() throws Exception {
        ConstantField field =
            new ConstantField(FieldType.STRING_FIELD, FIELD_NAME, "TOTO");

        field.setValue("Zorro est arrivé !!!");
        assertEquals("Zorro est arrivé !!!", field.convertFieldToSQL(null));

        Date now = new Date(System.currentTimeMillis());
        field.setValue(now);
        assertEquals(now, field.convertFieldToSQL(null));
    }


    public void test_convertFieldToSQL() throws Exception {
        Date now = new Date(System.currentTimeMillis());
        ConstantField field = new ConstantField(FieldType.DATE_FIELD, "SOURCE_FILE", now);
        assertEquals(now, field.convertFieldToSQL(null));
        assertEquals(now, field.convertFieldToSQL(""));
        assertEquals(now, field.convertFieldToSQL("Plein De Trucs"));
    }
}
