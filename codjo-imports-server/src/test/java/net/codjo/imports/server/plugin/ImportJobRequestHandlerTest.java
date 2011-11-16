package net.codjo.imports.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.imports.server.plugin.ImportJobRequestHandler.ImportJobDao;
import net.codjo.sql.server.ConnectionPoolMock;
import net.codjo.sql.server.plugin.JdbcServerOperations;
import net.codjo.test.common.LogString;
import net.codjo.workflow.common.message.Arguments;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.organiser.Job;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class ImportJobRequestHandlerTest {
    private ImportJobRequestHandler importJobRequestHandler;
    private LogString log = new LogString();
    private UserId userId = UserId.createId("loginTest", "passwordTest");
    private JdbcServerOperations jdbcServerOperationsMock = mock(JdbcServerOperations.class);
    private Connection connectionMock = mock(Connection.class);


    @Before
    public void setUp() throws Exception {
        ConnectionPoolMock connectionPoolMock = new ConnectionPoolMock(log);
        connectionPoolMock.mockGetConnection(connectionMock);
        stub(jdbcServerOperationsMock.getPool(Mockito.<UserId>anyObject())).toReturn(connectionPoolMock);

        importJobRequestHandler = new ImportJobRequestHandler(jdbcServerOperationsMock,
                                                              new ImportJobDaoMock());
    }


    @Test
    public void test_nominal() throws Exception {
        JobRequest jobRequest = new JobRequest("import");
        jobRequest.setArguments(new Arguments("import.fileName", "myFile"));

        Job job = importJobRequestHandler.createJob(jobRequest, userId);

        assertEquals("import", job.getType());
        assertEquals("AP_TEST", job.getTable());

        log.assertContent("getConnection(), releaseConnection(" + connectionMock + ")");
    }


    @Test
    public void test_error() throws Exception {
        ImportJobDao importJobDao = mock(ImportJobDao.class);
        Mockito.stub(importJobDao.getDestinationTable(connectionMock, "myFile"))
              .toThrow(new SQLException("Erreur !!!"));
        importJobRequestHandler = new ImportJobRequestHandler(jdbcServerOperationsMock, importJobDao);

        JobRequest jobRequest = new JobRequest("import");
        jobRequest.setId("id-007");
        jobRequest.setArguments(new Arguments("import.fileName", "myFile"));
        try {
            importJobRequestHandler.createJob(jobRequest, userId);
            fail();
        }
        catch (Exception e) {
            assertEquals("Impossible de créer l'objet Job correspondant à la JobRequest id-007",
                         e.getMessage());
        }
    }


    private static class ImportJobDaoMock implements ImportJobDao {
        public String getDestinationTable(Connection connection, String fileName) {
            assertNotNull(connection);
            assertEquals("myFile", fileName);
            return "AP_TEST";
        }
    }
}
