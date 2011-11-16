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
public interface Processor {
    void preProceed(Connection con, String quarantineTableName, File file) throws SQLException;


    void postProceed(Connection con, String quarantineTableName, File file, ImportFailureException ex)
          throws SQLException;
}
