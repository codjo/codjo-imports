/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.imports.gui.wizard;
import net.codjo.test.common.fixture.DirectoryFixture;
import net.codjo.util.file.FileUtil;
import java.io.File;
import junit.framework.TestCase;
/**
 *
 */
public class ImportFileUtilTest extends TestCase {
    private DirectoryFixture fixture;


    public void test_determineDestinationFileName() throws Exception {
        File sourceFile = new File("to_import.txt");
        String fileName = ImportFileUtil.determineDestinationFileName(sourceFile, "Decisiv");
        assertEquals("fede_Decisiv_to_import.txt", fileName);
    }


    public void test_copyFileToInbox() throws Exception {
        File sourceFile = new File(fixture.getAbsolutePath() + File.separator + "to_import.txt");
        FileUtil.saveContent(sourceFile, "contenu à importer");
        File resultFile = ImportFileUtil.copyFileToInbox(sourceFile, "Decisiv",
                                                         fixture.getAbsolutePath());
        assertTrue(resultFile.exists());
        assertEquals("fede_Decisiv_to_import.txt", resultFile.getName());
        assertEquals(FileUtil.loadContent(sourceFile), FileUtil.loadContent(resultFile));
    }


    public void test_checkFileName() throws Exception {
        assertFalse(ImportFileUtil.checkFileName("C:\\dev\\STOCK_ BNPP_0801.txt"));
        assertFalse(ImportFileUtil.checkFileName("C:\\dèv\\STOCK_BNPP_0801.txt"));
        assertTrue(ImportFileUtil.checkFileName("c:/dev/temp/ImportFileUtilTest/to_import.txt"));
        assertTrue(ImportFileUtil.checkFileName("c:\\dev\\temp\\ImportFileUtilTest-1\\to_import.txt"));
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Override
    protected void setUp() throws Exception {
        System.getProperties().put("user.name", "fede");
        String workingPath = System.getProperty("java.io.tmpdir") + File.separator + "ImportFileUtilTest";
        fixture = new DirectoryFixture(workingPath);
        fixture.doSetUp();
    }
}
