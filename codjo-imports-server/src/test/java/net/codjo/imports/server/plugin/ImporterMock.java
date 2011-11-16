/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.test.common.LogString;
import java.sql.SQLException;
/**
 *
 */
class ImporterMock implements Importer {
    private final LogString log;
    private ImportFailureException executeFailure;


    ImporterMock(LogString log) {
        this.log = log;
    }


    ImporterMock() {
        this(new LogString());
    }


    public void execute() throws SQLException, ImportFailureException {
        log.call("execute");
        if (executeFailure != null) {
            throw executeFailure;
        }
    }


    public String getDestinationTable() {
        return "MY_QUARANTINE";
    }


    public String getFileName() {
        return "to_import.txt";
    }


    public void mockExecuteFailure(ImportFailureException exception) {
        executeFailure = exception;
    }
}
