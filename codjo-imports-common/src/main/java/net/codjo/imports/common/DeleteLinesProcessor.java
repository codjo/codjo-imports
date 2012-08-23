/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Pre-traitement supprimant des lignes dans la table final.
 */
public class DeleteLinesProcessor extends ProcessorAdapter {
    private final String whereClause;


    /**
     * Constructeur.
     *
     * @param whereClause La clause where SQL (sans le mot clef where).
     */
    public DeleteLinesProcessor(String whereClause) {
        this.whereClause = whereClause;
    }


    /**
     * Constructeur par défaut sans clause where.
     */
    public DeleteLinesProcessor() {
        this.whereClause = null;
    }


    @Override
    public void preProceed(Connection con, String quarantineTableName, File file) throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate("delete from " + quarantineTableName
                               + ((whereClause != null) ? " where " + whereClause : ""));
        }
        finally {
            stmt.close();
        }
    }
}
