/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.test.DummyAgent;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.ImportFilter;
import net.codjo.imports.common.ProcessorAdapter;
import net.codjo.imports.common.UnknownImportException;
import net.codjo.sql.server.JdbcServiceUtil;
import net.codjo.sql.server.JdbcServiceUtilMock;
import net.codjo.test.common.LogString;
import net.codjo.test.common.PathUtil;
import net.codjo.tokio.TokioFixture;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import junit.framework.TestCase;
/**
 * Classe de test de {@link DefaultImporterFactory}.
 */
public class DefaultImporterFactoryTest extends TestCase {
    private static final String MY_QUARANTINE = "MY_QUARANTINE";
    private DefaultImporterFactory factory;
    private TokioFixture fixture = new TokioFixture(DefaultImporterFactoryTest.class);
    private PathUtil pathUtil = new PathUtil(DefaultImporterFactoryTest.class);
    private LogString log = new LogString();


    public void test_createImporter_forUnknownFile()
          throws Exception {
        try {
            factory.createImporter(new File("unknown.txt"));
            fail();
        }
        catch (UnknownImportException ex) {
            assertEquals("unknown.txt", ex.getInputFile().getName());
        }
    }


    public void test_executeImport() throws Exception {
        Importer importer = initDecisivImport();
        importer.execute();

        fixture.assertAllOutputs("ImportDecisiv");
    }


    public void test_importer_getDestinationTable()
          throws Exception {
        fixture.getJdbcFixture()
              .create(SqlTable.table(MY_QUARANTINE), "MY_FIELD_1 varchar(10), MY_FIELD_2 varchar(10)");
        fixture.insertInputInDb("ImportDecisiv");
        Importer importer =
              factory.createImporter(pathUtil.find("DefaultImporterFactoryTest.txt"));
        assertEquals(MY_QUARANTINE, importer.getDestinationTable());
    }


    public void test_setFilterFactory() throws Exception {
        assertNull(factory.getConfiguration().getFilterFactory());
        FilterFactoryMock filterFactory = new FilterFactoryMock(new ImportFilterAllMock());
        factory.getConfiguration().setFilterFactory(filterFactory);
        assertSame(filterFactory, factory.getConfiguration().getFilterFactory());

        initDecisivImport().execute();

        fixture.assertAllOutputs("EmptyQuarantine");
    }


    public void test_setFixedReadLine() throws Exception {
        assertTrue(factory.getConfiguration().isFixedReadLineFor("source-AGF"));
        factory.getConfiguration().setFixedReadLineFor("source-AGF", false);
        assertFalse(factory.getConfiguration().isFixedReadLineFor("source-AGF"));

        DefaultImporterFactory.DefaultImporter importer =
              (DefaultImporterFactory.DefaultImporter)initDecisivImport();

        assertFalse(importer.getImportBehavior().isFixedReadLine());
    }


    public void test_setPreProcessor() throws Exception {
        assertNull(factory.getConfiguration().getProcessorFor("source-AGF"));
        DeleteQuarantineProcessor processor = new DeleteQuarantineProcessor();
        factory.getConfiguration().setProcessorFor("source-AGF", processor);
        assertSame(processor, factory.getConfiguration().getProcessorFor("source-AGF"));

        DefaultImporterFactory.DefaultImporter importer =
              (DefaultImporterFactory.DefaultImporter)initDecisivImport();

        fixture.insertInputInDb("AjouteParasite", false);

        importer.execute();

        fixture.assertAllOutputs("ImportDecisiv");
    }


    @Override
    protected void setUp() throws Exception {
        fixture.doSetUp();
        fixture.getJdbcFixture().advanced().dropAllObjects();
        try {
            createSchema(fixture.getJdbcFixture());
        }
        catch (SQLException e) {
            fixture.doTearDown();
            fail();
        }

        JdbcServiceUtil mock =
              new JdbcServiceUtilMock(new LogString("jdbcServiceUtil", log), fixture.getJdbcFixture());
        factory = new DefaultImporterFactory(mock, new DefaultImportServerPluginConfiguration());
        factory.init(new DummyAgent(), new AclMessage(AclMessage.Performative.AGREE));
    }


    private static void createSchema(JdbcFixture jdbc)
          throws SQLException {
        // TODO a initialiser a partir de Datagen (utilisation de DatagenFixture)
        //      pour l'instant difficile car le fichier datagen ne fait pas parti du projet
        jdbc.create(SqlTable.table("PM_IMPORT_SETTINGS"), "      IMPORT_SETTINGS_ID  int  not null, "
                                                          + "    FILE_TYPE           varchar(30)  not null, "
                                                          + "    SOURCE_SYSTEM       varchar(30)  null, "
                                                          + "    RECORD_LENGTH       int  null, "
                                                          + "    COMMENT             varchar(255)  null, "
                                                          + "    FIXED_LENGTH        bit default 0  not null, "
                                                          + "    FIELD_SEPARATOR     varchar(2)  null, "
                                                          + "    HEADER_LINE         bit default 0  not null, "
                                                          + "    DEST_TABLE          varchar(30)  not null");
        jdbc.create(SqlTable.table("PM_FIELD_IMPORT_SETTINGS"),
                    "      IMPORT_SETTINGS_ID        int  not null, "
                    + "    POSITION                  int  not null, "
                    + "    LENGTH                    int  null, "
                    + "    DB_DESTINATION_FIELD_NAME varchar(30)  not null, "
                    + "    DESTINATION_FIELD_TYPE    varchar(1)  not null, "
                    + "    INPUT_DATE_FORMAT         varchar(10)  null, "
                    + "    REMOVE_LEFT_ZEROS         bit default 0  not null, "
                    + "    DECIMAL_SEPARATOR         varchar(1)  null, "
                    + "    EXPRESSION                text  null");
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }


    private Importer initDecisivImport() throws SQLException, ImportFailureException {
        fixture.getJdbcFixture().create(SqlTable.table(MY_QUARANTINE), "MY_FIELD_1 varchar(10), "
                                                                       + "MY_FIELD_2 varchar(10), "
                                                                       + "SOURCE_SYSTEM varchar(255), "
                                                                       + "SOURCE_FILE varchar(255), "
                                                                       + "CREATION_DATETIME date");

        fixture.insertInputInDb("ImportDecisiv");

        return factory.createImporter(pathUtil.find("DefaultImporterFactoryTest.txt"));
    }


    private static class ImportFilterAllMock implements ImportFilter {
        public boolean filteredLine(String line) {
            return true;
        }
    }

    private static class DeleteQuarantineProcessor extends ProcessorAdapter {
        @Override
        public void preProceed(Connection con, String quarantineTableName, File file) throws SQLException {
            con.createStatement().executeUpdate("delete " + quarantineTableName);
        }
    }
}
