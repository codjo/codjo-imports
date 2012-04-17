package net.codjo.imports.server.plugin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.codjo.imports.common.SQLRequestBuilder;
import net.codjo.imports.common.SQLRequestBuilderFactory;
import net.codjo.imports.server.plugin.ImportJobRequestHandler.ImportJobDao;

class DefaultImportJobDao implements ImportJobDao {

    public String getDestinationTable(Connection connection, String fileName) throws SQLException {
        SQLRequestBuilder sqlRequestBuilder = SQLRequestBuilderFactory.getSqlBuilder(connection);
        PreparedStatement statement = connection.prepareStatement(sqlRequestBuilder.getImportTable());
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
