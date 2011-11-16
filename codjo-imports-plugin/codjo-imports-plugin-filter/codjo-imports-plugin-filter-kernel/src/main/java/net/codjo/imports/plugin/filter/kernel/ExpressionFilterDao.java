/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.plugin.filter.kernel;
import net.codjo.imports.common.FilterFactory;
import net.codjo.imports.common.ImportFailureException;
import net.codjo.imports.common.ImportFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
/**
 * DAO pour les filtres d'import utilisant des expressions Java.
 *
 * @see net.codjo.imports.plugin.filter.kernel.ExpressionFilter
 */
public class ExpressionFilterDao implements FilterFactory {
    private static final Logger LOG = Logger.getLogger(ExpressionFilterDao.class);

    /**
     * Crée un {@link net.codjo.imports.common.ImportFilter} à partir d'un type d'import ou
     * d'un système source. Si le filtre associé à cet import est vide,
     * <code>null</code> est renvoyé.
     *
     * @param connection la connexion JDBC
     * @param fileType type d'import
     * @param sourceSystem le système source de l'import
     *
     * @return le filtre d'import
     *
     * @throws ImportFailureException s'il y a eu un problème de configuation ou d'accès
     *         à la base de données.
     */
    public ImportFilter createFilter(Connection connection, String fileType,
        String sourceSystem) throws ImportFailureException {
        PreparedStatement preparedStatement = null;

        String expression;
        try {
            preparedStatement =
                connection.prepareStatement(
                    "select FILTER_EXPRESSION from PM_IMPORT_SETTINGS where FILE_TYPE = ?");
            preparedStatement.setString(1, fileType);

            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                throw new FilterException("Aucun paramétrage avec FILE_TYPE=" + fileType
                    + " dans la table PM_IMPORT_SETTINGS");
            }

            expression = rs.getString("FILTER_EXPRESSION");

            if (rs.next()) {
                throw new FilterException("Il existe plusieurs lignes avec FILE_TYPE="
                    + fileType + " dans la table PM_IMPORT_SETTINGS");
            }

            ExpressionFilter expressionFilter = null;
            if ((expression != null) && (expression.trim().length() > 0)) {
                expressionFilter = new ExpressionFilter(expression);
            }

            return expressionFilter;
        }
        catch (SQLException ex) {
            throw new ImportFailureException("Problème d'accès à la base de données.", ex);
        }
        finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                    LOG.error("Erreur à la fermeture du statement.", e);
                }
            }
        }
    }
}
