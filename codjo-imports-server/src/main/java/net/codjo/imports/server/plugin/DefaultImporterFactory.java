/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.imports.common.ConstantField;
import net.codjo.imports.common.ImportBehavior;
import net.codjo.imports.common.ImportBehaviorDao;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.ImportFilter;
import net.codjo.imports.common.Processor;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.JdbcServiceUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
/**
 *
 */
class DefaultImporterFactory implements ImporterFactory {
    private final JdbcServiceUtil jdbcServiceUtil;
    private Agent agent;
    private AclMessage message;
    private ImportBehaviorDao dao = new ImportBehaviorDao();
    private ImportServerPluginConfiguration configuration;


    DefaultImporterFactory(JdbcServiceUtil jdbcServiceUtil, ImportServerPluginConfiguration configuration) {
        this.configuration = configuration;
        this.jdbcServiceUtil = jdbcServiceUtil;
    }


    public void init(Agent anAgent, AclMessage aMessage) {
        this.agent = anAgent;
        this.message = aMessage;
    }


    public Importer createImporter(File inputFile)
          throws SQLException, ImportFailureException {

        ConnectionPool connectionPool = jdbcServiceUtil.getConnectionPool(agent, message);
        Connection connection = connectionPool.getConnection();
        try {
            final java.sql.Timestamp now =
                  new java.sql.Timestamp(System.currentTimeMillis());

            dao.setFunctionHolders(getConfiguration().getFunctionHolders());
            ImportBehavior importBehavior = dao.getImportBehavior(connection, inputFile);

            importBehavior.addField(new ConstantField(Types.VARCHAR,
                                                      "SOURCE_SYSTEM", importBehavior.getSourceSystem()));
            importBehavior.addField(new ConstantField(Types.VARCHAR, "SOURCE_FILE",
                                                      getShortFileName(inputFile.getName())));
            importBehavior.addField(new ConstantField(Types.TIMESTAMP,
                                                      "CREATION_DATETIME", now));

            importBehavior.setFilter(getFilter(connection, importBehavior));
            importBehavior.setFixedReadLine(isFixedReadLineFor(importBehavior));
            importBehavior.setProcessor(getProcessorFor(importBehavior));

            return new DefaultImporter(importBehavior, inputFile, connectionPool);
        }
        finally {
            connectionPool.releaseConnection(connection);
        }
    }


    private static String getShortFileName(final String fileName) {
        if (fileName.length() > 30) {
            return fileName.substring(fileName.length() - 30);
        }
        return fileName;
    }


    public ImportServerPluginConfiguration getConfiguration() {
        return configuration;
    }


    private Processor getProcessorFor(ImportBehavior importBehavior) {
        return getConfiguration().getProcessorFor(importBehavior.getSourceSystem());
    }


    private boolean isFixedReadLineFor(ImportBehavior importBehavior) {
        return getConfiguration().isFixedReadLineFor(importBehavior.getSourceSystem());
    }


    private ImportFilter getFilter(Connection connection, ImportBehavior importBehavior) throws
                                                                                         ImportFailureException {
        if (getConfiguration().getFilterFactory() != null) {
            return getConfiguration().getFilterFactory().createFilter(connection,
                                                                      importBehavior.getFileType(),
                                                                      importBehavior.getSourceSystem());
        }
        return null;
    }


    static class DefaultImporter implements Importer {
        private final File inputFile;
        private final ConnectionPool connectionPool;
        private final ImportBehavior importBehavior;


        DefaultImporter(ImportBehavior importBehavior, File inputFile,
                        ConnectionPool connectionPool) {
            this.inputFile = inputFile;
            this.connectionPool = connectionPool;
            this.importBehavior = importBehavior;
        }


        public void execute() throws SQLException, ImportFailureException {
            Connection connection = connectionPool.getConnection();
            try {
                connection.setAutoCommit(false);
                importBehavior.proceed(inputFile, connection);
                connection.commit();
            }
            catch (InterruptedException e) {
                throw new ImportFailureException(e);
            }
            finally {
                connectionPool.releaseConnection(connection);
            }
        }


        public String getDestinationTable() {
            return importBehavior.getDestTable();
        }


        public String getFileName() {
            return inputFile.getName();
        }


        public ImportBehavior getImportBehavior() {
            return importBehavior;
        }
    }
}
