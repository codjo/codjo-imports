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
public class BooleanTranslatorTest extends TestCase {
    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translate() throws Exception {
        //Tests pour fichiers à longueur fixe
        BooleanTranslator field = new BooleanTranslator();
        assertEquals(field.translate(null), Boolean.FALSE);
        assertEquals(field.translate(""), Boolean.FALSE);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_translateField_Error() throws Exception {
        //Tests pour fichiers à longueur fixe
        BooleanTranslator field = new BooleanTranslator();
        try {
            field.translate("TOTO");
            fail("TOTO n'est pas un booleen");
        }
        catch (BadFormatException ef) {}
    }


    /**
     * A unit test for JUnit
     */
    public void test_getSQLType() {
        //Tests pour fichiers à longueur fixe
        BooleanTranslator field = new BooleanTranslator();
        assertEquals(field.getSQLType(), java.sql.Types.BIT);
    }
}
