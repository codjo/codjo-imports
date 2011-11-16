/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FieldImport}.
 */
public class FieldImportTest extends TestCase {
    public void test_booleanConvertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport field = new FieldImport("label", FieldType.BOOLEAN_FIELD, null, null);
        field.setPosition(1);
        field.setLength(5);
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("VRAI ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("FAUX ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("     ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("OUI  ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("NON  ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("O    ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("N    ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("YES  ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("NO   ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("TRUE ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("FALSE;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("VRAI ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("FAUX ;25;12/01/2001"));

        assertEquals(Boolean.TRUE, field.convertFieldToSQL("oui  ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("non  ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("o    ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("n    ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("yes  ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("no   ;25;12/01/2001"));
        assertEquals(Boolean.TRUE, field.convertFieldToSQL("true ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("false;25;12/01/2001"));

        assertEquals(Boolean.TRUE, field.convertFieldToSQL("  1  ;25;12/01/2001"));
        assertEquals(Boolean.FALSE, field.convertFieldToSQL("  0  ;25;12/01/2001"));
    }


    public void test_booleanTranslateField() throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport field = new FieldImport("label", FieldType.BOOLEAN_FIELD, null, null);
        assertEquals(field.translateField(null), Boolean.FALSE);
        assertEquals(field.translateField(""), Boolean.FALSE);
    }


    public void test_booleanTranslateField_Error()
          throws Exception {
        FieldImport field = new FieldImport("label", FieldType.BOOLEAN_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        try {
            field.translateField("TOTO");
            fail();
        }
        catch (BadFormatException e) {
            assertEquals(
                  "Mauvais format de [label] : net.codjo.imports.common.translator.BooleanTranslator "
                  + ": Mauvais format : TOTO n'est pas un booleen",
                  e.getMessage());
        }
    }


    public void test_booleanGetSQLType() {
        //Tests pour fichiers à longueur fixe
        FieldImport field = new FieldImport("label", FieldType.BOOLEAN_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.getSQLType(), java.sql.Types.BIT);
    }


    public void test_dateConstructor_badFormat() {
        try {
            new FieldImport("label", FieldType.DATE_FIELD, null, "Salut les gars");
            fail("Une exception IllegalArgumentException aurait du etre lancee");
        }
        catch (IllegalArgumentException ex) {
        }
    }


    public void test_dateConvertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport fieldA =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        fieldA.setPosition(8);
        fieldA.setLength(10);
        assertEquals(fieldA.convertFieldToSQL("ABC;25;2001/12/30"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldB =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy-MM-dd");
        fieldB.setPosition(8);
        fieldB.setLength(10);
        assertEquals(fieldB.convertFieldToSQL("ABC;25;2001-12-30"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldC =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyyMMdd");
        fieldC.setPosition(8);
        fieldC.setLength(8);
        assertEquals(fieldC.convertFieldToSQL("ABC;25;20011230"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldD =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd-MM-yy");
        fieldD.setPosition(8);
        fieldD.setLength(8);
        assertEquals(fieldD.convertFieldToSQL("ABC;25;30-12-01"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldE =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd-MM-yyyy");
        fieldE.setPosition(8);
        fieldE.setLength(10);
        assertEquals(fieldE.convertFieldToSQL("ABC;25;30-12-2001"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldF =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "ddMMyyyy");
        fieldF.setPosition(8);
        fieldF.setLength(8);
        assertEquals(fieldF.convertFieldToSQL("ABC;25;30122001"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldG =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd/MM/yyyy");
        fieldG.setPosition(8);
        fieldG.setLength(10);
        assertEquals(fieldG.convertFieldToSQL("ABC;25;30/12/2001"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldH =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd/MM/yy");
        fieldH.setPosition(8);
        fieldH.setLength(8);
        assertEquals(fieldH.convertFieldToSQL("ABC;25;30/12/01"),
                     java.sql.Date.valueOf("2001-12-30"));

        FieldImport fieldI =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd.MM.yyyy");
        fieldI.setPosition(8);
        fieldI.setLength(10);
        assertEquals(fieldI.convertFieldToSQL("ABC;25;30.12.2001"),
                     java.sql.Date.valueOf("2001-12-30"));
    }


    public void test_dateGetSQLType() {
        FieldImport fieldA =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        fieldA.setPosition(8);
        fieldA.setLength(10);
        assertEquals(fieldA.getSQLType(), java.sql.Types.DATE);
    }


    public void test_dateTranslateField() throws Exception {
        FieldImport field =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        field.setPosition(1);
        field.setLength(10);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
        assertEquals(field.translateField("0000/00/00"), null);
    }


    public void test_dateTranslateField_dd_MM_yy_HYPHEN()
          throws Exception {
        FieldImport field =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "dd-MM-yy");
        field.setPosition(1);
        field.setLength(8);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
        assertEquals(field.translateField("00-00-00"), null);
    }


    public void test_dateTranslateField_Error() throws Exception {
        FieldImport field =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("2001/12/30"),
                     java.sql.Date.valueOf("2001-12-30"));
        try {
            field.translateField("a string");
            fail("Une exception devrait etre lance. 'a string' n'est pas une date");
        }
        catch (BadFormatException ex) {
        }
        try {
            field.translateField("2001-12-30");
            fail("Une exception devrait etre lance. '2001-12-30' n'est pas une date");
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_dateTranslateField_ErrorBadOrder()
          throws Exception {
        FieldImport field =
              new FieldImport("une Date", FieldType.DATE_FIELD, null, "yyyy/MM/dd");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("2001/12/30"),
                     java.sql.Date.valueOf("2001-12-30"));
        try {
            Object result = field.translateField("30/12/2001");
            fail("Une exception devrait etre lance. '30/12/2001' n'a pas le bon ordre : "
                 + result);
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_dateTranslateField_ErrorFormatYear()
          throws Exception {
        FieldImport field =
              new FieldImport("label", FieldType.DATE_FIELD, null, "dd/MM/yyyy");
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField("30/12/2002"),
                     java.sql.Date.valueOf("2002-12-30"));
        try {
            Object result = field.translateField("30/12/02");
            fail("L'année est codé sur 4 charactère et non 2 : " + result);
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_dateTranslateField_ErrorALaCon()
          throws Exception {
        FieldImport field =
              new FieldImport("label", FieldType.DATE_FIELD, null, "dd/MM/yyyy");
        field.setPosition(1);
        field.setLength(3);
        try {
            Object result = field.translateField("30  /12/02");
            fail("ya des blancs : " + result);
        }
        catch (BadFormatException ex) {
        }
        try {
            Object result = field.translateField("30/12/02  ");
            fail("ya encore des blancs : " + result);
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_numberConvertFieldToSQL_BigFloat()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number number = (Number)fieldE.convertFieldToSQL("1234567890123.1234;2");
        assertTrue(number.doubleValue() == 1234567890123.1234);
    }


    public void test_numberConvertFieldToSQL_BigFloat_Twice()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number number = (Number)fieldE.convertFieldToSQL("1234567890123.1234;2");
        assertTrue(number.doubleValue() == 1234567890123.1234);

        Number numberBis = (Number)fieldE.convertFieldToSQL("1111111111111.1111;2");
        assertTrue(numberBis.doubleValue() == 1111111111111.1111);
    }


    public void test_numberConvertFieldToSQL_BugEugenio()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ",", null);
        fieldE.setPosition(1);
        fieldE.setLength(18);
        try {
            Object str = fieldE.convertFieldToSQL("1234567890123.1234;2");
            fail("Conversion doit echouer car le separateur parametré est ',' "
                 + " alors que le nombre utilise '.' : " + str);
        }
        catch (Exception ex) {
        }
    }


    public void test_numberCnvertFieldToSQL_Error() {
        FieldImport fieldA =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldA.setPosition(5);
        fieldA.setLength(3);
        try {
            fieldA.translateField("vvv");
            fail("Une exception devrait etre lance. vv n'est pas un nombre");
        }
        catch (BadFormatException ex) {
        }
        try {
            Object str = fieldA.translateField("2.5");
            fail("Une exception devrait etre lance. 2.5 n'est pas un entier : " + str);
        }
        catch (BadFormatException ex) {
        }
        try {
            Object str = fieldA.translateField("2,5");
            fail("Une exception devrait etre lance. 2,5 n'est pas un entier : " + str);
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_numberConvertFieldToSQL_Float()
          throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport fieldA = new FieldImport("label", FieldType.NUMERIC_FIELD, ",", null);
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals("2.5", fieldA.convertFieldToSQL("102	2,5;2001/12/30").toString());

        FieldImport fieldB = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldB.setPosition(5);
        fieldB.setLength(3);
        assertEquals("2.5", fieldB.convertFieldToSQL("102	2.5;2001/12/30").toString());

        FieldImport fieldC = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldC.setPosition(5);
        fieldC.setLength(2);
        assertEquals("25", fieldC.convertFieldToSQL("102	25;2001/12/30").toString());

        FieldImport fieldD = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldD.setPosition(5);
        fieldD.setLength(5);
        assertEquals("2.500", fieldD.convertFieldToSQL("102	2.500;2001/12/30").toString());

        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(5);
        fieldE.setLength(12);
        assertEquals("2.5000000001",
                     fieldE.convertFieldToSQL("102	2.5000000001;2001/12/30").toString());

        FieldImport fieldF = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldF.setPosition(5);
        fieldF.setLength(2);
        assertEquals("0.0", fieldF.convertFieldToSQL("102	.0;2001/12/30").toString());

        FieldImport fieldG = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldG.setPosition(5);
        fieldG.setLength(2);
        assertEquals("0.1", fieldG.convertFieldToSQL("102	.1;2001/12/30").toString());
    }


    public void test_numberCnvertFieldToSQL_Float_Grouping()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(9);
        try {
            fieldE.convertFieldToSQL("123,456.1");
            fail("Les séparateurs de miliers sont interdits");
        }
        catch (BadFormatException ex) {
        }
    }


    public void test_numberConvertFieldToSQL_Formatted()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number number = (Number)fieldE.convertFieldToSQL("1 345 789 123.123 ;2");
        assertEquals("1345789123.123", number.toString());
    }


    public void test_numberConvertFieldToSQL_Integer()
          throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport fieldA =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldA.setPosition(5);
        fieldA.setLength(2);
        assertEquals(fieldA.convertFieldToSQL("ABC;26;2001/12/30"), 26);

        FieldImport fieldB =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldB.setPosition(5);
        fieldB.setLength(2);
        assertEquals(fieldB.convertFieldToSQL("ABC;  ;2001/12/30"), null);
    }


    public void test_numberConvertFieldToSQL_Negative()
          throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport fieldA =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals("-25", fieldA.convertFieldToSQL("ABC;-25;2001/12/30").toString());

        FieldImport fieldB =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldB.setPosition(5);
        fieldB.setLength(5);
        assertEquals("-25", fieldB.convertFieldToSQL("ABC;-  25;2001/12/30").toString());

        FieldImport fieldC = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldC.setPosition(5);
        fieldC.setLength(5);
        assertEquals("-25.1", fieldC.convertFieldToSQL("ABC;-25.1;2001/12/30").toString());

        FieldImport fieldD = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldD.setPosition(5);
        fieldD.setLength(7);
        assertEquals("-25.2",
                     fieldD.convertFieldToSQL("ABC;-  25.2;2001/12/30").toString());
    }


    public void test_numberConvertFieldToSQL_Numeric_Decimal()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(19);
        Number number = (Number)fieldE.convertFieldToSQL("123456789012.312340;2");
        assertEquals("123456789012.312340", number.toString());
    }


    public void test_numberConvertFieldToSQL_Numeric_Entier()
          throws Exception {
        FieldImport fieldE = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldE.setPosition(1);
        fieldE.setLength(18);
        Number number = (Number)fieldE.convertFieldToSQL("123456789012312340;2");
        assertEquals("123456789012312340", number.toString());
    }


    public void test_numberConvertFieldToSQL_Zero()
          throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport fieldA =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals(fieldA.convertFieldToSQL("ABC;005;2001/12/30"), 5);

        FieldImport fieldB =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldB.setPosition(5);
        fieldB.setLength(3);
        assertEquals(fieldB.convertFieldToSQL("ABC;-05;2001/12/30"), -5);
    }


    public void test_numberGetSQLType() {
        FieldImport fieldG = new FieldImport("label", FieldType.NUMERIC_FIELD, ".", null);
        fieldG.setPosition(5);
        fieldG.setLength(2);
        assertEquals(fieldG.getSQLType(), java.sql.Types.NUMERIC);

        FieldImport fieldA =
              new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        fieldA.setPosition(5);
        fieldA.setLength(3);
        assertEquals(fieldA.getSQLType(), java.sql.Types.INTEGER);
    }


    public void test_numberTranslateField() throws Exception {
        FieldImport field = new FieldImport("label", FieldType.NUMERIC_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
    }


    public void test_stringConvertFieldToSQL() throws Exception {
        //Tests pour fichiers à longueur fixe
        FieldImport field = new FieldImport("label", FieldType.STRING_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.convertFieldToSQL("ABC;25;12/01/2001"), "ABC");
        assertEquals(field.convertFieldToSQL("A'C;25;12/01/2001"), "A'C");
    }


    public void test_stringTranslateField() throws Exception {
        FieldImport field = new FieldImport("label", FieldType.STRING_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.translateField(null), null);
        assertEquals(field.translateField(""), null);
    }


    public void test_stringGetSQLType() {
        FieldImport field = new FieldImport("label", FieldType.STRING_FIELD, null, null);
        field.setPosition(1);
        field.setLength(3);
        assertEquals(field.getSQLType(), java.sql.Types.VARCHAR);
    }


    public void test_extractField() throws Exception {
        String line = "toto;titi;tata";
        FieldImport field = new FieldImport("label", FieldType.STRING_FIELD, null, null);
        field.setSeparator(";");
        field.setFixedLength(false);

        field.setPosition(1);
        String result = field.extractField(line);
        assertEquals("toto", result);
        field.setPosition(2);
        assertEquals("titi", field.extractField(line));
        field.setPosition(3);
        assertEquals("tata", field.extractField(line));
        field.setPosition(4);
        try {
            field.extractField(line);
            fail("Le champ manquant n'a pas été détecté");
        }
        catch (FieldNotFoundException e) {
            ;
        }
    }
}
