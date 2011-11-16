/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.release_test.common;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.imports.common.FieldFactory;
import net.codjo.imports.common.FieldType;
import net.codjo.imports.common.ImportBehavior;
import net.codjo.imports.common.ImportExpressionException;
import net.codjo.imports.common.ImportFilter;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.DirectoryFixture;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Test la fonctionnalité d'import.
 */
public class GlobalImportTest {
    private JdbcFixture jdbcFixture;
    private DirectoryFixture directoryFixture = DirectoryFixture.newTemporaryDirectoryFixture();
    private CompositeFixture fixture = new CompositeFixture();
    private ImportBehavior importbehavior;


    @Before
    public void setUp() throws Exception {
        jdbcFixture = new DatabaseFactory().createJdbcFixture();
        fixture.addFixture(jdbcFixture);
        fixture.addFixture(directoryFixture);

        fixture.doSetUp();
    }


    @After
    public void tearDown() throws Exception {
        jdbcFixture.drop(SqlTable.table("DEST_IMPORT"));
        fixture.doTearDown();
    }


    /**
     * Test import taille variable.
     *
     * @throws Exception Erreur
     * @since 2.05
     */
    @Test
    public void test_importVariableLength() throws Exception {
        // Configurer
        importbehavior = new ImportBehavior(null, null, 0, null, false, false, "DEST_IMPORT");
        buildDestTable("DEST_IMPORT");

        addVariableLengthField("COL_STRING", 1, FieldType.STRING_FIELD, ";", "Valeur.substring(3)");
        addVariableLengthField("COL_DATE", 3, FieldType.DATE_FIELD, ";", "dd/MM/yyyy");
        addVariableLengthField("COL_DECIMAL", 2, FieldType.NUMERIC_FIELD, ";", ".");

        importbehavior.setFilter(new ImportFilter() {
            public boolean filteredLine(String line) {
                return line.startsWith("rem");
            }
        });

        // Construire le fichier
        String[] content =
              new String[]{
                    "rem ligne supprimé par un filtre;;",
                    "Le petit cheval blanc;9999.25;30/01/2002",
                    "Le grand toto; 888.25;28/02/2002"
              };

        // Importer
        importbehavior.proceed(createFile(content), jdbcFixture.getConnection());

        // Compare le resultat
        String[][] tableContent =
              new String[][]{
                    {"9999.25000", "2002-01-30", "petit cheval blanc"},
                    {"888.25000", "2002-02-28", "grand toto"}
              };
        jdbcFixture.assertContent(SqlTable.table("DEST_IMPORT"), tableContent);
    }


    /**
     * Test import taille fixe.
     *
     * @throws Exception Erreur
     * @since 2.05
     */
    @Test
    public void test_importFixedLength() throws Exception {
        // Configurer
        importbehavior =
              new ImportBehavior(null, null,
                                 44 + System.getProperty("line.separator").length(), null, true, false,
                                 "DEST_IMPORT");

        buildDestTable("DEST_IMPORT");

        addFixedLengthField("COL_STRING", 1, 23, FieldType.STRING_FIELD, "Valeur.substring(3)");
        addFixedLengthField("COL_DATE", 35, 10, FieldType.DATE_FIELD, "dd/MM/yyyy");
        addFixedLengthField("COL_DECIMAL", 25, 9, FieldType.NUMERIC_FIELD, ".");

        // Construire le fichier
        String[] content =
              new String[]{
                    "Le petit cheval blanc  ; 9999.26 ;30/01/2002",
                    "Le grand toto          ;  888.25 ;28/02/2002"
              };

        // Importer
        importbehavior.proceed(createFile(content), jdbcFixture.getConnection());

        // Compare le resultat
        String[][] tableContent =
              new String[][]{
                    {"9999.26000", "2002-01-30", "petit cheval blanc"},
                    {"888.25000", "2002-02-28", "grand toto"}
              };
        jdbcFixture.assertContent(SqlTable.table("DEST_IMPORT"), tableContent);
    }


    private File createFile(String[] content) throws IOException {
        File file = new File(directoryFixture, "testImport.txt");
        PrintWriter in = new PrintWriter(new FileWriter(file));
        try {
            for (String aContent : content) {
                in.println(aContent);
            }
        }
        finally {
            in.close();
        }
        return file;
    }


    private void buildDestTable(String tableName) throws SQLException {
        String sql =
              "create table " + tableName + " (      "
              + "    COL_DECIMAL numeric(17,5) null, "
              + "    COL_DATE date null,             "
              + "    COL_STRING varchar(100) null    "
              + ")                                   ";
        jdbcFixture.executeUpdate(sql);
    }


    private void addVariableLengthField(String colName, int position, char fieldType,
                                        String separatorField, String format)
          throws ImportExpressionException {
        if (fieldType == FieldType.DATE_FIELD) {
            importbehavior.addField(FieldFactory.newField(colName, position, 0,
                                                          fieldType, null, format, false, false,
                                                          separatorField));
        }
        else if (fieldType == FieldType.NUMERIC_FIELD) {
            importbehavior.addField(FieldFactory.newField(colName, position, 0,
                                                          fieldType, format, null, false, false,
                                                          separatorField));
        }
        else {
            importbehavior.addField(FieldFactory.newField(colName, position, 0,
                                                          fieldType, null, null, false, false, separatorField,
                                                          format));
        }
    }


    private void addFixedLengthField(String colName, int position, int length,
                                     char fieldType, String format) throws ImportExpressionException {
        if (fieldType == FieldType.DATE_FIELD) {
            importbehavior.addField(FieldFactory.newField(colName, position, length,
                                                          fieldType, null, format, false, true, null));
        }
        else if (fieldType == FieldType.NUMERIC_FIELD) {
            importbehavior.addField(FieldFactory.newField(colName, position, length,
                                                          fieldType, format, null, false, true, null));
        }
        else {
            importbehavior.addField(FieldFactory.newField(colName, position, length,
                                                          fieldType, null, null, false, true, null, format));
        }
    }
}
