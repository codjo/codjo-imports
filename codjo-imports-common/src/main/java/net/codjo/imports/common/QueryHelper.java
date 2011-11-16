/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
/**
 * Assistant pour la creation de requete SQL.
 *
 * <p> Cette assistant les particularites suivantes :
 *
 * <ul> <li> Specifique a une table. </li> <li> Les requetes sont de type PreparedStatement. </li> <li> Les
 * requetes ne font pas de commit explicite. </li> <li> Il peut être creer, et seulement etre utilise plus
 * tard. </li> </ul>
 *
 * Pour des exemples d'utilisation, voir la classe de test (QueryHelperTest). </p>
 */
public class QueryHelper {
    private Connection connection;
    private String dbTableName;

    // Delete Statement
    private PreparedStatement deleteOne;
    private PreparedStatement insertOne;
    private SQLFieldList insertValues;

    // Save Statement
    private PreparedStatement maxID;

    // Select Statement
    private PreparedStatement selectAll;
    private PreparedStatement selectOne;
    private SQLFieldList selector;
    private PreparedStatement updateOne;


    /**
     * Construit QueryHelper ne pouvant faire qu'un doInsert ou un doSelectAll.
     *
     * @param dbTableName  Le nom de la table.
     * @param con          La connection utilise
     * @param insertValues Liste des champs SQL servant a l'insertion.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public QueryHelper(String dbTableName, Connection con, SQLFieldList insertValues)
          throws SQLException {
        this(dbTableName, con, insertValues, null);
    }


    /**
     * Constructor for the QueryHelper object
     *
     * @param dbTableName  Le nom de la table.
     * @param con          La connection utilise
     * @param insertValues Liste des champs SQL servant a l'insertion.
     * @param selectors    Liste des champs SQL servant a la selection (dans les clauses where)
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public QueryHelper(String dbTableName, Connection con, SQLFieldList insertValues,
                       SQLFieldList selectors) throws SQLException {
        if (dbTableName == null || con == null || insertValues == null) {
            throw new IllegalArgumentException();
        }

        this.dbTableName = dbTableName;
        this.selector = selectors;
        this.insertValues = insertValues;
        this.connection = con;

        selectAll = connection.prepareStatement("select * from " + dbTableName);

        if (selector != null) {
            selectOne =
                  connection.prepareStatement("select * from " + dbTableName + " where "
                                              + buildClause(selector.fieldNamesSet(), " and "));

            deleteOne =
                  connection.prepareStatement("delete from " + dbTableName + " where "
                                              + buildClause(selector.fieldNamesSet(), " and "));
        }

        insertOne =
              connection.prepareStatement(buildInsertQuery(dbTableName,
                                                           insertValues.fieldNamesSet())
                                          + " select @@identity");

        if (selector != null) {
            updateOne =
                  buildUpdateStatement(dbTableName, insertValues.fieldNamesSet(),
                                       selector.fieldNamesSet(), connection);

            maxID =
                  connection.prepareStatement("select max(" + selector.fieldNames().next()
                                              + ") from " + dbTableName);
        }
    }


    /**
     * Construit la requete d'insertion des champs defini dans <code>columns </code>. La requete construite
     * peut etre utilise pour un <code>PreparedStatement</code>
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     *
     * @return La requete insert (eg. "insert into MA_TABLE (CA, CB) values (?, ?)")
     */
    public static String buildInsertQuery(String dbTableName, Collection columns) {
        return "insert into " + dbTableName + " " + "(" + buildDBFieldNameList(columns)
               + ")" + " " + buildDBFieldValuesList(columns.size());
    }


    /**
     * Construction du preparedStatement pour un insertion.
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     * @param con         La connection supportant le PreparedStatement
     *
     * @return Le PreparedStatement
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public static PreparedStatement buildInsertStatement(String dbTableName,
                                                         Collection columns, Connection con)
          throws SQLException {
        return con.prepareStatement(buildInsertQuery(dbTableName, columns));
    }


    /**
     * Construit la requete de selection des champs defini dans <code>columns</code> . La ligne a mettre a
     * selectionner est defini par la liste <code>whereList</code> . La requete construite peut etre utilise
     * pour un <code>PreparedStatement </code>
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a selectionner
     * @param whereList   Les colonnes utilise dans la clause where
     *
     * @return La requete update (eg. "select CA, CB from MA_TABLE where CX=?")
     */
    public static String buildSelectQuery(String dbTableName, Collection columns,
                                          Collection whereList) {
        String cols = "*";
        if (columns != null) {
            cols = buildDBFieldNameList(columns);
        }
        return "select " + cols + " from " + dbTableName + " where "
               + buildClause(whereList, " and ");
    }


    /**
     * Construit la requete d'update des champs defini dans <code>columns</code> . La ligne a mettre a jour
     * est defini par la liste <code>whereList</code> . La requete construite peut etre utilise pour un
     * <code>PreparedStatement </code>
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     * @param whereList   Les colonnes utilise dans la clause where
     *
     * @return La requete update (eg. "update MA_TABLE set CA=? where CX=?")
     */
    public static String buildUpdateQuery(String dbTableName, Collection columns,
                                          Collection whereList) {
        return "update " + dbTableName + " set " + buildClause(columns, " , ")
               + " where " + buildClause(whereList, " and ");
    }


    /**
     * Construit la requete d'update des champs defini dans <code>columns</code> . La ligne a mettre a jour
     * est defini par la liste <code>whereList</code> et la clause where à ajouter à la fin . La requete
     * construite peut etre utilise pour un <code>PreparedStatement </code>
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     * @param whereList   Les colonnes utilise dans la clause where (CX=?)
     * @param whereClause La clause where à ajouter (CZ='TOTO'and ...)
     *
     * @return La requete update (eg. "update MA_TABLE set CA=? where CX=? and CZ='TOTO'and ...")
     */
    public static String buildUpdateQueryWithWhereClause(String dbTableName,
                                                         Collection columns,
                                                         Collection whereList,
                                                         String whereClause) {
        return buildUpdateQuery(dbTableName, columns, whereList) + " and " + whereClause;
    }


    /**
     * Construction du preparedStatement pour un update.
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     * @param whereList   Les colonnes utilise dans la clause where
     * @param con         La connection supportant le PreparedStatement
     *
     * @return Le PreparedStatement
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public static PreparedStatement buildUpdateStatement(String dbTableName,
                                                         Collection columns,
                                                         Collection whereList,
                                                         Connection con)
          throws SQLException {
        return con.prepareStatement(buildUpdateQuery(dbTableName, columns, whereList));
    }


    /**
     * Construction du preparedStatement pour un update avec une clause where. Celle-ci est utilisée pour
     * affiner la clause where de l'update (and "clauseWhere").
     *
     * @param dbTableName Le nom physique de la table
     * @param columns     Les colonnes a inserer
     * @param whereList   Les colonnes utilise dans la clause where
     * @param whereClause La clause where à ajouter
     * @param connection  La connection supportant le PreparedStatement
     *
     * @return Le PreparedStatement
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public static PreparedStatement buildUpdateStatementWithWhereClause(
          String dbTableName,
          Collection columns,
          Collection whereList,
          String whereClause,
          Connection connection) throws SQLException {
        PreparedStatement statement;
        if ((whereClause != null) || (!"".equals(whereClause))) {
            statement =
                  connection.prepareStatement(buildUpdateQuery(dbTableName, columns,
                                                               whereList));
        }
        else {
            statement =
                  connection.prepareStatement(buildUpdateQueryWithWhereClause(dbTableName,
                                                                              columns,
                                                                              whereList,
                                                                              whereClause));
        }
        return statement;
    }


    /**
     * Effacement d'un enregistrement.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public void doDelete() throws SQLException {
        fillStatement(1, deleteOne, selector);
        deleteOne.executeUpdate();
    }


    /**
     * Insertion d'un enregistrement. L'enregistrement est definie par la liste de champs d'insertion
     * (modifiable par setInsertValue())). La methode retourne le resultat de la requete <code>select
     * &amp;&amp; identity</code> .
     *
     * @return l'id de la ligne insere(si identity)
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     * @see #setInsertValue
     */
    public java.math.BigDecimal doInsert() throws SQLException {
        fillStatement(1, insertOne, insertValues);
        ResultSet rs = insertOne.executeQuery();
        try {
            rs.next();
            return rs.getBigDecimal(1);
        }
        finally {
            rs.close();
        }
    }


    /**
     * Applique un select avec clause where sur la table.
     *
     * @return Le ResultSet de la requete.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public ResultSet doSelect() throws SQLException {
        fillStatement(1, selectOne, selector);
        return selectOne.executeQuery();
    }


    /**
     * Applique un select sur la table.
     *
     * @return Le ResultSet de la requete.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public ResultSet doSelectAll() throws SQLException {
        return selectAll.executeQuery();
    }


    /**
     * Applique un select sur la table avec un tri. Attention : Cette requete n'est pas optimisee (Statement
     * standard).
     *
     * @param orderClause La clause de tri (ex : "TABLE_NAME").
     *
     * @return Le ResultSet de la requete trie.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public ResultSet doSelectAllOrderedBy(String orderClause)
          throws SQLException {
        if (orderClause == null) {
            throw new IllegalArgumentException("Clause de tri non renseignee");
        }
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("select * from " + dbTableName + " order by "
                                 + orderClause);
    }


    /**
     * Mise-a-jours d'un enregistrement.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public void doUpdate() throws SQLException {
        fillStatement(1, updateOne, insertValues);
        fillStatement(insertValues.size() + 1, updateOne, selector);
        updateOne.executeUpdate();
    }


    /**
     * Retourne la connection de ce <code>queryHelper</code> .
     *
     * @return La Connection
     */
    public Connection getConnection() {
        return connection;
    }


    /**
     * Retourne la valeur du champ.
     *
     * @param dbFieldName Le nom du champs SQL.
     *
     * @return La valeur
     */
    public Object getInsertValue(String dbFieldName) {
        return insertValues.getFieldValue(dbFieldName);
    }


    /**
     * Retourne un identifiant unique non utilise. La colonne utilisee pour identifiant est le premier champs
     * SQL utilise comme selector. La methode retourne le max+1.
     *
     * @return L'identifiant unique.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    public int getUniqueID() throws SQLException {
        ResultSet resultSet = maxID.executeQuery();
        try {
            resultSet.next();
            return resultSet.getInt(1) + 1;
        }
        finally {
            resultSet.close();
        }
    }


    /**
     * Positionne la valeur date date'un champs de la ligne.
     *
     * @param dbFieldName Le nom du champs SQL.
     * @param date        The new InsertValue value
     *
     * @see net.codjo.imports.common.SQLFieldList#setFieldValue(java.lang.String, java.util.Date)
     */
    public void setInsertValue(String dbFieldName, java.util.Date date) {
        insertValues.setFieldValue(dbFieldName, date);
    }


    /**
     * Positionne la valeur d'un champs de la ligne. Cette methode positionne la valeur du champs
     * <code>dbFieldName</code> utilise pour modifier la base.
     *
     * @param dbFieldName Le nom du champs SQL.
     * @param value       La valeur.
     */
    public void setInsertValue(String dbFieldName, Object value) {
        insertValues.setFieldValue(dbFieldName, value);
    }


    /**
     * Sets the InsertValue attribute of the QueryHelper object
     *
     * @param dbFieldName The new InsertValue value
     * @param value       The new InsertValue value
     */
    public void setInsertValue(String dbFieldName, int value) {
        insertValues.setFieldValue(dbFieldName, new Integer(value));
    }


    /**
     * Sets the InsertValue attribute of the QueryHelper object
     *
     * @param dbFieldName The new InsertValue value
     * @param value       The new InsertValue value
     */
    public void setInsertValue(String dbFieldName, boolean value) {
        //noinspection BooleanConstructorCall
        insertValues.setFieldValue(dbFieldName, new Boolean(value));
    }


    /**
     * Positionne la valeur d'un champs selecteur. Cette methode positionne la valeur du champs
     * <code>dbFieldName</code> utilise dans la clause where.
     *
     * @param dbFieldName Le nom du champs selecteur
     * @param value       La nouvelle valeur.
     */
    public void setSelectorValue(String dbFieldName, Object value) {
        selector.setFieldValue(dbFieldName, value);
    }


    /**
     * Sets the SelectorValue attribute of the QueryHelper object
     *
     * @param dbFieldName The new SelectorValue value
     * @param value       The new SelectorValue value
     */
    public void setSelectorValue(String dbFieldName, int value) {
        selector.setFieldValue(dbFieldName, new Integer(value));
    }


    /**
     * Construction d'une clause liste.
     *
     * @param list Liste des champs pour la clause
     * @param sep  Le separateur de champs (ex " and ")
     *
     * @return la clause : "PERIOD=? and P=?"
     */
    private static String buildClause(Collection list, String sep) {
        StringBuilder buffer = new StringBuilder();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            buffer.append(iter.next()).append("=?");
            if (iter.hasNext()) {
                buffer.append(sep);
            }
        }
        return buffer.toString();
    }


    /**
     * Construit la liste des noms de colonne.
     *
     * @param columns Liste des colonnes a inserer
     *
     * @return La liste. La liste est de la forme : "COL1, COL2..."
     */
    private static String buildDBFieldNameList(Collection columns) {
        StringBuilder nameList = new StringBuilder();

        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            nameList.append((String)iter.next());
            if (iter.hasNext()) {
                nameList.append(", ");
            }
        }
        return nameList.toString();
    }


    /**
     * Construit le squelette liste des valeurs à inserer dans la BD.
     *
     * @param nbOfValues Nombre de valeurs
     *
     * @return le squelette, de la forme : "values (?, ?...)"
     */
    private static String buildDBFieldValuesList(int nbOfValues) {
        StringBuilder buffer = new StringBuilder("values (");

        for (int i = 0; i < nbOfValues; i++) {
            buffer.append("?");
            if (i < nbOfValues - 1) {
                buffer.append(", ");
            }
        }
        buffer.append(")");

        return buffer.toString();
    }


    /**
     * Remplissage du prepared Statement avec la liste de champs.
     *
     * @param idx   L'index a partir duquel on remplit le statement.
     * @param pstmt Le prepared statement
     * @param list  La liste des champs.
     *
     * @throws SQLException En cas d'erreur lors de l'acces a la base.
     */
    private static void fillStatement(int idx, PreparedStatement pstmt, SQLFieldList list)
          throws SQLException {
        for (Iterator iter = list.sqlFields(); iter.hasNext(); idx++) {
            SQLField field = (SQLField)iter.next();
            pstmt.setObject(idx, field.getValue(), field.getSQLType());
        }
        list.clearValues();
    }
}
