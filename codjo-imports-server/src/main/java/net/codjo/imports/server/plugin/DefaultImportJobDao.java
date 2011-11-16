package net.codjo.imports.server.plugin;
import net.codjo.imports.server.plugin.ImportJobRequestHandler.ImportJobDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DefaultImportJobDao implements ImportJobDao {
    private static final String IMPORT_TABLE =
          "select DEST_TABLE from PM_IMPORT_SETTINGS WHERE charindex(FILE_TYPE,?) >0 "
          + "order by char_length(FILE_TYPE) desc";


    public String getDestinationTable(Connection connection, String fileName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(IMPORT_TABLE);
        try {
            statement.setString(1, fileName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("DEST_TABLE");
            }
        }
        finally {
            statement.close();
        }
        return null;
    }
}
