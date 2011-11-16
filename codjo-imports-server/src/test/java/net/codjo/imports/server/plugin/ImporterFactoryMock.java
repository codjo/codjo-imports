/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.test.common.LogString;
import java.io.File;
import java.sql.SQLException;
/**
 * Mock de la classe {@link ImporterFactory}.
 */
class ImporterFactoryMock implements ImporterFactory {
    private LogString log;
    private Importer importer;
    private ImportFailureException createImporterFailure;


    ImporterFactoryMock(LogString log) {
        this.log = log;
        importer = new ImporterMock(new LogString("importer", log));
    }


    public void init(Agent agent, AclMessage message) {
        log.call("init", "agent:" + agent.getAID().getLocalName(),
                 "message:" + AclMessage.performativeToString(message.getPerformative()));
    }


    public Importer createImporter(File filePath)
          throws SQLException, ImportFailureException {
        log.call("createImporter", filePath);
        if (createImporterFailure != null) {
            throw createImporterFailure;
        }
        return importer;
    }


    public void mockCreateImporterFailure(ImportFailureException failure) {
        this.createImporterFailure = failure;
    }


    public void mockCreateImporter(Importer importerMock) {
        this.importer = importerMock;
    }
}
