/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.test.common.LogString;
import net.codjo.test.common.PathUtil;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.CommandFileMock;
import net.codjo.workflow.gui.wizard.WizardUtil;
import java.io.File;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public class ImportVtomCallerTest extends TestCase {
    private static final String TEMPORARY_DIR = System.getProperty("java.io.tmpdir");
    private LogString log = new LogString();
    private String filePath;


    @Override
    protected void setUp() throws Exception {
        filePath =
              PathUtil.findJavaFileDirectory(ImportVtomCallerTest.class).getAbsolutePath()
              + File.separator + "ImportVtomCallerTest.java";
    }


    public void test_call() throws Exception {
        ImportVtomCaller vtomCaller = new ImportVtomCaller(new CommandFileMock(log));

        Map wizardState =
              WizardUtil.createImportState(filePath, "Decisiv", TEMPORARY_DIR);

        System.setProperty("user.name", "fede");

        vtomCaller.call(wizardState);

        log.assertContent(
              "setTimeout(3600000), execute([fede, fede_Decisiv_ImportVtomCallerTest.java])");
    }


    public void test_callError() throws Exception {
        CommandFileMock mock = new CommandFileMock(log);
        CommandFile.ExecuteException executeException =
              new CommandFile.ExecuteException("erreur", "", -1);
        mock.mockExecuteFailure(executeException);
        ImportVtomCaller vtomCaller = new ImportVtomCaller(mock);

        try {
            vtomCaller.call(WizardUtil.createImportState(filePath, "Decisiv",
                                                         TEMPORARY_DIR));
            fail();
        }
        catch (CommandFile.ExecuteException ex) {
            assertSame(executeException, ex);
        }
    }
}
