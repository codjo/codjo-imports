/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 * Classe de test de {@link QueryHelper}.
 */
public class QueryHelperTest extends TestCase {
    public void test_buildInsertStatement() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        String query = QueryHelper.buildInsertQuery("MA_TABLE", columnList);
        assertEquals(query, "insert into MA_TABLE (C, B, A) values (?, ?, ?)");
    }


    public void test_buildSelectQuery() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select C, B, A from MA_TABLE where A=? and C=?");
    }


    public void test_buildSelectQuery_Star() {
        List columnList = null;

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildSelectQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "select * from MA_TABLE where A=? and C=?");
    }


    public void test_buildUpdateQuery() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String query = QueryHelper.buildUpdateQuery("MA_TABLE", columnList, whereList);
        assertEquals(query, "update MA_TABLE set C=? , B=? , A=? where A=? and C=?");
    }


    public void test_buildUpdateQueryWithWhereClause() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("C");
        columnList.add("B");
        columnList.add("A");

        List<String> whereList = new ArrayList<String>();
        whereList.add("A");
        whereList.add("C");

        String whereClause = "D='TITI'";

        String query = QueryHelper.buildUpdateQueryWithWhereClause("MA_TABLE", columnList,
                                                                   whereList, whereClause);
        assertEquals(query, "update MA_TABLE set C=? , B=? , A=? where A=? and C=? and D='TITI'");
    }
}
