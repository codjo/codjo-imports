/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common.fonctionalTest;
import net.codjo.imports.common.DeleteLinesProcessor;
import net.codjo.imports.common.FieldFactory;
import net.codjo.imports.common.FieldType;
import net.codjo.imports.common.ImportBehavior;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.ImportFilter;
import net.codjo.imports.common.JdbcFixture;
import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ImportTest}.
 */
public class ImportTest extends TestCase {
    private JdbcFixture jdbcfixtureInst = new JdbcFixture();
    private Connection connection;


    public void test_proceed_longueurVariable() throws Exception {
        ImportBehavior importbehavior = buildBehavior();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("delete from DEST_IMPORT");

        importbehavior.proceed(new File(ImportTest.class.getResource("inputFile.txt").getFile()),
                               connection);
        StringWriter result = getResult(stmt);

        assertEquals("\"COL_DECIMAL\", \"COL_DATE\", \"COL_STRING\""
                     + System.getProperty("line.separator")
                     + "\"9999.25000\", \"2002-01-30\", \"Le petit cheval blanc\""
                     + System.getProperty("line.separator")
                     + "\"888.25000\", \"2002-02-28\", \"Le grand toto\""
                     + System.getProperty("line.separator"), result.toString());
    }


    public void test_proceed_preprocessing() throws Exception {
        ImportBehavior importbehavior = buildBehavior();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into DEST_IMPORT (COL_STRING) values ('badbadbad')");

        importbehavior.setProcessor(new DeleteLinesProcessor());

        importbehavior.proceed(new File(ImportTest.class.getResource("inputFile.txt").getFile()),
                               connection);

        StringWriter result = getResult(stmt);

        assertEquals("\"COL_DECIMAL\", \"COL_DATE\", \"COL_STRING\""
                     + System.getProperty("line.separator")
                     + "\"9999.25000\", \"2002-01-30\", \"Le petit cheval blanc\""
                     + System.getProperty("line.separator")
                     + "\"888.25000\", \"2002-02-28\", \"Le grand toto\""
                     + System.getProperty("line.separator"), result.toString());
    }


    public void test_importWithError() throws Exception {
        ImportBehavior importbehavior = buildBehavior();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("delete from DEST_IMPORT");

        try {
            importbehavior.proceed(new File(ImportTest.class.getResource("inputFileWithError.txt").getFile()),
                                   connection);
            fail();
        }
        catch (ImportFailureException e) {
            assertEquals("Echec de l'import du fichier 'inputFileWithError.txt', "
                         + "at 4, 'Le grand toto; null;28/02/2002', "
                         + "Mauvais format de [COL_DECIMAL] : net.codjo.imports.common.translator.NumberTranslator : "
                         + "Mauvais format : null", e.getMessage());
        }
        StringWriter result = getResult(stmt);

        assertEquals("\"COL_DECIMAL\", \"COL_DATE\", \"COL_STRING\""
                     + System.getProperty("line.separator")
                     + "\"9999.25000\", \"2002-01-30\", \"Le petit cheval blanc\""
                     + System.getProperty("line.separator"), result.toString());
    }


    private StringWriter getResult(Statement statement)
          throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from DEST_IMPORT");
        StringWriter writer = new StringWriter();
        JdbcFixture.dumpResultSet(resultSet, writer);
        return writer;
    }


    private ImportBehavior buildBehavior() {
        ImportBehavior importbehavior =
              new ImportBehavior(null, null, 0, null, false, false, "DEST_IMPORT");

        importbehavior.addField(FieldFactory.newField("COL_DATE", 3, 0,
                                                      FieldType.DATE_FIELD,
                                                      null,
                                                      "dd/MM/yyyy",
                                                      false,
                                                      false,
                                                      ";"));

        importbehavior.addField(FieldFactory.newField("COL_STRING", 1, 0,
                                                      FieldType.STRING_FIELD, null, null, false, false, ";"));

        importbehavior.addField(FieldFactory.newField("COL_DECIMAL", 2, 0,
                                                      FieldType.NUMERIC_FIELD, ".", null, false, false, ";"));

        importbehavior.setFilter(new ImportFilter() {
            public boolean filteredLine(String line) {
                return line.startsWith("rem");
            }
        });
        return importbehavior;
    }


    @Override
    protected void setUp() throws SQLException {
        jdbcfixtureInst.setUp();
        jdbcfixtureInst.createSampleTable();
        connection = jdbcfixtureInst.getConnection();
    }


    @Override
    protected void tearDown() throws SQLException {
        jdbcfixtureInst.tearDown();
    }
}
