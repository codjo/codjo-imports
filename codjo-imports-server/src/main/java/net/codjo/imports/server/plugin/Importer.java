/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.imports.common.ImportFailureException;
import java.sql.SQLException;
/**
 *
 */
interface Importer {
    void execute() throws SQLException, ImportFailureException;


    String getDestinationTable();


    String getFileName();
}
