/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.sql.Connection;
/**
 * Interface permettant de créer des filtres d'import.
 *
 * @version $Revision: 1.4 $
 */
public interface FilterFactory {
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
     * @throws ImportFailureException s'il y a eu un problème
     */
    public ImportFilter createFilter(Connection connection, String fileType,
        String sourceSystem) throws ImportFailureException;
}
