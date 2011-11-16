/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FilterFunctions}.
 */
public class FilterFunctionsTest extends TestCase {
    private FilterFunctions holder = new FilterFunctions();

    public void test_fields() {
        assertEquals("de", holder.extractField("Ligne;de;donnees", ";", 2));
        assertEquals("0.03", holder.extractField("001;002;0.03;0.04", ";", 3));
        assertEquals("12.589", holder.extractField("0.2,5,8965,445,12.589,3625,102", ",", 5));
        assertEquals("separateur", holder.extractField("separateur---complique", "---", 1));
        assertEquals("tab2", holder.extractField("tab1	tab2	tab3	tab4", "\t", 2));
    }
}
