/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Fixture JDBC pour les tests.
 */
public class JdbcFixture {
    private String url;
    private String driver;
    private String username;
    private String password;
    private java.sql.Connection connection;


    public JdbcFixture() {
        setUrl("jdbc:hsqldb:.");
        setDriver("org.hsqldb.jdbcDriver");
        setUsername("sa");
        setPassword("");
    }


    public void setUp() {
        ;
    }


    public void createSampleTable() throws SQLException {
        dropTable("DEST_IMPORT");
        Statement stmt = getConnection().createStatement();
        try {
            stmt.executeUpdate("create table DEST_IMPORT ( "
                               + "      COL_DECIMAL numeric(17,5) null, " + "      COL_DATE date null, "
                               + "      COL_STRING varchar(100) null " + ")");
        }
        finally {
            stmt.close();
        }
    }


    public void dropTable(String tableName) throws SQLException {
        Statement stmt = getConnection().createStatement();
        try {
            stmt.executeUpdate("drop table " + tableName + " if exists");
        }
        finally {
            stmt.close();
        }
    }


    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


    public static void dumpResultSet(ResultSet resultSet, Writer outWriter)
          throws SQLException {
        PrintWriter out = new PrintWriter(outWriter);
        ResultSetMetaData rsmd = resultSet.getMetaData();

        // Spool Header
        int colmumnCount = rsmd.getColumnCount();
        for (int i = 1; i <= colmumnCount; i++) {
            out.print("\"" + rsmd.getColumnName(i) + "\"");
            if (i + 1 <= colmumnCount) {
                out.print(", ");
            }
        }
        out.println();

//        for (int i = 1; i <= colmumnCount; i++) {
//            out.print("\t" + rsmd.getColumnTypeName(i));
//        }
//        out.println();
        // Spool Content
        while (resultSet.next()) {
            for (int i = 1; i <= colmumnCount; i++) {
                out.print("\"" + resultSet.getObject(i) + "\"");
                if (i + 1 <= colmumnCount) {
                    out.print(", ");
                }
            }
            out.println();
        }
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getDriver() {
        return driver;
    }


    public void setDriver(String driver) {
        this.driver = driver;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public java.sql.Connection getConnection() {
        if (connection == null) {
            try {
                buildConnection();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(
                      "Erreur lors de la création de la connection !");
            }
        }
        return connection;
    }


    private void buildConnection() throws ClassNotFoundException, SQLException {
        Class.forName(getDriver());
        connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
    }
}
