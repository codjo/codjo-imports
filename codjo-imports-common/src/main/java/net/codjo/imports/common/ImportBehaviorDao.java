/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.codjo.expression.FunctionHolder;
/**
 * Classe permettant de charger un paramétrage d'import à partir de la BD.
 */
public class ImportBehaviorDao {
    private FunctionHolder[] functionHolders;


    /**
     * Chargement d'un ImportBehavior à partir de la BD.
     *
     * @param connection une connection
     * @param file       le fichier à importer
     * @param fileType   type du fichier à importer
     *
     * @return un comportement d'import
     *
     * @throws ImportFailureException Echec du chargement.
     */
    public ImportBehavior getImportBehavior(Connection connection, File file,
                                            String fileType) throws ImportFailureException {
        try {
            PreparedStatement preparedStatement =
                  buildPSImportSettingsId(connection, fileType, file);

            try {
                return loadBehavior(connection, preparedStatement, file);
            }
            finally {
                preparedStatement.close();
            }
        }
        catch (SQLException ex) {
            throw new ImportFailureException(ex, file);
        }
        catch (IllegalArgumentException illex) {
            throw new ImportFailureException(illex, file);
        }
    }


    /**
     * Chargement d'un ImportBehavior à partir de la BD.
     *
     * @param connection une connection
     * @param file       le fichier à importer
     *
     * @return un comportement d'import
     *
     * @throws ImportFailureException Echec du chargement.
     */
    public ImportBehavior getImportBehavior(Connection connection, File file)
          throws ImportFailureException {
        return getImportBehavior(connection, file, null);
    }


    private ImportBehavior loadBehavior(Connection con,
                                        PreparedStatement importSettingsIdPS, File file)
          throws SQLException, ImportExpressionException, UnknownImportException {
        ResultSet rs = importSettingsIdPS.executeQuery();
        if (rs.next()) {
            BigDecimal importId = rs.getBigDecimal("IMPORT_SETTINGS_ID");
            boolean fixedLength = rs.getBoolean("FIXED_LENGTH");
            String fieldSeparator =
                  convertFieldSeparator(rs.getString("FIELD_SEPARATOR"));

            ImportBehavior importBehavior = buildImportBehavior(rs);
            addFields(con, importBehavior, importId, fixedLength, fieldSeparator);

            return importBehavior;
        }
        else {
            throw new UnknownImportException(file);
        }
    }


    private PreparedStatement buildPSImportSettingsId(Connection con, String fileType,
                                                      File file) throws SQLException {
        PreparedStatement importSettingsIdPS;
        if ((fileType == null) || ("".equals(fileType))) {
            importSettingsIdPS = buildPSImportSettingsIdFromFileName(con, file.getName());
        }
        else {
            importSettingsIdPS = buildPSImportSettingsIdFromFileType(con, fileType);
        }
        return importSettingsIdPS;
    }


    private PreparedStatement buildPSImportSettingsIdFromFileType(Connection con,
                                                                  String fileType) throws SQLException {
        PreparedStatement importSettingsIdPS;

        importSettingsIdPS = con.prepareStatement(SQLRequestBuilderFactory.getSqlBuilder(con)
                                                        .getSelectSettingsFromType());
        importSettingsIdPS.setString(1, fileType);

        return importSettingsIdPS;
    }


    private PreparedStatement buildPSImportSettingsIdFromFileName(Connection con,
                                                                  String fileName) throws SQLException {
        PreparedStatement importSettingsIdPS;

        importSettingsIdPS = con.prepareStatement(SQLRequestBuilderFactory.getSqlBuilder(con)
                                                        .getSelectSettingsWithType());
        importSettingsIdPS.setString(1, fileName);

        return importSettingsIdPS;
    }


    private void addFields(Connection con, ImportBehavior importBehavior,
                           BigDecimal importSettingId, boolean fixedLength, String fieldSeparator)
          throws SQLException, ImportExpressionException {
        PreparedStatement fieldsST = con.prepareStatement(SQLRequestBuilderFactory.getSqlBuilder(con)
                                                                .getSelectFieldImportList());
        try {
            fieldsST.setBigDecimal(1, importSettingId);
            ResultSet rs = fieldsST.executeQuery();
            if (rs.next()) {
                do {
                    String expression = rs.getString("EXPRESSION");
                    char sourceFieldTypeChar;

                    try {
                        String sourceFieldType = rs.getString("SOURCE_FIELD_TYPE");
                        if (sourceFieldType != null && sourceFieldType.length() > 0) {
                            sourceFieldTypeChar = sourceFieldType.charAt(0);
                        }
                        else {
                            sourceFieldTypeChar = '\u0000';
                        }
                    }
                    catch (SQLException e) {
                        sourceFieldTypeChar = '\u0000';
                    }

                    importBehavior.addField(FieldFactory.newField(rs.getString(
                          "DB_DESTINATION_FIELD_NAME"), rs.getInt("POSITION"),
                                                                  rs.getInt("LENGTH"), sourceFieldTypeChar,
                                                                  rs.getString("DESTINATION_FIELD_TYPE").charAt(
                                                                        0),
                                                                  rs.getString("DECIMAL_SEPARATOR"),
                                                                  rs.getString("INPUT_DATE_FORMAT"),
                                                                  rs.getBoolean("REMOVE_LEFT_ZEROS"),
                                                                  fixedLength,
                                                                  fieldSeparator,
                                                                  expression,
                                                                  functionHolders));
                }
                while (rs.next());
            }
            else {
                throw new IllegalArgumentException(
                      "Paramétrage incomplet pour l'import : " + importSettingId);
            }
        }
        finally {
            fieldsST.close();
        }
    }


    private ImportBehavior buildImportBehavior(ResultSet resultSet)
          throws SQLException {
        return new ImportBehavior(resultSet.getString("FILE_TYPE"),
                                  resultSet.getString("SOURCE_SYSTEM"), resultSet.getInt("RECORD_LENGTH"),
                                  resultSet.getString("COMMENT"), resultSet.getBoolean("FIXED_LENGTH"),
                                  resultSet.getBoolean("HEADER_LINE"), resultSet.getString("DEST_TABLE"));
    }


    /**
     * Convertit (pour patcher) Le fieldSeparator. Cette methode est utilise a cause des BCP car il convertit "\t" en
     * "\\t".
     *
     * @param fieldSeparator Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private String convertFieldSeparator(String fieldSeparator) {
        if ("\\t".equals(fieldSeparator)) {
            return "\t";
        }
        else {
            return fieldSeparator;
        }
    }


    public void setFunctionHolders(List<FunctionHolder> functionHolders) {
        this.functionHolders = functionHolders.toArray(new FunctionHolder[functionHolders.size()]);
    }


    public void setFunctionHolder(FunctionHolder functionHolder) {
        this.functionHolders = new FunctionHolder[]{functionHolder};
    }
}
