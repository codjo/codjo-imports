/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.imports.common.ImportFailureException;
import java.io.File;
import java.sql.SQLException;
/**
 *
 */
interface ImporterFactory {
    public void init(Agent agent, AclMessage message);


    public Importer createImporter(File filePath)
          throws SQLException, ImportFailureException;
}
