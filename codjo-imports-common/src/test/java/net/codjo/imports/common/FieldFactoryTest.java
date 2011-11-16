/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import net.codjo.expression.KernelFunctionHolder;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FieldFactory}.
 */
public class FieldFactoryTest extends TestCase {
    public void test_convertFieldTypeToSqlType() throws Exception {
        assertEquals(Types.BIT,
            FieldFactory.convertFieldTypeToSqlType(FieldType.BOOLEAN_FIELD));
        assertEquals(Types.DATE,
            FieldFactory.convertFieldTypeToSqlType(FieldType.DATE_FIELD));
        assertEquals(Types.NUMERIC,
            FieldFactory.convertFieldTypeToSqlType(FieldType.NUMERIC_FIELD));
        assertEquals(Types.VARCHAR,
            FieldFactory.convertFieldTypeToSqlType(FieldType.STRING_FIELD));
        assertEquals(Types.VARCHAR,
            FieldFactory.convertFieldTypeToSqlType(FieldType.CLASS_FIELD));
        try {
            FieldFactory.convertFieldTypeToSqlType('A');
            fail("IllegalArgumentException attendue");
        }
        catch (IllegalArgumentException exception) {}
    }


    public void test_newField() throws Exception {
        FieldImport fieldImport =
            (FieldImport)FieldFactory.newField("MY_COLUMN", 1, 2,
                FieldType.BOOLEAN_FIELD, ".", "YYYY-MM-DD", false, false, ",");
        assertEquals(1, fieldImport.getPosition());
        assertEquals(2, fieldImport.getLength());
        assertEquals(false, fieldImport.getRemoveLeftZeros());
        assertEquals(false, fieldImport.getFixedLength());
        assertEquals(",", fieldImport.getSeparator());
        assertEquals("MY_COLUMN", fieldImport.getDBDestFieldName());

        Field fieldWithExpression =
            FieldFactory.newField("MY_COLUMN", 1, 2, FieldType.BOOLEAN_FIELD, ".",
                "YYYY-MM-DD", false, false, ",", "Valeur");

        assertTrue(fieldWithExpression instanceof FieldWithExpression);
        assertEquals("MY_COLUMN", fieldWithExpression.getDBDestFieldName());
    }


    public void test_newField_withClass() throws Exception {
        FieldImport blankFieldImport =
            (FieldImport)FieldFactory.newField("MY_COLUMN", 1, 2, FieldType.CLASS_FIELD,
                ".", "YYYY-MM-DD", false, false, ",");
        assertTrue(blankFieldImport instanceof BlankFieldImport);

        Field fieldWithExpression =
            FieldFactory.newField("MY_COLUMN", 1, 2, FieldType.CLASS_FIELD, ".",
                "YYYY-MM-DD", false, false, ",", "Valeur", new KernelFunctionHolder());
        assertTrue(fieldWithExpression instanceof FieldWithExpression);
    }
}
