package net.codjo.imports.server.plugin;
import static net.codjo.database.common.api.structure.SqlTable.table;
import net.codjo.tokio.TokioFixture;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class DefaultImportJobDaoTest {
    private TokioFixture fixture = new TokioFixture(DefaultImportJobDaoTest.class);
    private DefaultImportJobDao dao = new DefaultImportJobDao();
    private static final String PM_IMPORT_SETTINGS_STRUCTURE =
          "  IMPORT_SETTINGS_ID  int          not null, "
          + "FILE_TYPE           varchar(30)  not null, "
          + "DEST_TABLE          varchar(30)  not null";


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();

        fixture.getJdbcFixture().drop(table("PM_IMPORT_SETTINGS"));
        fixture.getJdbcFixture().create(table("PM_IMPORT_SETTINGS"), PM_IMPORT_SETTINGS_STRUCTURE);
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_getDestinationTable() throws Exception {
        fixture.insertInputInDb("nominal");

        assertEquals("MY_QUARANTINE_TEST",
                     dao.getDestinationTable(fixture.getConnection(), "DefaultDaoTest"));
    }


    @Test
    public void test_getDestinationTable_unknownFileType() throws Exception {
        fixture.insertInputInDb("nominal");

        assertNull(dao.getDestinationTable(fixture.getConnection(), "UnknownFileType"));
    }
}
