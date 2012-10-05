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
import java.util.List;
import junit.framework.TestCase;

public class ImportBehaviorDaoTest extends TestCase {
    private JdbcFixture jdbcFixture;
    private Connection connection;


    public void test_getImportBehavior() throws Exception {
        fillPmImportSettingsTables(false);

        ImportBehaviorDao importBehaviorDao = new ImportBehaviorDao();
        File fileToImport = new File("unexisting_file_To_Import.txt");
        String fileType = "IBOXX";
        ImportBehavior importBehavior =
              importBehaviorDao.getImportBehavior(connection, fileToImport, fileType);

        assertEquals("BEN_ASSET_TEMP", importBehavior.getDestTable());

        List fields = importBehavior.getFieldImportList();
        assertEquals(3, fields.size());
        assertListContainsField(fields, "ASSET_TEMP_ASSET_ISIN_CODE");
        assertListContainsField(fields, "ASSET_TEMP_ASSET_DATE");
        assertListContainsField(fields, "ASSET_TEMP_ASSET_LABEL");
    }


    public void test_getImportBehaviorWithSourceFieldType()
          throws Exception {
        fillPmImportSettingsTables(true);

        ImportBehaviorDao importBehaviorDao = new ImportBehaviorDao();
        File fileToImport = new File("unexisting_file_To_Import.txt");
        String fileType = "IBOXX";
        ImportBehavior importBehavior =
              importBehaviorDao.getImportBehavior(connection, fileToImport, fileType);

        assertEquals("BEN_ASSET_TEMP", importBehavior.getDestTable());

        List fields = importBehavior.getFieldImportList();
        assertEquals(3, fields.size());
        assertListContainsField(fields, "ASSET_TEMP_ASSET_ISIN_CODE");
        assertListContainsField(fields, "ASSET_TEMP_ASSET_DATE");
        assertListContainsField(fields, "ASSET_TEMP_ASSET_LABEL");
    }


    private void assertListContainsField(List fieldImportList, String destinationFieldName) {
        for (Object aFieldImportList : fieldImportList) {
            Field field = (Field)aFieldImportList;
            if (destinationFieldName.equals(field.getDBDestFieldName())) {
                return;
            }
        }
        assertTrue(false);
    }


    private void fillPmImportSettingsTables(boolean withSourceFieldType)
          throws SQLException {
        jdbcFixture.dropTable("PM_IMPORT_SETTINGS");
        jdbcFixture.dropTable("PM_FIELD_IMPORT_SETTINGS");

        Statement statement = connection.createStatement();

        statement.executeUpdate("create table PM_IMPORT_SETTINGS                   \n"
                                + "(                                                                   \n"
                                + "    IMPORT_SETTINGS_ID              int                    not null,\n"
                                + "    FILE_TYPE                       varchar(30)            not null,\n"
                                + "    SOURCE_SYSTEM                   varchar(30)            null    ,\n"
                                + "    RECORD_LENGTH                   int                    null    ,\n"
                                + "    COMMENT                         varchar(255)           null    ,\n"
                                + "    FIXED_LENGTH                    bit                    default 0 not null,\n"
                                + "    FIELD_SEPARATOR                 varchar(2)             null    ,\n"
                                + "    HEADER_LINE                     bit                    default 0 not null,\n"
                                + "    DEST_TABLE                      varchar(30)            not null,\n"
                                + "    FILTER_EXPRESSION               varchar(255)           null     \n"
                                + ")\n");
        statement.executeUpdate("insert into PM_IMPORT_SETTINGS "
                                + " (FILE_TYPE, COMMENT, DEST_TABLE, FIELD_SEPARATOR, FIXED_LENGTH, HEADER_LINE, IMPORT_SETTINGS_ID, RECORD_LENGTH, SOURCE_SYSTEM)"
                                + " values ('IBOXX', 'Import des titres IBOXX', 'BEN_ASSET_TEMP', ',', '0', '1', 1, 0, 'IBOXX')");

        String sourceFieldTypeDefinition = "";
        String sourceFieldTypeColumn = "";
        String sourceFieldTypeValue = "";
        if (withSourceFieldType) {
            sourceFieldTypeDefinition =
                  ",    SOURCE_FIELD_TYPE               varchar(1)             null     \n";
            sourceFieldTypeColumn = ", SOURCE_FIELD_TYPE";
            sourceFieldTypeValue = ", 'S'";
        }
        statement.execute("create table PM_FIELD_IMPORT_SETTINGS                   \n"
                          + "(                                                                   \n"
                          + "    IMPORT_SETTINGS_ID              int                    not null,\n"
                          + "    POSITION                        int                    not null,\n"
                          + "    LENGTH                          int                    null    ,\n"
                          + "    DB_DESTINATION_FIELD_NAME       varchar(30)            not null,\n"
                          + "    DESTINATION_FIELD_TYPE          varchar(1)             not null,\n"
                          + "    INPUT_DATE_FORMAT               varchar(10)            null    ,\n"
                          + "    REMOVE_LEFT_ZEROS               bit                    default 0 not null,\n"
                          + "    DECIMAL_SEPARATOR               varchar(1)             null    ,\n"
                          + "    EXPRESSION                      varchar(255)           null    ,\n"
                          + "    FIELD_IMPORT_SETTINGS_ID        numeric(18)                    \n"
                          + sourceFieldTypeDefinition + ")\n");

        statement.executeUpdate("insert into PM_FIELD_IMPORT_SETTINGS"
                                + " (DB_DESTINATION_FIELD_NAME, DESTINATION_FIELD_TYPE, EXPRESSION, FIELD_IMPORT_SETTINGS_ID, IMPORT_SETTINGS_ID, POSITION, REMOVE_LEFT_ZEROS"
                                + sourceFieldTypeColumn + ")"
                                + " values ('ASSET_TEMP_ASSET_ISIN_CODE', 'S', 'iif(Valeur == \"*\", null, Valeur)', 1, 1, 2, 0"
                                + sourceFieldTypeValue + ")");
        statement.executeUpdate("insert into PM_FIELD_IMPORT_SETTINGS"
                                + " (DB_DESTINATION_FIELD_NAME, DESTINATION_FIELD_TYPE, EXPRESSION, FIELD_IMPORT_SETTINGS_ID, IMPORT_SETTINGS_ID, POSITION, REMOVE_LEFT_ZEROS, INPUT_DATE_FORMAT)"
                                + " values ('ASSET_TEMP_ASSET_DATE', 'D', null, 2, 1, 1, 0, 'yyyy-MM-dd')");
        statement.executeUpdate("insert into PM_FIELD_IMPORT_SETTINGS"
                                + " (DB_DESTINATION_FIELD_NAME, DESTINATION_FIELD_TYPE, EXPRESSION, FIELD_IMPORT_SETTINGS_ID, IMPORT_SETTINGS_ID, POSITION, REMOVE_LEFT_ZEROS)"
                                + " values ('ASSET_TEMP_ASSET_LABEL', 'S', 'iif(Valeur == \"*\", null, Valeur)', 3, 1, 4, 0)");

//        statement.executeUpdate("insert into PM_FIELD_IMPORT_SETTINGS"
//            + " (DB_DESTINATION_FIELD_NAME, DESTINATION_FIELD_TYPE, EXPRESSION, FIELD_IMPORT_SETTINGS_ID, IMPORT_SETTINGS_ID, POSITION, REMOVE_LEFT_ZEROS)"
//            + " values ('ASSET_TEMP_ASSET_LABEL', 'S', 'ImportHelpers.proceed(Valeur)', 3, 1, 4, 0)");
    }


    @Override
    protected void setUp() throws SQLException {
        jdbcFixture = new JdbcFixture();
        jdbcFixture.setUp();
        connection = jdbcFixture.getConnection();
    }


    @Override
    protected void tearDown() throws SQLException {
        jdbcFixture.tearDown();
    }
}
