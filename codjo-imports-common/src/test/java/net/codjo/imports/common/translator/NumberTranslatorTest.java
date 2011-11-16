/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import junit.framework.TestCase;
/**
 */
public class NumberTranslatorTest extends TestCase {

    public void test_translate_Error() {
        NumberTranslator fieldA = new NumberTranslator();
        try {
            fieldA.translate("vvv");
            fail("Une exception devrait etre lance. vv n'est pas un nombre");
        }
        catch (BadFormatException e) {
        }
        try {
            Object str = fieldA.translate("2.5");
            fail("Une exception devrait etre lance. 2.5 n'est pas un entier : " + str);
        }
        catch (BadFormatException e) {
        }
        try {
            Object str = fieldA.translate("2,5");
            fail("Une exception devrait etre lance. 2,5 n'est pas un entier : " + str);
        }
        catch (BadFormatException e) {
        }
    }


    public void test_translate_jdk5() {
        NumberTranslator fieldA = new NumberTranslator('.');
        try {
            fieldA.translate("toto");
            fail();
        }
        catch (BadFormatException e) {
            assertEquals("net.codjo.imports.common.translator.NumberTranslator : Mauvais format : toto",
                         e.getMessage());
        }
    }


    public void test_getSQLType() {
        NumberTranslator fieldG = new NumberTranslator('.');
        assertEquals(fieldG.getSQLType(), java.sql.Types.NUMERIC);

        NumberTranslator fieldA = new NumberTranslator();
        assertEquals(fieldA.getSQLType(), java.sql.Types.INTEGER);
    }


    public void test_translateField() throws Exception {
        NumberTranslator field = new NumberTranslator();
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
    }


    public void test_removeAllCharOccurrence() {
        assertEquals(NumberTranslator.removeAllCharOccurrence(" a la con  ", ' '),
                     "alacon");
        assertEquals(NumberTranslator.removeAllCharOccurrence("20/12/2001", '/'),
                     "20122001");
    }
}
