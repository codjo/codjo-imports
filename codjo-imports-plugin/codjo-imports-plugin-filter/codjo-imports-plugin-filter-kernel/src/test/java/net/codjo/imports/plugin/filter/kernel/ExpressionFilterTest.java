/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ExpressionFilter}.
 */
public class ExpressionFilterTest extends TestCase {
    public void test_constructor_ko_syntax() throws Exception {
        try {
            new ExpressionFilter("Valeur.startsWith(\"201 )");
            fail("Aurait dû échouer car il manque un guillemet.");
        }
        catch (FilterException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_constructor_ko_methodNotFound()
          throws Exception {
        try {
            new ExpressionFilter("Valeur.noMethode(\"201 \")");
            fail("Aurait dû échouer car la méthode n'existe pas.");
        }
        catch (FilterException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_filteredLine() throws Exception {
        ExpressionFilter filter = new ExpressionFilter("Valeur.startsWith(\"201 \")");

        assertFalse(filter.filteredLine("100 doit passer"));
        assertTrue(filter.filteredLine("201 ne doit pas passer"));
    }


    public void test_filteredLine_ko() throws Exception {
        ExpressionFilter filter = new ExpressionFilter("Valeur.length() / 0 == 0");
        try {
            filter.filteredLine(null);
            fail("Aurait dû échouer car paramètre null.");
        }
        catch (FilterException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_filteredLine_withFunction_ok() throws Exception {
        ExpressionFilter filter = new ExpressionFilter("utils.extractField(Valeur, \";\", 3) == \"IBOXX\"");

        assertFalse(filter.filteredLine("ca;passe"));
        assertTrue(filter.filteredLine("ca;passe pas;IBOXX"));
    }


    public void test_filteredLine_withFunction_ko() throws Exception {
        try {
            new ExpressionFilter("utils.extractFieldsssss(Valeur, \";\", 3) == \"IBOXX\"");
            fail("Aurait dû échouer car la méthode appelée n'existe pas.");
        }
        catch (FilterException ex) {
            assertNotNull(ex.getCause());
        }
    }


    public void test_numLigne() {
        ExpressionFilter extendedExpressionFilter = new ExpressionFilter("NumLigne == 4");

        assertFalse(extendedExpressionFilter.filteredLine("ligne 1"));
        assertFalse(extendedExpressionFilter.filteredLine("ligne 2"));
        assertFalse(extendedExpressionFilter.filteredLine("ligne 3"));
        assertTrue(extendedExpressionFilter.filteredLine("ligne 4"));
        assertFalse(extendedExpressionFilter.filteredLine("ligne 5"));
    }
}
