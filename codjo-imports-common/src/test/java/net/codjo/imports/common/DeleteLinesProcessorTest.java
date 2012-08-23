/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.common;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import junit.framework.TestCase;
import org.easymock.MockControl;
/**
 * Classe de test de {@link DeleteLinesProcessor}.
 */
public class DeleteLinesProcessorTest extends TestCase {
    private MockControl ctrlCon;
    private Connection mockCon;
    private MockControl ctrlStmt;
    private Statement mockStmt;


    public void test_proceed_withWhereClause() throws Exception {
        mockCon.createStatement();
        ctrlCon.setReturnValue(mockStmt);

        mockStmt.executeUpdate("delete from TABLE where CONDITION");
        ctrlStmt.setReturnValue(5);
        mockStmt.close();

        replay();

        DeleteLinesProcessor processor = new DeleteLinesProcessor("CONDITION");
        processor.preProceed(mockCon, "TABLE", new File("file.txt"));

        verify();
    }


    public void test_proceed_noWhereClause() throws Exception {
        mockCon.createStatement();
        ctrlCon.setReturnValue(mockStmt);

        mockStmt.executeUpdate("delete from TABLE");
        ctrlStmt.setReturnValue(5);
        mockStmt.close();

        replay();

        DeleteLinesProcessor processor = new DeleteLinesProcessor();
        processor.preProceed(mockCon, "TABLE", new File("file.txt"));

        verify();
    }


    private void replay() {
        ctrlCon.replay();
        ctrlStmt.replay();
    }


    private void verify() {
        ctrlCon.verify();
        ctrlStmt.verify();
    }


    @Override
    protected void setUp() throws Exception {
        ctrlCon = MockControl.createControl(Connection.class);
        mockCon = (Connection)ctrlCon.getMock();

        ctrlStmt = MockControl.createControl(Statement.class);
        mockStmt = (Statement)ctrlStmt.getMock();
    }
}
