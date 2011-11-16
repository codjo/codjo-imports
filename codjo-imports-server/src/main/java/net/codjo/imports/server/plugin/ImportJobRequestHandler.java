package net.codjo.imports.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.plugin.JdbcServerOperations;
import net.codjo.workflow.common.message.JobRequest;
import net.codjo.workflow.common.organiser.Job;
import net.codjo.workflow.server.organiser.AbstractJobBuilder;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

class ImportJobRequestHandler extends AbstractJobBuilder {
    private static final Logger LOGGER = Logger.getLogger(ImportJobRequestHandler.class);
    private static final String IMPORT_FILE_NAME = "import.fileName";
    private final JdbcServerOperations jdbcServerOperations;
    private final ImportJobDao importJobDao;


    ImportJobRequestHandler(JdbcServerOperations jdbcServerOperations, ImportJobDao importJobDao) {
        this.jdbcServerOperations = jdbcServerOperations;
        this.importJobDao = importJobDao;
    }


    @Override
    public boolean accept(JobRequest jobRequest) {
        return "import".equals(jobRequest.getType());
    }


    @Override
    public Job createJob(JobRequest jobRequest, Job job, UserId userId) {
        try {
            return createJobImpl(jobRequest, job, userId);
        }
        catch (SQLException e) {
            throw new RuntimeException(
                  "Impossible de créer l'objet Job correspondant à la JobRequest "
                  + jobRequest.getId(), e);
        }
    }


    private Job createJobImpl(JobRequest jobRequest, Job job, UserId userId) throws SQLException {
        String importFileName = jobRequest.getArguments().get(IMPORT_FILE_NAME);
        ConnectionPool connectionPool = jdbcServerOperations.getPool(userId);

        Connection connection = connectionPool.getConnection();
        String destinationTable;
        try {
            destinationTable = importJobDao.getDestinationTable(connection, importFileName);
        }
        finally {
            try {
                connectionPool.releaseConnection(connection);
            }
            catch (SQLException e) {
                LOGGER.warn("Impossible de fermer la connexion !!!", e);
            }
        }

        job.setTable(destinationTable);
        return job;
    }


    interface ImportJobDao {
        String getDestinationTable(Connection connection, String fileName) throws SQLException;
    }
}
