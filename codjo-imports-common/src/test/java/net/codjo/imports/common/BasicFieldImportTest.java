/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import junit.framework.TestCase;
/**
 * Classe de test de {@link BasicFieldImport}.
 */
public class BasicFieldImportTest extends TestCase {
    //Tests pour fichiers à longueur fixe
    // Extraction Pos:5 Length:2
    // RemoveLeftZero:f
    protected FieldImport fieldA;

    // Extraction Pos:1 Length:4
    // RemoveLeftZero:f
    protected FieldImport fieldB;

    // Extraction Pos:5 Length:2
    // RemoveLeftZero:T
    protected FieldImport fieldE;

    // Extraction Pos:3 Length:5
    // RemoveLeftZero:f
    protected FieldImport fieldZ;

    //Tests pour fichiers à longueur variable
    // Extraction Pos:2 Separator:tabulation
    // RemoveLeftZero:f
    protected FieldImport fieldF;

    // Extraction Pos:2 Separator:";"
    // RemoveLeftZero:f
    protected FieldImport fieldG;

    // Extraction Pos:2 Separator:tabulation
    // RemoveLeftZero:T
    protected FieldImport fieldH;

    // Extraction Pos:5 Separator:tabulation
    // RemoveLeftZero:f
    protected FieldImport fieldI;

    // Extraction Pos:3 Separator: ;
    // RemoveLeftZero:f
    protected FieldImport fieldJ;

    // Extraction Pos:1 Separator:";"
    // RemoveLeftZero:f
    protected FieldImport fieldK;


    public void test_convertFieldToSQL_Error() throws BadFormatException {
        //Tests pour fichiers à longueur fixe
        try {
            fieldE.convertFieldToSQL("e");
            fail("Une exception devrait etre lancee (fixe).");
        }
        catch (FieldNotFoundException ef) {
        }

        //Tests pour fichiers à longueur variable
        try {
            fieldH.convertFieldToSQL("e");
            fail("Une exception devrait etre lancee (variable).");
        }
        catch (FieldNotFoundException ev) {
        }
    }


    public void test_convertFieldToSQL_Filter() throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldE.convertFieldToSQL("ABC;20"), "20");
        assertEquals(fieldE.convertFieldToSQL("ABC;02"), "2");
        assertEquals(fieldE.convertFieldToSQL("ABC;00"), "0");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldH.convertFieldToSQL("102	10	3080	FIL	FIPA	S"), "10");
        assertEquals(fieldH.convertFieldToSQL("102	01	3080	FIL	FIPA	S"), "1");
    }


    public void test_convertFieldToSQL_Filter_Tabulation()
          throws Throwable {
        //Tests pour fichiers à longueur variable
        assertEquals("test 1", "a", fieldJ.convertFieldToSQL("a;b; a \u0009  ;gg"));
        assertEquals("test 2", "4", fieldJ.convertFieldToSQL("a;b;\t4 ;gg"));
        assertEquals("test 3", "4", fieldJ.convertFieldToSQL("a;b;\t4\t;gg"));
        assertEquals("test 4", "4", fieldJ.convertFieldToSQL("a;b; \t4\t ;gg"));
        assertEquals("test 5", "", fieldJ.convertFieldToSQL("a;b; \t\t ;gg"));

        //Tests pour fichiers à longueur fixe
        assertEquals("test 10", "5", fieldZ.convertFieldToSQL("123 \t5\t 456789123456789"));
        assertEquals("test 10", "5", fieldZ.convertFieldToSQL("123\t5\t  456789123456789"));
        assertEquals("test 10", "5", fieldZ.convertFieldToSQL("123  \t5\t456789123456789"));
    }


    public void test_convertFieldToSQL_Filter_Trim()
          throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldB.convertFieldToSQL("   4;gg"), "4");
        assertEquals(fieldB.convertFieldToSQL("4   ;gg"), "4");
        assertEquals(fieldB.convertFieldToSQL(" 23 ;gg"), "23");
        assertEquals(fieldB.convertFieldToSQL("    ;gg"), "");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldF.convertFieldToSQL("102\t 01\t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t01 \t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t 01 \t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t3080\tFIL\tFIPA\tS"), "3080");
    }


    public void test_extractField_Basic() throws Throwable {
        //Tests pour fichiers à longueur fixe
        assertEquals(fieldA.convertFieldToSQL("ABC;25;12/01/2001"), "25");
        assertEquals(fieldA.convertFieldToSQL("ABC; 2;12/01/2001"), "2");

        assertEquals(fieldB.convertFieldToSQL("1.34"), "1.34");
        assertEquals(fieldB.convertFieldToSQL("1.34;FFEERTR"), "1.34");

        //Tests pour fichiers à longueur variable
        assertEquals(fieldF.convertFieldToSQL("102\t01\t3080\tFIL\tFIPA\tS"), "01");
        assertEquals(fieldF.convertFieldToSQL("102\t\t01\t3080\tFIL\tFIPA\tS"), "");

        assertEquals(fieldG.convertFieldToSQL("102;1.2;3080;FIL;FIPA;S"), "1.2");

        assertEquals(fieldI.convertFieldToSQL("102\t\t\t01\t3080\tFIL\tFIPA\tS"), "3080");

        assertEquals("1.2", fieldK.convertFieldToSQL(";1.2;3080;FIL;FIPA;S"));
    }


    @Override
    protected void setUp() {
        //Tests pour fichiers à longueur fixe
        fieldA = new BasicFieldImport("a");
        fieldA.setPosition(5);
        fieldA.setLength(2);

        fieldB = new BasicFieldImport("b");
        fieldB.setPosition(1);
        fieldB.setLength(4);

        fieldE = new BasicFieldImport("e");
        fieldE.setPosition(5);
        fieldE.setLength(2);
        fieldE.setRemoveLeftZeros(true);

        fieldZ = new BasicFieldImport("z");
        fieldZ.setPosition(4);
        fieldZ.setLength(5);
        fieldZ.setRemoveLeftZeros(false);

        //Tests pour fichiers à longueur variable
        fieldF = new BasicFieldImport("a");
        fieldF.setPosition(2);
        fieldF.setSeparator("\t");
        fieldF.setFixedLength(false);

        fieldG = new BasicFieldImport("b");
        fieldG.setPosition(2);
        fieldG.setSeparator(";");
        fieldG.setFixedLength(false);

        fieldH = new BasicFieldImport("e");
        fieldH.setPosition(2);
        fieldH.setSeparator("\t");
        fieldH.setFixedLength(false);
        fieldH.setRemoveLeftZeros(true);

        fieldI = new BasicFieldImport("f");
        fieldI.setPosition(5);
        fieldI.setSeparator("\t");
        fieldI.setFixedLength(false);

        fieldJ = new BasicFieldImport("j");
        fieldJ.setPosition(3);
        fieldJ.setSeparator(";");
        fieldJ.setFixedLength(false);

        fieldK = new BasicFieldImport("k");
        fieldK.setPosition(2);
        fieldK.setSeparator(";");
        fieldK.setFixedLength(false);
    }


    class BasicFieldImport extends FieldImport {
        BasicFieldImport(String dbName) {
            super(dbName);
        }


        @Override
        public int getSQLType() {
            return java.sql.Types.VARCHAR;
        }


        @Override
        public Object translateField(String field)
              throws BadFormatException {
            return field;
        }
    }
}
