/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.translator;
import junit.framework.TestCase;
/**
 * Description of the Class
 *
 * @author $Author: lopezla $
 * @version $Revision: 1.10 $
 */
public class DateTranslatorTest extends TestCase {
    /**
     * A unit test for JUnit
     */
    public void test_constructor_badFormat() {
        try {
            new DateTranslator("Salut les gars");
            fail("Une exception IllegalArgumentException aurait du etre lancee");
        }
        catch (IllegalArgumentException e) {
            ;
        }
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        DateTranslator fieldA = new DateTranslator("yyyy/MM/dd");
        assertEquals(fieldA.getSQLType(), java.sql.Types.DATE);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        DateTranslator field = new DateTranslator("yyyy/MM/dd");
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
        assertEquals(field.translate("0000/00/00"), null);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_translateField_dd_MM_yy_HYPHEN()
          throws Exception {
        DateTranslator field = new DateTranslator("dd-MM-yy");
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
        assertEquals(field.translate("00-00-00"), null);
    }


    public void test_translateField_MM_dd_yyyy() throws Exception {
        DateTranslator field = new DateTranslator("MM/dd/yyyy");
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
        assertEquals(field.translate("00/00/0000"), null);
    }


    public void test_translateField_yyyy_MM_dd_HH_mm_ss_S() throws Exception {
        DateTranslator field = new DateTranslator("yyyy-MM-dd HH:mm:ss.S");
        assertEquals(field.translate(null), null);
        assertEquals(field.translate("2008-12-31 00:00:00.0").toString(), "2008-12-31");
        assertEquals(field.translate(""), null);
        assertEquals(field.translate("0000-00-00 00:00:00.0"), null);
    }


    public void test_translateField_AllChars() throws Exception {
        DateTranslator field = new DateTranslator("G.y//M.d.h-H/m-s.S.E.D-F/w/W/a/k-K.z");
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
        assertEquals(field.translate("0.0//0.0.0-0/0-0.0.0.0-0/0/0/0/0-0.0"), null);
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_translateField_Error() throws Exception {
        DateTranslator field = new DateTranslator("yyyy/MM/dd");
        assertEquals(field.translate("2001/12/30"), java.sql.Date.valueOf("2001-12-30"));
        try {
            field.translate("a string");
            fail("Une exception devrait etre lance. 'a string' n'est pas une date");
        }
        catch (BadFormatException e) {
            ;
        }

        try {
            field.translate("2001-12-30");
            fail("Une exception devrait etre lance. '2001-12-30' n'est pas une date");
        }
        catch (BadFormatException e) {
            ;
        }
    }


    //Tests pour fichiers à longueur fixe
    /**
     * A unit test for JUnit
     *
     * @throws Exception Description of Exception
     */
    public void test_translateField_ErrorBadOrder()
          throws Exception {
        DateTranslator field = new DateTranslator("yyyy/MM/dd");
        assertEquals(field.translate("2001/12/30"), java.sql.Date.valueOf("2001-12-30"));
        try {
            Object result = field.translate("30/12/2001");
            fail("Une exception devrait etre lance. '30/12/2001' n'a pas le bon ordre : "
                 + result);
        }
        catch (BadFormatException e) {
            ;
        }
    }


    public void test_translateField_ErrorFormatYear()
          throws Exception {
        DateTranslator field = new DateTranslator("dd/MM/yyyy");
        assertEquals(field.translate("30/12/2002"), java.sql.Date.valueOf("2002-12-30"));
        try {
            Object result = field.translate("30/12/02");
            fail("L'année est codé sur 4 charactère et non 2 : " + result);
        }
        catch (BadFormatException e) {
            ;
        }
    }


    public void test_translateField_ErrorALaCon()
          throws Exception {
        DateTranslator field = new DateTranslator("dd/MM/yyyy");
        try {
            Object result = field.translate("30  /12/02");
            fail("ya des blancs : " + result);
        }
        catch (BadFormatException e) {
            ;
        }
        try {
            Object result = field.translate("30/12/02  ");
            fail("ya encore des blancs : " + result);
        }
        catch (BadFormatException e) {
            ;
        }
    }
}
