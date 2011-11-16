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
 * @version $Revision: 1.7 $
 */
public class StringTranslatorTest extends TestCase {
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField() throws Exception {
        StringTranslator field = new StringTranslator();
        assertEquals(field.translate(null), null);
        assertEquals(field.translate(""), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        StringTranslator field = new StringTranslator();
        assertEquals(field.getSQLType(), java.sql.Types.VARCHAR);
    }
}
