/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import net.codjo.expression.FunctionHolder;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FieldWithExpression}.
 */
public class FieldWithExpressionTest extends TestCase {
    public void test_constantExpression()
          throws ImportFailureException {
        FieldWithExpression fieldWithExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 0, 0, 'S', ".",
                                                         "dd.MM.yyyy", false, true, ";", "\"BONDS\"");

        assertEquals("BONDS", fieldWithExpression.convertFieldToSQL(""));
        assertEquals("BONDS",
                     fieldWithExpression.convertFieldToSQL("Ligne;contenant;n importe quoi"));
    }


    public void test_constantBisExpression()
          throws ImportFailureException {
        FieldWithExpression fieldWithExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 5, 'S', ".",
                                                         "dd.MM.yyyy", false, true, ";", "Valeur");

        assertEquals("Ligne",
                     fieldWithExpression.convertFieldToSQL("Ligne\tcontenant\tn importe quoi"));
    }


    public void test_expressionEvaluation()
          throws ImportFailureException {
        FieldWithExpression fieldWithExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 4, 2, 'N', ".",
                                                         "dd.MM.yyyy", false, true, ";", "Valeur / 100");

        BigDecimal res =
              (BigDecimal)fieldWithExpression.convertFieldToSQL("12;50;string");

        assertEquals(new Float("0.5"), res.floatValue());
    }


    public void test_expressionLigneEvaluation()
          throws ImportFailureException {
        FieldWithExpression fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.BOOLEAN_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Ligne.indexOf(\"TOTO\") > 0 , true, false)");
        assertEquals(Boolean.FALSE,
                     fieldExpression.convertFieldToSQL("titi;tutu;tata;tete"));
        assertEquals(Boolean.TRUE,
                     fieldExpression.convertFieldToSQL("titi;tutu;TOTO;tata;tete"));
    }


    public void test_expressionNumLigneEvaluation()
          throws Exception {
        FieldWithExpression fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.NUMERIC_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "NumLigne");
        assertEquals("-1", fieldExpression.convertFieldToSQL("TOTO;tata;tete").toString());
        assertEquals("20",
                     fieldExpression.convertFieldToSQL("TOTO;tata;tete", 20).toString());
    }


    public void test_expressionLigneEvaluationWithoutField()
          throws Exception {
        FieldWithExpression fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 0, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.BOOLEAN_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Ligne.indexOf(\"TOTO\") > 0 , true, false)");
        assertEquals(Boolean.FALSE,
                     fieldExpression.convertFieldToSQL("titi;tutu;tata;tete"));
        assertEquals(Boolean.TRUE,
                     fieldExpression.convertFieldToSQL("titi;tutu;TOTO;tata;tete"));
    }


    public void test_expressionSSModifEvaluation()
          throws ImportFailureException {
        FieldWithExpression fieldWithExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 8, 1, 'N', ".",
                                                         "dd.MM.yyyy", false, true, ";", "Valeur");

        BigDecimal bigdec = new java.math.BigDecimal("4");
        assertEquals(bigdec, fieldWithExpression.convertFieldToSQL("blabla;4;string"));
    }


    public void test_expressionExceptionCatched()
          throws ImportFailureException {
        try {
            FieldWithExpression fieldWithExpression =
                  (FieldWithExpression)FieldFactory.newField("DestColName", 8, 1, 'N', ".",
                                                             "dd.MM.yyyy", false, true, ";", "Vale * 2");

            BigDecimal bigdec = new java.math.BigDecimal("4");
            assertEquals(bigdec, fieldWithExpression.convertFieldToSQL("blabla;4;string"));
            fail("Une exception de type ImportFailureException aurait du etre levee");
        }
        catch (ImportExpressionException e) {
            assertEquals("Colonne 'DestColName' expression invalide : "
                         + "Vale * 2 : La variable (ou methode) \"Vale\" est inconnue",
                         e.getMessage());
        }
    }


    public void test_booleanTranslateFieldWithExpression_NoError()
          throws Exception {
        FieldWithExpression fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.NUMERIC_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==\"TOTO\", 1, 0)");

        assertEquals("1", fieldExpression.convertFieldToSQL("TOTO").toString());

        fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.NUMERIC_FIELD,
                                                         FieldType.STRING_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==1, \"TOTO\", \"TITI\")");

        assertEquals("TOTO", fieldExpression.convertFieldToSQL("1").toString());

        fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.BOOLEAN_FIELD,
                                                         FieldType.STRING_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==true,\"TOTO\", \"TITI\")");

        assertEquals("TITI", fieldExpression.convertFieldToSQL("false").toString());

        fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.BOOLEAN_FIELD,
                                                         FieldType.BOOLEAN_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==true,false, true)");

        assertEquals("false", fieldExpression.convertFieldToSQL("true").toString());

        fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.BOOLEAN_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==\"TOTO\",true, false)");

        assertEquals("true", fieldExpression.convertFieldToSQL("TOTO").toString());

        fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.NUMERIC_FIELD,
                                                         FieldType.BOOLEAN_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "iif(Valeur==1,true, false)");

        assertEquals("false", fieldExpression.convertFieldToSQL("2").toString());
    }


    public void test_expressionWithNullValue() throws Exception {
        FieldWithExpression fieldExpression =
              (FieldWithExpression)FieldFactory.newField("DestColName", 1, 3,
                                                         FieldType.STRING_FIELD,
                                                         FieldType.DATE_FIELD,
                                                         ".",
                                                         "YYYY-MM-dd",
                                                         false,
                                                         false,
                                                         ";",
                                                         "utils.convertNullToDate(Valeur)",
                                                         new NullDateFunctionHolder());

        assertEquals(new Date(0), fieldExpression.convertFieldToSQL("null"));

        Calendar calendar = Calendar.getInstance();
        calendar.set(2006, Calendar.APRIL, 5, 0, 0, 0);
        assertEquals(calendar.getTime().toString(),
                     fieldExpression.convertFieldToSQL("20060405").toString());
    }


    public void test_expression_withFunctionHolder()
          throws ImportFailureException {
        String dbName = "DestColName";
        FieldWithExpression fieldWithExpression =
              new FieldWithExpression(dbName, 'C', new BlankFieldImport(dbName),
                                      "utils.echoTwoFirstCharacters(Valeur,5)", new EchoFunctionHolder());

        assertEquals("c1;c2", fieldWithExpression.convertFieldToSQL("c1;c2;0;1;0"));

        fieldWithExpression =
              new FieldWithExpression(dbName, 'C', new BlankFieldImport(dbName),
                                      "utils.echoNumberOfLetter(Valeur,'c')", new EchoFunctionHolder());

        assertEquals("2", fieldWithExpression.convertFieldToSQL("c1;c2;0;1;0"));
    }


    public static class EchoFunctionHolder implements FunctionHolder {
        public List<String> getAllFunctions() {
            return null;
        }


        public String getName() {
            return "utils";
        }


        public String echoTwoFirstCharacters(String fileLine, int length) {
            return fileLine.substring(0, length);
        }


        public String echoNumberOfLetter(String fileLine, char letter) {
            int count = 0;
            for (int i = 0; i < fileLine.length(); i++) {
                if (fileLine.charAt(i) == letter) {
                    count++;
                }
            }
            return String.valueOf(count);
        }
    }

    public static class NullDateFunctionHolder implements FunctionHolder {
        public List<String> getAllFunctions() {
            return null;
        }


        public String getName() {
            return "utils";
        }


        public Date convertNullToDate(String fileLine) {
            SimpleDateFormat df =
                  (SimpleDateFormat)SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT,
                                                                     Locale.FRENCH);
            df.applyPattern("yyyyMMdd");

            Date result;
            try {
                result = df.parse(fileLine);
            }
            catch (ParseException e) {
                result = new Date(0);
            }
            return result;
        }
    }
}
