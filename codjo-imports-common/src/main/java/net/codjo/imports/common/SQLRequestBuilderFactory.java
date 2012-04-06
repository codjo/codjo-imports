package net.codjo.imports.common;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class SQLRequestBuilderFactory {
    private SQLRequestBuilderFactory() {
    }


    public static SQLRequestBuilder getSqlBuilder(Connection connection) throws SQLException {
        return getSqlBuilder(connection.getMetaData().getDriverName());
    }


    public static SQLRequestBuilder getSqlBuilder(String driverName) {
        if (driverName != null && driverName.toLowerCase().contains("oracle")) {
            return new OracleSQLRequestBuilder();
        }
        else {
            return new DefaultSQLRequestBuilder();
        }
    }


    private static class DefaultSQLRequestBuilder implements SQLRequestBuilder {
        private static final String SELECT_SETTINGS_FROM_TYPE =
              "select * from PM_IMPORT_SETTINGS where FILE_TYPE = ? ";
        private static final String SELECT_SETTINGS_WITH_TYPE =
              "select * from PM_IMPORT_SETTINGS where charindex(FILE_TYPE,?) >0 "
              + "order by char_length(FILE_TYPE) desc";
        private static final String SELECT_FIELD_IMPORT_LIST =
              "select * from PM_FIELD_IMPORT_SETTINGS where IMPORT_SETTINGS_ID = ? ";
        private static final String IMPORT_TABLE =
              "select DEST_TABLE from PM_IMPORT_SETTINGS WHERE charindex(FILE_TYPE,?) >0 "
              + "order by char_length(FILE_TYPE) desc";


        public String getSelectSettingsFromType() {
            return SELECT_SETTINGS_FROM_TYPE;
        }


        public String getSelectSettingsWithType() {
            return SELECT_SETTINGS_WITH_TYPE;
        }


        public String getSelectFieldImportList() {
            return SELECT_FIELD_IMPORT_LIST;
        }


        public String getImportTable() {
            return IMPORT_TABLE;
        }
    }

    private static class OracleSQLRequestBuilder implements SQLRequestBuilder {
        private static final String SELECT_SETTINGS_FROM_TYPE =
              "select * from PM_IMPORT_SETTINGS where FILE_TYPE = ? ";
        private static final String SELECT_SETTINGS_WITH_TYPE =
              "select * from PM_IMPORT_SETTINGS where instr(?,FILE_TYPE) >0 "
              + "order by length(FILE_TYPE) desc";
        private static final String SELECT_FIELD_IMPORT_LIST =
              "select * from PM_FIELD_IMPORT_SETTINGS where IMPORT_SETTINGS_ID = ? ";
        private static final String IMPORT_TABLE =
              "select DEST_TABLE from PM_IMPORT_SETTINGS WHERE instr(?,FILE_TYPE) >0 "
              + "order by length(FILE_TYPE) desc";


        public String getSelectSettingsFromType() {
            return SELECT_SETTINGS_FROM_TYPE;
        }


        public String getSelectSettingsWithType() {
            return SELECT_SETTINGS_WITH_TYPE;
        }


        public String getSelectFieldImportList() {
            return SELECT_FIELD_IMPORT_LIST;
        }


        public String getImportTable() {
            return IMPORT_TABLE;
        }
    }
}
