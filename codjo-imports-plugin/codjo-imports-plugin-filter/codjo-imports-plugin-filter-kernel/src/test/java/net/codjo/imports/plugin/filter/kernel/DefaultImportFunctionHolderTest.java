package net.codjo.imports.plugin.filter.kernel;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class DefaultImportFunctionHolderTest {
    private DefaultImportFunctionHolder functions = new DefaultImportFunctionHolder();


    @Test
    public void test_round() throws Exception {
        assertEquals(5.67, functions.round(new BigDecimal("5.667789"), 2).doubleValue(), 0.0);
        assertEquals(5.67, functions.round(new BigDecimal("5.665789"), 2).doubleValue(), 0.0);
        assertEquals(5.67, functions.round(new BigDecimal("5.665123"), 2).doubleValue(), 0.0);
        assertEquals(5.67, functions.round(new BigDecimal("5.665000"), 2).doubleValue(), 0.0);
        assertEquals(5.66, functions.round(new BigDecimal("5.664123"), 2).doubleValue(), 0.0);
        assertEquals(5.66567, functions.round(new BigDecimal("5.6656652345"), 5).doubleValue(), 0.0);
    }


    @Test
    public void test_stringToDate() throws Exception {
        assertEquals(java.sql.Date.valueOf("2006-09-22"),
                     functions.stringToDate("22/09/2006", "dd/MM/yyyy", "FRENCH"));
    }


    @Test
    public void test_extractDateFrom() throws Exception {
        assertEquals(java.sql.Date.valueOf("2006-09-22"),
                     functions.extractDateFrom("voici ma date 22/09/2006 tit it ti",
                                               "dd/MM/yyyy", " ", "FRENCH"));
    }


    @Test
    public void test_removeChar() throws Exception {
        assertEquals("blabla", functions.removeChar("\"blabla\"", "\""));
        assertEquals("blabla\"encore blabla", functions.removeChar("blabla\"encore blabla", "\""));
        assertEquals("blabla", functions.removeChar("blabla", "\""));
        assertEquals("blabla", functions.removeChar("blabla", "aa"));
    }


    @Test
    public void test_extractField() throws Exception {
        assertEquals("de", functions.extractField("NumLigne;de;donnees", ";", 2));
        assertEquals("0.03", functions.extractField("001;002;0.03;0.04", ";", 3));
        assertEquals("12.589", functions.extractField("0.2,5,8965,445,12.589,3625,102", ",", 5));
        assertEquals("separateur", functions.extractField("separateur---complique", "---", 1));
        assertEquals("tab2", functions.extractField("tab1	tab2	tab3	tab4", "\t", 2));
    }
}
