/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.sql.SQLException;
import junit.framework.TestCase;
/**
 * Classe de test de {@link SQLFieldList}.
 */
public class SQLFieldListTest extends TestCase {
    JdbcFixture jdbc = new JdbcFixture();


    public void test_addAll() {
        SQLFieldList listA = new SQLFieldList();
        listA.addStringField("a");
        listA.setFieldValue("a", "listA_valA");
        listA.addStringField("c");
        listA.setFieldValue("c", "listA_valC");

        SQLFieldList listB = new SQLFieldList();
        listB.addStringField("a");
        listB.setFieldValue("a", "listB_valA");
        listB.addStringField("b");
        listB.setFieldValue("b", "listB_valB");

        listA.addAll(listB);
        assertEquals(listA.getFieldValue("a"), "listB_valA");
        assertEquals(listA.getFieldValue("b"), "listB_valB");
        assertEquals(listA.getFieldValue("c"), "listA_valC");
    }


    public void test_clear() {
        SQLFieldList list = new SQLFieldList();
        list.addStringField("a");
        list.setFieldValue("a", "valA");
        assertEquals(list.getFieldValue("a"), "valA");
        list.clearValues();
        assertEquals(list.getFieldValue("a"), null);
    }


    public void test_constructor() throws Exception {
        jdbc.createSampleTable();

        SQLFieldList list = new SQLFieldList("DEST_IMPORT", jdbc.getConnection());

        // Verifie que le champs existe
        assertEquals(java.sql.Types.DATE, list.getFieldType("COL_DATE"));
        assertEquals(java.sql.Types.VARCHAR, list.getFieldType("COL_STRING"));
        assertEquals(java.sql.Types.NUMERIC, list.getFieldType("COL_DECIMAL"));
    }


    public void test_removeField() {
        SQLFieldList list = new SQLFieldList();
        list.addStringField("a");
        list.addStringField("b");
        list.setFieldValue("a", "valA");
        assertEquals(list.getFieldValue("a"), "valA");
        list.removeField("a");
        try {
            list.getFieldValue("a");
            fail("Colonne a supprime");
        }
        catch (Exception ex) {
        }
    }


    public void test_sortDBFieldList() throws Exception {
        jdbc.createSampleTable();
        SQLFieldList list = new SQLFieldList("DEST_IMPORT", jdbc.getConnection());

        // Verifie que les champs sont triés
        assertEquals("COL_DATE", (list.getSortedDBFieldNameList()).get(0));
        assertEquals("COL_DECIMAL", (list.getSortedDBFieldNameList()).get(1));
        assertEquals("COL_STRING", (list.getSortedDBFieldNameList()).get(2));
    }


    @Override
    protected void setUp() throws Exception {
        jdbc.setUp();
    }


    @Override
    protected void tearDown() throws SQLException {
        jdbc.tearDown();
    }
}
