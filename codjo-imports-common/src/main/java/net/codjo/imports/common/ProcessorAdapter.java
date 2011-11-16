/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class ProcessorAdapter implements Processor {

    public void preProceed(Connection con, String quarantineTableName, File file) throws SQLException {
    }


    public void postProceed(Connection con, String quarantineTableName, File file, ImportFailureException ex)
          throws SQLException {
    }
}