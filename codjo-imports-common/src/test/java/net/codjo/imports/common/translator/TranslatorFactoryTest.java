/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import net.codjo.imports.common.FieldType;
import junit.framework.TestCase;

/**
 */
public class TranslatorFactoryTest extends TestCase {
    public void test_newTranslatorBoolean() {
        Translator translator =
            TranslatorFactory.newTranslator(FieldType.BOOLEAN_FIELD, null, null);

        assertTrue(translator instanceof BooleanTranslator);
    }


    public void test_newTranslatorDate() throws Exception {
        Translator translator =
            TranslatorFactory.newTranslator(FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        assertTrue(translator instanceof DateTranslator);

        assertEquals(java.sql.Date.valueOf("2001-12-30"),
            translator.translate("2001/12/30"));
    }


    public void test_newTranslatorNumber() throws Exception {
        Translator translator =
            TranslatorFactory.newTranslator(FieldType.NUMERIC_FIELD, ".", null);
        assertTrue(translator instanceof NumberTranslator);

        assertEquals(new java.math.BigDecimal(12.5), translator.translate("12.5"));
    }


    public void test_newTranslatorString() throws Exception {
        Translator translator =
            TranslatorFactory.newTranslator(FieldType.STRING_FIELD, null, null);
        assertTrue(translator instanceof StringTranslator);
    }


    public void test_newTranslatorClass() throws Exception {
        Translator translator =
            TranslatorFactory.newTranslator(FieldType.CLASS_FIELD, null, null);
        assertTrue(translator instanceof StringTranslator);
    }
}
